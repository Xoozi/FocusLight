package com.george.focuslight.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.george.focuslight.MainActivity;
import com.george.focuslight.R;
import com.george.focuslight.ajaxobject.NormalResult;
import com.george.focuslight.ajaxobject.ResultCode;
import com.george.focuslight.ajaxobject.VoiceData;
import com.george.focuslight.api.GetPCVoiceListAPI;
import com.george.focuslight.data.AppProfile;
import com.xoozi.andromeda.net.ajax.AjaxException;
import com.xoozi.andromeda.net.ajax.AjaxRequestListener;
import com.xoozi.andromeda.net.ajax.HttpManager.Responses;
import com.xoozi.andromeda.utils.JSONAndObject;
import com.xoozi.andromeda.utils.Utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class RefreshService extends Service implements AjaxRequestListener {
	
	private static final long 		INTERVAL = 60 * 1000;
	private PendingIntent			_pendingIntent;
	private static final int		REF_ID = 555321;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Utils.amLog("Create RefreshService");
		_initWorkWhenCreateService();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Utils.amLog("destory RefreshService");
		//_cleanWorkWhenDestroyService();
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		//在长时间后台测试时发现，这里偶尔会因为null而抛出异常，预先检查一下
		if(null==intent){
			return super.onStartCommand(intent, flags, startId);
		}
		
		Utils.amLog("onStartCommand flags:"+intent.getFlags());
		
		//当服务被后台(Alarm服务)调用时，刷新服务器提供的语音列表
		if(Intent.FLAG_FROM_BACKGROUND==intent.getFlags()){
			
			_checkAndNotify();
		}
		
		return START_NOT_STICKY;
	}
	
	
	/**
	 * 服务创建时的初始化工作
	 */
	private void _initWorkWhenCreateService(){
		_startAutoUpdateAlarm();
	}
	
	
	/**
	 * 检查配置以决定是否开启自动更新
	 */
	private void _startAutoUpdateAlarm(){
		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		Utils.amLog("make repeat");
			
		_pendingIntent = PendingIntent.getService(RefreshService.this, 0, 
					new Intent(RefreshService.this, RefreshService.class),
					Intent.FLAG_ACTIVITY_NEW_TASK);
			
			
			
		alarmManager.cancel(_pendingIntent);
			
		long now = System.currentTimeMillis();
	
		alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, now, INTERVAL, _pendingIntent);
		
	}
	
	
	private void	_checkAndNotify(){
		GetPCVoiceListAPI.getPCVoiceList(AppProfile.getActiveUser(this), this);
	}

	@Override
	public void onComplete(Responses responses) {
		NormalResult result = new NormalResult();
		JSONAndObject.convertSingleObject(result, responses.response);
		
		if(ResultCode.RESULT_SUC == result.result){
			
			List<VoiceData> voices = new ArrayList<VoiceData>();
			voices = JSONAndObject.convert(VoiceData.class, responses.response,GetPCVoiceListAPI.VOICE_KEY);
			
			if(null!=voices){
				int length = voices.size();
				
				long when = System.currentTimeMillis();
				Notification notification = new Notification();
				
				notification.when = when;
				notification.icon = R.drawable.ic_launcher;
				notification.number = length;
				notification.defaults = Notification.DEFAULT_ALL;
				notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;
				
				
				Intent mainActivityIntent = new Intent(MainActivity.ACTION_AUDIO, null, this, 
						MainActivity.class);
			    PendingIntent launchIntent = PendingIntent.getActivity(this, 0, mainActivityIntent, 0);
			    
			    notification.setLatestEventInfo(this, getResources().getString(R.string.label_notification_title), 
			    		getResources().getString(R.string.label_notification_content), launchIntent);
				
				NotificationManager notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				
				notiManager.notify(REF_ID, notification);
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

	}

}
