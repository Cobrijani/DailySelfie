package com.example.dailyselfie;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class MainActivity extends Activity {

	static final int REQUEST_TAKE_PHOTO = 1;

	private String mCurrentPhotoPath;

	private File createImageFile() throws IOException {

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "IMG_" + timeStamp + "_";
		File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(imageFileName, ".jpg", storageDir);

		mCurrentPhotoPath = "file:" + image.getAbsolutePath();

		return image;

	}

	public void dispatchTakePictureIntent() {

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// File photoFile = null;
			//
			// try {
			// photoFile = createImageFile();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
			// if (photoFile != null) {
			// takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
			// Uri.fromFile(photoFile));
			//
			// startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
			// }
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();

			Bitmap imageBitmap = (Bitmap) extras.get("data");
			ImageView imageView = (ImageView) findViewById(R.id.test_image);
			imageView.setImageBitmap(imageBitmap);

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
}
