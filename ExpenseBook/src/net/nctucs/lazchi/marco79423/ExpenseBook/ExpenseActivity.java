package net.nctucs.lazchi.marco79423.ExpenseBook;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
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

	private Bitmap _picture;

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
		//設定照片
		byte [] bytes = getIntent().getExtras().getByteArray("picture");
		_picture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		_pictureImageView.setImageBitmap(_picture);

		//設定時間
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/M/d");
		String currentDate = formatter.format(new Date());
		_dateEditText.setText(currentDate);

		//設定分類
		List<String> categoryNames = _categorySqlModel.getAllCategoryNames();
		if(categoryNames.isEmpty())
			categoryNames = _categorySqlModel.addDefaultCategories();

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		for(String categoryName : categoryNames)
			adapter.add(categoryName);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		_categorySpinner.setAdapter(adapter);
	}

	private void _onSaveButtonClicked()
	{
		Resources resource = getResources();

		long spend = 0;
		Date date = null;
		long categoryId = 0;
		String note = "";

		//設定金額
		spend = Long.getLong(_spendEditText.getText().toString(), 0);

		//設定時間
		try
		{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy/M/d");
			date = formatter.parse(_dateEditText.getText().toString());
		}
		catch(ParseException e)
		{
			Toast.makeText(this, resource.getString(R.string.message_save_failed), Toast.LENGTH_LONG).show();
			return;
		}

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

		_expenseSqlModel.addExpense(_picture, spend, date, categoryId, note);
		Toast.makeText(this, resource.getString(R.string.message_save_successfully), Toast.LENGTH_LONG).show();
		finish();
	}
}