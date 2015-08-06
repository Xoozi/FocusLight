package com.george.focuslight.panel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.george.focuslight.R;
import com.george.focuslight.poppanel.IPopPanelAction4ObjectPanel;
import com.george.focuslight.poppanel.PopDropMenu;
import com.xoozi.andromeda.uicontroller.SelfInflaterPanel;

public class PanelAddedGreekText extends SelfInflaterPanel implements OnClickListener, IPopPanelAction4ObjectPanel {
	private IPanelAction _panelAction;
	private TextView	_textGreek;
	private ImageView	_imgEdit;
	private String		_greek;
	
	private PopDropMenu					_popDropMenu;
	
	private boolean		_haveData;

	public PanelAddedGreekText(Context context, LinearLayout baseLayout,IPanelAction panelAction) {
		super(context, baseLayout);
		_panelAction = panelAction;
		_initWork();
		
	}
	
	public	void	setGreek(String greek){
		_greek = greek;
		_textGreek.setText(_greek);
		
		//setVisibility(View.VISIBLE);
		_haveData = true;
	}
	
	public void	deleteGreek(){
		_textGreek.setText("");
		//setVisibility(View.GONE);
		_haveData = false;
	}
	
	private void	_initWork(){
		initPanel(R.layout.panel_normal);
		
		TextView title = ((TextView)_baseLayout.findViewById(R.id.text_title));
		
		title.setText(_context.getResources().getString(R.string.label_greek_text));
		
		_imgEdit = (ImageView)_baseLayout.findViewById(R.id.img_icon);
		
		_imgEdit.setBackgroundDrawable(_context.getResources().getDrawable(android.R.drawable.ic_menu_edit));
		
		_textGreek = (TextView)_baseLayout.findViewById(R.id.text_label);
		
		
		_panelView.setOnClickListener(this);
		
		_popDropMenu = new PopDropMenu(_context,title,this,_baseLayout);
	}

	@Override
	public void onClick(View v) {
		
		_popMenu();
	}
	
	private void	_popMenu(){
		_popDropMenu.show();
	}
	
	
	private boolean  _confirmExit(){
		
		Resources res = _context.getResources();
		
		new AlertDialog.Builder(_context)  
		  
		.setTitle(res.getString(R.string.title_delete_greek))  
  
		.setMessage("").setNegativeButton(res.getString(R.string.btn_cancel),  
		        new DialogInterface.OnClickListener() {  
  		        public void onClick(DialogInterface dialog, int which) {  
		            }  
		        })  
  		.setPositiveButton(res.getString(R.string.btn_delete),  
		        new DialogInterface.OnClickListener() {  
  
		            public void onClick(DialogInterface dialog, int whichButton) {  
		            	_panelAction.deleteGreek();
		            }  
  
		        }).show();  
		
		return true;
	}

	@Override
	public void setObject() {
		_panelAction.setGreek();
	}

	@Override
	public void deleteObject() {
		_confirmExit();
	}

	@Override
	public boolean canDelete() {
		
		return _haveData;
	}

}
