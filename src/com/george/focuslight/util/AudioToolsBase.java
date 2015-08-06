package com.george.focuslight.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;

import com.george.focuslight.data.AppProfile;

@SuppressLint("SimpleDateFormat")
public abstract class AudioToolsBase {
	protected static	final String	VOICE_DIR		= "voice";
	protected static 	final String	PC_VOICE_DIR	= "pc_voice";
	protected static final SimpleDateFormat 		_sdf 	= new SimpleDateFormat("yyyyMMddHHmmss");
	
	
	protected static    final String 	FILE_EXT 	= ".amr";
	protected Context				_context;
	protected AudioRecordAction 	_recordAction;
	protected File					_recordFile;
	protected Date					_recordDate;
	
	
	public	AudioToolsBase(Context context, AudioRecordAction recordAction){
		_context		= context;
		_recordAction	= recordAction;
	}
	
	public	void	startRecord(){
		
	}
	
	public	void	stopRecord(){
		
	}
	
	public	void	playBack(){
		
	}
	
	public	File	getRecord(){
		return _recordFile;
	}
	
	public 	Date	getRecordData(){
		return _recordDate;
	}
	
	public 	long	getRecordDuration(){
		return 0;
	}
	
	public  void	shutDown(){
		
	}
	
	public	void	cleanRecord(){
		
	}
	
	
	protected void	_getNewRecordFile(){
		_recordDate = new Date();
		String	dateString = _sdf.format(_recordDate);
		String 	fileName = AppProfile.getActiveUser(_context)+"_"+dateString+FILE_EXT;
		
		File appDir = AppUtils.getAppDir(_context);
		
		File voiceDir = new File(appDir,VOICE_DIR);
		
		if(!voiceDir.exists())
			voiceDir.mkdirs();
		
		_recordFile = new File(voiceDir,fileName);
	}
	
	protected void	_record(){
		
	}
	
	protected void	_playback(){
		
	}

	
	
	public	interface AudioRecordAction{
		
		public void	onRecordCompleted();
		
		
	}

}
