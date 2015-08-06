package com.george.focuslight;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import com.george.focuslight.ajaxobject.ActionData;
import com.george.focuslight.ajaxobject.NormalResult;
import com.george.focuslight.ajaxobject.ResultCode;
import com.george.focuslight.api.DeleteGreekAPI;
import com.george.focuslight.api.DeletePhotoActionAPI;
import com.george.focuslight.api.GetUserInfoAPI;
import com.george.focuslight.api.SetGreekAPI;
import com.george.focuslight.api.UploadPhotoAPI;
import com.george.focuslight.data.AppProfile;
import com.george.focuslight.panel.IPanelAction;
import com.george.focuslight.panel.PanelAddedDesktopPhoto;
import com.george.focuslight.panel.PanelAddedGreekText;
import com.george.focuslight.panel.PanelAddedStartPhotos;
import com.george.focuslight.poppanel.IPopPanelAction4Share;
import com.george.focuslight.poppanel.PopDropMenu;
import com.george.focuslight.poppanel.PopGreekEdit;
import com.george.focuslight.poppanel.PopLoading;
import com.george.focuslight.service.RefreshService;
import com.george.focuslight.tab.TabPanel;
import com.george.focuslight.util.PhotoTools;
import com.xoozi.andromeda.net.ajax.AjaxException;
import com.xoozi.andromeda.net.ajax.AjaxDownloader;
import com.xoozi.andromeda.net.ajax.AjaxRequestListener;
import com.xoozi.andromeda.net.ajax.HttpManager.Responses;
import com.xoozi.andromeda.utils.JSONAndObject;
import com.xoozi.andromeda.utils.MD5Tools;
import com.xoozi.andromeda.utils.Utils;
import com.zxing.activity.CaptureActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements IActivityAction {
	
	public	static final String ACTION_AUDIO = "com.george.focuslight.MainActivity.AUDIO";
	

	private TabPanel					_tabPanel;
	
	
	@SuppressLint("HandlerLeak")
	private Handler 	_handlerConvert	= new Handler()
	{
		public void handleMessage(Message msg) 
		{
			switch (msg.what) 
			{
			case 1:
				_getUserInfo();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		_initWork();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.action_settings:
			break;
			
		case R.id.action_clean_active:
			_cleanActive();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    
	    _tabPanel.onPhotoResultReturn(requestCode, resultCode, data);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		_tabPanel.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		_tabPanel.onResume();
	}
	
	
	
	private void	_initWork(){
		
		_tabPanel	= new TabPanel(getWindow().getDecorView(),this,this);
		
		if(ACTION_AUDIO.equals(getIntent().getAction())){
			_tabPanel.jumpToAudio();
		}
		
		
	
		Timer timer = new Timer();
		timer.schedule(new ConvertDocTimer(), 500);
		
		Intent service = new Intent(this, RefreshService.class);
		
		this.startService(service);
		
	}
	
	private void 	_getUserInfo(){
		_tabPanel.getUserInfoWhenInit();
	}
	

	
	/**
	 * 解除设备绑定
	 */
	private void	_cleanActive(){
		AppProfile.setActiveUser(this,AppProfile.PREFERENCE_DEFAULT_ACTIVE_USER);
		_jumpToStartPage();
	}
	
	/**
	 * 跳转到主界面
	 */
	private void	_jumpToStartPage(){
		
		Intent jumpToMain = new Intent(MainActivity.this,StartPageActivity.class);
		startActivity(jumpToMain);
		finish();
	}
	


	/**
	 *
	 * @author xoozi
	 *
	 */
	private class ConvertDocTimer extends TimerTask
	{
		@Override
		public void run() 
		{
			Message message = new Message();
			message.what = 1;
			_handlerConvert.sendMessage(message);
		}   	
    }





	@Override
	public void onViewPhoto(int requestCode) {
		Intent i = new Intent(Intent.ACTION_PICK, 
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				 
		startActivityForResult(i, requestCode);
	}


}
