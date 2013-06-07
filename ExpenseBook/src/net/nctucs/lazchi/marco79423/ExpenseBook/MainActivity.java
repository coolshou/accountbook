package net.nctucs.lazchi.marco79423.ExpenseBook;

//Java
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

//Android
import android.content.res.Resources;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

//介面
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;

import android.widget.Button;

//Dropbox
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

//工具
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


//功能

public class MainActivity extends Activity implements OnClickListener
{
	private static final int _REQUEST_LINK_TO_DROPBOX = 0;

	private Button _createNewExpenseButton;
	private Button _browseStatisticsButton;
	private Button _syncWithDropboxButton;
	private TextView _talkTextView;

	private ExpenseSqlModel _expenseSqlModel;
	private DbxAccountManager _dbxAccountManager;

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
	    _syncWithDropboxButton = (Button) findViewById(R.id.main_button_upload_database);
	    _talkTextView = (TextView) findViewById(R.id.main_view_talk);

	    _createNewExpenseButton.setOnClickListener(this);
	    _browseStatisticsButton.setOnClickListener(this);
	    _syncWithDropboxButton.setOnClickListener(this);

	    _expenseSqlModel = new ExpenseSqlModel(this);

	    //Dropbox
	    _dbxAccountManager = DbxAccountManager.getInstance(
			    getApplicationContext(),
			    Globals.DROPBOX_KEY ,
			    Globals.DROPBOX_SECRET
	    );
	}

	@Override
	public void onResume()
	{
		super.onResume();
		_expenseSqlModel.open();
		_setTalkTextView();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		_expenseSqlModel.close();
	}
	/*
     * Menu
     */

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		menu.clear();
		if(_dbxAccountManager.hasLinkedAccount())
			getMenuInflater().inflate(R.menu.menu_logged, menu);
		else
			getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.menu_download_database_from_dropbox: _onDownloadDatabaseFromDropboxItemClicked(); break;
			case R.id.menu_link_to_dropbox: _onLinkToDropboxItemClicked(); break;
			case R.id.menu_logout_from_dropbox: _onLogoutFromDropboxItemClicked(); break;
			case R.id.menu_exit: finish();
			default: return false;
		}
		return true;
	}

	/*
	 * 事件
	 */

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch(requestCode)
		{
			case _REQUEST_LINK_TO_DROPBOX:
				if(resultCode == Activity.RESULT_OK)
					Toast.makeText(this, R.string.message_link_to_dropbox_successful, Toast.LENGTH_LONG).show();
				else
					Toast.makeText(this, R.string.message_link_to_dropbox_failed, Toast.LENGTH_LONG).show();
				break;
		}
	}

	@Override
	public void onClick(View view)
	{
		switch(view.getId())
		{
			case R.id.main_button_new_expense: _onCreateNewExpenseButtonClicked(); break;
			case R.id.main_button_browse_statistics: _onBrowseStatisticsButtonClicked(); break;
			case R.id.main_button_upload_database: _onUploadDatabaseButtonClicked(); break;
		}
	}


	/*
	 * 功能
	 */
	private void _onLinkToDropboxItemClicked()
	{
		_dbxAccountManager.startLink(this, _REQUEST_LINK_TO_DROPBOX);
	}

	private void _onLogoutFromDropboxItemClicked()
	{
		_dbxAccountManager.unlink();
	}

	private void _onDownloadDatabaseFromDropboxItemClicked()
	{
		Resources resource = getResources();
		if(!_dbxAccountManager.hasLinkedAccount())
		{
			Toast.makeText(this, resource.getString(R.string.message_must_link_to_dropbox), Toast.LENGTH_LONG).show();
			return;
		}

		_expenseSqlModel.close();
		try
		{
			DbxFileSystem dbxFileSystem = DbxFileSystem.forAccount(_dbxAccountManager.getLinkedAccount());
			DbxPath dbxDatabasePath = new DbxPath(DbxPath.ROOT, Globals.DATABASE_NAME);

			if(!dbxFileSystem.exists(dbxDatabasePath))
			{
				Toast.makeText(this, resource.getString(R.string.message_must_link_to_dropbox), Toast.LENGTH_LONG).show();
				return;
			}

			DbxFile dbxDatabaseFile = dbxFileSystem.open(dbxDatabasePath);

			//File databaseFile = new File("/sdcard/mydata/accountbook.db");//getDatabasePath(Globals.DATABASE_NAME);
			File databaseFile = getDatabasePath(Globals.DATABASE_NAME);

			FileInputStream fromStream = dbxDatabaseFile.getReadStream();
			FileOutputStream toStream = new FileOutputStream(databaseFile);

			//for database
			byte[] buffer = new byte[1024];
			int bufferLen;
			while((bufferLen = fromStream.read(buffer)) != -1)
				toStream.write(buffer, 0, bufferLen);

			fromStream.close();
			toStream.close();
			dbxDatabaseFile.close();

			Toast.makeText(this, resource.getString(R.string.message_download_successful), Toast.LENGTH_LONG).show();

			_setTalkTextView();
		}
		catch(Exception e)
		{
			Toast.makeText(this, resource.getString(R.string.message_download_failed), Toast.LENGTH_LONG).show();
		}
		finally
		{
			_expenseSqlModel.open();
		}
	}

	private  void _onUploadDatabaseButtonClicked()
	{
		Resources resource = getResources();
		if(!_dbxAccountManager.hasLinkedAccount())
		{
			Toast.makeText(this, resource.getString(R.string.message_must_link_to_dropbox), Toast.LENGTH_LONG).show();
			return;
		}

		_expenseSqlModel.close();
		try
		{
			DbxFileSystem dbxFileSystem = DbxFileSystem.forAccount(_dbxAccountManager.getLinkedAccount());

			//Database file

			File databaseFile = getDatabasePath(Globals.DATABASE_NAME);
			//File databaseFile = new File("/sdcard/mydata/accountbook.db");
			if(databaseFile.exists())
			{
				DbxPath dbxDatabasePath = new DbxPath(DbxPath.ROOT, Globals.DATABASE_NAME);
				DbxFile dbxDatabaseFile;
				if(!dbxFileSystem.exists(dbxDatabasePath))
					dbxDatabaseFile = dbxFileSystem.create(dbxDatabasePath);
				else
					dbxDatabaseFile = dbxFileSystem.open(dbxDatabasePath);

				dbxDatabaseFile.writeFromExistingFile(databaseFile, false);
				dbxDatabaseFile.close();
			}

			Toast.makeText(this, resource.getString(R.string.message_upload_successful), Toast.LENGTH_LONG).show();
		}
		catch(Exception e)
		{
			Toast.makeText(this, resource.getString(R.string.message_upload_failed), Toast.LENGTH_LONG).show();
		}
		finally
		{
			_expenseSqlModel.open();
		}
	}

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

	private void _setTalkTextView()
	{
		Resources resource = getResources();
		long sum = _expenseSqlModel.getSumOfMonthlyExpenses();
		_talkTextView.setText(String.format(resource.getString(R.string.main_view_talk), sum));
	}
}
