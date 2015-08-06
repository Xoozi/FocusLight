package com.george.focuslight.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {

	static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		
		if (intent.getAction().equals(ACTION)){
			Intent refreshService = new Intent(context, RefreshService.class);
			context.startService(refreshService);
		}
	}
}
