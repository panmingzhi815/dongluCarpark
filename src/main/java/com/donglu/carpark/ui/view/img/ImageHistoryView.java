package com.donglu.carpark.ui.view.img;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.util.ImageUtils;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkImageHistory;
import com.google.common.util.concurrent.RateLimiter;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.custom.SashForm;


public class ImageHistoryView extends Composite implements View{
	private Presenter presenter;
	private Composite listComposite;
	private Text text_plateNO;
	private CDateTime dt_end;
	
	private RateLimiter rateLimiter = RateLimiter.create(2);
	private Label lbl_smallImage;
	private Label lbl_bigimage;

	public ImageHistoryView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(this, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		Group group = new Group(scrolledComposite, SWT.NONE);
		group.setText("查询");
		group.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridLayout gl_group = new GridLayout(9, false);
		gl_group.verticalSpacing = 10;
		gl_group.horizontalSpacing = 15;
		group.setLayout(gl_group);
		
		Label label = new Label(group, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("车牌");
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		text_plateNO = new Text(group, SWT.BORDER);
		text_plateNO.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text.widthHint = 71;
		text_plateNO.setLayoutData(gd_text);
		
		Label label_1 = new Label(group, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("类型");
		
		Combo combo = new Combo(group, SWT.READ_ONLY);
		combo.setItems(new String[] {"全部", "原始", "正确", "错误"});
		combo.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		combo.select(0);
		
		Label label_2 = new Label(group, SWT.NONE);
		label_2.setText("开始时间");
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		CDateTime dateTime = new CDateTime(group, CDT.BORDER);
		dateTime.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		dateTime.setPattern("yyyy-MM-dd HH:mm");
		dateTime.setSelection(new org.joda.time.DateTime().minusHours(1).toDate());
		
		Label label_6 = new Label(group, SWT.NONE);
		label_6.setText("结束时间");
		label_6.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		dt_end = new CDateTime(group, CDT.BORDER);
		dt_end.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		dt_end.setPattern("yyyy-MM-dd HH:mm\r\n");
		dt_end.setSelection(new Date());
		
		Button button = new Button(group, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!rateLimiter.tryAcquire()) {
					return;
				}
				dateTime.getSelection();
				String text = combo.getText();
				text=text.equals("全部")?null:text;
				getPresenter().search(text_plateNO.getText(),text, dateTime.getSelection(), dt_end.getSelection());
			}
		});
		button.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button.setText("查询");
		scrolledComposite.setContent(group);
		scrolledComposite.setMinSize(group.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		SashForm sashForm = new SashForm(composite, SWT.NONE);
		
		listComposite = new Composite(sashForm, SWT.NONE);
		listComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite_2 = new Composite(sashForm, SWT.NONE);
		GridLayout gl_composite_2 = new GridLayout(1, false);
		composite_2.setLayout(gl_composite_2);
		
		Label label_3 = new Label(composite_2, SWT.NONE);
		GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gridData.heightHint = 35;
		label_3.setLayoutData(gridData);
		
		Composite composite_1 = new Composite(composite_2, SWT.BORDER);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout gl_composite_1 = new GridLayout(1, false);
		gl_composite_1.marginWidth = 0;
		gl_composite_1.marginHeight = 0;
		composite_1.setLayout(gl_composite_1);
		
		lbl_smallImage = new Label(composite_1, SWT.NONE);
		lbl_smallImage.setText("小图片");
		GridData gd_lbl_smallImage = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd_lbl_smallImage.widthHint = 192;
		gd_lbl_smallImage.heightHint = 108;
		lbl_smallImage.setLayoutData(gd_lbl_smallImage);
		
		lbl_bigimage = new Label(composite_1, SWT.NONE);
		lbl_bigimage.setText("大图片");
		lbl_bigimage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Label lblNewLabel = new Label(composite_2, SWT.NONE);
		GridData gridData_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gridData_1.heightHint = 50;
		lblNewLabel.setLayoutData(gridData_1);
		sashForm.setWeights(new int[] {7, 3});
	}
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter=presenter;
	}

	@Override
	public ImageHistoryPresenter getPresenter() {
		return (ImageHistoryPresenter) presenter;
	}

	public Composite getListComposite() {
		return listComposite;
	}
	
	void initListener(){
		TableViewer tableViewer = getPresenter().getListPresenter().getView().getTableViewer();
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				SingleCarparkImageHistory ih = (SingleCarparkImageHistory) selection.getFirstElement();
				if (ih==null) {
					return;
				}
				System.out.println(ih);
				ImageUtils.setBackgroundImage(ImageUtils.getImageByte(ih.getSmallImage()), lbl_smallImage, lbl_smallImage.getDisplay());
				ImageUtils.setBackgroundImage(ImageUtils.getImageByte(ih.getBigImage()), lbl_bigimage, ih.getBigImage(), false);
			}
		});
		tableViewer.getTable().addKeyListener(new KeyAdapter(){
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode==32) {
					IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
					SingleCarparkImageHistory ih = (SingleCarparkImageHistory) selection.getFirstElement();
					if (ih==null) {
						return;
					}
					if (!ih.getType().equals("正确")) {
						getPresenter().getListPresenter().setTrue();
					}else{
						getPresenter().getListPresenter().setFalse();
					}
				}
			}
			
		});
		tableViewer.getTable().setToolTipText("可以按空格键进行正确错误切换");
	}
	
}
