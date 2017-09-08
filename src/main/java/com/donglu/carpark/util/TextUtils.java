package com.donglu.carpark.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.dongluhitec.card.common.ui.uitl.JFaceUtil;

public class TextUtils {
	static Map<String, String> map=new HashMap<>();
	/**
	 * 设置文本框车牌提示
	 * @param text
	 */
	public static void createPlateNOAutoCompleteField(Text text){
		init();
		TextContentAdapter ad = new TextContentAdapter(){
			@Override
			public void setControlContents(Control control, String s, int cursorPosition) {
				s=map.get(s);
				if ((text.getStyle()&SWT.MULTI)>0) {
					super.setControlContents(control, s, cursorPosition);
					return ;
				}
				Point selection = text.getSelection();
				String string = text.getText();
				string=string.replaceFirst(string.substring(0,selection.x), s);
				text.setText(string);
				text.setSelection(selection);
			}
			@Override
			public String getControlContents(Control control) {
				String controlContents = super.getControlContents(control);
				if ((text.getStyle()&SWT.MULTI)>0) {
					return controlContents;
				}
				controlContents=controlContents.substring(0, text.getSelection().x);
				return controlContents;
			}
    	};
    	SimpleContentProposalProvider proposalProvider = new SimpleContentProposalProvider(map.keySet().toArray(new String[map.keySet().size()]));
		proposalProvider.setFiltering(true);
		ContentProposalAdapter adapter = new ContentProposalAdapter(text, ad,
				proposalProvider, null, null);
		adapter.setPropagateKeys(true);
		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
		adapter.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				 IContentProposal proposal = (IContentProposal) element;
               return map.get(proposal.getContent());
			}
		});
	}
	private static void init() {
		map.put("yue", "粤");
		map.put("ji", "冀");
		map.put("yu", "豫");
		map.put("yun", "云");
		map.put("liao", "辽");
		map.put("hei", "黑");
		map.put("xiang", "湘");
		map.put("wan", "皖");
		map.put("lu", "鲁");
		map.put("xin", "新");
		map.put("su", "苏");
		map.put("zhe", "浙");
		map.put("gan", "赣");
		map.put("e", "鄂");
		map.put("gui", "桂");
		map.put("gan1", "甘");
		map.put("jin", "晋");
		map.put("meng", "蒙");
		map.put("shan", "陕");
		map.put("ji", "吉");
		map.put("min", "闽");
		map.put("gui1", "贵");
		map.put("chuan", "川");
		map.put("qing", "青");
		map.put("zang", "藏");
		map.put("qiong", "琼");
		map.put("ning", "宁");
		map.put("yu", "渝");
		map.put("jing", "京");
		map.put("jin2", "津");
		map.put("hu", "沪");
		
	}
	
	
	static Map<Text,List<Map<String, Rectangle>>> mapTextIco=new HashMap<>();
	/**
	 * 在文本框右侧添加图标、事件
	 * @param right 
	 */
	public static Text setTextEditIco(Text txt,String img,String title,Cursor cursor,int right, MouseListener mouseClick) {
		if (txt==null||img==null||cursor==null||mouseClick==null) {
			return txt;
		}
		Image image = JFaceUtil.getImage(img);
		if (image==null) {
//			log.error("设置文本框事件：{} 时，没有找到文本框图片：{}",title,img);
			return txt;
		}
		ImageData imageData = image.getImageData();
		Rectangle rectangle = new Rectangle(-1, 0, imageData.width, imageData.height);
//		System.out.println(title+"=="+rectangle);
		txt.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				GC gc = e.gc;
				if (rectangle.x<0) {
					Rectangle bounds = txt.getBounds();
					ImageData imageData = image.getImageData();
					int width = imageData.width;
					rectangle.x = bounds.width - width - right;
//					System.out.println(rectangle);
					synchronized (mapTextIco) {
						List<Map<String, Rectangle>> list = mapTextIco.getOrDefault(txt, new ArrayList<>());
//						System.out.println(list.size());
						boolean isHave = false;
						Map<String, Rectangle> m = null;
						for (Map<String, Rectangle> map : list) {
							if (isHave = map.keySet().contains(title)) {
								m = map;
								break;
							}
						}
						if (!isHave) {
							m = new HashMap<>();
							m.put(title, rectangle);
							list.add(m);
						} else {
							m.put(title, rectangle);
						}
						mapTextIco.put(txt, list);
					}
					
				}
				gc.drawImage(image, rectangle.x, 0);
			}
		});
		MouseMoveListener listener= (MouseMoveListener) txt.getData(MouseMoveListener.class.getName());
		if(listener==null){
//			System.out.println("添加鼠标移动监听");
			listener = new MouseMoveListener() {
				@Override
				public void mouseMove(MouseEvent e) {
					boolean isInIco=false;
					String t="";
					for (Map<String, Rectangle> map : mapTextIco.getOrDefault(txt, new ArrayList<>())) {
						for (String string : map.keySet()) {
							Rectangle r = map.get(string);
    						if(r.contains(e.x, e.y)){
    							isInIco=true;
//    							System.out.println(string+"==="+r);
    							t=string;
    							break;
    						}
						}
						
					}
//					System.out.println(isInIco+"=="+t);
					if (isInIco) {
						txt.setCursor(cursor);
						txt.setToolTipText(t);
					}else{
						txt.setCursor(null);
						txt.setToolTipText(null);
					}
				}
			};
			txt.setData(MouseMoveListener.class.getName(),listener);
			txt.addMouseMoveListener(listener);
		}
		txt.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				txt.redraw();
				if (!rectangle.contains(e.x, e.y)) {
					return;
				}
				mouseClick.mouseDown(e);
			}
			@Override
			public void mouseUp(MouseEvent e) {
				if (!rectangle.contains(e.x, e.y)) {
					return;
				}
				mouseClick.mouseUp(e);
			}
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if (!rectangle.contains(e.x, e.y)) {
					return;
				}
				mouseClick.mouseDoubleClick(e);
			}
		});
		return txt;
	}
	
	
}
