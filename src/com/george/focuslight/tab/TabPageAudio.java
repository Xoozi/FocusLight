package com.george.focuslight.tab;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import com.george.focuslight.R;
import com.george.focuslight.ajaxobject.ActionData;
import com.george.focuslight.ajaxobject.NormalResult;
import com.george.focuslight.ajaxobject.ResultCode;
import com.george.focuslight.ajaxobject.VoiceData;
import com.george.focuslight.api.GetPCVoiceListAPI;
import com.george.focuslight.api.GetUserInfoAPI;
import com.george.focuslight.data.AppProfile;
import com.george.focuslight.panel.IPanelAudioAction;
import com.george.focuslight.panel.PanelAudioFrom;
import com.george.focuslight.panel.PanelAudioTo;
import com.george.focuslight.poppanel.IPopPanelAction4Audio;
import com.george.focuslight.poppanel.InterphoneButton;
import com.george.focuslight.poppanel.InterphoneButton.InterphoneButtonListener;
import com.george.focuslight.poppanel.PopAudioRecord;
import com.george.focuslight.util.AudioToolsAMR;
import com.george.focuslight.util.AudioToolsBase;
import com.george.focuslight.util.AudioToolsBase.AudioRecordAction;
import com.xoozi.andromeda.net.ajax.AjaxException;
import com.xoozi.andromeda.net.ajax.AjaxRequestListener;
import com.xoozi.andromeda.net.ajax.HttpManager.Responses;
import com.xoozi.andromeda.utils.JSONAndObject;
import com.xoozi.andromeda.utils.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;


public class TabPageAudio extends TabPageBase implements AudioRecordAction, InterphoneButtonListener, IPopPanelAction4Audio, ITabPageAudioImp, OnCompletionListener{
	
	
	private static final long  PERIOD_NORMAL = 60 * 1000;
	private static final long  PERIOD_ACTIVE = 10 * 1000;

	private LinearLayout	_audioList;
	private List<IPanelAudioAction>	_audioPanels = new ArrayList<IPanelAudioAction>();
	
	private GetPCVoiceListListener	_pcVoiceListListener = new GetPCVoiceListListener();
	
	private Timer 					_timer;
	
	private Date					_lastFromDate;
	
	private SimpleDateFormat 		_sdf 	= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private MediaPlayer				_audioPlayer = new MediaPlayer();
	
	private IPanelAudioAction		_currentPlayback;
	
	private View					_parentPanel;
	
	private InterphoneButton		_btnInterphone;
	
	private ImageView				_imgVoiceFlag;
	
	private AudioToolsBase			_audioTools;
	
	private boolean					_active;
	
	@SuppressLint("HandlerLeak")
	private Handler 	_handlerGetPCVoice	= new Handler()
	{
		public void handleMessage(Message msg) 
		{
			switch (msg.what) 
			{
			case 1:
				_getPCVoiceList();
				break;
				
			case 2:
				_scrollToBottom();
				break;
			}
		};
	};

	public TabPageAudio(ScrollView basePanel, Context context,
			ITabPanelImp tabPanelImp, View parentPanel) {
		super(basePanel, context, tabPanelImp);
		_parentPanel = parentPanel;
		_initWork();
	}
	
	/**
	 * 监听对讲按钮
	 */
	@Override
	public void onPressed() {
		_imgVoiceFlag.setVisibility(View.VISIBLE);
		_audioTools.startRecord();
	}

	@Override
	public void onReleased() {
		_audioTools.stopRecord();
		onSendAudio(_audioTools.getRecord(), 
				_audioTools.getRecordData(),
				_audioTools.getRecordDuration());
	}
	/**
	 * 监听对讲按钮END
	 */
	
	/**
	 * 监听录制结束
	 */
	@Override
	public void onRecordCompleted() {
		_imgVoiceFlag.setVisibility(View.INVISIBLE);
	}
	
	
	@Override
	public void show() {
		_parentPanel.setVisibility(View.VISIBLE);
		_active = true;
		_changeQueryPCVoices();
	}

	@Override
	public void hide() {
		_parentPanel.setVisibility(View.INVISIBLE);
		_active = false;
		_changeQueryPCVoices();
	}
	
	@Override
	public void cancelPlayback(){
		if(_audioPlayer.isPlaying()){
			_audioPlayer.stop();
			
			if(null!=_currentPlayback)
				_currentPlayback.playbackCanceled();
		}
	}
	
	@Override
	public void playback(IPanelAudioAction playingPanel,File audioFile) {
		if(_audioPlayer.isPlaying()){
			_audioPlayer.stop();
			
			if(null!=_currentPlayback){
				_currentPlayback.playbackCompleted();
				
				if(_currentPlayback==playingPanel)
					return;
			}
		}
		
		
		try {
			_audioPlayer.reset();
			_audioPlayer.setDataSource(audioFile.getAbsolutePath());
			_audioPlayer.setOnCompletionListener(this);
			_audioPlayer.prepare();
			_audioPlayer.start();
			
			_currentPlayback = playingPanel;
			
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onCompletion(MediaPlayer mp) {
		_currentPlayback.playbackCompleted();
	}

	@Override
	public void onSendAudio(File audioFile, Date time, long duration) {
		Utils.amLog("audioFile:"+audioFile.getAbsolutePath());
		
		IPanelAudioAction audioPanel = new PanelAudioTo(_context, _audioList,
				this,
				audioFile, time, duration);
		_audioPanels.add(audioPanel);
		
		_autoScroll();
	}
	
	private void	_initWork(){
		
		_audioList = (LinearLayout) _basePanel.findViewById(R.id.list_audio);
		
		_btnInterphone	= (InterphoneButton)_parentPanel.findViewById(R.id.btn_interphone);
		
		_btnInterphone.setInterphoneButtonListener(this);
		
		_btnInterphone.setBtnText(_context.getResources().getString(R.string.btn_interphone_normal), 
				_context.getResources().getString(R.string.btn_interphone_pressed));
		
		_imgVoiceFlag = (ImageView)_parentPanel.findViewById(R.id.img_voice_flag);
		
		_audioTools = new AudioToolsAMR(_context, this);
		
		
		AudioManager audioManager = (AudioManager)_context.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setSpeakerphoneOn(true); 
	}
	
	private void	_getPCVoiceList(){
		GetPCVoiceListAPI.getPCVoiceList(AppProfile.getActiveUser(_context), _pcVoiceListListener);
	}
	
	public	void	onAddObject(){
		
	}
	
	private void	_changeQueryPCVoices(){
		if(null!=_timer){
			_timer.cancel();
			
			_timer = new Timer();
			long period;
			if(_active){
				period = PERIOD_ACTIVE;
			}else{
				period = PERIOD_NORMAL;
			}
			
			Utils.amLog("change period to:"+period);
			_timer.scheduleAtFixedRate(new GetPCVoiceTimer(), 500, period);
		}
	}
	
	public	void	startQueryPCVoices(){
		_timer = new Timer();
		long period;
		if(_active){
			period = PERIOD_ACTIVE;
		}else{
			period = PERIOD_NORMAL;
		}
		_timer.scheduleAtFixedRate(new GetPCVoiceTimer(), 500, period);
	}
	
	public	void	stopQueryPCVoices(){
		if(null!=_timer){
			_timer.cancel();
			_timer = null;
		}
	}
	
	private void 	_autoScroll(){
		AutoScrollTimer schedule =  new AutoScrollTimer();
		Timer timer = new Timer();
		timer.schedule(schedule, 100);
	}
	
	private void	_scrollToBottom(){
		int offset = _basePanel.getHeight();  
		if (offset < 0) {  
		offset = 0;  
		}  
		  
		_basePanel.scrollTo(0, offset);  
	}
	
	
	/**
	 *
	 * @author xoozi
	 *
	 */
	private class GetPCVoiceTimer extends TimerTask{
		@Override
		public void run() 
		{
			Message message = new Message();
			message.what = 1;
			_handlerGetPCVoice.sendMessage(message);
		}   	
   }
	
	private class AutoScrollTimer extends TimerTask{
		public void run() 
		{
			Message message = new Message();
			message.what = 2;
			_handlerGetPCVoice.sendMessage(message);
		}  
	}
	
	
	private class GetPCVoiceListListener implements AjaxRequestListener{
		

		@Override
		public void onComplete(Responses responses) {
			
			
			NormalResult result = new NormalResult();
			JSONAndObject.convertSingleObject(result, responses.response);
			
			if(ResultCode.RESULT_SUC == result.result){
				
				List<VoiceData> voices = new ArrayList<VoiceData>();
				voices = JSONAndObject.convert(VoiceData.class, responses.response,GetPCVoiceListAPI.VOICE_KEY);
				if(null!=voices){
					
					if(!_active)
						_tabPanelImp.onAudioCommin();
					
					for(VoiceData voice:voices){
						Date date;
						try {
							date = _sdf.parse(voice.create_time);
							
							if(null==_lastFromDate || date.after(_lastFromDate)){
								Utils.amLog("get voice type:"+voice.type+
										" time:"+voice.create_time+", url:"+voice.url+" date:"+_sdf.format(date));
								
								IPanelAudioAction audioPanel = new PanelAudioFrom(_context, _audioList,
										TabPageAudio.this,voice.url, date);
								_audioPanels.add(audioPanel);
								
								_lastFromDate = date;
								
								_autoScroll();
							}
							
							
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
					}
				}
			}
			
		}

		@Override
		public void onIOException(IOException e) {
			
			Utils.amLog(e.toString());
			
		}

		@Override
		public void onError(AjaxException e) {
			
			Utils.amLog(e.toString());
	
		}

		@Override
		public void onUpdateProgress(long total, long current) {
			
			Utils.amLog("current:"+current+", total:"+total);
		}
		
	}


	


	


	


	

}
