package com.example.dailyselfie;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends ListActivity {

	static final int REQUEST_TAKE_PHOTO = 1;
	public static final String BITMAP_IMAGE = "BitmapImage";

	public static final String TAG = "Daily selfie";

	private String mCurrentPhotoPath;
	private DailySelfieListAdapter mAdapter;

	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

	private AlarmManager mAlarmManager;
	private Intent mSelfieBroadcastIntent;
	private PendingIntent mSelfieBroadcastPendingIntent;

	private static final long INTERVAL_TIME_TWO_MINS = 2 * 60 * 1000;

	private String getAlbumName() {
		return getString(R.string.app_camera_album);
	}

	private File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

			if (storageDir != null) {
				if (!storageDir.mkdirs()) {
					if (!storageDir.exists()) {
						Log.d("DailySelfie", "Failed to create directory");
						return null;
					}
				}
			}
		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}
		return storageDir;
	}

	private void galleryAddPic() {
		Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		File f = new File(mCurrentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
	}

	private void handleBigCameraPhoto() {
		if (mCurrentPhotoPath != null) {
			setPic();
			galleryAddPic();
			mCurrentPhotoPath = null;
		}
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public static Bitmap decodeBitmapFromFile(String filepath, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filepath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filepath, options);
	}

	private void setPic() {

		Bitmap bitmap = decodeBitmapFromFile(mCurrentPhotoPath, 120, 100);

		if (bitmap != null) {

			DailySelfieImage newSelfieImage = new DailySelfieImage(bitmap, mCurrentPhotoPath);
			mAdapter.add(newSelfieImage);
		}
	}

	private File createImageFile() throws IOException {

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "IMG_" + timeStamp + "_";
		File storageDir = getAlbumDir();
		File image = File.createTempFile(imageFileName, ".jpg", storageDir);

		return image;

	}

	private File setUpPhotoFile() throws IOException {
		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();

		return f;
	}

	public void dispatchTakePictureIntent() {

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			File photoFile = null;

			try {
				photoFile = setUpPhotoFile();
				mCurrentPhotoPath = photoFile.getAbsolutePath();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (photoFile != null) {
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

				startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
			}
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "Entered onCreate()");
		super.onCreate(savedInstanceState);

		mAdapter = new DailySelfieListAdapter(getApplicationContext());
		mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		setListAdapter(mAdapter);

		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				DailySelfieImage image = (DailySelfieImage) parent.getAdapter().getItem(position);

				Intent zoomImageIntent = new Intent(MainActivity.this, ImageActivity.class);
				zoomImageIntent.putExtra(BITMAP_IMAGE, image.getImagePath());

				startActivity(zoomImageIntent);
			}
		});

		setAlarm();

	}

	private void setAlarm() {

		mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		mSelfieBroadcastIntent = new Intent(MainActivity.this, SelfieBroadcast.class);
		mSelfieBroadcastPendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, mSelfieBroadcastIntent, 0);

	}

	private void startAlarm() {
		Log.i(TAG, "Starting alarm service");
		mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INTERVAL_TIME_TWO_MINS,
				INTERVAL_TIME_TWO_MINS, mSelfieBroadcastPendingIntent);
	}

	private void stopAlarm() {
		Log.i(TAG, "Stopping alarm service");
		mAlarmManager.cancel(mSelfieBroadcastPendingIntent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
			handleBigCameraPhoto();

		}
	}

	public void loadItems() {
		File albumdir = getAlbumDir();
		File[] files = albumdir.listFiles();

		if (files != null) {
			for (File child : files) {
				Bitmap image = decodeBitmapFromFile(child.getAbsolutePath(), 120, 100);
				mAdapter.add(new DailySelfieImage(image, child.getAbsolutePath()));
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is
		// present.--------`
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.camera_menu_item) {
			dispatchTakePictureIntent();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "Entered onResume()");
		super.onResume();

		if (mAdapter.getCount() == 0) {
			loadItems();
		}
		stopAlarm();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "Entered onPause()");
		super.onPause();
		startAlarm();

	}
}
