package com.george.focuslight.data;

import java.util.ArrayList;
import java.util.List;



import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.preference.PreferenceManager;

public class AppProfile {
	
	public	static final String  PREFERENCE_DEFAULT_ACTIVE_USER			= "anonymous";
	
	private static final String  PREFERENCE_KEY_ACTIVE_USER				= "ActiveUser";
	
	private	static final String	DB_NAME = "profile";
	
	private	Context					_context;
	
	private ProfileDatabaseHelper	_dbHelper;
	
	public AppProfile(Context context){
		_context = context;
		
		_dbHelper = new ProfileDatabaseHelper(
				_context,
				DB_NAME,
				null,
				1);
	}
	
	
	/**
	 * 获取激活的用户guid
	 * @return
	 */
	public static	String	getActiveUser(Context context){
		SharedPreferences	preference = PreferenceManager.getDefaultSharedPreferences(context);
		
		return preference.getString(PREFERENCE_KEY_ACTIVE_USER, PREFERENCE_DEFAULT_ACTIVE_USER); 
	}
	
	
	/**
	 * 设置激活的用户guid
	 * @param guid
	 */
	public static	void	setActiveUser(Context context,String guid){
		SharedPreferences	preference = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor	editor	= preference.edit();
		editor.putString(PREFERENCE_KEY_ACTIVE_USER, guid);
		editor.commit();
	}
	
	public	void	recycle(){
		_dbHelper.close();
	}
	
	
	
	
	
	/**
	 * 获取profile中已经保存的用户guid
	 * @return
	 */
	public	List<String>	getUsersInProfile(){
		SQLiteDatabase db = _dbHelper.getReadableDatabase();
		List<String> result = new ArrayList<String>();
		
		Cursor	data = db.query(UserColumns.TABLE_NAME, null, null, null, null, null, null);
		
		while(data.moveToNext()){
			String guid = data.getString(data.getColumnIndexOrThrow(UserColumns.GUID));
			result.add(guid);
		}
		
		data.close();
		db.close();
		
		return result;
	}
	
	
	/**
	 * 将新激活的用户guid添加到profile
	 * @param guid
	 */
	public	void	addUserToProfile(String guid){
		
		SQLiteDatabase db = _dbHelper.getWritableDatabase();
		
		ContentValues	values = new ContentValues();
		values.put(UserColumns.GUID, guid.toString());
		
		db.insert(UserColumns.TABLE_NAME, null, values);
		
		
		db.close();
	}
	
	/**
	 * 清理
	 */
	public	void	cleanProfile(){
		SQLiteDatabase db = _dbHelper.getWritableDatabase();
		
		db.delete(UserColumns.TABLE_NAME, null, null);
		
		db.close();
	}
	
	
	/**
	 * 原型简单点，只记录guid
	 * @author xoozi
	 *
	 */
	private	interface UserColumns{
		public  static final String	TABLE_NAME = "Users";
		
		public	static final String _ID		= "_id";
		public	static final String GUID	= "guid";
		
		
		public static final String	CREATE_SQL	= "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +
				"("+
				" "+_ID+			" INTEGER PRIMARY KEY," +
				" "+GUID+			" TEXT UNIQUE NOT NULL" +
				");";
	}
	
	
	private final class ProfileDatabaseHelper extends SQLiteOpenHelper{
		
		public ProfileDatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(UserColumns.CREATE_SQL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
		}
		
	}

}
