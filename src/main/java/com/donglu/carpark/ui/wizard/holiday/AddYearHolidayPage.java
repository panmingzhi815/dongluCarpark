package com.donglu.carpark.ui.wizard.holiday;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.core.databinding.DataBindingContext;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.common.collect.Lists;

import org.eclipse.swt.widgets.Button;
import java.util.Date;

import org.eclipse.nebula.widgets.datechooser.DateChooser;
import org.joda.time.DateTime;

public class AddYearHolidayPage extends WizardPage {
	@SuppressWarnings("unused")
	private DataBindingContext m_bindingContext;

	private AddYearHolidayModel model;
	private Composite container;
	private ComboViewer comboViewer;
	private List<DateChooser> dc = Lists.newArrayList();

	public AddYearHolidayPage(AddYearHolidayModel model) {
		super("wizardPage");
		this.model = model;

		setImageDescriptor(JFaceUtil.getImageDescriptor("attendanceholidaygrup_72"));
		setMessage("请选择日期");
		setDescription("Wizard Page description");
		model.setSelect();
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		setControl(container);
		container.setLayout(new GridLayout(1, false));

		Composite composite2 = new Composite(container, SWT.NONE);
		composite2.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1));
		GridLayout gl_composite2 = new GridLayout(6, false);
		gl_composite2.horizontalSpacing = 9;
		composite2.setLayout(gl_composite2);

		Composite composite3 = new Composite(container, SWT.NONE);
		GridData gd_composite3 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_composite3.widthHint = 403;
		composite3.setLayoutData(gd_composite3);
		composite3.setLayout(new GridLayout(4, false));

		DateChooser dateChooser = new DateChooser(composite3, SWT.MULTI);

		dc.add(dateChooser);
		DateChooser dateChooser_1 = new DateChooser(composite3, SWT.MULTI);
		dc.add(dateChooser_1);
		DateChooser dateChooser_2 = new DateChooser(composite3, SWT.MULTI);
		dc.add(dateChooser_2);
		DateChooser dateChooser_3 = new DateChooser(composite3, SWT.MULTI);
		dc.add(dateChooser_3);
		DateChooser dateChooser_4 = new DateChooser(composite3, SWT.MULTI);
		dc.add(dateChooser_4);
		DateChooser dateChooser_5 = new DateChooser(composite3, SWT.MULTI);
		dc.add(dateChooser_5);
		DateChooser dateChooser_6 = new DateChooser(composite3, SWT.MULTI);
		dc.add(dateChooser_6);
		DateChooser dateChooser_7 = new DateChooser(composite3, SWT.MULTI);
		dc.add(dateChooser_7);
		DateChooser dateChooser_8 = new DateChooser(composite3, SWT.MULTI);
		dc.add(dateChooser_8);
		DateChooser dateChooser_9 = new DateChooser(composite3, SWT.MULTI);
		dc.add(dateChooser_9);
		DateChooser dateChooser_10 = new DateChooser(composite3, SWT.MULTI);
		dc.add(dateChooser_10);
		DateChooser dateChooser_11 = new DateChooser(composite3, SWT.MULTI);
		dc.add(dateChooser_11);
		int month = 1;
		for (DateChooser d : dc) {
			d.setNavigationEnabled(false);
			d.setFocusOnDate(new DateTime(model.getYear(), month, 1, 0, 0).toDate());
			d.setLocale(Locale.CHINESE);
			for (Date dd : model.getSelect()) {
				Calendar c = Calendar.getInstance();
				c.setTime(dd);
				int mm = c.get(Calendar.MONTH);
				if (mm == month - 1) {
					dc.get(month - 1).setSelectedDate(dd);
				}
				continue;
			}
			month++;
		}
		Label label_2 = new Label(composite2, SWT.NONE);
		label_2.setText("年");

		comboViewer = new ComboViewer(composite2, SWT.NONE);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider());
		comboViewer.setInput(getYears());
		combo.select(2);

		Button btnNewButton = new Button(composite2, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int year = Integer.parseInt(comboViewer.getCombo().getText());
				model.setYear(year);
				getWizard().loadHoliday(year);
				model.setSelect();
				for (int i = 0; i < dc.size(); i++) {
					dc.get(i).clearSelection();
					dc.get(i).setFocusOnDate(new DateTime(model.getYear(), i + 1, 1, 0, 0).toDate());
					for (Date d : model.getSelect()) {
						Calendar c = Calendar.getInstance();
						c.setTime(d);
						int month = c.get(Calendar.MONTH);
						if (i == month) {
							dc.get(i).setSelectedDate(d);
						}
						continue;
					}
				}
			}
		});
		btnNewButton.setText("确认");

		Button button = new Button(composite2, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int year = Integer.parseInt(comboViewer.getCombo().getText());
				for (int i = 0; i < dc.size(); i++) {
					dc.get(i).clearSelection();
					dc.get(i).setFocusOnDate(new DateTime(model.getYear(), i + 1, 1, 0, 0).toDate());
					for (Date d : StrUtil.getWeekendOfYear(year)) {
						Calendar c = Calendar.getInstance();
						c.setTime(d);
						int month = c.get(Calendar.MONTH);
						if (i == month) {
							dc.get(i).setSelectedDate(d);
						}
						continue;
					}
				}
			}
		});
		button.setText("全选周末");
		new Label(composite2, SWT.NONE);
		new Label(composite2, SWT.NONE);
		m_bindingContext = initDataBindings();
	}

	// 获取年
	private List<Integer> getYears() {
		List<Integer> years = new ArrayList<Integer>();

		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		for (int i = year - 2; i <= year + 8; i++) {
			years.add(i);
		}
		return years;
	}

	@Override
	public AddYearHolidayWizard getWizard() {
		return (AddYearHolidayWizard) super.getWizard();
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		return bindingContext;
	}

	public List<DateChooser> getDc() {
		return dc;
	}
}
