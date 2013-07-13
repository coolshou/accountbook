package net.nctucs.lazchi.marco79423.ExpenseBook;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;


class ExpenseSqlModel extends AbstractSqlModel
{
	public ExpenseSqlModel(Context context)
	{
		super(context);
	}

	public long addExpense(byte[] pictureBytes, String spendString, String dateString, long categoryId, String note)
	{
		ContentValues values = _prepareContentValues(pictureBytes, spendString, dateString, categoryId, note);
		return _database.insert(Globals.ExpenseTable.TABLE, null, values);
	}

	public int editExpense(long id, byte[] pictureBytes, String spend, String dateString, long categoryId, String note)
	{
		ContentValues values = _prepareContentValues(pictureBytes, spend, dateString, categoryId, note);
		return _database.update(Globals.ExpenseTable.TABLE, values, Globals.ExpenseTable.ID + "=" + id, null);
	}

	private ContentValues _prepareContentValues(byte[] pictureBytes, String spendString, String dateString, long categoryId, String note)
	{
		ContentValues values = new ContentValues();

		if(pictureBytes != null)
			values.put(Globals.ExpenseTable.PICTURE_BYTES, pictureBytes);

		values.put(Globals.ExpenseTable.SPEND_STRING, spendString);

		if(dateString.length() != 0)
			values.put(Globals.ExpenseTable.DATE_STRING, dateString);

		values.put(Globals.ExpenseTable.CATEGORY_ID, categoryId);

		if(note.length() != 0)
			values.put(Globals.ExpenseTable.NOTE, note);

		return values;
	}

	public int removeExpense(long id)
	{
		return _database.delete(Globals.ExpenseTable.TABLE, Globals.ExpenseTable.ID + "=" + id, null);
	}

	public List<HashMap<String, Object>> getAllExpenses()
	{
		List<HashMap<String, Object>> expenses = new ArrayList<HashMap<String, Object>>();

		final String[] ALL_FIELDS = {
			Globals.ExpenseTable.ID,
			Globals.ExpenseTable.PICTURE_BYTES,
			Globals.ExpenseTable.SPEND_STRING,
			Globals.ExpenseTable.DATE_STRING,
			Globals.ExpenseTable.CATEGORY_ID,
			Globals.ExpenseTable.NOTE
		};

		Cursor cursor = _database.query(
			Globals.ExpenseTable.TABLE,
			ALL_FIELDS,
			null,
			null,
			null,
			null,
			Globals.ExpenseTable.DATE_STRING + " DESC, " + Globals.ExpenseTable.ID + " DESC"
		);

		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			HashMap<String,Object> expense = new HashMap<String,Object>();
			expense.put(Globals.ExpenseTable.ID, cursor.getLong(0));
			expense.put(Globals.ExpenseTable.PICTURE_BYTES, cursor.getBlob(1));
			expense.put(Globals.ExpenseTable.SPEND_STRING, cursor.getString(2));
			expense.put(Globals.ExpenseTable.DATE_STRING, cursor.getString(3));
			expense.put(Globals.ExpenseTable.CATEGORY_ID, cursor.getLong(4));
			expense.put(Globals.ExpenseTable.NOTE, cursor.getString(5));

			expenses.add(expense);
			cursor.moveToNext();
		}

		cursor.close();
		return expenses;
	}

	public long getSumOfMonthlyExpenses()
	{
		long sum = 0;

		final String[] FIELDS = {
				Globals.ExpenseTable.SPEND_STRING,
				Globals.ExpenseTable.DATE_STRING,
		};

		Calendar calendar = Calendar.getInstance();
		int currentYear = calendar.get(Calendar.YEAR);
		int currentMonth = calendar.get(Calendar.MONTH);

		Cursor cursor = _database.query(Globals.ExpenseTable.TABLE, FIELDS, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			try
			{
				SimpleDateFormat formatter = new SimpleDateFormat(Globals.DATE_FORMAT);
				Date date = formatter.parse(cursor.getString(1));
				calendar.setTime(date);
				if(currentYear == calendar.get(Calendar.YEAR) && currentMonth == calendar.get(Calendar.MONTH))
					sum += cursor.getLong(0);
			}
			catch(ParseException e)
			{
				e.printStackTrace();
			}
			cursor.moveToNext();
		}

		cursor.close();
		return sum;
	}
}
