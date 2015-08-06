package com.george.focuslight.tab;

import android.content.Context;
import android.view.View;
import android.widget.ScrollView;

public class TabPageBase {
	protected ITabPanelImp	_tabPanelImp;
	protected ScrollView			_basePanel;
	protected Context			_context;
	
	public	TabPageBase(ScrollView basePanel, Context context, ITabPanelImp tabPanelImp){
		_basePanel 	= basePanel;
		_context	= context;
		_tabPanelImp= tabPanelImp;
	}
	
	public	void show(){
		_basePanel.setVisibility(View.VISIBLE);
	}
	
	public	void hide(){
		_basePanel.setVisibility(View.INVISIBLE);
	}

}
