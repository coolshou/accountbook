package net.nctucs.lazchi.marco79423.ExpenseBook;

//java
import java.io.IOException;

//Android
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;

//介面
import android.view.View;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.Toast;

//工具



public class CameraActivity extends Activity implements View.OnClickListener
{
	private Button _saveButton;
	private Button _continueButton;
	private SurfaceView _surfacePreview;

	private Camera _camera;
	private SurfaceHolder _surfaceHolder;

	private boolean _isQuickSave = false;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);

		_saveButton = (Button) findViewById(R.id.camera_button_save);
		_continueButton = (Button) findViewById(R.id.camera_button_continue);
		_surfacePreview = (SurfaceView) findViewById(R.id.camera_surface);

		_saveButton.setOnClickListener(this);
		_continueButton.setOnClickListener(this);

		_surfaceHolder = _surfacePreview.getHolder();
		_surfaceHolder.addCallback(_surfaceCallBack);
		// deprecated setting, but required on Android versions prior to 3.0
		_surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}


	@Override
	public void onClick(View view)
	{
		switch(view.getId())
		{
		case R.id.camera_button_save: _onSaveButtonClicked(); break;
		case R.id.camera_button_continue:  _onContinueButtonClicked(); break;
		}
	}

	private void _onSaveButtonClicked()
	{
		_isQuickSave = true;
		_camera.takePicture(null, null, _pictureCallback);
	}

	private void _onContinueButtonClicked()
	{
		_isQuickSave = false;
		_camera.takePicture(null, null, _pictureCallback);
	}

	private void _quickSaveExpense(byte[] bytes)
	{
		ExpenseSqlModel expenseSqlModel = new ExpenseSqlModel(this);
		expenseSqlModel.open();
		expenseSqlModel.addExpense(bytes);
		expenseSqlModel.close();
	}

	SurfaceHolder.Callback _surfaceCallBack = new SurfaceHolder.Callback()
	{
		@Override
		public void surfaceCreated(SurfaceHolder surfaceHolder)
		{
			// 打開相機
			_camera = Camera.open();
			try
			{
				//設定預覽
				_camera.setPreviewDisplay(_surfaceHolder);
				_camera.startPreview();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
		{
			// preview surface does not exist
			if (holder.getSurface() == null){
				return;
			}

			// stop preview before making changes
			try
			{
				_camera.stopPreview();
			}
			catch (Exception e)
			{
				// ignore: tried to stop a non-existent preview
			}

			// 取得相機參數
			Camera.Parameters parameters = _camera.getParameters();
			// 設定照片大小
			parameters.setPreviewSize(width, height);
			// 設定照片格式
			parameters.setPictureFormat(ImageFormat.JPEG);
			// 設定相機參數
			_camera.setParameters(parameters);

			// 開始預覽
			try
			{
				_camera.setPreviewDisplay(holder);
				_camera.startPreview();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder surfaceHolder)
		{
			// 停止預覽
			_camera.stopPreview();
			// 釋放相機資源
			_camera.release();
			_camera = null;
		}
	};

	//拍照回呼
	Camera.PictureCallback _pictureCallback = new Camera.PictureCallback()
	{
		@Override
		public void onPictureTaken(byte[] bytes, Camera camera)
		{
			Intent intent = new Intent();
			if(_isQuickSave)
			{
				_quickSaveExpense(bytes);
				intent.setClass(CameraActivity.this, StatisticsActivity.class);
			}
			else
			{
				intent.setClass(CameraActivity.this, ExpenseActivity.class);
				intent.putExtra("pictureBytes", bytes);
			}
			startActivity(intent);
			CameraActivity.this.finish();
		}
	};
}