package com.donglu.carpark.ui.common;

import org.eclipse.swt.widgets.Shell;

public class AbstractApp implements App {

	@Override
	public void open() {
		

	}

	@Override
	public boolean isOpen() {
		
		return false;
	}

	@Override
	public void focus() {
		

	}
	@Override
	public Shell getShell() {
		return null;
	}

}
