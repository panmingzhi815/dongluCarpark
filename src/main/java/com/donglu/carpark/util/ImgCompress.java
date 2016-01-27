package com.donglu.carpark.util;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.image.codec.jpeg.*;
/**
 * 图片压缩处理
 * @author 崔素强
 */
public class ImgCompress {
	private static Logger log = LoggerFactory.getLogger(ImgCompress.class);
	private static String IMAGE_FILE_PATH = "D:\\img\\20160114100106347_粤BD021W_small.jpg";
	private static Image img;
	private static int width;
	private static int height;
	
	public static void main(String[] args) throws Exception {
		while(true){
			compress(IMAGE_FILE_PATH);
			Thread.sleep(1000);
		}
	}
	/**
	 * 构造函数
	 * @param fileName 文件路径
	 * @throws IOException
	 */
	public ImgCompress(String fileName) throws IOException {
		File file = new File(fileName);// 读入文件
		IMAGE_FILE_PATH=fileName;
		img = ImageIO.read(file);      // 构造Image对象
		width = img.getWidth(null);    // 得到源图宽
		height = img.getHeight(null);  // 得到源图长
	}
	/**
	 * 构造函数
	 * @param file 文件对象
	 * @throws IOException
	 */
	public ImgCompress(File file) throws IOException {
		IMAGE_FILE_PATH=file.getPath();
		img = ImageIO.read(file);      // 构造Image对象
		width = img.getWidth(null);    // 得到源图宽
		height = img.getHeight(null);  // 得到源图长
	}
	/**
	 * 将文件压缩，不改变其大小
	 * @param fileName
	 */
	public static void compress(String fileName){
		try {
			log.info("准备压缩图片：{}",fileName);
			ImgCompress imgCompress = new ImgCompress(fileName);
			imgCompress.resizeFix(imgCompress.getWidth(), imgCompress.getHeight());
			log.info("压缩图片：[{}]成功",fileName);
		} catch (IOException e) {
			log.info("压缩图片：["+fileName+"]失败",e);
		}
	}
	
	/**
	 * 按照宽度还是高度进行压缩
	 * @param w int 最大宽度
	 * @param h int 最大高度
	 */
	public void resizeFix(int w, int h) throws IOException {
		if (width / height > w / h) {
			resizeByWidth(w);
		} else {
			resizeByHeight(h);
		}
	}
	/**
	 * 以宽度为基准，等比例放缩图片
	 * @param w int 新宽度
	 */
	public void resizeByWidth(int w) throws IOException {
		int h = height * w / width;
		resize(w, h);
	}
	/**
	 * 以高度为基准，等比例缩放图片
	 * @param h int 新高度
	 */
	public void resizeByHeight(int h) throws IOException {
		int w = width * h / height;
		resize(w, h);
	}
	/**
	 * 强制压缩/放大图片到固定的大小
	 * @param w int 新宽度
	 * @param h int 新高度
	 */
	@SuppressWarnings("restriction")
	public void resize(int w, int h) throws IOException {
		// SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 优先级比速度高 生成的图片质量比较好 但速度慢
		BufferedImage image = new BufferedImage(w, h,BufferedImage.SCALE_SMOOTH ); 
		image.getGraphics().drawImage(img, 0, 0, w, h, null); // 绘制缩小后的图
		File destFile = new File(IMAGE_FILE_PATH);
		FileOutputStream out = new FileOutputStream(destFile); // 输出到文件流
		// 可以正常实现bmp、png、gif转jpg
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
		encoder.encode(image); // JPEG编码
		out.close();
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
}