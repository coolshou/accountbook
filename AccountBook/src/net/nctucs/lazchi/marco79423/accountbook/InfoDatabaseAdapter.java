package net.nctucs.lazchi.marco79423.accountbook;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;


public class InfoDatabaseAdapter extends AbstractDatabaseAdapter 
{
	static final String TABLE = "info";
	
	static final String CREATE_TABLE = 
		"CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
		InfoData.ID + " _id INTEGER NOT NULL PRIMARY KEY," +
		InfoData.APP_VERSION + " TEXT" + 
		");";
	static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE;
	
	public InfoDatabaseAdapter(Context context) 
	{
		super(context);
	}
	
	public void setAppVersion(String appVersion)
	{
		ContentValues values = new ContentValues();
		values.put(InfoData.ID, 0);
		values.put(InfoData.APP_VERSION, appVersion);
		_database.replace(TABLE, null, values);
	}
	
	public String getAppVersion()
	{
		Cursor cursor = _database.query(TABLE, InfoData.ALL_FIELDS, InfoData.ID + " = 0", null, null, null ,null);
		cursor.moveToFirst();
		return cursor.getString(1);
	}
}
