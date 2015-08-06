package com.george.focuslight.tab;

import java.io.File;

import com.george.focuslight.panel.IPanelAudioAction;

public interface ITabPageAudioImp {

	public	void	playback(IPanelAudioAction playingPanel,File audioFile);
	public 	void	cancelPlayback();
}
