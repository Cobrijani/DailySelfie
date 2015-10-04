package com.example.dailyselfie;

import android.graphics.Bitmap;

public class DailySelfieImage {

	private Bitmap image;
	private String imageName;
	private String imagePath;

	public DailySelfieImage(Bitmap image, String imagePath) {
		this.image = image;
		this.imagePath = imagePath;
		getImageName();
	}

	public String getImageName() {
		if (imagePath.length() > 0)
			this.imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1, imagePath.length());

		return imageName;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

}
