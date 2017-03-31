package com.donglu.carpark.ui;

import java.awt.Canvas;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;


public class MyCanvas extends Canvas {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Image image;
	
	public MyCanvas(Image image) {
		this.image = image;
	}
	public MyCanvas() {
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Rectangle bounds = getBounds();
		if (image!=null) {
			g.drawImage(image, 0, 0,bounds.width,bounds.height,this);
		}
		g.setFont(new Font("微软雅黑", Font.BOLD, 30));
		FontMetrics metrics = g.getFontMetrics();
		String str = "视频已关闭";
		g.drawString(str, bounds.width/2-metrics.stringWidth(str)/2, metrics.getHeight());
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
		repaint();
	}
}
