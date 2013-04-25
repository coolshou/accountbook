package net.nctucs.lazchi.marco79423.accountbook;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ExpenseFragment extends Fragment implements OnClickListener
{
	private EditText _spendEditText;
	private DatePicker _datePicker; 
	private Spinner _categorySpinner;
	private EditText _noteEditText;
	private Button _okButton;
	private Button _cancelButton;
	
	CategoryDataSource _categoryDataSource;
	ExpenseDataSource _expenseDataSource;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.fragment_expense, container, false);
		_setViews(view);
		_setDatabase();
		
		_loadSpinnerData();
		return view;
	}

	private void _setViews(View view)
	{
		_spendEditText = (EditText) view.findViewById(R.id.edittext_spend);
		_datePicker = (DatePicker) view.findViewById(R.id.datepicker); 
		_categorySpinner = (Spinner) view.findViewById(R.id.spinner_expense);
		_noteEditText = (EditText) view.findViewById(R.id.edittext_note);			
		_okButton = (Button) view.findViewById(R.id.button_ok);
		_cancelButton = (Button) view.findViewById(R.id.button_cancel);
		
		_okButton.setOnClickListener(this);
		_cancelButton.setOnClickListener(this);
	}
	
	private void _setDatabase() 
	{
		_categoryDataSource = new CategoryDataSource(getActivity());
		_expenseDataSource = new ExpenseDataSource(getActivity());
		
		_categoryDataSource.open();
		_expenseDataSource.open();
	}
	
	private void _loadSpinnerData()
	{
		List<CategoryData> categories = _categoryDataSource.getAllCategories();
		List<String> categoryNames = new ArrayList<String>();
		for(CategoryData category: categories)
			categoryNames.add(category.getCategory());
		
		ArrayAdapter<String> adapter = 
				new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item);
		adapter.addAll(categoryNames);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		_categorySpinner.setAdapter(adapter);
		_categorySpinner.setOnItemSelectedListener(_selectListener);
		
		
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		_categoryDataSource.open();
		_expenseDataSource.open();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		_categoryDataSource.close();
		_expenseDataSource.close();
	}
	
	@Override
	public void onClick(View view) {
		switch(view.getId())
		{
		case R.id.button_ok: _saveTheExpense(); break;
		case R.id.button_cancel: _clearTheForm(); break;
		}
	} 
	
	private void _saveTheExpense()
	{
		
	}
	
	private void _clearTheForm() 
	{
		_spendEditText.setText("");
		_noteEditText.setText("");	
		
		_resetDatePicker();
	}
	
	private void _resetDatePicker()
	{
		final Calendar calendar = Calendar.getInstance();
		
		_datePicker.updateDate(
			calendar.get(Calendar.YEAR),
			calendar.get(Calendar.MONTH),
			calendar.get(Calendar.DAY_OF_MONTH)
		);
	}
	
	private Spinner.OnItemSelectedListener _selectListener = new Spinner.OnItemSelectedListener()
	{
		@Override
		public void onItemSelected(AdapterView<?>adapterView, View v, int position, long id)
		{
			Toast.makeText(getActivity(), "你選擇了" + adapterView.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
		}
		
		@Override
		//若是沒有選擇任何項目
		public void onNothingSelected(AdapterView<?>adapterView)
		{
			Toast.makeText(getActivity(), "你什麼也沒選", Toast.LENGTH_LONG).show();
		} 
	};
}
