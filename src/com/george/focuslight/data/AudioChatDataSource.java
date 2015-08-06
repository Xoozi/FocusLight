package com.george.focuslight.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;

public class AudioChatDataSource {
	
	private List<AudioRecord>	_records = new ArrayList<AudioRecord>();
	private	Context				_context;
	
	public AudioChatDataSource(Context context){
		_context = context;
	}
	
	public	void addAudioRecord(String filePath, RecordType recordType, Date time){
		
		_records.add(new AudioRecord(filePath, recordType, time));
	}
	
	public	int getCount(){
		return _records.size();
	}
	
	public	class AudioRecord{
		
		AudioRecord(String filePath, RecordType recordType, Date time){
			this.filePath 	= filePath;
			this.recordType	= recordType;
			this.time		= time;
		}
		
		public String		filePath;
		public RecordType	recordType;
		public Date			time;
	}
	
	
	public	enum RecordType{TO_RECORD, FROM_RECORD};
}
