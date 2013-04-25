package net.nctucs.lazchi.marco79423.accountbook;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ExpenseDataSource 
{
	private SQLiteDatabase database;
	private ExpenseSQLiteHelper dbHelper;
	
	public ExpenseDataSource(Context context)
	{
		dbHelper = new ExpenseSQLiteHelper(context);
	}
	
	public void open() throws SQLException 
	{
		database = dbHelper.getWritableDatabase();
	}
	
	public void close()
	{
		dbHelper.close();
	}
	
	public void addExpense(byte[] picture, long spend, String date, long categoryId, String note)
	{
		ContentValues values = new ContentValues();
		values.put(ExpenseData.PICTURE, picture);
		values.put(ExpenseData.SPEND, spend);
		values.put(ExpenseData.DATE, date);
		values.put(ExpenseData.CATEGORY_ID, categoryId);
		values.put(ExpenseData.NOTE, note);
		database.insert(ExpenseSQLiteHelper.TABLE, null, values);
	}

	public void deleteExpense(ExpenseData data)
	{
		database.delete(ExpenseSQLiteHelper.TABLE, "_id = " + data.getId(), null);
	}
	
	public List<ExpenseData> getAllExpenses()
	{
		List<ExpenseData> expenses = new ArrayList<ExpenseData>();
		
		Cursor cursor = database.query(ExpenseSQLiteHelper.TABLE, ExpenseData.ALL_FIELDS, null, null, null, null, null);
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
