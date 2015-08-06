package com.george.focuslight.tab;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import com.george.focuslight.HTMLErrorActivity;
import com.george.focuslight.MainActivity;
import com.george.focuslight.R;
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
import com.george.focuslight.util.PhotoTools;
import com.xoozi.andromeda.net.ajax.AjaxException;
import com.xoozi.andromeda.net.ajax.AjaxRequestListener;
import com.xoozi.andromeda.net.ajax.HttpManager.Responses;
import com.xoozi.andromeda.utils.JSONAndObject;
import com.xoozi.andromeda.utils.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class TabPageShare extends TabPageBase implements IPopPanelAction4Share, IPanelAction{
	
	private static final int REQUEST_CODE_VIEW_DESKTOP_PHOTO 		= 2001;
	private static final int REQUEST_CODE_VIEW_START_PHOTO 			= 2002;
	

	private PopGreekEdit				_popGreekEdit;
	private PopLoading					_popLoading;
	private PanelAddedDesktopPhoto		_panelDeskPhoto;
	private PanelAddedStartPhotos		_panelStartPhotos;
	private PanelAddedGreekText			_panelGreekText;
	
	private String						_md5StartPhoto;
	private String						_md5DeskPhoto;
	
	private String						_greek;
	
	private DesktopUploadListener		_desktopUploadListener		= new DesktopUploadListener();
	private StartPhotoUploadListener	_startPhotoUploadListener 	= new StartPhotoUploadListener();
	private GreekListener				_greekListener				= new GreekListener();
	private GetUserInfo					_getUserInfoListener		= new GetUserInfo();
	

	public TabPageShare(ScrollView basePanel, Context context,
			ITabPanelImp tabPanelImp) {
		super(basePanel, context, tabPanelImp);
		
		_initWork();
	}
	
	/**
	 * 响应同步资源面板
	 */
	@Override
	public void deleteDesktop() {
		_desktopUploadListener.delete = true;
		DeletePhotoActionAPI.deleteAction(DeletePhotoActionAPI.ActionType.DESKTOP, AppProfile.getActiveUser(_context), _desktopUploadListener);
		_popLoading.show();
	}

	@Override
	public void deleteStart() {
		_startPhotoUploadListener.delete = true;
		DeletePhotoActionAPI.deleteAction(DeletePhotoActionAPI.ActionType.START, AppProfile.getActiveUser(_context), _startPhotoUploadListener);
		_popLoading.show();
	}

	@Override
	public void deleteGreek() {
		_greekListener.delete = true;
		DeleteGreekAPI.deleteGreek(AppProfile.getActiveUser(_context), _greekListener);
		_popLoading.show();
	}
	
	@Override
	public void setDesktop() {
		_tabPanelImp.onViewPhoto(REQUEST_CODE_VIEW_DESKTOP_PHOTO);
	}

	@Override
	public void setStart() {
		_tabPanelImp.onViewPhoto(REQUEST_CODE_VIEW_START_PHOTO);
	}

	@Override
	public void setGreek() {
		_popGreekEdit.show();
	}
	/**
	 * 响应同步资源面板 END
	 */
	
	
	/**
	 * 响应弹出菜单
	 */
	@Override
	public void addDesktopPhoto() {
		
	}

	@Override
	public void addStartPhoto() {
		
	}

	@Override
	public void addGreekText() {
		
	}
	
	@Override
	public	void	greekEditEnd(String greek){
		//_panelGreekText.setGreek(greek);
		_greek = greek;
		_greekListener.delete = false;
		SetGreekAPI.setGreek(AppProfile.getActiveUser(_context), _greek, _greekListener);
		_popLoading.show();
	}
	/**
	 * 响应弹出菜单 END
	 */
	
	
	public	void	getUserInfoWhenInit(){
		//获取用户信息
		GetUserInfoAPI.getUserInfo(AppProfile.getActiveUser(_context), _getUserInfoListener);
		_popLoading.show();
	}
	
	public	void	onPhotoResultReturn(int requestCode, int resultCode, Intent data){

	    if (requestCode == REQUEST_CODE_VIEW_DESKTOP_PHOTO && resultCode == Activity.RESULT_OK && null != data) {
	    	String result = PhotoTools.selectedPhotoForDesktop(_context, data);
	       
	       if(null!=result){
	    	   
	    	   _md5DeskPhoto = result;
	    	   _desktopUploadListener.delete = false;
	    	   UploadPhotoAPI.uploadPhoto(UploadPhotoAPI.ActionType.DESKTOP, 
	    			   AppProfile.getActiveUser(_context), 
	    			   result, 
	    			   PhotoTools.photoFromMD5(_context, result), 
	    			   PhotoTools.thumbFromMD5(_context, result), 
	    			   _desktopUploadListener);
	    	   _popLoading.show();
	       }
	    }else if (requestCode == REQUEST_CODE_VIEW_START_PHOTO && resultCode == Activity.RESULT_OK && null != data) {
		       String result = PhotoTools.selectedPhotoForStart(_context, data);
		       
		   if(null!=result){
			   
			   _md5StartPhoto = result;
			   _startPhotoUploadListener.delete = false;
			   UploadPhotoAPI.uploadPhoto(UploadPhotoAPI.ActionType.START, 
					   AppProfile.getActiveUser(_context), 
					   result, 
					   PhotoTools.photoFromMD5(_context, result), 
	    			   PhotoTools.thumbFromMD5(_context, result), 
	    			   _startPhotoUploadListener);
			   _popLoading.show();
		   }
		}
	}
	
	
	private void	_initWork(){
		_popLoading   = new PopLoading(_context,_basePanel);
		
		_popGreekEdit	= new PopGreekEdit(_context,_basePanel,this);

		
		_panelDeskPhoto = new PanelAddedDesktopPhoto(_context,
				(LinearLayout)_basePanel.findViewById(R.id.field_added_desktop), this);
		
		_panelStartPhotos = new PanelAddedStartPhotos(_context,
				(LinearLayout)_basePanel.findViewById(R.id.field_added_start_photo), this);
		
		_panelGreekText = new PanelAddedGreekText(_context,
				(LinearLayout)_basePanel.findViewById(R.id.field_added_greek),this);
	}
	
	
	
	/**
	 * 桌面图上传监听
	 * @author xoozi
	 *
	 */
	private	class DesktopUploadListener implements AjaxRequestListener{
		
		public boolean delete = false;

		@Override
		public void onComplete(Responses responses) {
			Utils.amLog(responses.response);
			
			
			
			if(delete){
				_panelDeskPhoto.deletePhoto();
				Toast.makeText(_context, R.string.toast_desktop_delete_suc, Toast.LENGTH_SHORT).show();
			}else{
				_panelDeskPhoto.setPhoto(_md5DeskPhoto,null);
				Toast.makeText(_context, R.string.toast_desktop_upload_suc, Toast.LENGTH_SHORT).show();
				File photo = PhotoTools.photoFromMD5(_context, _md5DeskPhoto);
				photo.delete();
			}
			
			_popLoading.hide();
		}

		@Override
		public void onIOException(IOException e) {
			
			Utils.amLog(e.toString());
			_popLoading.hide();
		}

		@Override
		public void onError(AjaxException e) {
			
			Utils.amLog(e.toString());
			_popLoading.hide();
			HTMLErrorActivity.displayHTML(_context, e.getMessage());

		}

		@Override
		public void onUpdateProgress(long total, long current) {
			
			Utils.amLog("current:"+current+", total:"+total);
		}
		
	}
	
	
	/**
	 * 开机图片上传更新
	 * @author xoozi
	 *
	 */
	private class StartPhotoUploadListener implements AjaxRequestListener{
		
		public boolean delete = false;

		@Override
		public void onComplete(Responses responses) {
			Utils.amLog(responses.response);
			
			if(delete){
				_panelStartPhotos.deletePhoto();
				Toast.makeText(_context, R.string.toast_start_delete_suc, Toast.LENGTH_SHORT).show();
			}else{
				_panelStartPhotos.setPhoto(_md5StartPhoto, null);
				Toast.makeText(_context, R.string.toast_start_upload_suc, Toast.LENGTH_SHORT).show();
				File photo = PhotoTools.photoFromMD5(_context, _md5StartPhoto);
				photo.delete();
			}
			
			_popLoading.hide();
		}

		@Override
		public void onIOException(IOException e) {
			
			Utils.amLog(e.toString());
			_popLoading.hide();
		}

		@Override
		public void onError(AjaxException e) {
			
			Utils.amLog(e.toString());
			_popLoading.hide();
			HTMLErrorActivity.displayHTML(_context, e.getMessage());
		}

		@Override
		public void onUpdateProgress(long total, long current) {
			
			Utils.amLog("current:"+current+", total:"+total);
		}
		
	}
	
	
	private class GreekListener implements AjaxRequestListener{
		
		public boolean delete = false;

		@Override
		public void onComplete(Responses responses) {
			Utils.amLog(responses.response);
			
			
			if(delete){
				_panelGreekText.deleteGreek();
				Toast.makeText(_context, R.string.toast_greek_delete_suc, Toast.LENGTH_SHORT).show();
			}else{
				_panelGreekText.setGreek(_greek);
				Toast.makeText(_context, R.string.toast_greek_set_suc, Toast.LENGTH_SHORT).show();
			}
			
			_popLoading.hide();
		}

		@Override
		public void onIOException(IOException e) {
			
			Utils.amLog(e.toString());
			
			_popLoading.hide();
		}

		@Override
		public void onError(AjaxException e) {
			
			Utils.amLog(e.toString());
			_popLoading.hide();
			HTMLErrorActivity.displayHTML(_context, e.getMessage());
		}

		@Override
		public void onUpdateProgress(long total, long current) {
			
			Utils.amLog("current:"+current+", total:"+total);
		}
		
	}
	
	
	private class GetUserInfo implements AjaxRequestListener{
		
		public boolean delete = false;

		@SuppressWarnings("unchecked")
		@Override
		public void onComplete(Responses responses) {
			Utils.amLog(responses.response);
			
			NormalResult result = new NormalResult();
			JSONAndObject.convertSingleObject(result, responses.response);
			
			if(ResultCode.RESULT_SUC == result.result){
				
				List<ActionData> actions = new ArrayList<ActionData>();
				actions = JSONAndObject.convert(ActionData.class, responses.response,GetUserInfoAPI.ACTION_KEY);
				if(null!=actions){
					for(ActionData action:actions){
						
						if(action.translation.equals(GetUserInfoAPI.TRANSLATION_DESKTOP)){
							_panelDeskPhoto.setPhoto(action.md5, action.thumb);
						}else if(action.translation.equals(GetUserInfoAPI.TRANSLATION_START)){
							_panelStartPhotos.setPhoto(action.md5, action.thumb);
						}else if(action.translation.equals(GetUserInfoAPI.TRANSLATION_GEEK)){
							_panelGreekText.setGreek(action.text);
						}
					}
				}
				
			}
			
			_popLoading.hide();
		}

		@Override
		public void onIOException(IOException e) {
			
			Utils.amLog(e.toString());
			
			_popLoading.hide();
		}

		@Override
		public void onError(AjaxException e) {
			
			Utils.amLog(e.toString());
			
			_popLoading.hide();
			HTMLErrorActivity.displayHTML(_context, e.getMessage());
			
			
		}

		@Override
		public void onUpdateProgress(long total, long current) {
			
			Utils.amLog("current:"+current+", total:"+total);
		}
		
	}


	

}
