package net.nctucs.lazchi.marco79423.accountbook;

import java.util.Calendar;
import java.util.List;

import junit.framework.Assert;

import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.database.SQLException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ExpenseFragment extends Fragment implements OnClickListener
{
	private EditText _spendEditText;
	private EditText _dateEditText; 
	private Spinner _categorySpinner;
	private EditText _noteEditText;
	private Button _okButton;
	private Button _cancelButton;
	
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
		View view = inflater.inflate(R.layout.fragment_expense, container, false);

		_spendEditText = (EditText) view.findViewById(R.id.edittext_spend);	
		_dateEditText = (EditText) view.findViewById(R.id.edittext_date); 
		_categorySpinner = (Spinner) view.findViewById(R.id.spinner_expense);
		_noteEditText = (EditText) view.findViewById(R.id.edittext_note);			
		_okButton = (Button) view.findViewById(R.id.button_ok);
		_cancelButton = (Button) view.findViewById(R.id.button_cancel);
	
		Assert.assertNotNull("_spendEditText is null", _spendEditText);
		Assert.assertNotNull("_dateEditText is null", _dateEditText);
		Assert.assertNotNull("_categorySpinner is null", _categorySpinner);
		Assert.assertNotNull("_noteEditText is null", _noteEditText);
		Assert.assertNotNull("_okButton is null", _okButton);
		Assert.assertNotNull("_cancelButton is null", _cancelButton);
		
		_dateEditText.setOnClickListener(this);
		_okButton.setOnClickListener(this);
		_cancelButton.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		try
		{
			_openDatabases();
			_loadSpinnerData();
			_resetTheForm();
		}
		catch(SQLException exception)
		{
			Log.e(ExpenseFragment.class.getName(), "資料庫打不開 " + exception.toString());
		}
	}
	
	private void _loadSpinnerData()
	{
		Assert.assertNotNull("_categoryDatabaseAdapter is null", _categoryDatabaseAdapter);
		
		List<String> categoryNames = _categoryDatabaseAdapter.getAllCategoryNames();
		ArrayAdapter<String> adapter = 
				new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item);
		adapter.addAll(categoryNames);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	
		Assert.assertNotNull("_categorySpinner is null", _categorySpinner);
		_categorySpinner.setAdapter(adapter);		
	}
	
	private void _resetTheForm() 
	{
		_spendEditText.setText("");
		_dateEditText.setText(DateFormat.getDateFormat(getActivity()).format(Calendar.getInstance().getTime()));
		_noteEditText.setText("");	
	}
	
	private void _openDatabases() throws SQLException
	{
		Assert.assertNotNull("_categoryDatabaseAdapter is null", _categoryDatabaseAdapter);
		Assert.assertNotNull("_expenseDatabaseAdapter is null", _categoryDatabaseAdapter);
		
		
		_expenseDatabaseAdapter.open();
		_categoryDatabaseAdapter.open();
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
	
	@Override
	public void onClick(View view) 
	{
		switch(view.getId())
		{
		//case R.id.edittext_date: _showDatePicker(); break;
		case R.id.button_ok: _onClickOkButton(); break;
		case R.id.button_cancel: _resetTheForm(); break;
		}
	} 
	
	/*
	private void _showDatePicker()
	{
		DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener()
		{
			@Override
			public void onDateSet(DatePicker view, int year, int month, int day)
			{
				//Calendar c = Calendar
				//_dateEditText.setText(DateFormat.getDateInstance().format());
			}
		};
		
		Calendar c = Calendar.getInstance();
		new DatePickerDialog(getActivity(), listener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
	}*/
	
	private void _onClickOkButton()
	{
		if(_saveTheExpense())
			Toast.makeText(getActivity(), R.string.save_successfully, Toast.LENGTH_LONG).show();
		else
			Toast.makeText(getActivity(), R.string.save_failed, Toast.LENGTH_LONG).show();
		_resetTheForm();
	}
	
	private boolean _saveTheExpense()
	{
		byte[] picture = null;
		long spend = 0;
		String date = "";
		long categoryId = 0;
		String note = "";

		boolean error = false;
		
		try
		{
			spend = Long.parseLong(_spendEditText.getText().toString());
		} 
		catch(NumberFormatException exception)
		{
			error = true;
			Log.e(ExpenseFragment.class.getName(), exception.toString());
		}
		
		date = _dateEditText.getText().toString();
		
		try
		{
			categoryId = _categoryDatabaseAdapter.getCategoryIdByCategoryName(_categorySpinner.getSelectedItem().toString());
		}
		catch(NullPointerException exception)
		{
			error = true;
			Log.e(ExpenseFragment.class.getName(), exception.toString());
		}
		
		note = _noteEditText.getText().toString();
	
		long row = _expenseDatabaseAdapter.addExpense(picture, spend, date, categoryId, note);
		if(row == -1)
		{
			error = true;
		}
		
		return !error;
	}
}
