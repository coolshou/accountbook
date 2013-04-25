package net.nctucs.lazchi.marco79423.accountbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class InfoDataSource 
{
	private SQLiteDatabase database;
	private InfoSQLiteHelper dbHelper;
	
	public InfoDataSource(Context context)
	{
		dbHelper = new InfoSQLiteHelper(context);
	}
	
	public void open() throws SQLException 
	{
		database = dbHelper.getWritableDatabase();
	}
	
	public void close()
	{
		dbHelper.close();
	}
		
	public void setAppVersion(String appVersion)
	{
		ContentValues values = new ContentValues();
		values.put(InfoData.ID, 0);
		values.put(InfoData.APP_VERSION, appVersion);
		database.replace(InfoSQLiteHelper.TABLE, null, values);
	}
	
	public String getAppVersion()
	{
		Cursor cursor = database.query(InfoSQLiteHelper.TABLE, InfoData.ALL_FIELDS, InfoData.ID + " = 0", null, null, null ,null);
		cursor.moveToFirst();
		return cursor.getString(1);
	}
}
