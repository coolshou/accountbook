package net.nctucs.lazchi.marco79423.ExpenseBook;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;


/**
 * Created by Marco on 2013/6/1.
 */
public class CategorySqlModel extends AbstractSqlModel
{
	public CategorySqlModel(Context context)
	{
		super(context);
	}

	public List<String> addDefaultCategories()
	{
		//設定預設類別
		String[] categories = {"食", "衣", "住", "行", "育", "樂"};
		for(String category : categories)
			addCategory(category, 0);
		return getAllCategoryNames();
	}

	public long addCategory(String category, long orderId)
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


}
