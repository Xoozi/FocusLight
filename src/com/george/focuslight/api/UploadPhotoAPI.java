package com.george.focuslight.api;

import java.io.File;

import com.xoozi.andromeda.net.ajax.AjaxFormFile;
import com.xoozi.andromeda.net.ajax.AjaxParameters;
import com.xoozi.andromeda.net.ajax.AjaxRequestListener;

public class UploadPhotoAPI extends FocusLightBaseAPI{

	private static final String	INTERFACE_URL 	= "/upload/";
	
	private static final String GUID 			= "guid";
	private static final String MD5 			= "md5";
	private static final String PHOTO 			= "photo";
	private static final String THUMB 			= "thumb";
	private static final String TYPE 			= "type";
	
	private static final String ACTION_TYPE_DESKTOP = "desktop";
	private static final String ACTION_TYPE_START = "start";
	//private static final String	ACTION_TYPE_GREEK = "greek";
	
	public static void uploadPhoto(ActionType actionType, String guid, String md5, File photoFile, File thumbFile,AjaxRequestListener listener){
		
		AjaxFormFile[] formFiles 	= new AjaxFormFile[2];
		formFiles[0] 				= new AjaxFormFile(photoFile,PHOTO,null);
		formFiles[1] 				= new AjaxFormFile(thumbFile,THUMB,null);
		
		AjaxParameters params = new AjaxParameters();
		
		params.add(GUID, guid);
		params.add(MD5, md5);
		
		if(ActionType.DESKTOP==actionType){
			params.add(TYPE, ACTION_TYPE_DESKTOP);
		}else{
			params.add(TYPE, ACTION_TYPE_START);
		}
		
		upload(API_SERVER+INTERFACE_URL,  params, 
				formFiles, listener);
	}
	
	public	enum ActionType{DESKTOP, START};
}
