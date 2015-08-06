package com.george.focuslight.ajaxobject;

import com.xoozi.andromeda.net.ajax.AjaxObject;

public class NormalResult implements AjaxObject {
	
	public int result;
	public String message;
	
	
	@Override
	public String toString() {
	
		return "errorCode:"+result+", msg:"+message;
	}
	
}
