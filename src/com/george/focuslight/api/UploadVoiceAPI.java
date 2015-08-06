package com.george.focuslight.api;

import java.io.File;

import com.xoozi.andromeda.net.ajax.AjaxFormFile;
import com.xoozi.andromeda.net.ajax.AjaxParameters;
import com.xoozi.andromeda.net.ajax.AjaxRequestListener;

public class UploadVoiceAPI extends FocusLightBaseAPI {
	
	private static final String	INTERFACE_URL 	= "/upload_mobile_voice/";
	//private static final String	INTERFACE_URL 	= "/upload_pc_voice/";
	
	private static final String GUID 			= "guid";
	private static final String VOICE 			= "voice";
	
	public static void uploadVoice(String guid, File voiceFile, AjaxRequestListener listener){
		
		AjaxFormFile[] formFiles 	= new AjaxFormFile[1];
		formFiles[0] 				= new AjaxFormFile(voiceFile,VOICE,null);
		
		AjaxParameters params = new AjaxParameters();
		
		params.add(GUID, guid);
		
		upload(API_SERVER+INTERFACE_URL,  params, 
				formFiles, listener);
	}

}
