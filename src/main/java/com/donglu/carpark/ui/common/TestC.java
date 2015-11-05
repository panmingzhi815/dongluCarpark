package com.donglu.carpark.ui.common;

import org.eclipse.swt.widgets.Composite;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;

class TestC extends AbstractListView<SingleCarparkCarpark>{

		public TestC(Composite parent, int style) {
			super(parent, style,SingleCarparkCarpark.class,
					new String[]{"name"},
					new String[]{"停车场名称"},
					new int[]{150}, null);
			setTableTitle("停车场信息");
		}

		@Override
		protected void searchMore() {
			AbstractListView<SingleCarparkCarpark>.Model m = getModel();
			m.setCountSearch(100);
			m.setCountSearchAll(10000);
		}
	
}