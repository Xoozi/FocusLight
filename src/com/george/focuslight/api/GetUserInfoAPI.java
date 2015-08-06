package com.george.focuslight.api;

import com.xoozi.andromeda.net.ajax.AjaxParameters;
import com.xoozi.andromeda.net.ajax.AjaxRequestListener;

public class GetUserInfoAPI extends FocusLightBaseAPI {
	
	public static final String	ACTION_KEY = "action";
	public static final String	TRANSLATION_DESKTOP = "desktop";
	public static final String  TRANSLATION_START	= "start";
	public static final String  TRANSLATION_GEEK	= "greek";
	
	
	private static final String	INTERFACE_URL 	= "/get_userinfo_lite/";
	
	private static final String GUID 			= "guid";
	
	
	public static void	getUserInfo(String guid, AjaxRequestListener listener){
		AjaxParameters params = new AjaxParameters();
		
		params.add(GUID, guid);
		
		request(API_SERVER+INTERFACE_URL,params, HTTPMETHOD_GET,null,listener);
	}

}
