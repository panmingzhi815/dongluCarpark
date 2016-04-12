package com.donglu.carpark.util;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkUserService;
import com.dongluhitec.card.domain.db.CardUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.common.base.Strings;
import jp.ne.so_net.ga2.no_ji.jcom.ReleaseManager;
import jp.ne.so_net.ga2.no_ji.jcom.excel8.ExcelApplication;
import jp.ne.so_net.ga2.no_ji.jcom.excel8.ExcelWorkbook;
import jp.ne.so_net.ga2.no_ji.jcom.excel8.ExcelWorkbooks;
import jp.ne.so_net.ga2.no_ji.jcom.excel8.ExcelWorksheet;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

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
			row.createCell(12).setCellValue(user.getRemark());
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
				String status = getCellStringValue(row, 10);
				if (status.equals("处理成功")) {
					continue;
				}
				// 导入用户
				CardUser cardUser = null;
				String name,plateNO,address,type,validTo,carparkNo,remark,telephone,parkingSpace,carparkSlotType;
				plateNO=getCellStringValue(row, 2);
				if (StrUtil.isEmpty(plateNO)) {
					throw new Exception("空的车牌");
				}
				String caparkCode=getCellStringValue(row, 10);
				String caparkName=getCellStringValue(row, 11);
				
				if (map.get(caparkCode) == null) {
					SingleCarparkCarpark findCarparkByCode = sp.getCarparkService().findCarparkByCode(caparkCode);
					if (StrUtil.isEmpty(findCarparkByCode)) {
						throw new Exception("停车场未找到");
					}else{
						map.put(caparkCode, findCarparkByCode);
					}
				}
				SingleCarparkUser findUserByPlateNo = carparkUserService.findUserByPlateNo(plateNO,map.get(caparkCode).getId());
				if (!StrUtil.isEmpty(findUserByPlateNo)) {
					throw new Exception("车牌已存在");
				}
				name = getCellStringValue(row, 1);
				address=getCellStringValue(row, 4);
				type=getCellStringValue(row, 5);
				validTo=getCellStringValue(row, 6);
				carparkNo=getCellStringValue(row, 7);
				telephone=getCellStringValue(row, 3);
				parkingSpace=getCellStringValue(row, 8);
				carparkSlotType=getCellStringValue(row, 9);
				
				remark=getCellStringValue(row, 12);
				SingleCarparkUser user=new SingleCarparkUser();
				user.setName(name);
				user.setPlateNo(plateNO);
				user.setAddress(address);
				user.setType(type);
				user.setValidTo(StrUtil.parse(validTo, USER_VALIDTO));
				user.setCarparkNo(carparkNo);
				user.setRemark(remark);
				user.setCreateDate(new Date());
				user.setCarpark(map.get(caparkCode));
				carparkUserService.saveUser(user);
				try {
					user.setCarparkSlotType(SingleCarparkUser.CarparkSlotTypeEnum.valueOf(carparkSlotType));
				} catch (Exception e) {
					throw new Exception("没有找到车位类型");
				}
				setCellStringvalue(row, 13, "处理成功", cellStyle);
			} catch (Exception e) {
				failNum++;
				e.printStackTrace();
				setCellStringvalue(row, 13, "保存失败" + e.getMessage(), cellStyle);
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
		HSSFRow row = sheet.createRow(currentRow);
//		row.createCell(0).setCellValue("编号");
		for (int i = 0; i < names.length; i++) {
			String string = names[i];
			HSSFCell createCell = row.createCell(i+1);
			row.createCell(i).setCellValue(string);
		}
		currentRow++;
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow(currentRow + i);
			Object o = list.get(i);
			
//			row.createCell(0).setCellValue(i+1);
			for (int j = 0; j < cloumns.length; j++) {
				String string = cloumns[j];
				Object fieldValueByName = CarparkUtils.getFieldValueByName(string, o);
//				row.createCell(j+1).setCellValue(fieldValueByName+"");
				row.createCell(j).setCellValue(fieldValueByName+"");
			}
		}
		FileOutputStream fileOut = new FileOutputStream(path);
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();
	}

}
