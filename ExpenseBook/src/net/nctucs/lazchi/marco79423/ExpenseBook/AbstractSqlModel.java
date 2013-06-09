package net.nctucs.lazchi.marco79423.ExpenseBook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

class AbstractSqlModel
{
	protected SQLiteDatabase _database;

	private final DatabaseHelper _helper;

	public AbstractSqlModel(Context context)
	{
		_helper = new DatabaseHelper(context);
	}

	public void open()
	{
		_database = _helper.getWritableDatabase();
	}

	public void close()
	{
		_database.close();
	}
}
