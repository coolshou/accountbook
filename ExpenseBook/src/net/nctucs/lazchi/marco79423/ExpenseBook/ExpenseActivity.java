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

import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ExpenseActivity extends Activity implements View.OnClickListener
{
	private static final int _DATE_DIALOG_ID = 0;

	private byte[] _pictureBytes;
	private String _currentCheckedCategoryName;

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
		TextView spendTextView = (TextView) findViewById(R.id.expense_view_spend);
		spendTextView.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent)
			{
				if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
					_showCalculatorFragment();
				return true;
			}
		});

		//設定分類的 listener
		RadioGroup categoryRadioGroup = (RadioGroup) findViewById(R.id.expense_radio_category);
		categoryRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int id)
			{
				Button button = (RadioButton)findViewById(id);
				_currentCheckedCategoryName = button.getText().toString();
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

		TextView spendTextView = (TextView) findViewById(R.id.expense_view_spend);
		if(spendTextView.getText().toString().equals("0"))
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

		RadioGroup categoryRadioGroup = (RadioGroup) findViewById(R.id.expense_radio_category);
		for(String categoryName : categoryNames)
		{
			RadioButton button = new RadioButton(this);
			button.setText(categoryName);
			button.setTextSize(25);
			button.setTextColor(getResources().getColor(R.color.font));
			categoryRadioGroup.addView(button);
		}


		//設定資料
		Bundle bundle = getIntent().getExtras();

		if(bundle == null)
		{
			//設定預設花費
			TextView spendTextView = (TextView) findViewById(R.id.expense_view_spend);
			spendTextView.setText("0");

			//設定預設分類
			RadioButton button = (RadioButton)categoryRadioGroup.getChildAt(0);
			button.setChecked(true);

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
			String spendString = bundle.getString(Globals.Expense.SPEND_STRING, "0");
			TextView spendTextView = (TextView) findViewById(R.id.expense_view_spend);
			spendTextView.setText(spendString);

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
			String categoryName = bundle.getString(Globals.Expense.CATEGORY);
			if(categoryName != null)
			{
				int categoryId = categoryNames.indexOf(categoryName);
				RadioButton button = (RadioButton)categoryRadioGroup.getChildAt(categoryId);
				button.setChecked(true);
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

		String spendString = "0";
		String dateString = null;
		long categoryId = 0;
		String note = "";

		//設定金額
		TextView spendTextView = (TextView) findViewById(R.id.expense_view_spend);
		spendString = spendTextView.getText().toString();


		//設定時間
		EditText dateEditText = (EditText) findViewById(R.id.expense_edit_date);
		dateString = dateEditText.getText().toString();

		//設定分類
		try
		{
			CategorySqlModel categorySqlModel = new CategorySqlModel(this);
			categorySqlModel.open();

			categoryId = categorySqlModel.getCategoryId(_currentCheckedCategoryName);
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
			expenseSqlModel.addExpense(_pictureBytes, spendString, dateString, categoryId, note);
		else
			expenseSqlModel.editExpense(bundle.getLong(Globals.Expense.ID), _pictureBytes, spendString, dateString, categoryId, note);
		expenseSqlModel.close();

		Toast.makeText(this, resource.getString(R.string.message_save_successfully), Toast.LENGTH_LONG).show();

		//移動到統計頁面
		Intent intent = new Intent();
		intent.setClass(this, BrowseActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
	}

	public void setSpendEditText(BigInteger spend)
	{
		TextView spendTextView = (TextView) findViewById(R.id.expense_view_spend);
		spendTextView.setText(spend.toString());
	}

	private void _showCalculatorFragment()
	{
		TextView spendTextView = (TextView) findViewById(R.id.expense_view_spend);
		String spendString = spendTextView.getText().toString();

		CalculatorFragment calculatorFragment = CalculatorFragment.newInstance(new BigInteger(spendString));
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