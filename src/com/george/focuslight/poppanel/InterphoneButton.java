package com.george.focuslight.poppanel;

import com.george.focuslight.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;


public class InterphoneButton extends Button {
	
	
	private	boolean						_pressed;
	private InterphoneButtonListener	_interphoneBtnListener;
	private String						_normalText;
	private String						_pressedText;
	
	public InterphoneButton(Context context) {
		super(context);
		_initWork();
	}

	public InterphoneButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		_initWork();
	}

	public InterphoneButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		_initWork();
	}

	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:  
			_pressed = true;
			setBackgroundDrawable(this.getContext().getResources().getDrawable(R.drawable.shape_pop_btn_field_press));
			setText(_pressedText);
	
			if(null!=_interphoneBtnListener)
				_interphoneBtnListener.onPressed();
			break;
		case MotionEvent.ACTION_UP:
			_pressed = false;
			setBackgroundDrawable(this.getContext().getResources().getDrawable(R.drawable.shape_pop_btn_field_normal));
			setText(_normalText);
			
			if(null!=_interphoneBtnListener)
				_interphoneBtnListener.onReleased();
			break;
		default:
			break;
		}
		return true;
	}
	
	public	void	setBtnText(String normalText, String pressedText){
		_normalText = normalText;
		_pressedText= pressedText;
		setText(_normalText);
	}
	
	public	void	setInterphoneButtonListener(InterphoneButtonListener interphoneBtnListener){
		_interphoneBtnListener	= interphoneBtnListener;
	}

	
	
	private void	_initWork(){

		_pressed = false;
	}
	
	
	public interface	InterphoneButtonListener{
		public	void	onPressed();
		public	void	onReleased();
	}
	


}
