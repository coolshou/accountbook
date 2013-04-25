package net.nctucs.lazchi.marco79423.accountbook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class InfoSQLiteHelper extends SQLiteOpenHelper 
{
	public static final String TABLE = "info";
		
	private static final String _DATABASE_NAME = "accountbook.db";
	private static final int _DATABASE_VERSION = 1;

	public InfoSQLiteHelper(Context context) 
	{
		super(context, _DATABASE_NAME, null, _DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) 
	{
		final String query = 
			"CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
			InfoData.ID + " _id INTEGER NOT NULL PRIMARY KEY," +
			InfoData.APP_VERSION + " TEXT" + 
			");";
		database.execSQL(query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(InfoSQLiteHelper.class.getName(),
				"更新資料庫檔案，從版本" + oldVersion + "更新至"	+ newVersion + 
				"注意，這會清除資料庫檔案");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE);
		onCreate(db);
	}
}
