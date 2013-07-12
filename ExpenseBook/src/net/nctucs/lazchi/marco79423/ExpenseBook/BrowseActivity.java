package net.nctucs.lazchi.marco79423.ExpenseBook;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
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

public class BrowseActivity extends Activity implements OnItemClickListener
{
	private SimpleAdapter _dataAdapter;
	private List<HashMap<String, Object>> _expenses;

	AnimatorSet _listAnimatorSet;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browse);

		//編輯按鈕
		//Button editButton = (Button) findViewById(R.id.browse_button_edit);
		//editButton.setOnClickListener(BrowseActivity.this);

		//刪除按鈕
		//Button deleteButton = (Button) findViewById(R.id.browse_button_delete);
		//deleteButton.setOnClickListener(BrowseActivity.this);

		//設定清單動畫
		ListView expenseListView = (ListView) findViewById(R.id.browse_list_expense);
		expenseListView.setOnItemClickListener(BrowseActivity.this);
		_listAnimatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(BrowseActivity.this, R.animator.browse_list);
		_listAnimatorSet.setTarget(expenseListView);

		registerForContextMenu(expenseListView);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		//設定清單
		_setExpenseListView();
	}

	/*
	 * 事件
	 */

	@Override
	public void onBackPressed()
	{
		Intent intent = new Intent();
		intent.setClass(BrowseActivity.this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
	{
		menu.setHeaderTitle("您想刪除這筆支出嗎？");
		menu.add(0, Menu.FIRST, 0, "刪除");
		menu.add(0, Menu.FIRST + 1, 1, "取消");
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		if(item.getItemId() == Menu.FIRST + 1)
			return true;

		//刪除該筆支出
		AdapterContextMenuInfo  info = (AdapterContextMenuInfo) item.getMenuInfo();
		int _selectedPosition = info.position;

		long id = (Long)_expenses.get(_selectedPosition).get(Globals.Expense.ID);
		_deleteExpenseFromDatabase(id);

		_expenses.remove(_selectedPosition);
		_dataAdapter.notifyDataSetChanged();

		return true;
	}


	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
	{
		HashMap<String, Object> expense = _expenses.get(position);

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
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}

	void _setExpenseListView()
	{
		ExpenseSqlModel expenseSqlModel = new ExpenseSqlModel(BrowseActivity.this);
		CategorySqlModel categorySqlModel = new CategorySqlModel(BrowseActivity.this);

		expenseSqlModel.open();
		categorySqlModel.open();

		_expenses = expenseSqlModel.getAllExpenses();

		//設定分類資料
		for(int i = 0; i < _expenses.size(); i++)
		{
			String categoryName = categorySqlModel.getCategoryName((Long)_expenses.get(i).get(Globals.ExpenseTable.CATEGORY_ID));
			_expenses.get(i).put(Globals.Expense.CATEGORY, categoryName);
		}

		expenseSqlModel.close();
		categorySqlModel.close();

		//設定清單資料
		_dataAdapter = new SimpleAdapter(
			this,
			_expenses,
			R.layout.browse_item_expense,
			new String[] {
				Globals.Expense.PICTURE_BYTES,
				Globals.Expense.SPEND,
				Globals.Expense.DATE_STRING,
				Globals.Expense.CATEGORY
			},
			new int[] {
				R.id.browse_item_view_picture,
				R.id.browse_item_view_spend,
				R.id.browse_item_view_date,
				R.id.browse_item_view_category
			}
		);
		_dataAdapter.setViewBinder(_simepleViewBinder);

		ListView expenseListView = (ListView) findViewById(R.id.browse_list_expense);
		expenseListView.setAdapter(_dataAdapter);

		//開啟動畫
		_listAnimatorSet.start();
	}

	private void _deleteExpenseFromDatabase(long id)
	{
		Resources resources = getResources();

		ExpenseSqlModel expenseSqlModel = new ExpenseSqlModel(BrowseActivity.this);
		expenseSqlModel.open();
		if(expenseSqlModel.removeExpense(id) == 0)
		{
			Toast.makeText(this, resources.getString(R.string.message_delete_item_failed), Toast.LENGTH_LONG).show();
			expenseSqlModel.close();
			return;
		}
		expenseSqlModel.close();



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