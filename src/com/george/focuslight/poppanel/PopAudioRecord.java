package com.george.focuslight.poppanel;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;


import com.george.focuslight.R;
import com.george.focuslight.panel.IPanelAudioAction;
import com.george.focuslight.poppanel.InterphoneButton.InterphoneButtonListener;
import com.george.focuslight.tab.ITabPageAudioImp;
import com.george.focuslight.util.AudioToolsAMR;
import com.george.focuslight.util.AudioToolsBase;
import com.george.focuslight.util.AudioToolsBase.AudioRecordAction;
import com.xoozi.andromeda.uicontroller.PopPanelBase;
import com.xoozi.andromeda.utils.Utils;

public class PopAudioRecord extends PopPanelBase implements IPanelAudioAction, OnClickListener, AudioRecordAction, InterphoneButtonListener {
	
	private IPopPanelAction4Audio					_popPanelAction;//activity给弹出窗口的回调
	private ITabPageAudioImp						_iTabPageAudioImp;
	private AudioToolsBase							_audioTools;
	private Button									_btnSend;
	private Button									_btnCancel;
	private InterphoneButton						_btnInterphone;
	private ImageButton								_btnPlayback;
	private ImageView								_imgVoiceFlag;
	

	public PopAudioRecord(Context context, View rootPanel, IPopPanelAction4Audio popPanelAction,
			ITabPageAudioImp iTabPageAudioImp) {
		super(context, rootPanel, PopPanelBase.PopMode.AT_LOCATION, false);
		_iTabPageAudioImp = iTabPageAudioImp;
		_popPanelAction = popPanelAction;
		_initWork();
	}

	@Override
	public void onClick(View view) {
		
		switch(view.getId()){
		case R.id.btn_audio_send:
			_hide();
			_popPanelAction.onSendAudio(_audioTools.getRecord(), 
									_audioTools.getRecordData(),
									_audioTools.getRecordDuration());
			break;
			
		case R.id.btn_audio_cancel:
			_audioTools.shutDown();
			_iTabPageAudioImp.cancelPlayback();
			_hide();
			break;
			
			
		case R.id.btn_audio_playback:
			_btnPlayback.setBackgroundDrawable(_context.getResources().getDrawable(android.R.drawable.ic_media_pause));
			_iTabPageAudioImp.playback(this, _audioTools.getRecord());
			break;
		}
	}
	
	
	@Override
	public void onRecordCompleted() {
		Utils.amLog("record is ok");
		_btnSend.setEnabled(true);
		_btnPlayback.setEnabled(true);
		_btnCancel.setEnabled(true);
		_btnPlayback.setVisibility(View.VISIBLE);
		_imgVoiceFlag.setVisibility(View.INVISIBLE);
	}
	
	public	void	show(){
		_show();
		_btnPlayback.setVisibility(View.INVISIBLE);
		_btnSend.setEnabled(false);
	}
	
	protected void	_show(){
		
		//检查是否需要初始化
		if(null==_popMenu){
			_popMenu = _initPopView();
		}
		
		if(PopMode.DROP_DOWN == _popMode)
			_popMenu.showAsDropDown(_rootPanel);
		else
			_popMenu.showAtLocation(_rootPanel, Gravity.CENTER, 0,0);
	}
	
	@Override
	protected void _initWork() {
		_basePanel = _layoutInflater.inflate(R.layout.pop_audio_record, null);
		
		_imgVoiceFlag = (ImageView)_basePanel.findViewById(R.id.img_voice_flag);
		
		_btnSend 	= (Button)_basePanel.findViewById(R.id.btn_audio_send);
		_btnCancel	= (Button)_basePanel.findViewById(R.id.btn_audio_cancel);
		_btnInterphone	= (InterphoneButton)_basePanel.findViewById(R.id.btn_interphone);
		_btnPlayback= (ImageButton)_basePanel.findViewById(R.id.btn_audio_playback);
		
		_btnSend.setOnClickListener(this);
		_btnCancel.setOnClickListener(this);
		_btnInterphone.setInterphoneButtonListener(this);
		_btnPlayback.setOnClickListener(this);
		
		
		_btnInterphone.setBtnText(_context.getResources().getString(R.string.btn_interphone_normal), 
				_context.getResources().getString(R.string.btn_interphone_pressed));
		
		//_audioTools = new AudioToolsPCM(_context, this);
		_audioTools = new AudioToolsAMR(_context, this);
	}

	@Override
	public void onPressed() {
		
		_audioTools.startRecord();
		_btnPlayback.setEnabled(false);
		_btnSend.setEnabled(false);
		_btnCancel.setEnabled(false);
		_btnPlayback.setVisibility(View.INVISIBLE);
		_imgVoiceFlag.setVisibility(View.VISIBLE);
	}

	@Override
	public void onReleased() {
		_audioTools.stopRecord();
	}

	@Override
	public String getUrl() {
		return null;
	}

	@Override
	public String getType() {
		return null;
	}

	@Override
	public void playbackCompleted() {
		
		_btnPlayback.setBackgroundDrawable(_context.getResources().getDrawable(android.R.drawable.ic_media_play));
	}
	
	
	@Override
	public void playbackCanceled() {
		_btnPlayback.setBackgroundDrawable(_context.getResources().getDrawable(android.R.drawable.ic_media_play));
	}

	

	

}
