package net.nctucs.lazchi.marco79423.accountbook;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class CategoryDataSource 
{
	private SQLiteDatabase database;
	private CategorySQLiteHelper dbHelper;
	
	public CategoryDataSource(Context context)
	{
		dbHelper = new CategorySQLiteHelper(context);
	}
	
	public void open() throws SQLException 
	{
		database = dbHelper.getWritableDatabase();
	}
	
	public void close()
	{
		dbHelper.close();
	}
	
	public void addCategory(String category, long orderId)
	{
		ContentValues values = new ContentValues();
		values.put(CategoryData.CATEGORY, category);
		values.put(CategoryData.ORDER_ID, orderId);
		database.insert(CategorySQLiteHelper.TABLE, null, values);
	}

	public void deleteCategory(CategoryData data)
	{
		database.delete(CategorySQLiteHelper.TABLE, "_id = " + data.getId(), null);
	}
	
	public void changeCategoryOrder(CategoryData data, long orderId)
	{
		/*
		Cursor cursor = database.query(CategorySQLiteHelper.TABLE, CategoryData.ALL_FIELDS, CategoryData.ID + " = " + data.getId(), null, null, null ,null);
		cursor.moveToFirst();
		database.replace(CategorySQLiteHelper.TABLE, null, initialValues)*/
	}
	
	public List<CategoryData> getAllCategories()
	{
		List<CategoryData> categories = new ArrayList<CategoryData>();
		
		Cursor cursor = database.query(CategorySQLiteHelper.TABLE, CategoryData.ALL_FIELDS, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			CategoryData data = new CategoryData(
				cursor.getLong(0),
				cursor.getString(1),
				cursor.getLong(2)
			);
			
			categories.add(data);
			cursor.moveToNext();
		}
		
		cursor.close();
		return categories;
	}
}
