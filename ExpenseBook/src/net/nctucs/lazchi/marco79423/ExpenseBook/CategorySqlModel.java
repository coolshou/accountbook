package net.nctucs.lazchi.marco79423.ExpenseBook;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class CategorySqlModel extends AbstractSqlModel
{
	public CategorySqlModel(Context context)
	{
		super(context);
	}

	@Override
	public void open()
	{
		super.open();
		if(getAllCategoryNames().isEmpty())
			_addDefaultCategories();
	}

	long addCategory(String category, long orderId)
	{
		if(!_database.isOpen())
			return -1;

		ContentValues values = new ContentValues();
		values.put(Globals.CategoryTable.CATEGORY, category);
		values.put(Globals.CategoryTable.ORDER_ID, orderId);
		return _database.insert(Globals.CategoryTable.TABLE, null, values);
	}

	public long getCategoryId(String categoryName)
	{
		Cursor cursor = _database.query(
			Globals.CategoryTable.TABLE,
			new String[]{Globals.CategoryTable.ID},
			Globals.CategoryTable.CATEGORY + " = ?",
			new String[]{categoryName},
			null,
			null,
			null,
			null
		);

		cursor.moveToFirst();
		return cursor.getLong(0);
	}

	public String getCategoryName(long categoryId)
	{
		Cursor cursor = _database.query(
				Globals.CategoryTable.TABLE,
				new String[]{Globals.CategoryTable.CATEGORY},
				Globals.CategoryTable.ID + " = ?",
				new String[]{String.valueOf(categoryId)},
				null,
				null,
				null,
				null
		);
		cursor.moveToFirst();
		return cursor.getString(0);
	}

	public List<String> getAllCategoryNames()
	{
		List<String> categoryNames = new ArrayList<String>();

		Cursor cursor = _database.query(Globals.CategoryTable.TABLE, new String[]{"category"}, null, null, null, null, null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast())
		{
			categoryNames.add(cursor.getString(0));
			cursor.moveToNext();
		}

		cursor.close();
		return categoryNames;
	}

	List<String> _addDefaultCategories()
	{
		//設定預設類別
		String[] categories = {"未分類", "飲食", "衣物", "住宿", "行動", "教育", "娛樂"};
		for(String category : categories)
			addCategory(category, 0);
		return getAllCategoryNames();
	}
}
