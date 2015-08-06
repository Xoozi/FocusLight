package com.george.focuslight.api;

import java.io.File;

import com.xoozi.andromeda.net.ajax.AjaxParameters;
import com.xoozi.andromeda.net.ajax.AjaxRequestListener;
import com.xoozi.andromeda.net.ajax.AjaxRunner;

public class DownLoadFileAPI extends FocusLightBaseAPI {
	
	private static final String	INTERFACE_URL 	= "/get_pc_voice/";
	//private static final String	INTERFACE_URL 	= "/get_mobile_voice/";
	private static final String GUID = "guid";
	private static final String URL = "url";
	
	public static void download(String url, File outputFile, AjaxRequestListener listener){
		
		AjaxParameters params = new AjaxParameters();
		AjaxRunner.downloadFile(url, params, outputFile, listener);
	}
	
	public static void downloadPlus(String url, String guid,File outputFile, AjaxRequestListener listener){
		
		AjaxParameters params = new AjaxParameters();
		params.add(GUID, guid);
		params.add(URL, url);
		AjaxRunner.downloadFile(API_SERVER+INTERFACE_URL, params, outputFile, listener);
	}

}
