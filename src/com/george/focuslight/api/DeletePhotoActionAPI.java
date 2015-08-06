package com.george.focuslight.api;

import com.xoozi.andromeda.net.ajax.AjaxParameters;
import com.xoozi.andromeda.net.ajax.AjaxRequestListener;

public class DeletePhotoActionAPI extends FocusLightBaseAPI{
	
	private static final String	INTERFACE_URL 	= "/delete_action/";
	
	private static final String GUID 			= "guid";
	private static final String TYPE 			= "type";
	
	private static final String ACTION_TYPE_DESKTOP = "desktop";
	private static final String ACTION_TYPE_START = "start";
	
	
	public static void deleteAction(ActionType actionType, String guid,AjaxRequestListener listener){
		
		
		AjaxParameters params = new AjaxParameters();
		
		params.add(GUID, guid);
		
		if(ActionType.DESKTOP==actionType){
			params.add(TYPE, ACTION_TYPE_DESKTOP);
		}else{
			params.add(TYPE, ACTION_TYPE_START);
		}
		
		request(API_SERVER+INTERFACE_URL,params, HTTPMETHOD_GET,null,listener);
	}
	
	public	enum ActionType{DESKTOP, START};

}
