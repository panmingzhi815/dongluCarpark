package com.donglu.carpark.ui.common;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

import java.util.ArrayList;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class Test {
	private DataBindingContext m_bindingContext;

	protected Shell shell;
	private Text text;

	private TestC testC;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {
				try {
					Test window = new Test();
					window.open();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	
	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		shell.setLayout(new GridLayout(1, false));
		
		testC = new TestC(shell, SWT.NONE);
		testC.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		text = new Text(shell, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Button btnTest = new Button(shell, SWT.NONE);
		btnTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				ArrayList<SingleCarparkCarpark> list = new ArrayList<>();
				SingleCarparkCarpark c=new SingleCarparkCarpark();
				c.setName("11111");
				list.add(c);
				testC.getModel().setList(list);;
			}
		});
		btnTest.setText("test");
		m_bindingContext = initDataBindings();

	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(text);
		IObservableValue textShellObserveValue = PojoProperties.value("text").observe(shell);
		bindingContext.bindValue(observeTextTextObserveWidget, textShellObserveValue, null, null);
		//
		return bindingContext;
	}
}
