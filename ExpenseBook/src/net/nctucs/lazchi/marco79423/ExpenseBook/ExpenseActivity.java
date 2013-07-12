package net.nctucs.lazchi.marco79423.ExpenseBook;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class ExpenseActivity extends Activity implements View.OnClickListener
{
	private static final int _DATE_DIALOG_ID = 0;

	private byte[] _pictureBytes;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.expense);

		//儲存按扭
		Button saveButton = (Button) findViewById(R.id.expense_button_save);
		saveButton.setOnClickListener(this);

		//取消按扭
		Button cancelButton = (Button) findViewById(R.id.expense_button_cancel);
		cancelButton.setOnClickListener(this);

		//設定花費的 listener
		EditText spendEditText = (EditText) findViewById(R.id.expense_edit_spend);
		spendEditText.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent)
			{
				if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
					_showCalculatorFragment();
				return true;
			}
		});

		//設定日期的 listener
		EditText dateEditText = (EditText) findViewById(R.id.expense_edit_date);
		dateEditText.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent)
			{
				if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
					showDialog(_DATE_DIALOG_ID);
				return true;
			}
		});
	}

	@Override
	public void onResume()
	{
		super.onResume();

		_prepareExpenseForm();

		EditText spendEditText = (EditText) findViewById(R.id.expense_edit_spend);
		if(Long.parseLong(spendEditText.getText().toString()) == 0)
			_showCalculatorFragment();
	}


	/*
	 * 事件
	 */

	@Override
	public void onClick(View view)
	{
		switch(view.getId())
		{
			case R.id.expense_button_cancel: onBackPressed(); break;
			case R.id.expense_button_save: _onSaveButtonClicked(); break;
		}
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
	}

	@Override
	protected Dialog onCreateDialog(int id)
	{

		if(id == _DATE_DIALOG_ID)
		{
			SimpleDateFormat formatter = new SimpleDateFormat(Globals.DATE_FORMAT);
			try
			{
				EditText dateEditText = (EditText) findViewById(R.id.expense_edit_date);
				Date date = formatter.parse(dateEditText.getText().toString());
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH);
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				return new DatePickerDialog(this, _dateSetListener, year, month, day);
			}
			catch(ParseException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	private void _prepareExpenseForm()
	{

		//設定分類
		CategorySqlModel categorySqlModel = new CategorySqlModel(this);
		categorySqlModel.open();
		List<String> categoryNames = categorySqlModel.getAllCategoryNames();
		categorySqlModel.close();

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.expense_spinner);
		for(String categoryName : categoryNames)
			adapter.add(categoryName);
		adapter.setDropDownViewResource(R.layout.expense_spinner);

		Spinner categorySpinner = (Spinner) findViewById(R.id.expense_edit_category);
		categorySpinner.setAdapter(adapter);

		//設定資料
		Bundle bundle = getIntent().getExtras();

		if(bundle == null)
		{
			//設定預設花費
			EditText spendEditText = (EditText) findViewById(R.id.expense_edit_spend);
			spendEditText.setText("0");

			//設定預設時間
			SimpleDateFormat formatter = new SimpleDateFormat(Globals.DATE_FORMAT);
			String dateString = formatter.format(new Date());

			EditText dateEditText = (EditText) findViewById(R.id.expense_edit_date);
			dateEditText.setText(dateString);
		}
		else
		{
			//設定照片
			_pictureBytes = bundle.getByteArray(Globals.Expense.PICTURE_BYTES);
			if(_pictureBytes != null)
			{
	            ImageView pictureImageView = (ImageView) findViewById(R.id.expense_view_picture);
				Bitmap picture = BitmapFactory.decodeByteArray(_pictureBytes, 0, _pictureBytes.length);
				pictureImageView.setImageBitmap(picture);
			}

			//設定花費
			long spend = bundle.getLong(Globals.Expense.SPEND, -1);
			if(spend != -1)
			{
				EditText spendEditText = (EditText) findViewById(R.id.expense_edit_spend);
				spendEditText.setText(String.valueOf(spend));
			}

			//設定時間
			String dateString = bundle.getString(Globals.Expense.DATE_STRING);
			if(dateString == null)
			{
				SimpleDateFormat formatter = new SimpleDateFormat(Globals.DATE_FORMAT);
				dateString = formatter.format(new Date());
			}

			EditText dateEditText = (EditText) findViewById(R.id.expense_edit_date);
			dateEditText.setText(dateString);

			//設定分類
			String category = bundle.getString(Globals.Expense.CATEGORY);
			if(category != null)
			{
				int categoryId = categoryNames.indexOf(category);
				categorySpinner.setSelection(categoryId);
			}

			//設定筆記
			String note = bundle.getString(Globals.Expense.NOTE);
			if(note != null)
			{
				EditText noteEditText = (EditText) findViewById(R.id.expense_edit_note);
				noteEditText.setText(note);
			}
		}
	}

	private void _onSaveButtonClicked()
	{
		Resources resource = getResources();

		long spend = 0;
		String dateString = null;
		long categoryId = 0;
		String note = "";

		//設定金額
		try
		{
			EditText spendEditText = (EditText) findViewById(R.id.expense_edit_spend);
			spend = Long.parseLong(spendEditText.getText().toString());
		}
		catch(NumberFormatException e)
		{
			spend = 0;
		}

		//設定時間
		EditText dateEditText = (EditText) findViewById(R.id.expense_edit_date);
		dateString = dateEditText.getText().toString();

		//設定分類
		try
		{
			CategorySqlModel categorySqlModel = new CategorySqlModel(this);
			categorySqlModel.open();

			Spinner categorySpinner = (Spinner) findViewById(R.id.expense_edit_category);
			categoryId = categorySqlModel.getCategoryId(categorySpinner.getSelectedItem().toString());
			categorySqlModel.close();
		}
		catch(NullPointerException e)
		{
			Toast.makeText(this, resource.getString(R.string.message_save_failed), Toast.LENGTH_LONG).show();
			return;
		}

		//設定筆記
		EditText noteEditText = (EditText) findViewById(R.id.expense_edit_note);
		note = noteEditText.getText().toString();

		ExpenseSqlModel expenseSqlModel = new ExpenseSqlModel(this);
		expenseSqlModel.open();

		Bundle bundle = getIntent().getExtras();

		//新增或是編輯
		if(bundle == null)
			expenseSqlModel.addExpense(_pictureBytes, spend, dateString, categoryId, note);
		else
			expenseSqlModel.editExpense(bundle.getLong(Globals.Expense.ID), _pictureBytes, spend, dateString, categoryId, note);
		expenseSqlModel.close();

		Toast.makeText(this, resource.getString(R.string.message_save_successfully), Toast.LENGTH_LONG).show();

		//移動到統計頁面
		Intent intent = new Intent();
		intent.setClass(this, BrowseActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
	}

	public void setSpendEditText(long spend)
	{
		EditText spendEditText = (EditText) findViewById(R.id.expense_edit_spend);
		spendEditText.setText(String.valueOf(spend));
	}

	private void _showCalculatorFragment()
	{
		EditText spendEditText = (EditText) findViewById(R.id.expense_edit_spend);
		long spend;
		try
		{
			spend = Long.parseLong(spendEditText.getText().toString());
		}
		catch(NumberFormatException e)
		{
			spend = 0;
		}
		CalculatorFragment calculatorFragment = CalculatorFragment.newInstance(spend);
		calculatorFragment.show(getFragmentManager(), "calculator");
	}

	private final DatePickerDialog.OnDateSetListener _dateSetListener = new DatePickerDialog.OnDateSetListener()
	{
		@Override
		public void onDateSet(DatePicker datePicker, int year, int month, int day)
		{
			GregorianCalendar calender = new GregorianCalendar(year, month, day);
			SimpleDateFormat formatter = new SimpleDateFormat(Globals.DATE_FORMAT);

			EditText dateEditText = (EditText) findViewById(R.id.expense_edit_date);
			dateEditText.setText(formatter.format(calender.getTime()));
		}
	};
}