package com.george.focuslight.panel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.george.focuslight.R;
import com.george.focuslight.api.DownLoadFileAPI;
import com.george.focuslight.api.GetPCVoiceListAPI;
import com.george.focuslight.data.AppProfile;
import com.george.focuslight.tab.ITabPageAudioImp;
import com.george.focuslight.util.AudioToolsAMR;
import com.xoozi.andromeda.net.ajax.AjaxException;
import com.xoozi.andromeda.net.ajax.AjaxRequestListener;
import com.xoozi.andromeda.net.ajax.HttpManager.Responses;
import com.xoozi.andromeda.uicontroller.SelfInflaterPanel;
import com.xoozi.andromeda.utils.Utils;

public class PanelAudioFrom extends SelfInflaterPanel implements OnClickListener, IPanelAudioAction, AjaxRequestListener{
	
	private ITabPageAudioImp _tabPageAudioImp;
	private	TextView	_textTime;
	private TextView	_textDuration;
	private ProgressBar	_progressBar;
	private String		_url;
	private Date		_time;
	private File		_audioFile;
	private long		_duration;
	private View		_btnPlayback;

	public PanelAudioFrom(Context context, LinearLayout baseLayout,
			ITabPageAudioImp tabPageAudioImp,
			String url,Date time) {
		super(context, baseLayout);
		_tabPageAudioImp = tabPageAudioImp;
		_url = url;
		_time = time;
		_initWork();
	}
	
	

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_playback:
			_btnPlayback.setBackgroundDrawable(_context.getResources().getDrawable(android.R.drawable.ic_media_pause));
			_tabPageAudioImp.playback(this,_audioFile);
			break;
		}
	}

	
	@SuppressLint("SimpleDateFormat")
	private	void	_initWork(){

		initPanel(R.layout.panel_voice_from);
		
		_btnPlayback = _panelView.findViewById(R.id.btn_playback);
		
		_btnPlayback.setOnClickListener(this);
		
		_btnPlayback.setVisibility(View.GONE);
		
		_progressBar = (ProgressBar)_panelView.findViewById(R.id.progress_transmitting);
		
		_textTime = (TextView)_panelView.findViewById(R.id.text_time);
		
		_textDuration = (TextView)_panelView.findViewById(R.id.text_duratation);
		
		/*SimpleDateFormat 		sdf 	= new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
		String	dateString = sdf.format(_time);
		_textTime.setText(dateString);*/
		
		
		_audioFile = AudioToolsAMR.getPCVoiceFile(_context, _time);
		
		DownLoadFileAPI.downloadPlus(_url,AppProfile.getActiveUser(_context), _audioFile, this);
	}



	@Override
	public String getUrl() {
	
		return null;
	}



	@Override
	public String getType() {
		
		return GetPCVoiceListAPI.TYPE_PC;
	}
	
	@Override
	public  void  playbackCompleted(){
		_btnPlayback.setBackgroundDrawable(_context.getResources().getDrawable(android.R.drawable.ic_media_play));
	}
	
	@Override
	public void playbackCanceled() {
		//do nothing
	}



	@Override
	public void onComplete(Responses responses) {
		Utils.amLog(responses.response);
		
		try {
			_duration = AudioToolsAMR.getAmrDuration(_audioFile);
			
			float sec = ((float)_duration)/1000.0f;
			
			String	duration = String.format("%.1f\"", sec);
			_textDuration.setText(duration);
			
			int len = (int)sec;
			if(len>10){
				len = 10;
			}else if(len<1){
				len = 1;
			}
			StringBuilder sb = new StringBuilder();
			for(int index=0;index<len;index++){
				sb.append("  ");
			}
			_textTime.setText(sb.toString());
			
			_progressBar.setVisibility(View.GONE);
			_btnPlayback.setVisibility(View.VISIBLE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
