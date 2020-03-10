package com.donglu.carpark.ui.view;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;

import java.util.Arrays;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.task.CarInTask;
import com.donglu.carpark.ui.view.message.MessageUtil;
import com.donglu.carpark.util.ConstUtil;
import com.donglu.carpark.util.ImageUtils;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.CarCheckHistory;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceVoiceTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;

import lombok.extern.slf4j.Slf4j;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FillLayout;

@Slf4j
public class CarInCheckApp {

	protected Shell shell;
	private Text txt_plate;
	private Text txt_time;
	private CarInTask task;
	private CarparkDatabaseServiceProvider sp;
	private CarCheckHistory cc;
	private CarparkMainModel model;
	private CLabel lbl_img;
	private Label lbl_msg;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			CarInCheckApp window = new CarInCheckApp();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public CarInCheckApp() {
		
	}
	public CarInCheckApp(CarInTask task,CarparkDatabaseServiceProvider sp,CarparkMainModel model) {
		this.task = task;
		this.sp = sp;
		this.model = model;
	}
	/**
	 * Open the window.
	 */
	public void open() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				createContents();
				shell.open();
				shell.layout();
			}
		});
	}

	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		shell = new Shell(Display.getDefault().getActiveShell(),SWT.ON_TOP|SWT.MIN|SWT.CLOSE|SWT.TITLE);
		shell.setSize(643, 520);
		shell.setText("入场确认");
		shell.setImage(JFaceUtil.getImage("donglu"));
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				try {
					cc.setStatus("取消确认");
					sp.getCarparkInOutService().saveEntity(cc);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		shell.setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite.setLayout(new GridLayout(6, false));
		
		Label label = new Label(composite, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label.setText("车牌号");
		
		txt_plate = new Text(composite, SWT.BORDER);
		GridData gd_txt_plate = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_txt_plate.widthHint = 110;
		txt_plate.setLayoutData(gd_txt_plate);
		txt_plate.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_1.setText("时间");
		
		txt_time = new Text(composite, SWT.BORDER);
		txt_time.setEditable(false);
		txt_time.setText("2019-12-12 12:22:22");
		GridData gd_txt_time = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_txt_time.widthHint = 160;
		txt_time.setLayoutData(gd_txt_time);
		txt_time.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		Button button = new Button(composite, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				carIn();
			}
		});
		button.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		button.setText(" 入 场 确 认 ");
		
		Button button_1 = new Button(composite, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openDoor();
			}
		});
		GridData gd_button_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_button_1.horizontalIndent = 10;
		button_1.setLayoutData(gd_button_1);
		button_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		button_1.setText(" 手 动 抬 杆 ");
		
		Composite composite_img = new Composite(shell, SWT.NONE);
		composite_img.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite_img.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		lbl_img = new CLabel(composite_img, SWT.NONE);
		lbl_img.setText("图片");
		
		lbl_msg = new Label(shell, SWT.NONE);
		lbl_msg.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lbl_msg.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		lbl_msg.setText("车辆入场确认");
		init();
	}

	protected void openDoor() {
		try {
			boolean showContentToDevice = task.getPresenter().showContentToDevice("手动开闸", task.getDevice(), task.getPresenter().getModel().getMapVoice().get(DeviceVoiceTypeEnum.临时车进场语音).getContent(), true);
			System.out.println(showContentToDevice);
			if (showContentToDevice) {
				task.getPresenter().saveOpenDoor(task.getDevice(), task.getBigImage(), txt_plate.getText(), true,new Date());
				cc.setStatus("手动抬杆");
				cc.setOperaName(ConstUtil.getUserName());
	    		sp.getCarparkInOutService().saveEntity(cc);
	    		task.getPresenter().refreshCarCheck();
	    		close();
			}
		} catch (Exception e) {
			log.error("入场手动抬杆时发生错误",e);
			MessageUtil.info("入场手动抬杆时发生错误", e.getMessage());
		}
	}
	private void init() {
		try {
			txt_plate.setText(task.getPlateNO());
			txt_time.setText(StrUtil.formatDateTime(task.getDate()));
			shell.setText((StrUtil.isEmpty(task.getPlateNO())?"无牌车":task.getPlateNO())+"入场确认");
			ImageUtils.setBackgroundImage(task.getBigImage(), lbl_img,task.getBigImgFileName());
			if ((cc=task.getCarCheck())==null) {
				cc = new CarCheckHistory();
				cc.setPlate(task.getPlateNO());
				cc.setTime(task.getDate());
				cc.setType("进场");
				cc.setSourcePlate(task.getPlateNO());
				cc.setBigImage(task.getBigImgFileName());
				cc.setSmallImage(task.getSmallImgFileName());
				cc.setDeviceIp(task.getDevice().getIp());
				cc.setDeviceName(task.getDevice().getName());
				cc.setOperaName(ConstUtil.getUserName());
				Long saveEntity = sp.getCarparkInOutService().saveEntity(cc);
				cc.setId(saveEntity);
				model.addCarChecks(Arrays.asList(cc));
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}

	protected void carIn() {
		try {
    		String plateNO=txt_plate.getText();
    		if (StrUtil.isEmpty(plateNO)||plateNO.length()<4) {
    			lbl_msg.setText("车牌格式不正确");
    			return;
			}
    		cc.setPlate(txt_plate.getText());
    		cc.setEditedPlate(!cc.getSourcePlate().equals(plateNO));
    		cc.setStatus("确认放行");
    		cc.setOperaName(ConstUtil.getUserName());
    		sp.getCarparkInOutService().saveEntity(cc);
    		log.info("车辆：{}入场确认",txt_plate.getText());
    		if (!cc.getSourcePlate().equals(plateNO)) {
    			task.setEditPlateNo(plateNO);
    			model.setInShowPlateNO(plateNO);
    			task.refreshUserAndHistory();
    		}
			task.checkUser(false);
			model.setInShowPlateNO(plateNO);
			close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void close() {
		try {
			if (!shell.isDisposed()) {
				shell.dispose();
			}
			task.getPresenter().refreshCarCheck();
		} catch (Exception e) {
			log.error("关闭确认时发生错误",e);
		}
	}
}
