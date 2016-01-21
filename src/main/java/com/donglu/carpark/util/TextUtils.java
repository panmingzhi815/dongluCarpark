package com.donglu.carpark.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class TextUtils {
	static Map<String, String> map=new HashMap<>();
	public static void createPlateNOAutoCompleteField(Text text){
		init();
		TextContentAdapter ad = new TextContentAdapter(){
			@Override
			public void setControlContents(Control control, String text, int cursorPosition) {
				text=map.get(text);
				super.setControlContents(control, text, cursorPosition);
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
	
	
}
