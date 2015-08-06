package com.george.focuslight.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.george.focuslight.data.AppProfile;

import android.content.Context;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;


public class AudioToolsAMR extends AudioToolsBase{
	
	private 	MediaRecorder		_audioRecorder = new MediaRecorder();  
	
	
	/** 
     * 得到amr的时长 
     *  
     * @param file 
     * @return 
     * @throws IOException 
     */  
    public static long getAmrDuration(File file) throws IOException {  
        long duration = -1;  
        int[] packedSize = { 12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0, 0, 0 };  
        RandomAccessFile randomAccessFile = null;  
        try {  
            randomAccessFile = new RandomAccessFile(file, "rw");  
            long length = file.length();//文件的长度  
            int pos = 6;//设置初始位置  
            int frameCount = 0;//初始帧数  
            int packedPos = -1;  
            /////////////////////////////////////////////////////  
            byte[] datas = new byte[1];//初始数据值  
            while (pos <= length) {  
                randomAccessFile.seek(pos);  
                if (randomAccessFile.read(datas, 0, 1) != 1) {  
                    duration = length > 0 ? ((length - 6) / 650) : 0;  
                    break;  
                }  
                packedPos = (datas[0] >> 3) & 0x0F;  
                pos += packedSize[packedPos] + 1;  
                frameCount++;  
            }  
            /////////////////////////////////////////////////////  
            duration += frameCount * 20;//帧数*20  
        } finally {  
            if (randomAccessFile != null) {  
                randomAccessFile.close();  
            }  
        }  
        return duration;  
    }   
    
    
    public static File	getPCVoiceFile(Context context, Date date){
		File appDir = AppUtils.getAppDir(context);
		
		File PCVoiceDir = new File(appDir,PC_VOICE_DIR);
		
		String	dateString = _sdf.format(date);
		String 	fileName = AppProfile.getActiveUser(context)+"_"+dateString+FILE_EXT;
		
		if(!PCVoiceDir.exists())
			PCVoiceDir.mkdirs();
		
		return new File(PCVoiceDir, fileName);
	}

	public AudioToolsAMR(Context context, AudioRecordAction recordAction) {
		super(context, recordAction);
	}
	
	
	

	@Override
	public void startRecord() {
		_record();
	}

	@Override
	public void stopRecord() {
		
		if(null!=_audioRecorder){
			_audioRecorder.stop();
			_recordAction.onRecordCompleted();
			_audioRecorder.release();
			_audioRecorder = null;
		}
			
	}

	@Override
	public void playBack() {
		_playback();
	}

	
	@Override
	public long	getRecordDuration(){
		long result = 0;
		
		try {
			result = getAmrDuration(_recordFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public void shutDown() {
		if(null!=_audioRecorder){
		
			cleanRecord();
		}
		
	}

	@Override
	public void cleanRecord() {
		if(null!=_recordFile){
			_recordFile.delete();
			_recordFile = null;
		}
		
	}
	
	
	@Override
	protected void	_record(){
		_getNewRecordFile();
		
		_audioRecorder = new MediaRecorder();
		_audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);  
		//_audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		_audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);  
		//_audioRecorder.setAudioChannels(AudioFormat.CHANNEL_CONFIGURATION_MONO);  
	
		_audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);  
		_audioRecorder.setOutputFile(_recordFile.getAbsolutePath());
		
		try {
			_audioRecorder.prepare();
			_audioRecorder.start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void	_playback(){
		
		
	}

	
	
	

}
