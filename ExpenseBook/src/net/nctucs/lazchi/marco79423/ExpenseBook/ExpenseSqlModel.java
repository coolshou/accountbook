package net.nctucs.lazchi.marco79423.ExpenseBook;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.widget.Toast;

import android.util.Log;

public class ExpenseSqlModel extends AbstractSqlModel
{
	public ExpenseSqlModel(Context context)
	{
		super(context);
	}

	public long addExpense(Bitmap picture, long spend, Date date, long categoryId, String note)
	{
		ByteArrayOutputStream pictureOutStream = new ByteArrayOutputStream();
		picture.compress(Bitmap.CompressFormat.JPEG, 100, pictureOutStream);

		String dateString = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/M/d");
		dateString = formatter.format(date);

		ContentValues values = new ContentValues();
		values.put(Globals.ExpenseTable.PICTURE, pictureOutStream.toByteArray());
		values.put(Globals.ExpenseTable.SPEND, spend);
		if(dateString.length() != 0)
			values.put(Globals.ExpenseTable.DATE, dateString);
		values.put(Globals.ExpenseTable.CATEGORY_ID, categoryId);
		if(note.length() != 0)
			values.put(Globals.ExpenseTable.NOTE, note);
		return _database.insert(Globals.ExpenseTable.TABLE, null, values);
	}

	public long addExpense(Bitmap picture)
	{
		return addExpense(picture, 0, new Date(), 1, new String());
	}

	public List<ContentValues> getAllExpenses()
	{
		List<ContentValues> expenses = new ArrayList<ContentValues>();

		final String[] ALL_FIELDS = {
			Globals.ExpenseTable.ID,
			Globals.ExpenseTable.PICTURE,
			Globals.ExpenseTable.SPEND,
			Globals.ExpenseTable.DATE,
			Globals.ExpenseTable.CATEGORY_ID,
			Globals.ExpenseTable.NOTE
		};

		Cursor cursor = _database.query(Globals.ExpenseTable.TABLE, ALL_FIELDS, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			ContentValues values = new ContentValues();
			values.put(Globals.ExpenseTable.ID, cursor.getLong(0));
			values.put(Globals.ExpenseTable.PICTURE, cursor.getBlob(1));
			values.put(Globals.ExpenseTable.SPEND, cursor.getLong(2));
			values.put(Globals.ExpenseTable.DATE, cursor.getString(3));
			values.put(Globals.ExpenseTable.CATEGORY_ID, cursor.getLong(4));
			values.put(Globals.ExpenseTable.NOTE, cursor.getString(5));

			expenses.add(values);
			cursor.moveToNext();
		}

		cursor.close();
		return expenses;
	}
}
