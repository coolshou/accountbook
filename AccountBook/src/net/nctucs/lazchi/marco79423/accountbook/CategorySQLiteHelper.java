package net.nctucs.lazchi.marco79423.accountbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CategorySQLiteHelper extends SQLiteOpenHelper 
{
	public static final String TABLE = "categories";
	
	private static final String _DATABASE_NAME = "accountbook.db";
	private static final int _DATABASE_VERSION = 1;

	public CategorySQLiteHelper(Context context) 
	{
		super(context, _DATABASE_NAME, null, _DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database)
	{
		final String query = 
			"CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
			CategoryData.ID + " INTEGER NOT NULL PRIMARY KEY," +
			CategoryData.CATEGORY + " TEXT," +
			CategoryData.ORDER_ID + " NUMERIC" +
			");";
		database.execSQL(query);
		
		_insertDefaultCategories(database);
	}
	
	private void _insertDefaultCategories(SQLiteDatabase database)
	{
		//食
		String[] categories = {"食", "衣", "住", "行", "育", "樂"};
		for(String category : categories)
		{
			ContentValues values = new ContentValues();
			values.put(CategoryData.CATEGORY, category);
			database.insert(TABLE, null, values);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(CategorySQLiteHelper.class.getName(),
				"更新資料庫檔案，從版本" + oldVersion + "更新至"	+ newVersion + 
				"注意，這會清除資料庫檔案");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE);
		onCreate(db);
	}
}
