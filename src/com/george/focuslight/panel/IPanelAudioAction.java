package com.george.focuslight.panel;

public interface IPanelAudioAction {
	public  String	getUrl();
	public	String	getType();
	public  void  playbackCompleted();
	public  void  playbackCanceled();
}
