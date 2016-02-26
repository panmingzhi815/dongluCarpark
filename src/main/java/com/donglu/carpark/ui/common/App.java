package com.donglu.carpark.ui.common;

import org.eclipse.swt.widgets.Shell;

public interface App {
	public void open();
	public default void openAsyncExec(){open();};
	boolean isOpen();
	public void focus();
	public Shell getShell();
}
