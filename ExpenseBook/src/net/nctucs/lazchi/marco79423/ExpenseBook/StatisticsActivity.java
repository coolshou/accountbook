package net.nctucs.lazchi.marco79423.ExpenseBook;

import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Marco on 2013/6/1.
 */
public class StatisticsActivity extends Activity
{
	private ListView _expenseListView;

	private ExpenseSqlModel _expenseSqlModel;
	private CategorySqlModel _categorySqlModel;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistics);

		_expenseListView = (ListView) findViewById(R.id.statistics_list_expense);

		_expenseSqlModel = new ExpenseSqlModel(this);
		_categorySqlModel = new CategorySqlModel(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		_expenseSqlModel.open();
		_categorySqlModel.open();

		_setExpenseListView();
	}

	@Override
	protected void onPause()
	{
		super.onPause();

		_expenseSqlModel.close();
		_categorySqlModel.close();
	}

	void _setExpenseListView()
	{
		ArrayList<HashMap<String, Object>> expenses = new ArrayList<HashMap<String, Object>>();
		List<ContentValues> expenseValues = _expenseSqlModel.getAllExpenses();

		for(ContentValues expenseValue: expenseValues)
		{
			HashMap<String,Object> expense = new HashMap<String,Object>();

			byte [] bytes = expenseValue.getAsByteArray(Globals.ExpenseTable.PICTURE);
			Bitmap picture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

			final long spend = expenseValue.getAsLong(Globals.ExpenseTable.SPEND);
			final String date = expenseValue.getAsString(Globals.ExpenseTable.DATE);
			final String category = _categorySqlModel.getCategoryName(expenseValue.getAsLong(Globals.CategoryTable.ID));

			expense.put("picture", picture);
			expense.put("spend", String.valueOf(spend));
			expense.put("date", date);
			expense.put("category", category);
			expenses.add(expense);
		}

		SimpleAdapter adapter = new SimpleAdapter(
			this,
			expenses,
			R.layout.statistics_item_expense,
			new String[] { "picture", "spend", "date", "category"},
			new int[] {
				R.id.statistics_item_view_picture,
				R.id.statistics_item_spend,
				R.id.statistics_item_view_date,
				R.id.statistics_item_view_category
			}
		);

		_expenseListView.setAdapter(adapter);
	}
}