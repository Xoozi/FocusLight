package com.george.focuslight.panel;

import java.io.File;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.george.focuslight.R;
import com.george.focuslight.api.DownLoadFileAPI;
import com.george.focuslight.poppanel.IPopPanelAction4ObjectPanel;
import com.george.focuslight.poppanel.PopDropMenu;
import com.george.focuslight.util.PhotoTools;
import com.xoozi.andromeda.net.ajax.AjaxException;
import com.xoozi.andromeda.net.ajax.AjaxRequestListener;
import com.xoozi.andromeda.net.ajax.HttpManager.Responses;
import com.xoozi.andromeda.uicontroller.SelfInflaterPanel;
import com.xoozi.andromeda.utils.Utils;

public class PanelAddedStartPhotos extends SelfInflaterPanel implements OnClickListener, AjaxRequestListener, IPopPanelAction4ObjectPanel  {
	private IPanelAction _panelAction;
	private String 		_md5;
	private ImageView	_imgPhoto;
	
	private PopDropMenu					_popDropMenu;
	
	private boolean		_haveData;

	public PanelAddedStartPhotos(Context context, LinearLayout baseLayout, IPanelAction panelAction) {
		super(context, baseLayout);
		_panelAction = panelAction;
		_initWork();
	}
	
	public void setPhoto(String md5, String url){
		_md5 = md5;
		
		File thumb  = PhotoTools.thumbFromMD5(_context, _md5);
		
		_imgPhoto.setBackgroundDrawable(Drawable.createFromPath(thumb.getAbsolutePath()));
		
		//setVisibility(View.VISIBLE);
		
		if(!thumb.exists() && null!=url){
			DownLoadFileAPI.download(url, thumb, this);
		}
		
		_haveData = true;
	}
	
	public void	deletePhoto(){
		_imgPhoto.setBackgroundDrawable(null);
		//setVisibility(View.GONE);
		_haveData = false;
	}
	
	private void	_initWork(){
		initPanel(R.layout.panel_normal);
		
		TextView title = ((TextView)_baseLayout.findViewById(R.id.text_title));
		
		title.setText(_context.getResources().getString(R.string.label_start_photo));
		
		_imgPhoto = (ImageView)_baseLayout.findViewById(R.id.img_icon);
		
		_panelView.setOnClickListener(this);
		
		_popDropMenu = new PopDropMenu(_context,title,this,_baseLayout);
	}

	@Override
	public void onClick(View view) {
		
		_popMenu();
	}
	
	
	private void	_popMenu(){
		_popDropMenu.show();
	}
	
	
	private boolean  _confirmExit(){
		
		Resources res = _context.getResources();
		
		new AlertDialog.Builder(_context)  
		  
		.setTitle(res.getString(R.string.title_delete_start))  
  
		.setMessage("").setNegativeButton(res.getString(R.string.btn_cancel),  
		        new DialogInterface.OnClickListener() {  
  		        public void onClick(DialogInterface dialog, int which) {  
		            }  
		        })  
  		.setPositiveButton(res.getString(R.string.btn_delete),  
		        new DialogInterface.OnClickListener() {  
  
		            public void onClick(DialogInterface dialog, int whichButton) {  
		            	_panelAction.deleteStart();
		            }  
  
		        }).show();  
		
		return true;
	}

	@Override
	public void onComplete(Responses responses) {
		File thumb  = PhotoTools.thumbFromMD5(_context, _md5);
		_imgPhoto.setBackgroundDrawable(Drawable.createFromPath(thumb.getAbsolutePath()));
	}

	@Override
	public void onIOException(IOException e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(AjaxException e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdateProgress(long total, long current) {
		Utils.amLog("upload startPhoto progress current:"+current+", total:"+total);
	}

	@Override
	public void setObject() {
		_panelAction.setStart();
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
