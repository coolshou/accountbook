package net.nctucs.lazchi.marco79423.ExpenseBook;

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

	private ImageView _pictureImageView;
	private EditText _spendEditText;
	private EditText _dateEditText;
	private Spinner _categorySpinner;
	private EditText _noteEditText;

	private CategorySqlModel _categorySqlModel;
	private ExpenseSqlModel _expenseSqlModel;

	private byte[] _pictureBytes;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.expense);

		Button saveButton = (Button) findViewById(R.id.expense_button_save);
		Button cancelButton = (Button) findViewById(R.id.expense_button_cancel);

		_pictureImageView = (ImageView) findViewById(R.id.expense_view_picture);
		_spendEditText = (EditText) findViewById(R.id.expense_edit_spend);
		_dateEditText = (EditText) findViewById(R.id.expense_edit_date);
		_categorySpinner = (Spinner) findViewById(R.id.expense_edit_category);
		_noteEditText = (EditText) findViewById(R.id.expense_edit_note);

		saveButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);

		_categorySqlModel = new CategorySqlModel(this);
		_expenseSqlModel = new ExpenseSqlModel(this);

		_dateEditText.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent)
			{
				showDialog(_DATE_DIALOG_ID);
				return false;
			}
		});
	}

	@Override
	public void onResume()
	{
		super.onResume();
		_categorySqlModel.open();
		_expenseSqlModel.open();

		_prepareExpenseForm();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		_categorySqlModel.close();
		_expenseSqlModel.close();
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
		Intent intent = new Intent();

		Bundle bundle = getIntent().getExtras();
		long id = bundle.getLong(Globals.Expense.ID, -1);

		//新增或是編輯
		if(id == -1)
		{
			intent.setClass(this, MainActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);

		}
		else
		{
			intent.setClass(this, BrowseActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
		}
		finish();
	}

	@Override
	protected Dialog onCreateDialog(int id)
	{

		if(id == _DATE_DIALOG_ID)
		{
			SimpleDateFormat formatter = new SimpleDateFormat(Globals.DATE_FORMAT);
			try
			{
				Date date = formatter.parse(_dateEditText.getText().toString());
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
		Bundle bundle = getIntent().getExtras();

		//設定照片
		_pictureBytes = bundle.getByteArray(Globals.Expense.PICTURE_BYTES);
		Bitmap picture = BitmapFactory.decodeByteArray(_pictureBytes, 0, _pictureBytes.length);
		_pictureImageView.setImageBitmap(picture);

		//設定花費
		long spend = bundle.getLong(Globals.Expense.SPEND, -1);
		if(spend != -1)
			_spendEditText.setText(String.valueOf(spend));

		//設定時間
		String dateString = bundle.getString(Globals.Expense.DATE_STRING);
		if(dateString == null)
		{
			SimpleDateFormat formatter = new SimpleDateFormat(Globals.DATE_FORMAT);
			dateString = formatter.format(new Date());
		}
		_dateEditText.setText(dateString);

		//設定分類
		List<String> categoryNames = _categorySqlModel.getAllCategoryNames();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.expense_spinner);
		for(String categoryName : categoryNames)
			adapter.add(categoryName);

		adapter.setDropDownViewResource(R.layout.expense_spinner);
		_categorySpinner.setAdapter(adapter);

		String category = bundle.getString(Globals.Expense.CATEGORY);
		if(category != null)
		{
			int categoryId = categoryNames.indexOf(category);
			_categorySpinner.setSelection(categoryId);
		}

		//設定筆記
		String note = bundle.getString(Globals.Expense.NOTE);
		if(note != null)
			_noteEditText.setText(note);
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
			spend = Long.parseLong(_spendEditText.getText().toString());
		}
		catch(NumberFormatException e)
		{
			spend = 0;
		}

		//設定時間
		dateString = _dateEditText.getText().toString();

		//設定分類
		try
		{
			categoryId = _categorySqlModel.getCategoryId(_categorySpinner.getSelectedItem().toString());
		}
		catch(NullPointerException e)
		{
			Toast.makeText(this, resource.getString(R.string.message_save_failed), Toast.LENGTH_LONG).show();
			return;
		}

		//設定筆記
		note = _noteEditText.getText().toString();

		Bundle bundle = getIntent().getExtras();
		long id = bundle.getLong(Globals.Expense.ID, -1);

		//新增或是編輯
		if(id == -1)
			_expenseSqlModel.addExpense(_pictureBytes, spend, dateString, categoryId, note);
		else
			_expenseSqlModel.editExpense(id, _pictureBytes, spend, dateString, categoryId, note);

		Toast.makeText(this, resource.getString(R.string.message_save_successfully), Toast.LENGTH_LONG).show();

		//移動到統計頁面
		Intent intent = new Intent();
		intent.setClass(this, BrowseActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

		finish();
	}

	private final DatePickerDialog.OnDateSetListener _dateSetListener = new DatePickerDialog.OnDateSetListener()
	{
		@Override
		public void onDateSet(DatePicker datePicker, int year, int month, int day)
		{
			GregorianCalendar calender = new GregorianCalendar(year, month, day);
			SimpleDateFormat formatter = new SimpleDateFormat(Globals.DATE_FORMAT);
			_dateEditText.setText(formatter.format(calender.getTime()));
		}
	};
}