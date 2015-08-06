package com.george.focuslight.panel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.george.focuslight.R;
import com.george.focuslight.api.GetPCVoiceListAPI;
import com.george.focuslight.api.UploadVoiceAPI;
import com.george.focuslight.data.AppProfile;
import com.george.focuslight.tab.ITabPageAudioImp;
import com.xoozi.andromeda.net.ajax.AjaxException;
import com.xoozi.andromeda.net.ajax.AjaxRequestListener;
import com.xoozi.andromeda.net.ajax.HttpManager.Responses;
import com.xoozi.andromeda.uicontroller.SelfInflaterPanel;
import com.xoozi.andromeda.utils.Utils;

public class PanelAudioTo extends SelfInflaterPanel implements OnClickListener, IPanelAudioAction, AjaxRequestListener{
	private ITabPageAudioImp _tabPageAudioImp;
	private	TextView	_textTime;
	private TextView	_textDuration;
	private ProgressBar	_progressBar;
	private File		_audioFile;
	private Date		_time;
	private long		_duration;
	private View		_btnPlayback;

	public PanelAudioTo(Context context, LinearLayout baseLayout, 
			 ITabPageAudioImp tabPageAudioImp,
			File audioFile, Date time, long duration) {
		super(context, baseLayout);
		_tabPageAudioImp = tabPageAudioImp;
		_audioFile = audioFile;
		_time = time;
		_duration = duration;
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
	
	@Override
	public String getUrl() {
		
		return _audioFile.getAbsolutePath();
	}



	@Override
	public String getType() {
		
		return GetPCVoiceListAPI.TYPE_MOBILE;
	}
	
	@Override
	public  void  playbackCompleted(){
		_btnPlayback.setBackgroundDrawable(_context.getResources().getDrawable(android.R.drawable.ic_media_play));
	}
	
	@Override
	public void playbackCanceled() {
		//do nothing
	}

	
	@SuppressLint("SimpleDateFormat")
	private	void	_initWork(){
		initPanel(R.layout.panel_voice_to);
		
		_btnPlayback = _panelView.findViewById(R.id.btn_playback);
		
		_btnPlayback.setOnClickListener(this);
		
		_textTime = (TextView)_panelView.findViewById(R.id.text_time);
		
		_textDuration = (TextView)_panelView.findViewById(R.id.text_duratation);
		
		_progressBar = (ProgressBar)_panelView.findViewById(R.id.progress_transmitting);
		
		/*SimpleDateFormat 		sdf 	= new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		
		String	dateString = sdf.format(_time);
		_textTime.setText(dateString);*/
		
		
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
		
		UploadVoiceAPI.uploadVoice(AppProfile.getActiveUser(_context), _audioFile, this);
	}






	@Override
	public void onComplete(Responses responses) {
		Utils.amLog(responses.response);
		_progressBar.setVisibility(View.GONE);
	}



	@Override
	public void onIOException(IOException e) {
		Utils.amLog(e.toString());
		_progressBar.setVisibility(View.GONE);
	}



	@Override
	public void onError(AjaxException e) {
		Utils.amLog(e.toString());
		_progressBar.setVisibility(View.GONE);
	}



	@Override
	public void onUpdateProgress(long total, long current) {
		// TODO Auto-generated method stub
		
	}



	

}
