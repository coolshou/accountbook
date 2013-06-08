package net.nctucs.lazchi.marco79423.ExpenseBook;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.widget.AdapterView.*;

/**
 * Created by Marco on 2013/6/1.
 */
public class StatisticsActivity extends Activity implements View.OnClickListener, OnItemClickListener
{
	private ListView _expenseListView;
	private Button _editButton;
	private Button _deleteButton;

	private ExpenseSqlModel _expenseSqlModel;
	private CategorySqlModel _categorySqlModel;

	private View _selectedView;
	private int _selectedPosition = -1;
	SimpleAdapter _dataAdapter;

	ArrayList<HashMap<String, Object>> _expenses;


	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistics);

		_expenseListView = (ListView) findViewById(R.id.statistics_list_expense);
		_editButton = (Button) findViewById(R.id.statistics_button_edit);
		_deleteButton = (Button) findViewById(R.id.statistics_button_delete);

		_expenseListView.setOnItemClickListener(this);
		_editButton.setOnClickListener(this);
		_deleteButton.setOnClickListener(this);

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

	/*
	 * 事件
	 */

	@Override
	public void onClick(View view)
	{
		switch(view.getId())
		{
			case R.id.statistics_button_edit: _onEditButtonClicked(); break;
			case R.id.statistics_button_delete: _onDeleteButtonClicked(); break;
		}
	}

	private void _onEditButtonClicked()
	{
		Resources resources = getResources();
		if(_selectedPosition == -1)
		{
			Toast.makeText(this, resources.getString(R.string.message_select_item_first), Toast.LENGTH_LONG).show();
			return;
		}

		HashMap<String, Object> expense = _expenses.get(_selectedPosition);

		//移動到統計頁面
		Intent intent = new Intent();
		intent.putExtra("id", (Long)expense.get("id"));
		intent.putExtra("pictureBytes", (byte[])expense.get("pictureBytes"));
		intent.putExtra("spend", (Long)expense.get("spend"));
		intent.putExtra("date", (String)expense.get("date"));
		intent.putExtra("category", (String)expense.get("category"));
		intent.putExtra("note", (String)expense.get("note"));

		intent.setClass(this, ExpenseActivity.class);
		startActivity(intent);

		finish();
	}

	private void _onDeleteButtonClicked()
	{
		Resources resources = getResources();
		if(_selectedPosition == -1)
		{
			Toast.makeText(this, resources.getString(R.string.message_select_item_first), Toast.LENGTH_LONG).show();
			return;
		}

		long id = (Long)_expenses.get(_selectedPosition).get("id");
		if(_expenseSqlModel.removeExpense(id) == 0)
		{
			Toast.makeText(this, resources.getString(R.string.message_delete_item_failed), Toast.LENGTH_LONG).show();
			return;
		}
		_expenses.remove(_selectedPosition);
		_dataAdapter.notifyDataSetChanged();
		_selectedPosition = -1;

		Toast.makeText(this, resources.getString(R.string.message_delete_item_successful), Toast.LENGTH_LONG).show();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
	{
		if(_selectedView == view)
		{
			_selectedView.setBackgroundResource(R.color.background);
			_selectedView = null;
			_selectedPosition = -1;
		}
		else
		{
			if(_selectedView != null)
				_selectedView.setBackgroundResource(R.color.background);
			view.setBackgroundResource(R.color.main);
			_selectedView = view;
			_selectedPosition = position;
		}
	}

	void _setExpenseListView()
	{
		_expenses = new ArrayList<HashMap<String, Object>>();
		List<ContentValues> expenseValues = _expenseSqlModel.getAllExpenses();

		for(ContentValues expenseValue: expenseValues)
		{
			HashMap<String,Object> expense = new HashMap<String,Object>();

			byte [] pictureBytes = expenseValue.getAsByteArray(Globals.ExpenseTable.PICTURE);
			final long id = expenseValue.getAsLong(Globals.ExpenseTable.ID);
			final long spend = expenseValue.getAsLong(Globals.ExpenseTable.SPEND);
			final String dateString = expenseValue.getAsString(Globals.ExpenseTable.DATE);
			final String category = _categorySqlModel.getCategoryName(expenseValue.getAsLong(Globals.ExpenseTable.CATEGORY_ID));
			final String note = expenseValue.getAsString(Globals.ExpenseTable.NOTE);

			expense.put("id", id);
			expense.put("pictureBytes", pictureBytes);
			expense.put("spend", spend);
			expense.put("dateString", dateString);
			expense.put("category", category);
			expense.put("note", note);
			_expenses.add(expense);
		}

		_dataAdapter = new SimpleAdapter(
			this,
			_expenses,
			R.layout.statistics_item_expense,
			new String[] {"pictureBytes", "spend", "dateString", "category"},
			new int[] {
				R.id.statistics_item_view_picture,
				R.id.statistics_item_spend,
				R.id.statistics_item_view_date,
				R.id.statistics_item_view_category
			}
		);
		_dataAdapter.setViewBinder(_simepleViewBinder);
		_expenseListView.setAdapter(_dataAdapter);
	}

	SimpleAdapter.ViewBinder _simepleViewBinder = new SimpleAdapter.ViewBinder()
	{

		@Override
		public boolean setViewValue(View view, Object data, String text)
		{
			if((view instanceof ImageView) && (data instanceof byte[]))
			{
				byte [] pictureBytes = (byte[]) data;
				Bitmap picture = BitmapFactory.decodeByteArray(pictureBytes, 0, pictureBytes.length);
				ImageView imageView = (ImageView) view;
				imageView.setImageBitmap(picture);
				return true;
			}
			else if((view instanceof TextView) && (data instanceof String))
			{
				TextView textView = (TextView) view;
				textView.setText((String) data);
			}
			return false;
		}
	};
}