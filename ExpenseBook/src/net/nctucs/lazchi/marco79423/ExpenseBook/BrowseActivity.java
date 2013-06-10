package net.nctucs.lazchi.marco79423.ExpenseBook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.widget.AdapterView.*;

public class BrowseActivity extends Activity implements View.OnClickListener, OnItemClickListener
{
	private ListView _expenseListView;

	private ExpenseSqlModel _expenseSqlModel;
	private CategorySqlModel _categorySqlModel;

	private View _selectedView;
	private int _selectedPosition = -1;
	private SimpleAdapter _dataAdapter;

	private ArrayList<HashMap<String, Object>> _expenses;


	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browse);

		_expenseListView = (ListView) findViewById(R.id.statistics_list_expense);
		_expenseListView.setOnItemClickListener(this);

		_expenseSqlModel = new ExpenseSqlModel(this);
		_categorySqlModel = new CategorySqlModel(this);

		Button editButton = (Button) findViewById(R.id.browse_button_edit);
		editButton.setOnClickListener(this);

		Button deleteButton = (Button) findViewById(R.id.browse_button_delete);
		deleteButton.setOnClickListener(this);
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
			case R.id.browse_button_edit: _onEditButtonClicked(); break;
			case R.id.browse_button_delete: _onDeleteButtonClicked(); break;
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
		intent.putExtra(Globals.Expense.ID, (Long)expense.get(Globals.Expense.ID));
		intent.putExtra(Globals.Expense.PICTURE_BYTES, (byte[])expense.get(Globals.Expense.PICTURE_BYTES));
		intent.putExtra(Globals.Expense.SPEND, (Long)expense.get(Globals.Expense.SPEND));
		intent.putExtra(Globals.Expense.DATE_STRING, (String)expense.get(Globals.Expense.DATE_STRING));
		intent.putExtra(Globals.Expense.CATEGORY, (String)expense.get(Globals.Expense.CATEGORY));
		intent.putExtra(Globals.Expense.NOTE, (String)expense.get(Globals.Expense.NOTE));

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

		//確認視窗
		AlertDialog.Builder builder = new AlertDialog.Builder(BrowseActivity.this);
		builder.setTitle(R.string.browse_dialog_delete_title);
		builder.setMessage(R.string.browse_dialog_delete_message);
		builder.setPositiveButton(R.string.browse_dialog_delete_confirm, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialogInterface, int i)
			{
				long id = (Long)_expenses.get(_selectedPosition).get(Globals.Expense.ID);
				_deleteExpense(id);
			}
		});

		builder.setNegativeButton(R.string.browse_dialog_delete_cancel, null);
		builder.create().show();
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

			expense.put(Globals.Expense.ID, id);
			expense.put(Globals.Expense.PICTURE_BYTES, pictureBytes);
			expense.put(Globals.Expense.SPEND, spend);
			expense.put(Globals.Expense.DATE_STRING, dateString);
			expense.put(Globals.Expense.CATEGORY, category);
			expense.put(Globals.Expense.NOTE, note);
			_expenses.add(expense);
		}

		_dataAdapter = new SimpleAdapter(
			this,
			_expenses,
			R.layout.browse_item_expense,
			new String[] {
				Globals.Expense.PICTURE_BYTES,
				Globals.Expense.SPEND,
				Globals.Expense.DATE_STRING,
				Globals.Expense.NOTE
			},
			new int[] {
				R.id.browse_item_view_picture,
				R.id.browse_item_spend,
				R.id.browse_item_view_date,
				R.id.browse_item_view_category
			}
		);
		_dataAdapter.setViewBinder(_simepleViewBinder);
		_expenseListView.setAdapter(_dataAdapter);
	}

	private void _deleteExpense(long id)
	{
		Resources resources = getResources();
		if(_expenseSqlModel.removeExpense(id) == 0)
		{
			Toast.makeText(this, resources.getString(R.string.message_delete_item_failed), Toast.LENGTH_LONG).show();
			return;
		}
		_expenses.remove(_selectedPosition);
		_dataAdapter.notifyDataSetChanged();

		_selectedView.setBackgroundResource(R.color.background);
		_selectedView = null;
		_selectedPosition = -1;

		Toast.makeText(this, resources.getString(R.string.message_delete_item_successful), Toast.LENGTH_LONG).show();
	}

	private final SimpleAdapter.ViewBinder _simepleViewBinder = new SimpleAdapter.ViewBinder()
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