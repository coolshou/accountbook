package net.nctucs.lazchi.marco79423.accountbook;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

public class ExpenseFragment extends Fragment implements OnClickListener
{
	EditText _spendEditText;
	DatePicker _datePicker; 
	Spinner _classSpinner;
	EditText _noteEditText;
	Button _okButton;
	Button _cancelButton;
	DBHelper _dbHelper;
	
	public ExpenseFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.fragment_pay, container, false);
		_setViews(view);
		_openDB();
		return view;
	}
	
	private void _setViews(View view)
	{
		_spendEditText = (EditText) view.findViewById(R.id.edittext_spend);
		_datePicker = (DatePicker) view.findViewById(R.id.datepicker); 
		_classSpinner = (Spinner) view.findViewById(R.id.spinner_class);
		_noteEditText = (EditText) view.findViewById(R.id.edittext_note);			
		_okButton = (Button) view.findViewById(R.id.button_ok);
		_cancelButton = (Button) view.findViewById(R.id.button_cancel);
		
		_okButton.setOnClickListener(this);
		_cancelButton.setOnClickListener(this);
	}
	
	private void _openDB()
	{
		//_dbHelper = new DBHelper(this);
	}
	
	@Override
	public void onClick(View view) {
		switch(view.getId())
		{
		case R.id.button_ok: Log.v("AccountBook", "ok"); break;
		case R.id.button_cancel: Log.v("AccountBook", "cancel"); break;
		}
	}
	
	private void _add()
	{
		
	}

}
