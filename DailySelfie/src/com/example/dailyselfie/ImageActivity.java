package com.example.dailyselfie;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;

public class ImageActivity extends Activity {

	ImageView zoomedImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);

		zoomedImage = (ImageView) findViewById(R.id.image_view_zoom);

		Intent imageIntent = getIntent();
		String imagePath = imageIntent.getStringExtra(MainActivity.BITMAP_IMAGE);
		new LoadBitmapAsyncTask().execute(imagePath);
	}

	public static Bitmap rotateBitmap(Bitmap bitmap, float degrees) {

		Matrix matrix = new Matrix();
		matrix.postRotate(degrees);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

	}

	class LoadBitmapAsyncTask extends AsyncTask<String, Void, Bitmap> {

		ProgressDialog progDialog;

		@Override
		protected Bitmap doInBackground(String... params) {
			DisplayMetrics display = getResources().getDisplayMetrics();
			Bitmap bitmap = MainActivity.decodeBitmapFromFile(params[0], display.widthPixels, display.heightPixels);

			return rotateBitmap(bitmap, 90);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDialog = new ProgressDialog(ImageActivity.this);

			progDialog.setMessage("Loading image...");
			progDialog.setIndeterminate(false);
			progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDialog.setCancelable(true);
			progDialog.show();
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			progDialog.dismiss();
			zoomedImage.setImageBitmap(result);
		}

	}

}
