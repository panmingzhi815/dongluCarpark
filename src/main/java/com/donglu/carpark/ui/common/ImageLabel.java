package com.donglu.carpark.ui.common;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ImageLabel extends Label {
	private Image mouseDownImage;
	private Image backgroundImage;
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public ImageLabel(Composite parent, int style) {
		super(parent, style);
		new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				setBackgroundImage(backgroundImage);
			}

			@Override
			public void mouseDown(MouseEvent e) {
				setBackgroundImage(mouseDownImage);
			}
		};
	}
	@Override
	public void dispose() {
		super.dispose();
	}
	@Override
	public void setBackgroundImage(Image image) {
		backgroundImage = image;
		super.setBackgroundImage(image);
	}
}
