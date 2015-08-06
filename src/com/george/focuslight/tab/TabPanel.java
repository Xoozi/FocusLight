package com.george.focuslight.tab;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.ScrollView;

import com.george.focuslight.IActivityAction;
import com.george.focuslight.R;
import com.xoozi.andromeda.utils.Utils;

public class TabPanel implements ITabPanelImp {
	
	private	static final int	TAB_ID_SHARE = R.id.tab_button_share;
	private static final int	TAB_ID_AUDIO = R.id.tab_button_audio;
	
	private IActivityAction _iActivityAction;
	private Context 		_context;
	private	int				_currentFocusPage;
	private TabPageShare	_tabPageShare;
	private TabPageAudio	_tabPageAudio;
	
	
	private TabItem			_tabItemShare;
	private TabItem			_tabItemAudio;
	
	public	TabPanel(View basePanel, Context context, IActivityAction iActivityAction){
		
		_context	= context;
		
		_iActivityAction = iActivityAction;
		
		_tabPageShare = new TabPageShare((ScrollView)basePanel.findViewById(R.id.tabpanel_share), _context, this);
		_tabPageAudio = new TabPageAudio((ScrollView)basePanel.findViewById(R.id.scroll_audio), _context, this,
				basePanel.findViewById(R.id.tabpanel_audio));
		
		_tabItemShare	= new TabItem(this,
				basePanel.findViewById(R.id.tab_button_share), 
				(TextView)basePanel.findViewById(R.id.tab_button_badge_share),
				basePanel.findViewById(R.id.field_focus_share));
		
		_tabItemAudio	= new TabItem(this,
				basePanel.findViewById(R.id.tab_button_audio), 
				(TextView)basePanel.findViewById(R.id.tab_button_badge_audio),
				basePanel.findViewById(R.id.field_focus_audio));
		
		onSelectTab(TAB_ID_SHARE);
	}
	
	@Override
	public void onAudioCommin() {
		_tabItemAudio.showHello();
	}
	

	@Override
	public void onSelectTab(int id) {
		_currentFocusPage = id;
		
		if(TAB_ID_SHARE == id){
			_tabPageShare.show();
			_tabPageAudio.hide();
			_tabItemShare.setFocus(true);
			_tabItemAudio.setFocus(false);
		}else if(TAB_ID_AUDIO == id){
			_tabPageShare.hide();
			_tabPageAudio.show();
			_tabItemShare.setFocus(false);
			_tabItemAudio.setFocus(true);
			_tabItemAudio.hideHello();
		}
	}


	@Override
	public void onViewPhoto(int requestCode) {
		_iActivityAction.onViewPhoto(requestCode);
	}
	
	public	void	onAddObject(){
		switch(_currentFocusPage){
		case TAB_ID_SHARE:
			//_tabPageShare.onAddObject();
			break;
			
		case TAB_ID_AUDIO:
			_tabPageAudio.onAddObject();
			break;
		}
	}
	
	
	public	void	onPhotoResultReturn(int requestCode, int resultCode, Intent data){
		_tabPageShare.onPhotoResultReturn(requestCode, resultCode, data);
	}
	
	public	void	getUserInfoWhenInit(){
		_tabPageShare.getUserInfoWhenInit();
	}
	

	public void onPause() {
		Utils.amLog("onPause stop query pc voices");
		_tabPageAudio.stopQueryPCVoices();
	}

	public void onResume() {
		Utils.amLog("onResume start query pc voices");
		_tabPageAudio.startQueryPCVoices();
	}
	
	public void	jumpToAudio(){
		onSelectTab(TAB_ID_AUDIO);
	}


	

}
