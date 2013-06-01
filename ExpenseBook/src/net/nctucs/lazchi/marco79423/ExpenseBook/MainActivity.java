package net.nctucs.lazchi.marco79423.ExpenseBook;

//Java
import java.io.IOException;

//Android
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

//介面
import android.provider.MediaStore;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//工具
import android.util.Log;

//功能



public class MainActivity extends Activity implements OnClickListener
{
	private static final int _REQUEST_LINK_TO_DROPBOX = 0;

	private Button _createNewExpenseButton;
	private Button _browseStatisticsButton;
	private Button _syncWithDropboxButton;

	/*
	 * 生命週期
	 */
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

	    _createNewExpenseButton = (Button) findViewById(R.id.main_button_new_expense);
	    _browseStatisticsButton = (Button) findViewById(R.id.main_button_browse_statistics);
	    _syncWithDropboxButton = (Button) findViewById(R.id.main_button_sync);

	    _createNewExpenseButton.setOnClickListener(this);
	    _browseStatisticsButton.setOnClickListener(this);
	    _syncWithDropboxButton.setOnClickListener(this);
	}

	@Override
	protected void onResume() 
    {
		super.onResume();
	}
    
    @Override
	protected void onPause() 
    {
	    super.onPause();
	}


	/*
	 * 事件
	 */

	@Override
	public void onClick(View view)
	{
		switch(view.getId())
		{
			case R.id.main_button_new_expense: _onCreateNewExpenseButtonClicked(); break;
			case R.id.main_button_browse_statistics: _onBrowseStatisticsButtonClicked(); break;
		}
	}


	/*
	 * 功能
	 */

	private void _onCreateNewExpenseButtonClicked()
	{
		Intent intent = new Intent();
		intent.setClass(this, CameraActivity.class);
		startActivity(intent);
	}

	private void _onBrowseStatisticsButtonClicked()
	{
		Intent intent = new Intent();
		intent.setClass(this, StatisticsActivity.class);
		startActivity(intent);
	}
}
