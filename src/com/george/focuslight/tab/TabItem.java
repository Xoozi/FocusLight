package com.george.focuslight.tab;

import com.george.focuslight.R;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class TabItem implements OnClickListener{

	private ITabPanelImp	_tabPanelImp;
	private int				_tabItemId;
	private TextView		_textBadge;
	private View			_focusMarker;
	private ImageView		_imgHello;
	
	public	TabItem(ITabPanelImp tabPanelImp, View baseView, TextView textBadge, View focusMarker){
		baseView.setOnClickListener(this);
		_focusMarker 	= focusMarker;
		_tabItemId 		= baseView.getId();
		_tabPanelImp	= tabPanelImp;
		
		_imgHello = (ImageView) baseView.findViewById(R.id.tab_img_badge_audio);
	}

	@Override
	public void onClick(View arg0) {
		_tabPanelImp.onSelectTab(_tabItemId);
	}
	
	public	void	setFocus(boolean focus){
		if(focus){
			_focusMarker.setVisibility(View.VISIBLE);
		}else{
			_focusMarker.setVisibility(View.INVISIBLE);
		}
	}
	
	public 	void	setBadge(int count){
		
		String badge;
		
		if(count<=0){
			badge = "";
		}else{
			badge = String.valueOf(count);
		}
		
		_textBadge.setText(badge);
	}
	
	public	void	showHello(){
		_imgHello.setVisibility(View.VISIBLE);
	}
	
	public	void	hideHello(){
		_imgHello.setVisibility(View.INVISIBLE);
	}
}
