package net.nctucs.lazchi.marco79423.accountbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public abstract class AbstractDatabaseAdapter 
{
	protected SQLiteDatabase _database;
	
	private static final String _DATABASE_NAME = "accountbook.db";
	private static final int _DATABASE_VERSION = 2;
	
	private final Context _context;
	private DatabaseHelper _databaseHelper;
	
	public AbstractDatabaseAdapter(Context context)
	{
		_context = context;
		_databaseHelper = new DatabaseHelper(_context);
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper
	{

		public DatabaseHelper(Context context) 
		{
			super(context, _DATABASE_NAME, null, _DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase database) 
		{
			database.execSQL(ExpenseDatabaseAdapter.CREATE_TABLE);
			database.execSQL(CategoryDatabaseAdapter.CREATE_TABLE);
			database.execSQL(InfoDatabaseAdapter.CREATE_TABLE);
			
			//設定預設類別
			_insertDefaultCategories(database);
		}

		private void _insertDefaultCategories(SQLiteDatabase database)
		{
			//設定預設類別
			String[] categories = {"食", "衣", "住", "行", "育", "樂"};
			for(String category : categories)
			{
				ContentValues values = new ContentValues();
				values.put(CategoryData.CATEGORY, category);
				database.insert(CategoryDatabaseAdapter.TABLE, null, values);
			}
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) 
		{
			Log.w(AbstractDatabaseAdapter.class.getName(),
					"更新資料庫檔案，從版本" + oldVersion + "更新至"	+ newVersion + 
					"注意，這會清除資料庫檔案");
			database.execSQL(ExpenseDatabaseAdapter.DELETE_TABLE);
			database.execSQL(CategoryDatabaseAdapter.DELETE_TABLE);
			database.execSQL(InfoDatabaseAdapter.DELETE_TABLE);
			onCreate(database);
		}
	}
	
	public void open() throws SQLException 
	{
		_database = _databaseHelper.getWritableDatabase();
	}
	
	public void close()
	{
		_database.close();
	}
}
