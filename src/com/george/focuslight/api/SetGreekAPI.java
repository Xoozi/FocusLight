package com.george.focuslight.api;

import com.xoozi.andromeda.net.ajax.AjaxParameters;
import com.xoozi.andromeda.net.ajax.AjaxRequestListener;

public class SetGreekAPI extends FocusLightBaseAPI{
	
	private static final String	INTERFACE_URL 	= "/set_greek/";
	
	private static final String GUID 			= "guid";
	private static final String GREEK			= "greek";
	
	
	public static void setGreek(String guid, String greek, AjaxRequestListener listener){
		
		
		AjaxParameters params = new AjaxParameters();
		
		params.add(GUID, guid);
		params.add(GREEK, greek);
		
		request(API_SERVER+INTERFACE_URL,params, HTTPMETHOD_GET,null,listener);
	}

}

