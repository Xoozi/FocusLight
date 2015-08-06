package com.george.focuslight;


import java.io.IOException;

import com.george.focuslight.ajaxobject.NormalResult;
import com.george.focuslight.ajaxobject.ResultCode;
import com.george.focuslight.api.VerifyUserAPI;
import com.george.focuslight.data.AppProfile;
import com.xoozi.andromeda.net.ajax.AjaxException;
import com.xoozi.andromeda.net.ajax.AjaxRequestListener;
import com.xoozi.andromeda.net.ajax.HttpManager.Responses;
import com.xoozi.andromeda.utils.JSONAndObject;
import com.xoozi.andromeda.utils.Utils;
import com.zxing.activity.CaptureActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;


/**
 * 起始页面
 * @author xoozi
 *
 */
public class StartPageActivity extends Activity implements OnClickListener, AjaxRequestListener {
	
	
	
	private static final int REQUEST_CODE_CAPTURE_BARCODE 	= 2002;
	private EditText	_editGUID;
	private String		_guid;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_start_page);
		
		_initWork();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

	    if(requestCode == REQUEST_CODE_CAPTURE_BARCODE && resultCode == RESULT_OK && null != data){
	    	Bundle bundle = data.getExtras();
	    	String barcode = bundle.getString("result");
			
	    	_editGUID.setText(barcode);
	    }
	}
	
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.btn_scan_guid:
			_scanBarcode();
			break;
			
		case R.id.btn_active_guid:
			_verifyUser();
			break;
		}
	}
	
	/**
	 * http请求监听
	 */
	@Override
	public void onComplete(Responses responses) {
		Utils.amLog("response:"+responses.response);
		
		NormalResult result = new NormalResult();
		JSONAndObject.convertSingleObject(result, responses.response);
		
		if(ResultCode.RESULT_SUC == result.result){
			
			//校验通过，记录该guid并把该设备guid设置为当前激活的guid
			AppProfile profile = new AppProfile(this);
			AppProfile.setActiveUser(this,_guid);
			profile.addUserToProfile(_guid);
			profile.recycle();
			
			//跳转到主界面
			_jumpToMain();
			
		}else if(ResultCode.RESULT_USER_NOT_EXIST == result.result){
			Toast.makeText(this, R.string.toast_no_user, Toast.LENGTH_SHORT).show();
		}else if(ResultCode.RESULT_USER_NOT_ACTIVE == result.result){
			Toast.makeText(this, R.string.toast_not_active, Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(this, result.toString(), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onIOException(IOException e) {
		Utils.amLog("ioexception:"+e.toString());
		int dummy;
		Toast.makeText(this, R.string.toast_verify_failed, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onError(AjaxException e) {
		Utils.amLog("ajaxexception:"+e.toString());
		
		Toast.makeText(this, R.string.toast_verify_failed, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onUpdateProgress(long total, long current) {
		Utils.amLog("progress current:"+current+", total:"+total);
	}
	/**
	 * http请求监听 END
	 */
	
	
	
	private void	_initWork(){
		
		if(_isActived()){
			_jumpToMain();
			return;
		}
		
		
		
		_editGUID	= (EditText) findViewById(R.id.edit_guid);
		
		findViewById(R.id.btn_scan_guid).setOnClickListener(this);
		findViewById(R.id.btn_active_guid).setOnClickListener(this);
	}
	
	
	/**
	 * 扫描二维码
	 */
	private void	_scanBarcode(){
		Intent openCameraIntent = new Intent(StartPageActivity.this,CaptureActivity.class);
		startActivityForResult(openCameraIntent, REQUEST_CODE_CAPTURE_BARCODE);
	}
	
	/**
	 * 校验设备码
	 */
	private void	_verifyUser(){
		_guid = _editGUID.getText().toString();
		
		if(_guid.length()<6){
			Toast.makeText(this, R.string.toast_guid_invalide, Toast.LENGTH_SHORT).show();
			return;
		}else{
			VerifyUserAPI.verifyUser(_guid, this);
		}
	}
	
	/**
	 * 跳转到主界面
	 */
	private void	_jumpToMain(){
		
		Intent jumpToMain = new Intent(StartPageActivity.this,MainActivity.class);
		startActivity(jumpToMain);
		finish();
	}
	
	
	/**
	 * 在本客户端上是否有设备已经被激活
	 * @return
	 */
	private boolean	_isActived(){
		return !AppProfile.PREFERENCE_DEFAULT_ACTIVE_USER.equals(AppProfile.getActiveUser(this));
	}



}
