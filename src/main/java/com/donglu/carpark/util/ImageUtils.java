package com.donglu.carpark.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.Login;
import com.donglu.carpark.ui.common.ImageDialog;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.common.io.Files;

public class ImageUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageUtils.class);
	/**
	 * 获取保存到CLabel的图片
	 * @param smallImage
	 * @param cl
	 * @param shell
	 * @return
	 */
	public static Image getImage(final byte[] smallImage, CLabel cl, Shell shell) {
		if (smallImage == null || smallImage.length <= 0) {
			cl.setText("无图片");
			return null;
		}

		ByteArrayInputStream stream = null;
		try {
			stream = new ByteArrayInputStream(smallImage);
			Image img = new Image(shell.getDisplay(), stream);
			Rectangle rectangle = cl.getBounds();
			ImageData data = img.getImageData().scaledTo(rectangle.width, rectangle.height);
			ImageDescriptor createFromImageData = ImageDescriptor.createFromImageData(data);
			Image createImg = createFromImageData.createImage();
			img.dispose();
			img = null;
			cl.setText("");
			return createImg;
		} catch (Exception e) {
			LOGGER.error("图片转换错误",e);
			return null;
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 将图片数据直接显示至cLabel背景
	 * 每次显示图片后，将图片保存至clabel的引用中，下次再要显示时，先判断是否有引用，如果有引用，则先要销毁以前的引用，避免swt 的 handler 资源泄漏
	 * @param imageBytes 数据原始二进制数据
	 * @param cLabel 显示控件
	 * @param device 当前显示的窗体 display
	 */
	public static void setBackgroundImage(byte[] imageBytes, Control cLabel, Display device) {
		if (cLabel.getData("lastImage") != null){
			Image lastImage = (Image)cLabel.getData("lastImage");
			lastImage.dispose();
			cLabel.setBackgroundImage(null);
			cLabel.setData("lastImage",null);
			LOGGER.info("销毁图片成功！");
		}
		Object data1 = cLabel.getData("imageDisposeListener");
		if (data1==null) {
			LOGGER.info("设置图片时添加disponse监听");
			DisposeListener listener = new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent e) {
					if (cLabel.getData("lastImage") != null){
						Image lastImage = (Image)cLabel.getData("lastImage");
						lastImage.dispose();
						cLabel.setBackgroundImage(null);
						cLabel.setData("lastImage",null);
						LOGGER.info("label销毁监听器--销毁图片成功！");
						cLabel.removeDisposeListener(this);
					}
				}
			};
			cLabel.setData("imageDisposeListener", listener);
			cLabel.addDisposeListener(listener);
		}
		if(cLabel.getData("imageType")!=null&&cLabel.getData("imageType").equals("big")){
			setMouseDoubleClick(cLabel,!(cLabel.getData("imageType")!=null&&cLabel.getData("imageType").equals("big")));
		}
		if (imageBytes == null || imageBytes.length <= 0) {
			if (cLabel instanceof CLabel) {
				((CLabel)cLabel).setText("无图片");
			}
			return;
		}

		try (ByteArrayInputStream stream = new ByteArrayInputStream(imageBytes)) {
			Image img = new Image(device, stream);
			int width = cLabel.getBounds().width;
			int height = cLabel.getBounds().height;
			if (width<=0||height<=0) {
				return;
			}
			ImageData data = img.getImageData().scaledTo(width, height);
			Image createImg = ImageDescriptor.createFromImageData(data).createImage();
			img.dispose();
			if (cLabel instanceof CLabel) {
				((CLabel)cLabel).setText("");
			}
			cLabel.setBackgroundImage(createImg);
			cLabel.setData("lastImage",createImg);
		} catch (Exception e) {
			LOGGER.error("图片转换错误", e);
		}
	}
	
	private static void setMouseDoubleClick(Control label, boolean isClose) {
		System.out.println(label.getListeners(SWT.MouseDoubleClick).length);
		Object data = label.getData("labelMouseDoubleClick");
		if (data!=null) {
			return;
		}
		MouseAdapter listener=null;
		if (isClose) {
			listener = new MouseAdapter() {
				@Override
				public void mouseDoubleClick(MouseEvent e) {
					label.getShell().dispose();
				}
			};
		} else {
			listener = new MouseAdapter() {
				@Override
				public void mouseDoubleClick(MouseEvent e) {
					String lastImage = (String) label.getData("imgName");
					if (StrUtil.isEmpty(lastImage)) {
						return;
					}
					ImageDialog imageDialog = new ImageDialog(lastImage);
					imageDialog.open();
				}
			};
		}
		label.addMouseListener(listener);
		System.out.println(label.getListeners(SWT.MouseDoubleClick).length);
		label.setData("labelMouseDoubleClick",listener);
	}

	/**
	 * 将图片数据直接显示至Label背景
	 * 每次显示图片后，将图片保存至label的引用中，下次再要显示时，先判断是否有引用，如果有引用，则先要销毁以前的引用，避免swt 的 handler 资源泄漏
	 * @param imageBytes 数据原始二进制数据
	 * @param Label 显示控件
	 * @param device 当前显示的窗体 display
	 */
	public static void setBackgroundImage(byte[] imageBytes, Label cLabel, Display device) {
		if (cLabel.getData("lastImage") != null){
			Image lastImage = (Image)cLabel.getData("lastImage");
			lastImage.dispose();
			cLabel.setBackgroundImage(null);
			cLabel.setData("lastImage",null);
			LOGGER.info("销毁图片成功！");
		}

		if (imageBytes == null || imageBytes.length <= 0) {
			cLabel.setText("无图片");
			return;
		}

		try (ByteArrayInputStream stream = new ByteArrayInputStream(imageBytes)) {
			Image img = new Image(device, stream);
			int width = cLabel.getBounds().width;
			int height = cLabel.getBounds().height;
			if (width<=0||height<=0) {
				return;
			}
			ImageData data = img.getImageData().scaledTo(width, height);
			Image createImg = ImageDescriptor.createFromImageData(data).createImage();
			img.dispose();
			cLabel.setText("");
			cLabel.setBackgroundImage(createImg);
			cLabel.setData("lastImage",createImg);
		} catch (Exception e) {
			LOGGER.error("图片转换错误", e);
		}
	}
	
	
	/**
	 * 获得保存到Label的图片
	 * @param image
	 * @param lbl
	 * @param shell
	 * @return
	 */
	public static Image getImage(byte[] image, Label lbl,Shell shell) {
		if (image==null||image.length<=0) {
			return null;
		}
		ByteArrayInputStream stream = null;
		try {
			stream = new ByteArrayInputStream(image);
			Image newImg = new Image(shell.getDisplay(), stream);
			Rectangle rectangle = lbl.getBounds();
			System.out.println(rectangle);
			if (rectangle.width==0) {
				return newImg;
			}
			ImageData data = newImg.getImageData().scaledTo(rectangle.width, rectangle.height);
			ImageDescriptor createFromImageData = ImageDescriptor.createFromImageData(data);
			Image createImg = createFromImageData.createImage();
			newImg.dispose();
			newImg = null;
			return createImg;
		} catch (Exception e) {
			LOGGER.error("获取图片失败",e);
			return null;
		}finally{
			if (stream!=null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 获取图片字节
	 * @param filePath
	 * @param img
	 * @return
	 */
	public static byte[] getImageByte(String img) {
		
		if (StrUtil.isEmpty(img)) {
			return null;
		}
		String filePath=(String) CarparkFileUtils.readObject(ConstUtil.CLIENT_IMAGE_SAVE_FILE_PATH);
		try {
			byte[] image;
			String pathname = (filePath==null?System.getProperty("user.dir"):filePath)+"/img/"+img;
			File file=new File(pathname);
			LOGGER.info("获取图片{}",pathname);
			if (file.exists()) {
				LOGGER.info("在本地找到图片，获取图片{}",file);
				image=Files.toByteArray(file);
			}else{
				String substring = img.substring(img.lastIndexOf("/")+1);
				LOGGER.info("本地未找到图片，准备到服务器获取图片{}",pathname);
				CarparkDatabaseServiceProvider sp=Login.injector.getInstance(CarparkDatabaseServiceProvider.class);
				image = sp.getImageService().getImage(substring);
				LOGGER.info("从获取图片成功");
			}
			return image;
		} catch (Exception e) {
			LOGGER.info("根据图片名称获得图片失败");
			return null;
		}

	}
	public static byte[] getImageBytes(Image image){
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			ImageLoader imageLoader = new ImageLoader();
			imageLoader.data = new ImageData[] { image.getImageData() };
			imageLoader.save(baos, image.type);
			
			byte[] imageByteArray = baos.toByteArray();
			try {
				baos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return imageByteArray;
		} catch (Exception e) {
			LOGGER.info("更据图片获取图片字节失败");
			return null;
		}
	}
	
	public static void setBackgroundImage(byte[] bigImage, CLabel label, String imageName) {
		label.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				label.setData("imgName", imageName);
				Listener[] listeners = label.getListeners(SWT.MouseDoubleClick);
				if (listeners.length==0) {
					MouseAdapter listener = new MouseAdapter() {
						@Override
						public void mouseDoubleClick(MouseEvent e) {
							String lastImage = (String)label.getData("imgName");
							if (StrUtil.isEmpty(lastImage)) {
								return;
							}
							ImageDialog imageDialog = new ImageDialog(lastImage);
							imageDialog.open();
						}
					};
					label.addMouseListener(listener);
					label.setData("labelMouseDoubleClick",listener);
				}
				setBackgroundImage(bigImage, label, label.getDisplay());
			}
		});
	}
	public static void setBackgroundImage(byte[] bigImage, Control label, String imageName, boolean isClose) {
		label.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				setMouseDoubleClick(label, isClose);
				label.setData("imgName", imageName);
				setBackgroundImage(bigImage, label, label.getDisplay());
			}
		});
	}
	
	public static void bindImageWithBig(Object model,String propertyName,String imageName,Control label){
		IObservableValue iObservableValue = BeanProperties.value(propertyName).observe(model);
		iObservableValue.addValueChangeListener(new IValueChangeListener() {
			@Override
			public void handleValueChange(ValueChangeEvent event) {
				byte[] image=(byte[]) iObservableValue.getValue();
				setBackgroundImage(image, label, label.getDisplay());
			}
		});
		if (!StrUtil.isEmpty(imageName)) {
			IObservableValue iObservableNameValue = BeanProperties.value(imageName).observe(model);
			iObservableNameValue.addValueChangeListener(new IValueChangeListener() {
				@Override
				public void handleValueChange(ValueChangeEvent event) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							Object value = iObservableNameValue.getValue();
							label.setData("imgName", value);
						}
					});
				}
			});
		}
	}
	public static void bindImageWithBig(Object model,String propertyName,Control label){
		bindImageWithBig(model, propertyName, null, label);
	}
	
	/**
	 * 设置图片名称
	 * @param lbl_outSmallImg
	 * @param string
	 */
	public static void setBackgroundImageName(CLabel lbl_outSmallImg, String string) {
		lbl_outSmallImg.setData("imgName", string);
	}
}
