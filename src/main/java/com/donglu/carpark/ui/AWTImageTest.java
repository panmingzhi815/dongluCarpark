package com.donglu.carpark.ui;

import java.awt.Frame;

import javax.swing.ImageIcon;


public class AWTImageTest {
	public static void main(String[] args) {
		Frame f=new Frame();
		f.setSize(500, 500);
		MyCanvas c=new MyCanvas();
		f.add(c);
		f.setVisible(true);
		c.setImage(new ImageIcon("D:\\git\\dongluCarpark\\work\\donglu.png").getImage());
	}
}
