package com.donglu.carpark.util;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkUserService;
import com.donglu.carpark.util.ExcelImportExport.ExcelImportExportData;
import com.dongluhitec.card.domain.db.CardUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.ui.util.ProcessBarMonitor;
import com.google.common.base.Strings;
import jp.ne.so_net.ga2.no_ji.jcom.ReleaseManager;
import jp.ne.so_net.ga2.no_ji.jcom.excel8.ExcelApplication;
import jp.ne.so_net.ga2.no_ji.jcom.excel8.ExcelWorkbook;
import jp.ne.so_net.ga2.no_ji.jcom.excel8.ExcelWorkbooks;
import jp.ne.so_net.ga2.no_ji.jcom.excel8.ExcelWorksheet;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.imageio.ImageIO;

import static org.hamcrest.CoreMatchers.instanceOf;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("all")
public class ExcelImportExportImpl implements ExcelImportExport {


	private static final String USER_VALIDTO = "yyyy-MM-dd HH:mm:ss";

	@Override
	public int getExcelRowNum(String excelPath) {
		try (FileInputStream fis = new FileInputStream(excelPath);) {
			HSSFWorkbook wb = new HSSFWorkbook(fis);
			HSSFSheet sheet = wb.getSheetAt(0);
			return sheet.getLastRowNum() + 1;
		} catch (Exception e) {
			return 0;
		}
	}

	public File getTempFile(String lastType) throws IOException {
		File file = new File(System.getProperty("user.dir") + File.separator + "temp" + File.separator + lastType);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(System.getProperty("user.dir") + File.separator + "temp" + File.separator + lastType + File.separator + System.currentTimeMillis() + "." + lastType);
		while (file.exists()) {
			file = new File(System.getProperty("user.dir") + File.separator + "temp" + File.separator + lastType + File.separator + System.currentTimeMillis() + "." + lastType);
		}
		file.createNewFile();
		return file;
	}

	public CellStyle getAlignCenterStyle(HSSFWorkbook wb, short horizal, short vertical) {
		CellStyle cellStyle = wb.createCellStyle();
		if (horizal != 0)
			cellStyle.setAlignment(horizal);
		if (vertical != 0)
			cellStyle.setVerticalAlignment(vertical);
		cellStyle.setBorderBottom((short) 1);
		cellStyle.setBorderLeft((short) 1);
		cellStyle.setBorderRight((short) 1);
		cellStyle.setBorderTop((short) 1);
		return cellStyle;
	}

	public void setCellImageValue(HSSFWorkbook wb, Sheet sheet, int row, int cell, BufferedImage bufferedImage) throws Exception {
		// 先成生临时图片
		File file = getTempFile("jpeg");
		FileOutputStream fos = new FileOutputStream(file);
		ImageIO.write(bufferedImage, "JPEG", fos);
		fos.close();

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		boolean jpeg = ImageIO.write(bufferedImage, "JPEG", bos);
		if (jpeg == false) {
			throw new Exception("生成图片数据失败！");
		}
		int pictureIdx = wb.addPicture(bos.toByteArray(), wb.PICTURE_TYPE_JPEG);

		CreationHelper helper = wb.getCreationHelper();

		Drawing drawing = sheet.createDrawingPatriarch();

		ClientAnchor anchor = helper.createClientAnchor();
		anchor.setCol1(cell);
		anchor.setRow1(row);
		Picture pict = drawing.createPicture(anchor, pictureIdx);

		pict.resize();
	}

	private static BufferedImage convert(int width, int height, BufferedImage input) throws Exception {
		// 初始化输出图片
		BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// 重新绘图
		Image image = input.getScaledInstance(output.getWidth(), output.getHeight(), output.getType());
		output.createGraphics().drawImage(image, null, null);
		return output;
	}

	private void copyTemplement(String templateExcel, String targetExcel) throws Exception {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			File file2 = new File(templateExcel);
			// 新建文件输入流并对它进行缓冲
			inBuff = new BufferedInputStream(new FileInputStream(file2));

			File file = new File(targetExcel);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			// 新建文件输出流并对它进行缓冲
			outBuff = new BufferedOutputStream(new FileOutputStream(new File(targetExcel)));

			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuff.flush();
		} catch (Exception e) {
			throw new Exception("拷贝模板出错！", e);
		} finally {
			// 关闭流
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
	}

	public int autoCopySheet(HSSFWorkbook wb, int totalSize, int pageSize, int pageCellRow) {
		int page = (int) Math.ceil(totalSize / (pageSize * 1.0));
		for (int i = 0; i < page; i++) {
			HSSFSheet cloneSheet = wb.cloneSheet(0);
			cloneSheet.setActive(true);
			wb.setSheetName(i + 1, "第" + (i + 1) + "页");
			// 写总页数
			cloneSheet.getRow(pageCellRow).getCell(0).setCellValue("第" + (i + 1) + "页,共" + page + "页");
		}
		return page;
	}

	private String getCellStringValue(HSSFRow row, int column) {
		if (row == null)
			return "";
		try {
			HSSFCell cell = row.getCell(column);
			return cell == null ? "" : cell.getStringCellValue();
		} catch (Exception e) {
			return "";
		}

	}

	private String getCellStringValue(HSSFSheet sheet, String position) {
		int row = Integer.parseInt(position.substring(1)) - 1;
		int cel = position.charAt(0) - 'A';
		Cell cell = sheet.getRow(row).getCell(cel);
		return cell == null ? "" : cell.getStringCellValue();
	}

	private void setCellStringvalue(HSSFRow row, int column, Object value, CellStyle cellStyle) {
		if (row != null) {
			HSSFCell cell = row.getCell(column);
			if (cell == null) {
				cell = row.createCell(column);
			}
			cell.setCellValue(StrUtil.isEmpty(value) ? "" : String.valueOf(value));
			if (cellStyle != null) {
				cell.setCellStyle(cellStyle);
			}
		}
	}

	public static Cell createCellValue(HSSFSheet sheet, int row, int cel, String value, CellStyle style) {
		Cell cell = sheet.getRow(row).createCell(cel);
		if (style != null) {
			cell.setCellStyle(style);
		}
		cell.setCellValue(value == null ? "" : value);
		return cell;
	}

	public static Cell setCellValue(HSSFSheet sheet, int row, int cel, String value, CellStyle style) {
		Cell cell = sheet.getRow(row).getCell(cel);
		if (style != null) {
			cell.setCellStyle(style);
		}
		cell.setCellValue(Strings.isNullOrEmpty(value) == true ? " " : value);
		return cell;
	}

	public static Cell setCellValue(HSSFSheet sheet, String celName, String value, CellStyle style) {
		int row = Integer.parseInt(celName.substring(1)) - 1;
		int cel = celName.charAt(0) - 'A';
		Cell cell = sheet.getRow(row).getCell(cel);
		if (style != null) {
			cell.setCellStyle(style);
		}
		cell.setCellValue(Strings.isNullOrEmpty(value) == true ? " " : value);
		return cell;
	}

	private void paintBackgroupColour(HSSFWorkbook book, HSSFRow row, int startColumn, int endColumn) {
		HSSFCellStyle style = book.createCellStyle();

		style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		style.setBorderBottom((short) 1);
		style.setBorderLeft((short) 1);
		style.setBorderRight((short) 1);
		style.setBorderTop((short) 1);
		for (int i = startColumn; i <= endColumn; i++) {
			HSSFCell cell = row.getCell(i);
			if (cell == null) {
				cell = row.createCell(i);
			}
			cell.setCellStyle(style);
		}
	}

	public static void printExcel(HSSFSheet sheet, int index) {
		HSSFPrintSetup printSetup = sheet.getPrintSetup();

	}

	@Override
	public void printExcel(String filePath) throws Exception {
		ReleaseManager rm = new ReleaseManager();
		ExcelApplication excel = new ExcelApplication(rm);
		ExcelWorkbooks xlBooks = excel.Workbooks();
		ExcelWorkbook xlBook = xlBooks.Open(filePath);
		ExcelWorksheet xlSheet = excel.ActiveSheet();
		xlSheet.PrintOut();
		xlBook.Close(false, null, false);
		excel.Quit();
	}

	public static void main(String[] args) {
		int[] ints = new int[] { 1, 99, 2, 22, 34, 66, 77, 55, 44, 33, 88, 23 };
		int no = 0;
		for (int i : ints) {
			int flag = 0;
			for (int j : ints) {
				if (i > j) {
					flag = i;
				} else {
					flag = j;
				}
			}
			if (no < flag) {
				no = flag;
			}
		}
		System.out.println(no);
		// System.out.println(f.exists());
	}

	@Override
	public void exportUser(String path, List<SingleCarparkUser> list) throws Exception {
		copyTemplement(UserTemplate, path);

		HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(path));
		HSSFSheet sheet = wb.getSheetAt(0);
		int currentRow = 2;
		for (int i = 0; i < list.size(); i++) {
			HSSFRow row = sheet.createRow(currentRow + i);
			
			row.createCell(0).setCellValue(i+1);
			SingleCarparkUser user = list.get(i);
			row.createCell(1).setCellValue(user.getName());
			row.createCell(2).setCellValue(user.getPlateNo());
			row.createCell(3).setCellValue(user.getTelephone());
			row.createCell(4).setCellValue(user.getAddress());
			row.createCell(5).setCellValue(user.getType());
			row.createCell(6).setCellValue(StrUtil.formatDate(user.getValidTo(), USER_VALIDTO));
			row.createCell(7).setCellValue(user.getCarparkNo());
			row.createCell(8).setCellValue(user.getParkingSpace());
			row.createCell(9).setCellValue(user.getCarparkSlotType()+"");
			row.createCell(10).setCellValue(user.getCarpark().getCode());
			row.createCell(11).setCellValue(user.getCarpark().getName());
			row.createCell(12).setCellValue(user.getMonthChargeCode());
			row.createCell(13).setCellValue(user.getMonthChargeName());
			row.createCell(14).setCellValue(user.getRemark());
		}
		FileOutputStream fileOut = new FileOutputStream(path);
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();
	}

	@Override
	public int importUser(String path, CarparkDatabaseServiceProvider sp) throws Exception  {
		int failNum = 0;
		CarparkUserService carparkUserService = sp.getCarparkUserService();
		HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(path));
		HSSFSheet sheet = wb.getSheetAt(0);
		int currentRow = 0;
		HSSFRow row = sheet.getRow(currentRow);
		Map<String, SingleCarparkCarpark> map=new HashMap<>();
		Map<String, SingleCarparkMonthlyCharge> mapCharge=new HashMap<>();
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setBorderBottom((short) 1);
		cellStyle.setBorderLeft((short) 1);
		cellStyle.setBorderRight((short) 1);
		cellStyle.setBorderTop((short) 1);
		boolean isContent=false;
		while (row != null) {
			try {
				if (!isContent) {
					String cellStringValue = getCellStringValue(row, 0);
					if (cellStringValue!=null&&cellStringValue.equals("编号")) {
						isContent=true;
					}else{
						if (currentRow>=1) {
							isContent=true;
						}
					}
					continue;
				}
				String status = getCellStringValue(row, 15);
				if (status.equals("处理成功")) {
					continue;
				}
				// 导入用户
				String name,plateNO,address,type,validTo,carparkNo,remark,telephone,parkingSpace,carparkSlotType;
				plateNO=getCellStringValue(row, 2);
				if (StrUtil.isEmpty(plateNO)) {
					throw new Exception("空的车牌");
				}
				String caparkCode=getCellStringValue(row, 10);
				
				if (map.get(caparkCode) == null) {
					SingleCarparkCarpark findCarparkByCode = sp.getCarparkService().findCarparkByCode(caparkCode);
					if (StrUtil.isEmpty(findCarparkByCode)) {
						throw new Exception("停车场未找到");
					}else{
						map.put(caparkCode, findCarparkByCode);
					}
				}
				SingleCarparkUser user=new SingleCarparkUser();
				SingleCarparkUser findUserByPlateNo = carparkUserService.findUserByPlateNo(plateNO,map.get(caparkCode).getId());
				if (!StrUtil.isEmpty(findUserByPlateNo)) {
					user=findUserByPlateNo;
				}
				String chargeCode=getCellStringValue(row, 12);
				if (!StrUtil.isEmpty(chargeCode)) {
					SingleCarparkMonthlyCharge mc = mapCharge.get(chargeCode);
					if (mc==null) {
						mc = sp.getCarparkService().findMonthlyChargeByCode(chargeCode, map.get(caparkCode));
						if (mc==null) {
							throw new Exception("收费标准未找到");
						}
						mapCharge.put(chargeCode, mc);
					}
					user.setMonthChargeId(mc.getId());
					user.setMonthChargeCode(mc.getChargeCode());
					user.setMonthChargeName(mc.getChargeName());
				}
				name = getCellStringValue(row, 1);
				address=getCellStringValue(row, 4);
				type=getCellStringValue(row, 5);
				validTo=getCellStringValue(row, 6);
				carparkNo=getCellStringValue(row, 7);
				telephone=getCellStringValue(row, 3);
				parkingSpace=getCellStringValue(row, 8);
				if (!StrUtil.isEmpty(parkingSpace)) {
					SingleCarparkUser u =sp.getCarparkUserService().findUserByParkingSpace(parkingSpace);
					if (u!=null) {
						throw new Exception("车位已经存在");
					}
				}
				carparkSlotType=getCellStringValue(row, 9);
				
				remark=getCellStringValue(row, 14);
				
				user.setName(name);
				user.setPlateNo(plateNO);
				user.setAddress(address);
				user.setType(type);
				user.setTelephone(telephone);
				Date parse = StrUtil.parse(validTo, USER_VALIDTO);
				if (parse==null) {
					parse=StrUtil.parse(validTo, "yyyy-MM-dd");
					if (parse!=null) {
						parse=StrUtil.getTodayBottomTime(parse);
					}
				}
				user.setValidTo(parse);
				user.setCarparkNo(carparkNo);
				user.setRemark(remark);
				user.setCarpark(map.get(caparkCode));
				user.setParkingSpace(parkingSpace);
				try {
					user.setCarparkSlotType(SingleCarparkUser.CarparkSlotTypeEnum.valueOf(carparkSlotType));
				} catch (Exception e) {
					if (user.getCarparkSlotType()==null) {
						user.setCarparkSlotType(SingleCarparkUser.CarparkSlotTypeEnum.非固定车位);
					}
				}
				carparkUserService.saveUser(user);
				setCellStringvalue(row, 15, "处理成功", cellStyle);
			} catch (Exception e) {
				failNum++;
				e.printStackTrace();
				setCellStringvalue(row, 15, "保存失败" + e.getMessage(), cellStyle);
			} finally {
				currentRow++;
				row = sheet.getRow(currentRow);
			}
		}
		FileOutputStream fileOut = new FileOutputStream(path);
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();
		return failNum;
	}








	@Override
	public void export(String path,String[] names, String[] cloumns, List<? extends Object> list) throws Exception {
		copyTemplement(NomalTemplate, path);

		HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(path));
		HSSFSheet sheet = wb.getSheetAt(0);
		int currentRow = 0;
		HSSFRow row;
        if (!StrUtil.isEmpty(names)) {
        	row = sheet.createRow(currentRow);
        	//		row.createCell(0).setCellValue("编号");
        	for (int i = 0; i < names.length; i++) {
        		String string = names[i];
        		HSSFCell createCell = row.createCell(i + 1);
        		row.createCell(i).setCellValue(string);
        	}
        	currentRow++;
        }
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow(currentRow + i);
			Object o = list.get(i);
			if (o instanceof List<?>) {
				List<?> l=(List<?>) o;
				int index=0;
				for (Object object : l) {
					if(object instanceof Double) {
						row.createCell(index++).setCellValue((Double)object);
					}else
					row.createCell(index++).setCellValue(String.valueOf(object));
				}
				continue;
			}
//			row.createCell(0).setCellValue(i+1);
			for (int j = 0; j < cloumns.length; j++) {
				String string = cloumns[j];
				Object fieldValueByName = CarparkUtils.getFieldValueByName(string, o);
//				row.createCell(j+1).setCellValue(fieldValueByName+"");
				row.createCell(j).setCellValue(fieldValueByName==null?"":String.valueOf(fieldValueByName));
			}
		}
		FileOutputStream fileOut = new FileOutputStream(path);
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();
	}

	@Override
	public List<String> importPlateNOByUser(String path) throws Exception {
		List<String> list=new ArrayList<>();
		int failNum = 0;
		HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(path));
		HSSFSheet sheet = wb.getSheetAt(0);
		int currentRow = 0;
		HSSFRow row = sheet.getRow(currentRow);
		boolean isContent=false;
		while (row != null) {
			try {
				if (!isContent) {
					String cellStringValue = getCellStringValue(row, 0);
					if (cellStringValue!=null&&cellStringValue.equals("编号")) {
						isContent=true;
					}else{
						if (currentRow>=1) {
							isContent=true;
						}
					}
					continue;
				}
				// 根据用户excel导入信息
				String plateNO=getCellStringValue(row, 2);
				if (StrUtil.isEmpty(plateNO)) {
					throw new Exception("空的车牌");
				}
				String[] split = plateNO.split(",");
				for (String string : split) {
					if (!list.contains(string)) {
						list.add(string);
					}
				}
			} catch (Exception e) {
				failNum++;
				e.printStackTrace();
			} finally {
				currentRow++;
				row = sheet.getRow(currentRow);
			}
		}
		return list;
	}

	@Override
	public void export(String path, String[] names, String[] cloumns, List<? extends Object> list, ProcessBarMonitor monitor) throws Exception {
		copyTemplement(NomalTemplate, path);

		HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(path));
		HSSFSheet sheet = wb.getSheetAt(0);
		int currentRow = 0;
		HSSFRow row = sheet.createRow(currentRow);
//		row.createCell(0).setCellValue("编号");
		int columnWidth[]=new int[cloumns.length];
		for (int i = 0; i < names.length; i++) {
			String string = names[i];
			HSSFCell createCell = row.createCell(i+1);
			row.createCell(i).setCellValue(string);
			columnWidth[i]=string.getBytes("GBK").length;
		}
		currentRow++;
		String string;
		Object fieldValueByName;
		int size=0;
		int sheetSize=0;
		for (int i = 0; i < list.size(); i++) {
			if(monitor.isDispose()){
				throw new Exception("导出已终止！");
			}
			monitor.showMessage("正在导出第"+(i+1)+"条数据");
			monitor.dowork(i+1);
			row = sheet.createRow(currentRow + i-size);
			Object o = list.get(i);
			
//			row.createCell(0).setCellValue(i+1);
			for (int j = 0; j < cloumns.length; j++) {
				string = cloumns[j];
				fieldValueByName = CarparkUtils.getFieldValueByName(string, o);
//				row.createCell(j+1).setCellValue(fieldValueByName+"");
				if (!StrUtil.isEmpty(fieldValueByName)) {
					columnWidth[j]=Math.max(fieldValueByName.toString().getBytes().length, columnWidth[j]/256)*256;
				}
				HSSFCell createCell = row.createCell(j);
				createCell.setCellValue(fieldValueByName==null?"":fieldValueByName+"");
			}
			if(i>0&&i%65530==0){
				sheetSize=sheetSize+1;
				try {
					sheet=wb.getSheetAt(sheetSize);
					if (sheet==null) {
						sheet = wb.createSheet();
					}
				} catch (Exception e) {
					sheet = wb.createSheet();
				}
				size+=65530;
			}
		}
		for (int j = 0; j < columnWidth.length; j++) {
			int i = columnWidth[j];
			if (i<=0) {
				continue;
			}
			sheet.setColumnWidth(j, i);
		}
		FileOutputStream fileOut = new FileOutputStream(path);
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();
	}

	public void exportOperaCharge(String path,String title, List<SingleCarparkInOutHistory> list) throws Exception {
		copyTemplement(OperaChargeTemplate, path);

		HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(path));
		HSSFSheet sheet = wb.getSheetAt(0);
		int currentRow = 1;
		HSSFRow row =row = sheet.getRow(0);
		row.getCell(0).setCellValue(title);
		currentRow++;
		float freeMoneyTotal=0;
		float factMoneyTotal=0;
		float shouldMoneyTotal=0;
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow(currentRow);
			SingleCarparkInOutHistory o = list.get(i);
			int j=0;
			row.createCell(j++).setCellValue(i+1);
			row.createCell(j++).setCellValue(o.getPlateNo());
			row.createCell(j++).setCellValue(o.getInDevice());
			row.createCell(j++).setCellValue(o.getInTimeLabel());
			row.createCell(j++).setCellValue(o.getOutDevice());
			row.createCell(j++).setCellValue(o.getOutTimeLabel());
			float shouldMoney = o.getShouldMoney()==null?0:o.getShouldMoney();
			shouldMoneyTotal+=shouldMoney;
			row.createCell(j++).setCellValue(shouldMoney);
			float factMoney = o.getFactMoney()==null?0:o.getFactMoney();
			factMoneyTotal+=factMoney;
			row.createCell(j++).setCellValue(factMoney);
			float freeMoney = o.getFreeMoney()==null?0:o.getFreeMoney();
			freeMoneyTotal+=freeMoney;
			row.createCell(j++).setCellValue(freeMoney);
			row.createCell(j++).setCellValue(o.getFreeReason());
			currentRow++;
		}
		row = sheet.createRow(currentRow);
		int j=0;
		row.createCell(j++).setCellValue("");
		row.createCell(j++).setCellValue("");
		row.createCell(j++).setCellValue("");
		row.createCell(j++).setCellValue("");
		row.createCell(j++).setCellValue("");
		row.createCell(j++).setCellValue("合计:");
		row.createCell(j++).setCellValue(shouldMoneyTotal);
		row.createCell(j++).setCellValue(factMoneyTotal);
		row.createCell(j++).setCellValue(freeMoneyTotal);
		FileOutputStream fileOut = new FileOutputStream(path);
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();
	}
	
	public int importExcel(String path, int[] is, Function<String[], String> function) throws Exception {
		int failNum = 0;
		HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(path));
		HSSFSheet sheet = wb.getSheetAt(0);
		int currentRow = 0;
		HSSFRow row = sheet.getRow(currentRow);
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setBorderBottom((short) 1);
		cellStyle.setBorderLeft((short) 1);
		cellStyle.setBorderRight((short) 1);
		cellStyle.setBorderTop((short) 1);
		boolean isContent=false;
		while (row != null) {
			try {
				if (!isContent) {
					String cellStringValue = getCellStringValue(row, 0);
					if (cellStringValue!=null&&cellStringValue.equals("编号")) {
						isContent=true;
					}else{
						if (currentRow>=1) {
							isContent=true;
						}
					}
					continue;
				}
				String status = getCellStringValue(row, 15);
				if (status.equals("处理成功")) {
					continue;
				}
				// 导入用户
				String name;
				name = getCellStringValue(row, 1);
				String[] ss=new String[is.length];
				for (int j = 0; j < is.length; j++) {
					int i = is[j];
					ss[j]=getCellStringValue(row, i);
				}
				function.apply(ss);
				setCellStringvalue(row, 15, "处理成功", cellStyle);
			} catch (Exception e) {
				failNum++;
				e.printStackTrace();
				setCellStringvalue(row, 15, "保存失败" + e.getMessage(), cellStyle);
			} finally {
				currentRow++;
				row = sheet.getRow(currentRow);
			}
		}
		FileOutputStream fileOut = new FileOutputStream(path);
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();
		return failNum;
	}

	@Override
	public void exportInOutHistory(String path, List<SingleCarparkInOutHistory> list, ProcessBarMonitor monitor) throws Exception {

		copyTemplement(NomalTemplate, path);

		HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(path));
		HSSFSheet sheet = wb.getSheetAt(0);
		int currentRow = 0;
		HSSFRow row = sheet.createRow(currentRow);
//		row.createCell(0).setCellValue("编号");
		String[] names=new String[]{"车牌号","车辆类型","用户名","进场设备","进场时间","出场设备","出场时间","停留时间","操作员","应收金额","实收金额","免费金额","免费原因","归账编号","备注","进场车牌","出场场车牌"};
		int columnWidth[]=new int[names.length];
		for (int i = 0; i < names.length; i++) {
			String string = names[i];
			HSSFCell createCell = row.createCell(i+1);
			row.createCell(i).setCellValue(string);
			columnWidth[i]=string.getBytes("GBK").length;
		}
		currentRow++;
		String string;
		Object fieldValueByName;
		int size=0;
		int sheetSize=0;
		int totalSize=0;
		int fixSize=0;
		int lt2HourTemp=0;
		int lt2HourFix=0;
		int ge2HourTemp=0;
		int ge2HourFix=0;
		int ge3HourTemp=0;
		int ge3HourFix=0;
		int notOutTemp=0;
		int notOutFix=0;
		for (int i = 0; i < list.size(); i++) {
			if(monitor.isDispose()){
				throw new Exception("导出已终止！");
			}
			monitor.showMessage("正在导出第"+(i+1)+"条数据");
			monitor.dowork(i+1);
			row = sheet.createRow(currentRow + i-size);
			SingleCarparkInOutHistory o = list.get(i);
			if (o.getInTime()!=null) {
				totalSize++;
				if (o.getOutTime()!=null) {
					if (o.getCarType().equals("固定车")) {
						fixSize++;
						if (o.getStillTimeCount()<2*60*60) {
							lt2HourFix++;
    					}else if (o.getStillTimeCount()>=2*60*60&&o.getStillTimeCount()<3*60*60) {
    						ge2HourFix++;
    					}else{
    						ge3HourFix++;
    					}
					}else {
    					if (o.getStillTimeCount()<2*60*60) {
    						lt2HourTemp++;
    					}else if (o.getStillTimeCount()>=2*60*60&&o.getStillTimeCount()<3*60*60) {
    						ge2HourTemp++;
    					}else{
    						ge3HourTemp++;
    					}
					}
				}else {
					if (o.getCarType().equals("固定车")) {
						notOutFix++;
					}else {
						notOutTemp++;
					}
				}
			}
			Object[] os=new Object[] {o.getPlateNo(),o.getCarType(),o.getUserName(),o.getInDevice(),o.getInTimeLabel(),o.getOutDevice(),o.getOutTimeLabel(),
					o.getStillTimeLabel(),o.getOperaName(),o.getShouldMoney(),o.getFactMoney(),o.getFreeMoney(),o.getFreeReason(),o.getReturnAccount(),o.getRemark(),
					o.getInPlateNO(),o.getOutPlateNO()};
//			row.createCell(0).setCellValue(i+1);
			for (int j = 0; j < names.length; j++) {
				fieldValueByName = os[j];
//				row.createCell(j+1).setCellValue(fieldValueByName+"");
				if (!StrUtil.isEmpty(fieldValueByName)) {
					columnWidth[j]=Math.max(fieldValueByName.toString().getBytes().length, columnWidth[j]/256)*256;
				}
				HSSFCell createCell = row.createCell(j);
				createCell.setCellValue(fieldValueByName==null?"":fieldValueByName+"");
			}
			if(i>0&&i%65530==0){
				sheetSize=sheetSize+1;
				try {
					sheet=wb.getSheetAt(sheetSize);
					if (sheet==null) {
						sheet = wb.createSheet();
					}
				} catch (Exception e) {
					sheet = wb.createSheet();
				}
				size+=65530;
			}
		}
		int rowTotalStart=currentRow + list.size()-size;
		sheet.addMergedRegion(new CellRangeAddress(rowTotalStart, rowTotalStart, 1, 2));
		sheet.addMergedRegion(new CellRangeAddress(rowTotalStart, rowTotalStart, 3, 4));
		sheet.addMergedRegion(new CellRangeAddress(rowTotalStart, rowTotalStart, 5, 6));
		sheet.addMergedRegion(new CellRangeAddress(rowTotalStart, rowTotalStart, 7, 8));
		sheet.addMergedRegion(new CellRangeAddress(rowTotalStart, rowTotalStart, 9, 10));
		row = sheet.createRow(rowTotalStart);
		row.createCell(0).setCellValue("");
		row.createCell(1).setCellValue("总数");
		row.createCell(3).setCellValue("2小时内");
		row.createCell(5).setCellValue("2小时外");
		row.createCell(7).setCellValue("3小时外");
		row.createCell(9).setCellValue("未出");
		row = sheet.createRow(rowTotalStart+1);
		row.createCell(0).setCellValue("总车次");
		row.createCell(1).setCellValue("外来车次");
		row.createCell(2).setCellValue("职工车次");
		row.createCell(3).setCellValue("外来车次");
		row.createCell(4).setCellValue("职工车次");
		row.createCell(5).setCellValue("外来车次");
		row.createCell(6).setCellValue("职工车次");
		row.createCell(7).setCellValue("外来车次");
		row.createCell(8).setCellValue("职工车次");
		row.createCell(9).setCellValue("外来车次");
		row.createCell(10).setCellValue("职工车次");
		row = sheet.createRow(rowTotalStart+2);
		row.createCell(0).setCellValue(totalSize+"");
		row.createCell(1).setCellValue(totalSize-fixSize+"");
		row.createCell(2).setCellValue(fixSize+"");
		row.createCell(3).setCellValue(lt2HourTemp+"");
		row.createCell(4).setCellValue(lt2HourFix+"");
		row.createCell(5).setCellValue(ge2HourTemp+"");
		row.createCell(6).setCellValue(ge2HourFix+"");
		row.createCell(7).setCellValue(ge3HourTemp+"");
		row.createCell(8).setCellValue(ge3HourFix+"");
		row.createCell(9).setCellValue(notOutTemp+"");
		row.createCell(10).setCellValue(notOutFix+"");
		
		for (int j = 0; j < columnWidth.length; j++) {
			int i = columnWidth[j];
			if (i<=0) {
				continue;
			}
			sheet.setColumnWidth(j, i);
		}
		FileOutputStream fileOut = new FileOutputStream(path);
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();
	
	}

	@Override
	public void exportInOutHistory(String path,int total, ExcelImportExportData<SingleCarparkInOutHistory> sp, ProcessBarMonitor monitor) throws Exception {
		int size=0;
		int sheetSize=0;
		int totalSize=0;
		int fixSize=0;
		int lt2HourTemp=0;
		int lt2HourFix=0;
		int ge2HourTemp=0;
		int ge2HourFix=0;
		int ge3HourTemp=0;
		int ge3HourFix=0;
		int notOutTemp=0;
		int notOutFix=0;
		List<SingleCarparkInOutHistory> list = new ArrayList<>();
		List<SingleCarparkInOutHistory> cl = new ArrayList<>();
		int pageSize = 65500;
		while(true) {
			list.clear();
			list=sp.getData(size, pageSize);
			if(list.isEmpty()) {
				break;
			}
			size+=list.size();
			String name = new File(path).getName();
			String parent = new File(path).getParent();
			String excelName=parent+(parent.endsWith("\\")?"":"\\")+name.substring(0, name.lastIndexOf("."))+(sheetSize>0?"-"+sheetSize:"")+name.substring(name.lastIndexOf("."));
			copyTemplement(NomalTemplate, excelName);
			
			HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(excelName));
			HSSFSheet sheet = wb.getSheetAt(0);
			int currentRow = 0;
			HSSFRow row = sheet.createRow(currentRow);
//		row.createCell(0).setCellValue("编号");
			String[] names=new String[]{"车牌号","车辆类型","用户名","进场设备","进场时间","出场设备","出场时间","停留时间","操作员","应收金额","现金金额","网上金额","免费金额","免费原因","归账编号","备注","进场车牌","出场场车牌"};
			int columnWidth[]=new int[names.length];
			for (int i = 0; i < names.length; i++) {
				String string = names[i];
				HSSFCell createCell = row.createCell(i+1);
				row.createCell(i).setCellValue(string);
				columnWidth[i]=string.getBytes("GBK").length;
			}
			currentRow++;
			String string;
			Object fieldValueByName;
			for (int i = 0; i < list.size(); i++) {
				if(monitor.isDispose()){
					throw new Exception("导出已终止！");
				}
				monitor.showMessage("正在导出第"+(i+sheetSize*pageSize)+"条数据");
				monitor.dowork(i+sheetSize*pageSize+1);
				row = sheet.createRow(currentRow + i);
				SingleCarparkInOutHistory o = list.get(i);
				if (o.getInTime()!=null) {
					totalSize++;
					if (o.getOutTime()!=null) {
						if (o.getCarType().equals("固定车")) {
							fixSize++;
							if (o.getStillTimeCount()<2*60*60) {
								lt2HourFix++;
							}else if (o.getStillTimeCount()>=2*60*60&&o.getStillTimeCount()<3*60*60) {
								ge2HourFix++;
							}else{
								ge3HourFix++;
							}
						}else {
							if (o.getStillTimeCount()<2*60*60) {
								lt2HourTemp++;
							}else if (o.getStillTimeCount()>=2*60*60&&o.getStillTimeCount()<3*60*60) {
								ge2HourTemp++;
							}else{
								ge3HourTemp++;
							}
						}
					}else {
						if (o.getCarType().equals("固定车")) {
							notOutFix++;
						}else {
							notOutTemp++;
						}
					}
				}
				Object[] os=new Object[] {o.getPlateNo(),o.getCarType(),o.getUserName(),o.getInDevice(),o.getInTimeLabel(),o.getOutDevice(),o.getOutTimeLabel(),
						o.getStillTimeLabel(),o.getOperaName(),o.getShouldMoney(),
						o.getFactMoney()==null?null:(o.getFactMoney()-o.getOnlineMoney()),o.getOnlineMoney(),o.getFreeMoney(),o.getFreeReason(),o.getReturnAccount(),o.getRemarkString(),
						o.getInPlateNO(),o.getOutPlateNO()};
//			row.createCell(0).setCellValue(i+1);
				for (int j = 0; j < names.length; j++) {
					fieldValueByName = os[j];
//				row.createCell(j+1).setCellValue(fieldValueByName+"");
					if (!StrUtil.isEmpty(fieldValueByName)) {
						columnWidth[j]=Math.max(fieldValueByName.toString().getBytes().length, columnWidth[j]/256)*256;
					}
					HSSFCell createCell = row.createCell(j);
					createCell.setCellValue(fieldValueByName==null?"":fieldValueByName+"");
				}
			}
			if (total<=size) {
				int rowTotalStart = currentRow + list.size();
				sheet.addMergedRegion(new CellRangeAddress(rowTotalStart, rowTotalStart, 1, 2));
				sheet.addMergedRegion(new CellRangeAddress(rowTotalStart, rowTotalStart, 3, 4));
				sheet.addMergedRegion(new CellRangeAddress(rowTotalStart, rowTotalStart, 5, 6));
				sheet.addMergedRegion(new CellRangeAddress(rowTotalStart, rowTotalStart, 7, 8));
				sheet.addMergedRegion(new CellRangeAddress(rowTotalStart, rowTotalStart, 9, 10));
				row = sheet.createRow(rowTotalStart);
				row.createCell(0).setCellValue("");
				row.createCell(1).setCellValue("总数");
				row.createCell(3).setCellValue("2小时内");
				row.createCell(5).setCellValue("2小时外");
				row.createCell(7).setCellValue("3小时外");
				row.createCell(9).setCellValue("未出");
				row = sheet.createRow(rowTotalStart + 1);
				row.createCell(0).setCellValue("总车次");
				row.createCell(1).setCellValue("外来车次");
				row.createCell(2).setCellValue("职工车次");
				row.createCell(3).setCellValue("外来车次");
				row.createCell(4).setCellValue("职工车次");
				row.createCell(5).setCellValue("外来车次");
				row.createCell(6).setCellValue("职工车次");
				row.createCell(7).setCellValue("外来车次");
				row.createCell(8).setCellValue("职工车次");
				row.createCell(9).setCellValue("外来车次");
				row.createCell(10).setCellValue("职工车次");
				row = sheet.createRow(rowTotalStart + 2);
				row.createCell(0).setCellValue(totalSize + "");
				row.createCell(1).setCellValue(totalSize - fixSize + "");
				row.createCell(2).setCellValue(fixSize + "");
				row.createCell(3).setCellValue(lt2HourTemp + "");
				row.createCell(4).setCellValue(lt2HourFix + "");
				row.createCell(5).setCellValue(ge2HourTemp + "");
				row.createCell(6).setCellValue(ge2HourFix + "");
				row.createCell(7).setCellValue(ge3HourTemp + "");
				row.createCell(8).setCellValue(ge3HourFix + "");
				row.createCell(9).setCellValue(notOutTemp + "");
				row.createCell(10).setCellValue(notOutFix + "");
			}
			for (int j = 0; j < columnWidth.length; j++) {
				int i = columnWidth[j];
				if (i<=0) {
					continue;
				}
				sheet.setColumnWidth(j, i);
			}
			FileOutputStream fileOut = new FileOutputStream(excelName);
			wb.write(fileOut);
			fileOut.flush();
			fileOut.close();
			sheetSize++;
		}
	}

	public String exportChargeInfo(SingleCarparkInOutHistory singleCarparkInOutHistory) {
		try {
			String path=System.getProperty("user.dir")+File.separator+"print"+File.separator+singleCarparkInOutHistory.getPlateNo()+singleCarparkInOutHistory.getOutTime().getTime()+".xls";
			if(!new File(path).getParentFile().exists()) {
				new File(path).getParentFile().mkdirs();
			}
			copyTemplement(ChargeInfoTemplate, path);

			HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(path));
			HSSFSheet sheet = wb.getSheetAt(0);
			sheet.getRow(3).getCell(2).setCellValue(singleCarparkInOutHistory.getPlateNo());
			sheet.getRow(4).getCell(2).setCellValue(StrUtil.formatDate(singleCarparkInOutHistory.getInTime(), "yyyy-MM-dd HH:mm:ss"));
			sheet.getRow(5).getCell(2).setCellValue(StrUtil.formatDate(singleCarparkInOutHistory.getOutTime(), "yyyy-MM-dd HH:mm:ss"));
			sheet.getRow(6).getCell(2).setCellValue(CarparkUtils.getCarStillTime(StrUtil.MinusTime2(singleCarparkInOutHistory.getInTime(), singleCarparkInOutHistory.getOutTime())).replaceAll("停车", "").replaceAll(",", ""));
			sheet.getRow(7).getCell(2).setCellValue(singleCarparkInOutHistory.getFactMoney()+"元");
			sheet.getRow(8).getCell(2).setCellValue(singleCarparkInOutHistory.getOperaName());
			sheet.getRow(10).getCell(0).setCellValue("打印时间："+StrUtil.formatDateTime(new Date()));
			FileOutputStream fileOut = new FileOutputStream(path);
			wb.write(fileOut);
			fileOut.flush();
			fileOut.close();
			return path;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
