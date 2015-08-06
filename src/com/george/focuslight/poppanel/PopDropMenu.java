package com.george.focuslight.poppanel;


import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.PopupWindow;

import com.george.focuslight.R;
import com.xoozi.andromeda.uicontroller.PopPanelBase;

public class PopDropMenu extends PopPanelBase implements OnClickListener {
	
	private IPopPanelAction4ObjectPanel				_popPanelAction;//父控件给弹出窗口的回调
	private View									_rootParent;
	private View									_deleteBtn;

	public PopDropMenu(Context context, View rootPanel,IPopPanelAction4ObjectPanel popPanelAction, View rootParent) {
		super(context, rootPanel, PopPanelBase.PopMode.AT_LOCATION, true);
		
		_popPanelAction = popPanelAction;
		_rootParent		= rootParent;
		
		_initWork();
	}


	
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.btn_set:
			_popPanelAction.setObject();
			_hide();
			break;
			
		case R.id.btn_delete:
			_popPanelAction.deleteObject();
			_hide();
			break;
		}
	}

	
	@Override
	protected void _initWork() {
		_basePanel = _layoutInflater.inflate(R.layout.pop_panel_menu, null);
		
		_basePanel.findViewById(R.id.btn_set).setOnClickListener(this);
		
		_deleteBtn = _basePanel.findViewById(R.id.btn_delete);
		_deleteBtn.setOnClickListener(this);
	}
	
	protected PopupWindow	_initPopView(){
		
		PopupWindow	result = null;
		
		int[] rootLocation = new int[2];  
		_rootPanel.getLocationOnScreen(rootLocation);  
		
		int[]   parentLocation = new int[2];
		_rootParent.getLocationOnScreen(parentLocation);
		
		int rightSideOfRoot = rootLocation[0] + _rootPanel.getWidth();
		int rightSideOfParent = parentLocation[0] + _rootParent.getWidth();
		
		int menuWidth = rightSideOfParent - rightSideOfRoot;
		  
        result = new PopupWindow(_basePanel, menuWidth,  
                ViewGroup.LayoutParams.WRAP_CONTENT, true);  
        result.setFocusable(true);  
        result.setOutsideTouchable(true);   
        
        //之所以这样做，是因为对应PopWindow，setOutsideTouchable这些设置不管用
        //只要设置了backgroundDrawable，就支持域外点击
        //没有设置backgroundDrawable，就是模态的
        //可能是官方的bug，先这样用着吧
        if(_alowOutTouch){
        	result.setBackgroundDrawable(_context.getResources().getDrawable(R.drawable.shape_pop_panel));
        }
        
        result.setOnDismissListener(this);
		return result;
	}
	
	protected void	_show(){
		
		//检查是否需要初始化
		if(null==_popMenu){
			_popMenu = _initPopView();
		}
		

		int[] location = new int[2];  
		_rootPanel.getLocationOnScreen(location);  
	     _popMenu.showAtLocation(_rootPanel, Gravity.NO_GRAVITY, location[0]+_rootPanel.getWidth(), location[1]-_popMenu.getHeight()); 
	
	     if(_popPanelAction.canDelete()){
	    	 _deleteBtn.setVisibility(View.VISIBLE);
	     }else{
	    	 _deleteBtn.setVisibility(View.GONE);
	     }
	}
	
	public	void	show(){
		_show();
	}
	
	




	

}
