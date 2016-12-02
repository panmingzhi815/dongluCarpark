package com.donglu.carpark.service.background.haiyu;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.Security;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class DesUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DesUtils.class);

    /** 字符串默认键值 */
    private static String strDefaultKey = "gty";

    /** 加密工具 */
    private Cipher encryptCipher = null;

    /** 解密工具 */
    private Cipher decryptCipher = null;

    /**
     * 将byte数组转换为表示16进制值的字符串， 如：byte[]{8,18}转换为：0813， 和public static byte[]
     * hexStr2ByteArr(String strIn) 互为可逆的转换过程
     *
     * @param arrB
     *            需要转换的byte数组
     * @return 转换后的字符串
     * @throws Exception
     *             本方法不处理任何异常，所有异常全部抛出
     */
    public static String byteArr2HexStr(byte[] arrB) throws Exception {
        int iLen = arrB.length;
        // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
        StringBuffer sb = new StringBuffer(iLen * 2);
        for (int i = 0; i < iLen; i++) {
            int intTmp = arrB[i];
            // 把负数转换为正数
            while (intTmp < 0) {
                intTmp = intTmp + 256;
            }
            // 小于0F的数需要在前面补0
            if (intTmp < 16) {
                sb.append("0");
            }
            sb.append(Integer.toString(intTmp, 16));
        }
        return sb.toString();
    }

    /**
     * 将表示16进制值的字符串转换为byte数组， 和public static String byteArr2HexStr(byte[] arrB)
     * 互为可逆的转换过程
     *
     * @param strIn
     *            需要转换的字符串
     * @return 转换后的byte数组
     * @throws Exception
     *             本方法不处理任何异常，所有异常全部抛出
     * @author <a href="mailto:leo841001@163.com">LiGuoQing</a>
     */
    public static byte[] hexStr2ByteArr(String strIn) throws Exception {
        byte[] arrB = strIn.getBytes("UTF-8");
        int iLen = arrB.length;

        // 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
        byte[] arrOut = new byte[iLen / 2];
        for (int i = 0; i < iLen; i = i + 2) {
            String strTmp = new String(arrB, i, 2);
            arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
        }
        return arrOut;
    }

    /**
     * 默认构造方法，使用默认密钥
     *
     * @throws Exception
     */
    public DesUtils() throws Exception {
        this(strDefaultKey);
    }

    /**
     * 指定密钥构造方法
     *
     * @param strKey
     *            指定的密钥
     * @throws Exception
     */
    @SuppressWarnings("restriction")
    public DesUtils(String strKey) throws Exception {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
        Key key = getKey(strKey.getBytes("UTF-8"));

        encryptCipher = Cipher.getInstance("DES");
        encryptCipher.init(Cipher.ENCRYPT_MODE, key);

        decryptCipher = Cipher.getInstance("DES");
        decryptCipher.init(Cipher.DECRYPT_MODE, key);
    }

    /**
     * 加密字节数组
     *
     * @param arrB
     *            需加密的字节数组
     * @return 加密后的字节数组
     * @throws Exception
     */
    public byte[] encrypt(byte[] arrB) throws Exception {
        return encryptCipher.doFinal(arrB);
    }

    /**
     * 加密字符串
     *
     * @param strIn
     *            需加密的字符串
     * @return 加密后的字符串
     * @throws Exception
     */
    public String encrypt(String strIn) throws Exception {
        return byteArr2HexStr(encrypt(strIn.getBytes("UTF-8")));
    }

    /**
     * 解密字节数组
     *
     * @param arrB
     *            需解密的字节数组
     * @return 解密后的字节数组
     * @throws Exception
     */
    public byte[] decrypt(byte[] arrB) throws Exception {
        return decryptCipher.doFinal(arrB);
    }

    /**
     * 解密字符串
     *
     * @param strIn
     *            需解密的字符串
     * @return 解密后的字符串
     * @throws Exception
     */
    public String decrypt(String strIn) throws Exception {
        return new String(decrypt(hexStr2ByteArr(strIn)));
    }

    /**
     * 从指定字符串生成密钥，密钥所需的字节数组长度为8位 不足8位时后面补0，超出8位只取前8位
     *
     * @param arrBTmp
     *            构成该字符串的字节数组
     * @return 生成的密钥
     * @throws Exception
     */
    private Key getKey(byte[] arrBTmp) throws Exception {
        // 创建一个空的8位字节数组（默认值为0）
        byte[] arrB = new byte[8];

        // 将原始字节数组转换为8位
        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
            arrB[i] = arrBTmp[i];
        }

        // 生成密钥
        Key key = new javax.crypto.spec.SecretKeySpec(arrB, "DES");

        return key;
    }

    /**
     * 将JSON字符串加密并返回加密结果
     *
     * @param json
     * @param deskey
     * @return
     * @throws Exception
     */
    public static String endryptJson(String json, String deskey) throws Exception {
        DesUtils des = new DesUtils(deskey);
        return des.encrypt(json);
    }

    public static void main(String[] args) {
        try {
            String test = "加密测试加密测试";
            DesUtils des = new DesUtils("gty");// 自定义密钥
            System.out.println("加密前的字符：" + test);
            System.out.println("加密后的字符：" + des.encrypt(test));
            System.out.println("解密后的字符：" + des.decrypt(des.encrypt(test)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String post(String http,String content) throws Exception {
        try {
            String encryptStr = encrypt(content);

            URL url = new URL(http);
            HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setDoInput(true);
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setConnectTimeout(2000);
            httpUrlConnection.setReadTimeout(2000);
            httpUrlConnection.setRequestProperty("content-type", "application/json");
            httpUrlConnection.setRequestMethod("POST");

            OutputStream outputStream = httpUrlConnection.getOutputStream();
            outputStream.write(encryptStr.getBytes(Charset.forName("utf-8")));
            outputStream.flush();
            outputStream.close();

            InputStream inputStream = httpUrlConnection.getInputStream();
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);

            String readContent = new String(bytes, Charset.forName("utf-8"));
            inputStream.close();
            return decrypt(readContent);
        } catch (Exception e) {
            LOGGER.error("发送内容失败 \r\n {} \r\n {}",http,content);
            throw new IOException(e);
        }
    }

    public static String generateJsonStr(HaiYuConfig haiYuConfig,Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> data = new LinkedHashMap<>();
        //header参数容器
        Map<String, Object> header = new HashMap<>();
        //序列号,唯一序列号
        header.put("sequeceid", UUID.randomUUID().toString().replace("-", ""));
        //用户名和密码
        header.put("username", haiYuConfig.getUsername());
        header.put("password", haiYuConfig.getPassword());
        //约定的密钥
        header.put("key", haiYuConfig.getKey());
        data.put("header", header);
        //
        data.put("body", object);

        return mapper.writeValueAsString(data);
    }

    public static Boolean isReturnJsonSuccess(String post) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(post);
            String textValue = node.get("header").get("status").getTextValue();
            return textValue.equals("000");
        }catch (Exception e){
            LOGGER.error("解析海誉返回消息失败!",e);
            return null;
        }
    }
}
