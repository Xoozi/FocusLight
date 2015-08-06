package com.george.focuslight.ajaxobject;

/**
 * 服务器端返回错误码
 * @author xoozi
 *
 */
public interface ResultCode {
	
	public static final int	RESULT_SUC 				= 0;			//成功返回
	public static final int	RESULT_REQUEST_ERROR	= 2001;			//请求方法错误
	public static final int	RESULT_USER_NOT_EXIST 	= 2002;			//用户不存在
	public static final int	RESULT_FORM_INVALID 	= 2003;			//表单非法
	public static final int	RESULT_USER_NOT_ACTIVE 	= 2004;			//用户未激活
	
}
