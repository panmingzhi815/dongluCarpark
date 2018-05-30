package com.donglu.carpark.hardware;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dongluhitec.card.domain.db.Device;

public class CarparkScreenServiceImpl implements CarparkScreenService {
	private final Logger LOGGER = LoggerFactory.getLogger(CarparkScreenServiceImpl.class);
	private CarparkScreenServiceLog log;
	Map<String, Lock> mapDeviceLocks=new HashMap<>();
	
	public boolean showCarparkQrCode(Device device, int type, String content) {
		if (device==null) {
			return true;
		}
		String address = device.getLink().getAddress();
		System.out.println(address);
		byte[] bs=new byte[194];
		bs[0]=(byte) type;
		byte[] bytes = content.getBytes();
		System.arraycopy(bytes, 0, bs, 1, bytes.length);
		return sendMessage(device,(byte) 0x50,bs,194);
	}
	private boolean sendMessage(Device device,int code, byte[] bs,int contentLength) {
		byte[] sendMessage = sendMessage(device, code, bs, contentLength, 0);
		return sendMessage!=null;
	}
	private byte[] sendMessage(Device device,int code, byte[] bs,int contentLength, int retuenDataLength) {
		
		String address = device.getLink().getAddress();
		byte[] head=new byte[]{0x01,0x57,0x00,0x01,0x00,0x01,(byte) code,0x02};
		String[] split = address.split(":");
		byte bytes[]=new byte[head.length+contentLength+2];
		System.arraycopy(head, 0, bytes, 0, head.length);
		if (bs!=null&&contentLength>0) {
			System.arraycopy(bs, 0, bytes, head.length, bs.length);
		}
		bytes[bytes.length-2]=0x03;
		bytes[bytes.length-1]=BCC(bytes, 0, bytes.length-1);
		Lock lock = getLock(address);
		try {
			lock.lock();
			try (Socket s = new Socket(split[0], Integer.valueOf(split[1]))) {
				println(bytes.length + "==向设备发送消息：" + byteArrayToHexString(bytes));
				s.setSoTimeout(2000);
				OutputStream os = s.getOutputStream();
				os.write(bytes);
				os.flush();
				InputStream is = s.getInputStream();
				byte[] b = null;
				if (retuenDataLength >= 0) {
					b = new byte[10 + retuenDataLength];
					is.read(b);
					println("收到设备返回消息：" + byteArrayToHexString(b));
					if (retuenDataLength > 0) {
						byte[] data = new byte[retuenDataLength];
						System.arraycopy(b, head.length, data, 0, retuenDataLength);
						return data;
					}
				}
				return b;
			} catch (Exception e) {
				LOGGER.error("对设备{}发送消息是发生错误", device);
				LOGGER.error("", e);
				println("发生错误:" + e);
				return null;
				//			throw new RuntimeException(e);
			} 
		} finally {
			lock.unlock();
		}
	}
	/**
	 * @param address
	 * @return
	 */
	public Lock getLock(String address) {
		Lock lock = mapDeviceLocks.getOrDefault(address, new ReentrantLock());
		mapDeviceLocks.put(address, lock);
		return lock;
	}

	public boolean carIn(Device device, String plate, String content, boolean isOpen) {
		byte[] bs=new byte[51];
		try {
			byte[] bytes = plate.getBytes("GBK");
			System.arraycopy(bytes, 0, bs, 0, bytes.length);
			byte[] bytes2 = content.getBytes("GBK");
			System.arraycopy(bytes2, 0, bs, 9, bytes2.length);
			bs[bs.length-1]=(byte) (isOpen?1:0);
			return sendMessage(device, (byte) 0x51, bs, 51);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public boolean carOut(Device device, String plate, int times, String shouldMoney, String leftMoney)  {
		try {
			byte[] bs=new byte[25];
			byte[] bytes = plate.getBytes("GBK");
			System.arraycopy(bytes, 0, bs, 0, bytes.length);
			bytes = new byte[2];
			bytes[0]=(byte) (times%0xff);
			bytes[1]=(byte) (times/0xff);
			System.arraycopy(bytes, 0, bs, 9, bytes.length);
			bytes = shouldMoney.getBytes("GBK");
			System.arraycopy(bytes, 0, bs, 11, bytes.length);
			bytes = leftMoney.getBytes("GBK");
			System.arraycopy(bytes, 0, bs, 18, bytes.length);
			return sendMessage(device, (byte) 0x52, bs, 25);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public boolean showCarparkPosition(Device device, int position) {
		byte[] bs=new byte[2];
		bs[0]=(byte) (position%256);
		bs[1]=(byte) (position/256);
		return sendMessage(device, (byte) 0x53, bs, 2);
	}

	public boolean showCarparkUsualContent(Device device, String content) {
		try {
			return sendMessage(device, (byte) 0x54, content.getBytes("GBK"),41);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
     * Checksum.
     *
     * @param bb
     * @param start
     * @param length
     * @return
     */
    public static byte BCC(byte[] bb, int start, int length) {
        byte checksum = bb[start];
        for (int i = start + 1; i < (start + length); i++) {
            checksum ^= bb[i];
        }
        checksum |= 0x20;
        return checksum;
    }
    
    public static String byteArrayToHexString(byte[] bytes) {
        StringBuilder format = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String upperCase = Integer.toHexString(bytes[i]&0xff).toUpperCase();
            if(upperCase.length()==1){
            	format.append("0");
            }
			format.append(upperCase+" ");
        }
        return format.toString();
    }
	
    public static void main(String[] args) {
		UUID uuid = UUID.randomUUID();
		System.out.println(uuid.toString().replaceAll("-", ""));
	}
    
    void println(String s){
    	System.out.println(s);
    	if(log!=null){
    		log.log(s);
    	}
    }

	@Override
	public boolean screenOpenDoor(Device device,int type) {
		LOGGER.info("对一体机进行开闸：{},{}",device,type);
		return sendMessage(device, 0x55, new byte[]{(byte) type}, 1);
	}

	@Override
	public boolean restartScreen(Device device) {
		return sendMessage(device, 0x56, null, 0);
	}

	@Override
	public boolean setScreenColor(Device device,int type) {
		return sendMessage(device, 0x57, new byte[]{(byte) type}, 1);
	}

	@Override
	public boolean showSingleRowContent(Device device, int rowIndex, int voice, String content) {
		try {
			byte[] bs=new byte[43];
			bs[0]=(byte) rowIndex;
			bs[1]=(byte) voice;
			System.arraycopy(content.getBytes("GBK"), 0, bs, 2, content.getBytes("GBK").length);
			return sendMessage(device, 0x58, bs, bs.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean initDevice(Device device) {
		byte[] bs = sendMessage(device, 0xA0, null, 0,1);
		return bs[0]=='y';
	}
	@Override
	public boolean setDeviceDateTime(Device device, Date value) {
		Calendar c = Calendar.getInstance();
		c.setTime(value);
		byte bs[]=new byte[7];
		bs[0]= Integer.valueOf((c.get(Calendar.YEAR)%100)+"", 16).byteValue();
		bs[1]=Integer.valueOf((c.get(Calendar.MONTH)+1)+"", 16).byteValue();
		bs[2]=Integer.valueOf((c.get(Calendar.DAY_OF_MONTH))+"", 16).byteValue();
		bs[3]=Integer.valueOf((c.get(Calendar.DAY_OF_WEEK))+"", 16).byteValue();
		bs[4]=Integer.valueOf((c.get(Calendar.HOUR_OF_DAY))+"", 16).byteValue();
		bs[5]=Integer.valueOf((c.get(Calendar.MINUTE))+"", 16).byteValue();
		bs[6]=Integer.valueOf((c.get(Calendar.SECOND))+"", 16).byteValue();
		sendMessage(device, 0xba, bs, bs.length, -1);
		return true;
	}
	@Override
	public Date readDeviceDateTime(Device device) {
		byte[] bs = sendMessage(device, 0xbb, null, 0, 6);
		if(bs==null){
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.set(Integer.valueOf("20"+(Integer.toHexString(bs[0]&0xff))+""), Integer.valueOf((Integer.toHexString(bs[1]&0xff))+"")-1, 
				Integer.valueOf((Integer.toHexString(bs[2]&0xff))+""), Integer.valueOf((Integer.toHexString(bs[3]&0xff))+""), 
				Integer.valueOf((Integer.toHexString(bs[4]&0xff))+""), Integer.valueOf((Integer.toHexString(bs[5]&0xff))+""));
		return c.getTime();
	}
	@Override
	public void setLog(CarparkScreenServiceLog log) {
		this.log = log;
	}
	@Override
	public boolean setQrCodeColor(Device device,int type) {
		return sendMessage(device, 0x60, new byte[]{(byte) type}, 1);
	}
	@Override
	public boolean setQrCodeTime(Device device,int seconds) {
		byte[] bs=new byte[2];
		bs[0]=(byte) (seconds%256);
		bs[1]=(byte) (seconds/256);
		return sendMessage(device, 0x61, bs, 2);
	}
	@Override
	public boolean showCarparkQrCode(Device device, int type, String qrCode, String voice) {
		if (device==null) {
			return true;
		}
		if (voice==null||voice.trim().isEmpty()) {
			return showCarparkQrCode(device, type, qrCode);
		}
		String address = device.getLink().getAddress();
		System.out.println(address);
		byte[] bs=new byte[194];
		bs[0]=(byte) type;
		byte[] bytes = qrCode.getBytes();
		System.arraycopy(bytes, 0, bs, 1, bytes.length);
		try {
			byte[] bytes2 = voice.getBytes("GBK");
			System.arraycopy(bytes2, 0, bs, 101, bytes2.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sendMessage(device,(byte) 0x62,bs,194);
	}
}
