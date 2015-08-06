package com.george.focuslight.poppanel;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.george.focuslight.R;
import com.xoozi.andromeda.uicontroller.PopPanelBase;
import com.xoozi.andromeda.uicontroller.PopPanelBase.PopMode;

public class PopGreekEdit extends PopPanelBase implements OnClickListener{

	private EditText						_editGreek;
	private IPopPanelAction4Share					_popPanelAction;//activity给弹出窗口的回调

	public PopGreekEdit(Context context, View rootPanel, IPopPanelAction4Share popPanelAction) {
		super(context, rootPanel, PopPanelBase.PopMode.AT_LOCATION, false);
		_popPanelAction = popPanelAction;
		
		_initWork();
	}

	@Override
	public void onClick(View view) {
		if(R.id.btn_edit_ok==view.getId()){
			_hide();
			_popPanelAction.greekEditEnd(_editGreek.getText().toString());
		}else if(R.id.btn_edit_cancel==view.getId()){
			_hide();
		}
	}
	
	protected void	_show(){
		
		//检查是否需要初始化
		if(null==_popMenu){
			_popMenu = _initPopView();
		}
		
		if(PopMode.DROP_DOWN == _popMode)
			_popMenu.showAsDropDown(_rootPanel);
		else
			_popMenu.showAtLocation(_rootPanel, Gravity.CENTER, 0,0);
	}
	
	public	void	show(){
		_show();
	}
	
	public	void	editGreek(String greek){
		_editGreek.setText(greek);
		_show();
	}
	
	
	@Override
	protected void _initWork() {
		_basePanel = _layoutInflater.inflate(R.layout.pop_greek_edit, null);
		
		_editGreek = (EditText) _basePanel.findViewById(R.id.edit_greek);
		
		_basePanel.findViewById(R.id.btn_edit_ok).setOnClickListener(this);
		_basePanel.findViewById(R.id.btn_edit_cancel).setOnClickListener(this);
	}

}
