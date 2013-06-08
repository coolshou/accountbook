package net.nctucs.lazchi.marco79423.ExpenseBook;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import android.util.Log;

public class ExpenseActivity extends Activity implements View.OnClickListener
{
	private Button _saveButton;
	private Button _cancelButton;

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
		setContentView(R.layout.activity_expense);

		_saveButton = (Button) findViewById(R.id.expense_button_save);
		_cancelButton = (Button) findViewById(R.id.expense_button_cancel);

		_pictureImageView = (ImageView) findViewById(R.id.expense_view_picture);
		_spendEditText = (EditText) findViewById(R.id.expense_edit_spend);
		_dateEditText = (EditText) findViewById(R.id.expense_edit_date);
		_categorySpinner = (Spinner) findViewById(R.id.expense_edit_category);
		_noteEditText = (EditText) findViewById(R.id.expense_edit_note);

		_saveButton.setOnClickListener(this);
		_cancelButton.setOnClickListener(this);

		_categorySqlModel = new CategorySqlModel(this);
		_expenseSqlModel = new ExpenseSqlModel(this);
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

	@Override
	public void onClick(View view)
	{
		switch(view.getId())
		{
			case R.id.expense_button_cancel: finish(); break;
			case R.id.expense_button_save: _onSaveButtonClicked(); break;
		}
	}

	private void _prepareExpenseForm()
	{
		Bundle bundle = getIntent().getExtras();

		//設定照片
		_pictureBytes = bundle.getByteArray("pictureBytes");
		Bitmap picture = BitmapFactory.decodeByteArray(_pictureBytes, 0, _pictureBytes.length);
		_pictureImageView.setImageBitmap(picture);

		//設定花費
		long spend = bundle.getLong("spend", -1);
		if(spend != -1)
			_spendEditText.setText(String.valueOf(spend));

		//設定時間
		String dateString = bundle.getString("dateString");
		if(dateString == null)
		{
			SimpleDateFormat formatter = new SimpleDateFormat(Globals.DATE_FORMAT);
			dateString = formatter.format(new Date());
		}
		_dateEditText.setText(dateString);

		//設定分類
		List<String> categoryNames = _categorySqlModel.getAllCategoryNames();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		for(String categoryName : categoryNames)
			adapter.add(categoryName);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		_categorySpinner.setAdapter(adapter);

		String category = bundle.getString("category");
		if(category != null)
		{
			int categoryId = categoryNames.indexOf(category);
			_categorySpinner.setSelection(categoryId);
		}

		//設定筆記
		String note = bundle.getString("note");
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
		long id = bundle.getLong("id", -1);

		//新增或是編輯
		if(id == -1)
			_expenseSqlModel.addExpense(_pictureBytes, spend, dateString, categoryId, note);
		else
			_expenseSqlModel.editExpense(id, _pictureBytes, spend, dateString, categoryId, note);

		Toast.makeText(this, resource.getString(R.string.message_save_successfully), Toast.LENGTH_LONG).show();

		//移動到統計頁面
		Intent intent = new Intent();
		intent.setClass(this, StatisticsActivity.class);
		startActivity(intent);

		finish();
	}
}