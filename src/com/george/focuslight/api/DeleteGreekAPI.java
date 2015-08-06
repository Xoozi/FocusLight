package com.george.focuslight.api;

import com.xoozi.andromeda.net.ajax.AjaxParameters;
import com.xoozi.andromeda.net.ajax.AjaxRequestListener;

public class DeleteGreekAPI extends FocusLightBaseAPI{
	
	private static final String	INTERFACE_URL 	= "/delete_greek/";
	
	private static final String GUID 			= "guid";
	
	
	public static void deleteGreek(String guid, AjaxRequestListener listener){
		
		
		AjaxParameters params = new AjaxParameters();
		
		params.add(GUID, guid);
		
		request(API_SERVER+INTERFACE_URL,params, HTTPMETHOD_GET,null,listener);
	}

}

