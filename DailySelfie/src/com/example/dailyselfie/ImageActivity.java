package com.example.dailyselfie;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;

public class ImageActivity extends Activity {

	ImageView zoomedImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);

		DisplayMetrics display = getResources().getDisplayMetrics();

		zoomedImage = (ImageView) findViewById(R.id.image_view_zoom);

		Intent imageIntent = getIntent();
		String imagePath = imageIntent.getStringExtra(MainActivity.BITMAP_IMAGE);
		Bitmap bitMap = MainActivity.decodeBitmapFromFile(imagePath, display.widthPixels, display.heightPixels);

		zoomedImage.setImageBitmap(rotateBitmap(bitMap, 90));

	}

	public static Bitmap rotateBitmap(Bitmap bitmap, float degrees) {

		Matrix matrix = new Matrix();
		matrix.postRotate(degrees);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

	}

}
