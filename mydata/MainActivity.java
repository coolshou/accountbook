package net.nctucs.lazchi.marco79423.ExpenseBook;

//Java
import java.io.IOException;

//Android
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

//介面
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
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;


public class MainActivity extends Activity 
{
	private static final int _REQUEST_LINK_TO_DROPBOX = 0;
	private static final String TEST_FILE_NAME = "hello_dropbox.txt";
	
	private EditText editText;
	private Button button1;
	private Button button2;	
	
	private DbxAccountManager _dbxAccountManager;
	private DbxFile _file;
	
	/*
	 * 生命週期
	 */
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        editText = (EditText) findViewById(R.id.editText1);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        
        button1.setOnClickListener(new OnClickListener()
        {
        	 @Override
             public void onClick(View v) {
        		 MainActivity.this._saveToDropbox();
             }
        });
        button2.setOnClickListener(new OnClickListener()
        {
        	@Override
        	public void onClick(View v) {
        		MainActivity.this._syncWithDropbox();
        	}
        });
        
        
        //Dropbox
        _dbxAccountManager = DbxAccountManager.getInstance(
        	getApplicationContext(), 
        	Globals.DROPBOX_KEY , 
        	Globals.DROPBOX_SECRET
        );
    }
    
    @Override
	protected void onResume() 
    {
		super.onResume();
		if(_dbxAccountManager.hasLinkedAccount())
			_openLinkFile();	
	}
    
    @Override
	protected void onPause() 
    {
		super.onPause();
		if(_dbxAccountManager.hasLinkedAccount())
			_closeLinkFile();	
	}


	/*
     * Menu
     */
	@Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch(item.getItemId())
    	{
    	case R.id.link_to_dropbox: _onLinkToDropboxClicked(); break;
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
    			Toast.makeText(this, R.string.link_to_dropbox_successful, Toast.LENGTH_LONG).show();
    		else
    			Toast.makeText(this, R.string.link_to_dropbox_failed, Toast.LENGTH_LONG).show();
    		break;
    	}
    }
    
    /*
     * Dropbox 相關
     */
    
    private void _onLinkToDropboxClicked()
    {
    	_dbxAccountManager.startLink(this, _REQUEST_LINK_TO_DROPBOX);
    }
    
    private void _openLinkFile()
    {
    	try 
    	{
			DbxFileSystem dbxFileSystem = DbxFileSystem.forAccount(_dbxAccountManager.getLinkedAccount());
			DbxPath testPath = new DbxPath(DbxPath.ROOT, TEST_FILE_NAME);
			if(!dbxFileSystem.exists(testPath))
			{
				_file = dbxFileSystem.create(testPath);
			}
			else
			{
				_file = dbxFileSystem.open(testPath);
				try 
				{
					editText.setText(_file.readString());
                    Toast.makeText(this,  "同步完畢", Toast.LENGTH_LONG).show();
				} catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		} catch (DbxException e){
			e.printStackTrace();
			return;
		}

        _file.addListener(
            new DbxFile.Listener()
            {
	            @Override
	            public void onFileChange(DbxFile dbxFile)
	            {
					try
					{
						if(dbxFile.getNewerStatus().isCached)
						{
							MainActivity.this.runOnUiThread(new Runnable()
							{
								@Override
								public void run()
								{
									try
									{
										Toast.makeText(MainActivity.this, "newer is cached", Toast.LENGTH_LONG).show();
										_file.update();
										editText.setText(_file.readString());
									} catch(IOException e)
									{
										e.printStackTrace();
									}
								}
							});
						}

					} catch(DbxException e)
					{
						e.printStackTrace();
					}
	            }
            }
        );
	}
    
    private void _closeLinkFile() 
    {
    	_file.close();
	}
    
    private void _syncWithDropbox()
    {
    	try 
        {
    		_file.update();
            editText.setText(_file.readString());
        }
        catch(IOException e)
        {
            Toast.makeText(this,  e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
    
    private void _saveToDropbox()
    {
    	try 
    	{
        	_file.writeString(editText.getText().toString());
    	} 
    	catch(DbxException e)
    	{
    		Toast.makeText(this,  e.getMessage(), Toast.LENGTH_LONG).show();
    	}
    	catch(DbxFile.StreamExclusionException e)
    	{
    		Toast.makeText(this,  e.getMessage(), Toast.LENGTH_LONG).show();
    	}
    	catch(IOException e)
        {
        	Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
