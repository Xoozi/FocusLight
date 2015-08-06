package com.george.focuslight;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;


public class HTMLErrorActivity extends Activity {

	private WebView	_webView ;

	private static String	_HTMLData=null;
	
	public static void displayHTML(Context context,String html){
		_HTMLData = html;
		Intent intent = new Intent(context,HTMLErrorActivity.class);
		context.startActivity(intent);
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState){
		 super.onCreate(savedInstanceState);
	     setContentView(R.layout.activity_html_error);
	     
		_initWork();		
	}
	
	
	private void _initWork(){

		_webView	= (WebView) findViewById(R.id.webview);
		_webView.getSettings().setSupportZoom(true);
		_webView.getSettings().setBuiltInZoomControls(true);
		
		try
		{
			//_webView.getSettings().setDisplayZoomControls(false);
		}catch(Exception e)
		{
			
		}
		_webView.getSettings().setUseWideViewPort(true);
		_webView.setInitialScale(120);
		
		if(null!=_HTMLData){
			_webView.loadDataWithBaseURL(null,_HTMLData, "text/html", "UTF-8",null);
		}
		

	}

}
