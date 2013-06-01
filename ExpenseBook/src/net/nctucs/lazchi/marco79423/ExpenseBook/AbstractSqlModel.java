package net.nctucs.lazchi.marco79423.ExpenseBook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Marco on 2013/6/1.
 */
public class AbstractSqlModel
{
	protected SQLiteDatabase _database;

	private DatabaseHelper _helper;

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
