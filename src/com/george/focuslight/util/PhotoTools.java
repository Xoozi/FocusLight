package com.george.focuslight.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.xoozi.andromeda.utils.MD5Tools;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;

public class PhotoTools {
	
	private static final int 	THUMB_HEIGHT 	= 60;	//缩略图像素高度
	private static final int	START_HEIGHT	= 768;
	private static final int	START_WIDTH		= 1024;
	private static final String EXT_NAME		= "jpg";
	private static final String THUMB_PRX		= "thumb_";
	
	private static final int 	COMPRESS_QUALITY= 100;
	
	public static File	thumbFromMD5(Context context, String md5){
		File appDir = AppUtils.getAppDir(context);
        return new File(appDir, THUMB_PRX+md5+"."+EXT_NAME);
	}
	
	
	
	public static File	photoFromMD5(Context context, String md5){
		File appDir = AppUtils.getAppDir(context);
        return new File(appDir, md5+"."+EXT_NAME);
	}
	
	public static String selectedPhotoForDesktop(Context context, Intent data){
		String md5 = null;
		
		Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
       

        Cursor cursor = context.getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String photoPath = cursor.getString(columnIndex);
        cursor.close();
       
        
        File photoFile = new File(photoPath);
        
        
        //选择桌面不对原图片做缩放，只是生成一个缩略图
        Bitmap	thumb=null;
        Bitmap	desktopBmp=null;
        FileOutputStream thumbStream = null;
        FileOutputStream desktopPhotoStream = null;
        
        //将缩略图和原图改成基于md5码的名字存在app目录
        try {
        	md5 = MD5Tools.getFileMD5String(photoFile);
   		
        	File destThumb = thumbFromMD5(context, md5);
        	File desktopPhoto = photoFromMD5(context, md5);
           
        	if(!destThumb.exists()){
        		//如果此缩略图还没创建过，将缩略图转换成JPG 并保存为文件
        		//生成一个缩略图
        		thumb = _getThumbnail(photoFile);
        		thumbStream = new FileOutputStream(destThumb);       
        		thumb.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, thumbStream); 
           }
        	
        	if(!desktopPhoto.exists()){
        		desktopBmp = _getDesktopBitmap(photoFile);
        		desktopPhotoStream = new FileOutputStream(desktopPhoto);
        		desktopBmp.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, desktopPhotoStream); 
        	}
           
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(null!=thumb)
				thumb.recycle();
			if(null!=desktopBmp){
				desktopBmp.recycle();
			}
			try{
				if(null!=thumbStream)
					thumbStream.close();
				if(null!=desktopPhotoStream)
					desktopPhotoStream.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        return md5;
	}
	
	
	public static String selectedPhotoForStart(Context context, Intent data){
		String md5 = null;
		Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
       

        Cursor cursor = context.getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String photoPath = cursor.getString(columnIndex);
        cursor.close();
       
        
        File photoFile = new File(photoPath);
        
        
        //选择开机画面需要对原图片做缩放，将原图片按fitheight缩放到1024*768
        
      //将缩略图和缩放后的原图改成基于md5码的名字存在app目录
        
        Bitmap	thumb=null;
        Bitmap	startPhoto=null;
        FileOutputStream thumbStream = null;
        FileOutputStream startPhotoStream = null;
        //将缩略图和原图改成基于md5码的名字存在app目录
        
        try {
        	md5 			= MD5Tools.getFileMD5String(photoFile);
        	File destThumb 		= thumbFromMD5(context, md5);
        	File startPhotoFile	= photoFromMD5(context, md5);
           
        	if(!destThumb.exists()){
        		//如果此缩略图还没创建过，将缩略图转换成JPG 并保存为文件
        		//生成一个缩略图
        		thumb = _getThumbnail(photoFile);
        		thumbStream = new FileOutputStream(destThumb);       
        		thumb.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, thumbStream); 
        	}
        	
        	if(!startPhotoFile.exists()){
        		startPhoto = _getStartPhoto(photoFile);
        		startPhotoStream = new FileOutputStream(startPhotoFile);
        		startPhoto.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, startPhotoStream); 
        	}
           
          
           //result = new Pair<String,File>(md5, photoFile);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(null!=thumb)
				thumb.recycle();
			if(null!=startPhoto)
				startPhoto.recycle();
			try{
				if(null!=thumbStream)
					thumbStream.close();
				
				if(null!=startPhotoStream)
					startPhotoStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        
        return md5;
	}
	
	
	/**
	 * 取得缩略图
	 * @param photoFile
	 * @return
	 */
	private	static Bitmap	_getThumbnail(File photoFile){
		
		return _getScaleImage(photoFile, THUMB_HEIGHT);
	}
	
	/**
	 * 取得缩略图
	 * @param photoFile
	 * @return
	 */
	private	static Bitmap	_getStartPhoto(File photoFile){
		
		return _getStartBitmap(photoFile, START_WIDTH,START_HEIGHT);
	}
	
	
	/**
	 * 按适合高度缩放创造新位图
	 * @param imageFile
	 * @param pixHeight
	 * @return
	 */
	private static Bitmap 	_getScaleImage(File imageFile, int pixHeight){
		BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options); //此时返回bm为空
        options.inJustDecodeBounds = false;
         //计算缩放比
        int be = (int)((float)options.outHeight / (float)pixHeight);
        if (be <= 0)
            be = 1;
        
        options.inSampleSize = be;
        //重新读入图片
        bitmap=BitmapFactory.decodeFile(imageFile.getAbsolutePath(),options);
        
        return bitmap;
	}
	
	/**
	 * 按适合和宽度高度缩放创造新位图
	 * @param imageFile
	 * @param pixHeight
	 * @return
	 */
	private static Bitmap 	_getStartBitmap(File imageFile,int pixWidth, int pixHeight){
		BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options); //此时返回bm为空
        options.inJustDecodeBounds = false;
         //计算缩放比
        int beh = (int)((float)options.outHeight / (float)pixHeight);
        if (beh <= 0)
            beh = 1;
        
        int bew = (int)((float)options.outWidth / (float)pixWidth);
        if (bew <= 0)
            bew = 1;
        
        options.inSampleSize = Math.max(beh, bew);
        
        Log.w("_getStartBitmap", "outHeight:"+options.outHeight+", outWidth"+
        options.outWidth+", inSampleSize:"+options.inSampleSize);
        //重新读入图片
        bitmap=BitmapFactory.decodeFile(imageFile.getAbsolutePath(),options);
        
        return bitmap;
	}
	
	/**
	 * 载入桌面图片的位图
	 * @param imageFile
	 * @return
	 */
	private static Bitmap 	_getDesktopBitmap(File imageFile){
		BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options); //此时返回bm为空
        options.inJustDecodeBounds = false;
      
        options.inSampleSize = 1;
        
        bitmap=BitmapFactory.decodeFile(imageFile.getAbsolutePath(),options);
        
        return bitmap;
	}
	
	

}
