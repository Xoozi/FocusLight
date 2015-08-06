package com.george.focuslight.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.george.focuslight.data.AppProfile;
import com.xoozi.andromeda.utils.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;

public class AudioToolsPCM extends AudioToolsBase{
	
	private static	final int		MAX_MILLIS 	= 20*1000;
	
	private	Context				_context;
	private AudioRecordAction	_recordAction;
	private AudioTrack 			_audioTrack;
	private boolean				_isRecording;

	
	public AudioToolsPCM(Context context, AudioRecordAction recordAction) {
		super(context, recordAction);
		//FILE_EXT = ".pcm";
	}
	
	public	void	startRecord(){
		_isRecording = true;
		_getNewRecordFile();
		
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
			@Override
			protected void onPostExecute(Void result) {
				_recordAction.onRecordCompleted();
			}

			@Override
			protected Void doInBackground(Void... params) {
	            _record();
	            return null;
			}
	          
		};
	    task.execute();
	}
	
	public	void	stopRecord(){
		_isRecording = false;
	}
	
	public	void	playBack(){
		_playback();
	}
	
	public	File	getRecord(){
		return _recordFile;
	}
	
	public  void	shutDown(){
		if(null!=_audioTrack){
			_audioTrack.stop();
			_audioTrack.release();
			_audioTrack = null;
		}
		
	}
	
	public	void	cleanRecord(){
		
		_recordFile.delete();
		_recordFile = null;
	}
	
	protected void	_getNewRecordFile(){
		Date	now = new Date();
		String	dateString = _sdf.format(now);
		String 	fileName = AppProfile.getActiveUser(_context)+"_"+dateString+FILE_EXT;
		
		File appDir = AppUtils.getAppDir(_context);
		
		File voiceDir = new File(appDir,VOICE_DIR);
		
		if(!voiceDir.exists())
			voiceDir.mkdir();
		
		_recordFile = new File(voiceDir,fileName);
	}
	
	protected void	_record(){
		long startTime = System.currentTimeMillis();
		long currentTime = 0;
		int frequency = 11025;
	    int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
	    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

	    try {
	    	_recordFile.createNewFile();
	    } catch (IOException e) {
	    	Utils.amLog("IO Exception"+e.toString());
	    }

	    try {
	      OutputStream os = new FileOutputStream(_recordFile);
	      BufferedOutputStream bos = new BufferedOutputStream(os);
	      DataOutputStream dos = new DataOutputStream(bos);

	      int bufferSize = AudioRecord.getMinBufferSize(frequency,
	                                                    channelConfiguration,
	                                                    audioEncoding);
	      short[] buffer = new short[bufferSize];

	      // Create a new AudioRecord object to record the audio.
	      AudioRecord audioRecord = 
	        new AudioRecord(MediaRecorder.AudioSource.MIC,
	                        frequency,
	                        channelConfiguration,
	                        audioEncoding, bufferSize); 
	      audioRecord.startRecording();

	      while (_isRecording) {
	    	  currentTime = System.currentTimeMillis();
	    	  if((currentTime-startTime)>=MAX_MILLIS)
	    		  break;
	    	  
	    	  int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
	    	  for (int i = 0; i < bufferReadResult; i++)
	    		  dos.writeShort(buffer[i]);
	      }

	      audioRecord.stop();
	      dos.close();
	    } catch (Throwable t) {
	    	Utils.amLog("An error occurred during recording"+t);
	    }
	}
	
	protected void _playback() {
	    /**
	     * Listing 15-19: Playing raw audio with Audio Track
	     */
	    int frequency = 11025;
	    int channelConfiguration = AudioFormat.CHANNEL_OUT_MONO;
	    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;


	    // Short array to store audio track (16 bit so 2 bytes per short)
	    int audioLength = (int)(_recordFile.length()/2);
	    short[] audio = new short[audioLength];

	    try {
	      InputStream is = new FileInputStream(_recordFile);
	      BufferedInputStream bis = new BufferedInputStream(is);
	      DataInputStream dis = new DataInputStream(bis);

	      int i = 0;
	      while (dis.available() > 0) {
	        audio[i] = dis.readShort();
	        i++;
	      }

	      // Close the input streams.
	      dis.close();

	      // Create and play a new AudioTrack object
	      _audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
	                                             frequency,
	                                             channelConfiguration,
	                                             audioEncoding,
	                                             audioLength,
	                                             AudioTrack.MODE_STREAM);
	      _audioTrack.play(); 
	      _audioTrack.write(audio, 0, audioLength);
	    } catch (Throwable t) {
	      Utils.amLog("An error occurred during playback" + t);
	    }
	  }
	

	

}
