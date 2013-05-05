package net.nctucs.lazchi.marco79423.accountbook;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;


public class ExpenseDatabaseAdapter extends AbstractDatabaseAdapter 
{
	static final String TABLE = "expenses";
	
	static final String CREATE_TABLE = 
		"CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
		ExpenseData.ID + " INTEGER NOT NULL PRIMARY KEY," +
		ExpenseData.PICTURE + " BLOB," +
		ExpenseData.SPEND + " NUMERIC," +
		ExpenseData.DATE + " DATE," +
		ExpenseData.CATEGORY_ID + " NUMERIC," +
		ExpenseData.NOTE + " TEXT" +
		");";
	static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE;
	
	public ExpenseDatabaseAdapter(Context context) 
	{
		super(context);
	}

	public long addExpense(byte[] picture, long spend, String date, long categoryId, String note)
	{
		ContentValues values = new ContentValues();
		values.put(ExpenseData.PICTURE, picture);
		values.put(ExpenseData.SPEND, spend);
		values.put(ExpenseData.DATE, date);
		values.put(ExpenseData.CATEGORY_ID, categoryId);
		values.put(ExpenseData.NOTE, note);
		return _database.insert(TABLE, null, values);
	}

	public void deleteExpense(ExpenseData data)
	{
		_database.delete(TABLE, "_id = " + data.getId(), null);
	}
	
	public List<ExpenseData> getAllExpenses()
	{
		List<ExpenseData> expenses = new ArrayList<ExpenseData>();
		
		Cursor cursor = _database.query(TABLE, ExpenseData.ALL_FIELDS, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			ExpenseData data = new ExpenseData(
				cursor.getLong(0),
				cursor.getBlob(1),
				cursor.getLong(2),
				cursor.getString(3),
				cursor.getLong(4),
				cursor.getString(5)
			);
			expenses.add(data);
			cursor.moveToNext();
		}
		
		cursor.close();
		return expenses;
	}
}
