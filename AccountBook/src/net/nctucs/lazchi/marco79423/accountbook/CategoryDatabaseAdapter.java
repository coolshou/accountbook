package net.nctucs.lazchi.marco79423.accountbook;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;


public class CategoryDatabaseAdapter extends AbstractDatabaseAdapter 
{
	static final String TABLE = "categories";
	
	static final String CREATE_TABLE = 
		"CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
		CategoryData.ID + " INTEGER NOT NULL PRIMARY KEY," +
		CategoryData.CATEGORY + " TEXT," +
		CategoryData.ORDER_ID + " NUMERIC" +
		");";
	static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE;
	
	public CategoryDatabaseAdapter(Context context) 
	{
		super(context);
	}

	public long addCategory(String category, long orderId)
	{
		Assert.assertNotNull("_database is null", _database);
		
		ContentValues values = new ContentValues();
		values.put(CategoryData.CATEGORY, category);
		values.put(CategoryData.ORDER_ID, orderId);
		return _database.insert(TABLE, null, values);
	}

	public void deleteCategory(CategoryData data)
	{
		Assert.assertNotNull("_database is null", _database);
		
		_database.delete(TABLE, "_id = " + data.getId(), null);
	}
	
	/*
	public void exchangeCategoryOrder(long orderId1, long orderId2)
	{
		Cursor cursor = database.query(TABLE, CategoryData.ALL_FIELDS, CategoryData.ID + " = " + data.getId(), null, null, null ,null);
		cursor.moveToFirst();
		database.replace(TABLE, null, initialValues)
	}*/
	
	public String getCategoryNameByCategoryId(long categoryId)
	{
		Assert.assertNotNull("_database is null", _database);
		
		Cursor cursor = _database.query(
			TABLE, CategoryData.ALL_FIELDS, CategoryData.ID + " = ?", new String[]{ String.valueOf(categoryId)}, null, null, null, null);
		cursor.moveToFirst();
		return cursor.getString(1);
	}
	
	public long getCategoryIdByCategoryName(String categoryName)
	{
		Assert.assertNotNull("_database is null", _database);
		
		Cursor cursor = _database.query(
			TABLE, CategoryData.ALL_FIELDS, CategoryData.CATEGORY + " = ?", new String[]{categoryName}, null, null, null, null);
		
		cursor.moveToFirst();
		return cursor.getLong(0);
	}
	
	public List<String> getAllCategoryNames()
	{
		Assert.assertNotNull("_database is null", _database);
		
		List<String> categoryNames = new ArrayList<String>();
		
		Cursor cursor = _database.query(TABLE, CategoryData.ALL_FIELDS, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			categoryNames.add(cursor.getString(1));
			cursor.moveToNext();
		}
		
		cursor.close();
		return categoryNames;
	}
}
