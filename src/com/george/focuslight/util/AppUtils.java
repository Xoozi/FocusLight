package com.george.focuslight.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;

import com.george.focuslight.R;

public class AppUtils {
	private static final int IO_STEP = 1024;

	public static File getAppDir(Context context){
		File rootDir = Environment.getExternalStorageDirectory();
    	//在数据目录中找应用目录， 如果不存在，就建立
    	File appDir = new File(rootDir, 
    			context.getResources().getString(R.string.app_name));
		if(!appDir.exists())
			appDir.mkdirs();
		
		return appDir;
	}
	
	public static void dumpFile2Stream(File file,ByteArrayOutputStream  stream) throws IOException
	{
		FileInputStream		sourceStream	= null;
		long 				fileSize 		= 0;
		long 				readedBytes 	= 0;
		
		byte[] 	cache			  = new byte[IO_STEP];
		
		try
		{
			sourceStream	= new FileInputStream(file);
			
			fileSize							= sourceStream.getChannel().size();

			while(readedBytes<fileSize)
			{
				int readCnt	= sourceStream.read(cache, 0, IO_STEP);
				
				if(readCnt<0)
					break;
				stream.write(cache, 0, readCnt);
				readedBytes+=readCnt;
			}	
			
		}
		catch(IOException e)
		{
			throw e;
		}
		finally
		{
			if(null!=sourceStream)
				sourceStream.close();
		}
	
	}
	
	
	public static void copyFile(File sourceFile, File destFile) throws IOException{
		FileInputStream		sourceStream	= null;
		FileOutputStream 	destStream		= null;
		long 				fileSize 		= 0;
		long 				readedBytes 	= 0;
		
		byte[] 	cache			  = new byte[IO_STEP];
		
		try
		{
			sourceStream	= new FileInputStream(sourceFile);
			destStream		= new FileOutputStream(destFile);
			
			fileSize							= sourceStream.getChannel().size();
			/*if(null!=_progressReporter)
				_progressReporter.start(fileSize);*/
			while(readedBytes<fileSize)
			{
				int readCnt	= sourceStream.read(cache, 0, IO_STEP);
				
				if(readCnt<0)
					break;
				destStream.write(cache, 0, readCnt);
				readedBytes+=readCnt;
				
			}	
			
			
		}
		catch(IOException e)
		{
			throw e;
		}
		finally
		{
			if(null!=sourceStream)
				sourceStream.close();
			if(null!=destStream)
				destStream.close();
		}
	}
}
