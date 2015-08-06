package com.george.focuslight.api;

import com.xoozi.andromeda.net.ajax.AjaxParameters;
import com.xoozi.andromeda.net.ajax.AjaxRequestListener;

public class GetPCVoiceListAPI extends FocusLightBaseAPI {
	
	private static final String	INTERFACE_URL 	= "/query_pc_voice_list/";
	//private static final String	INTERFACE_URL 	= "/query_mobile_voice_list/";
	
	private static final String GUID 			= "guid";
	
	public	static final String VOICE_KEY		= "voice";
	
	public static final String	TYPE_PC			= "pc";
	public static final String	TYPE_MOBILE		= "mobile";
	
	
	public static void	getPCVoiceList(String guid, AjaxRequestListener listener){
		AjaxParameters params = new AjaxParameters();
		
		params.add(GUID, guid);
		
		request(API_SERVER+INTERFACE_URL,params, HTTPMETHOD_GET,null,listener);
	}

}
