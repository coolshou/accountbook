package net.nctucs.lazchi.marco79423.accountbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class StatisticsFragment extends Fragment 
{
	private ListView _expensesListView;
	
	CategoryDatabaseAdapter _categoryDatabaseAdapter;
	ExpenseDatabaseAdapter _expenseDatabaseAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		_categoryDatabaseAdapter = new CategoryDatabaseAdapter(getActivity());
		_expenseDatabaseAdapter = new ExpenseDatabaseAdapter(getActivity());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.fragment_statistics, container, false);
		
		_expensesListView = (ListView) view.findViewById(R.id.listview_expenses);
		
		Assert.assertNotNull("_expensesListView is null", _expensesListView);
		
		return view;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		try
		{
			_openDatabases();
			_loadListViewData();
		}
		catch(SQLException exception)
		{
			Log.e(StatisticsFragment.class.getName(), "資料庫打不開 " + exception.toString());
		}
	}
	
	private void _openDatabases() throws SQLException
	{
		Assert.assertNotNull("_categoryDatabaseAdapter is null", _categoryDatabaseAdapter);
		Assert.assertNotNull("_expenseDatabaseAdapter is null", _categoryDatabaseAdapter);
		
		_expenseDatabaseAdapter.open();
		_categoryDatabaseAdapter.open();
	}
	
	private void _loadListViewData() 
	{
		Assert.assertNotNull("_expenseDatabaseAdapter is null", _categoryDatabaseAdapter);
		Assert.assertNotNull("_categoryDatabaseAdapter is null", _categoryDatabaseAdapter);
		
		ArrayList<HashMap<String,Object>> expenseList = new ArrayList<HashMap<String,Object>>();
		List<ExpenseData> expenseDataList = _expenseDatabaseAdapter.getAllExpenses();
		
		for(ExpenseData expenseData: expenseDataList)
		{
			HashMap<String,Object> item = new HashMap<String,Object>();
	
			final String category = _categoryDatabaseAdapter.getCategoryNameByCategoryId(expenseData.getCategoryId());
			final String note = expenseData.getNote();
			
			item.put("picture", expenseData.getPicture());
			item.put("spend", String.valueOf(expenseData.getSpend()) + "元");
			item.put("date", "時間：" + expenseData.getDate());
			item.put("category", "類別：" + category);
			item.put("note", note);
			expenseList.add(item);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(
			getActivity(),
			expenseList,
			R.layout.listitem_expense,
			new String[] {"picture", "spend", "date", "category"},
			new int[] {R.id.imageview_picture, R.id.textview_spend, R.id.textview_date, R.id.textview_category}
		);
		
		Assert.assertNotNull("_expensesListView is null", _expensesListView);
		_expensesListView.setAdapter(adapter);		
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		_closeDatabases();
	}
	
	private void _closeDatabases()
	{
		_categoryDatabaseAdapter.close();
		_expenseDatabaseAdapter.close();
	}
	
	public void update()
	{
		_loadListViewData();
	}
}