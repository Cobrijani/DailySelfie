package com.example.dailyselfie;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DailySelfieListAdapter extends BaseAdapter {

	private final List<DailySelfieImage> mImages;
	private final Context mContext;
	private static LayoutInflater inflater = null;

	public DailySelfieListAdapter(Context mContext) {
		this.mImages = new ArrayList<DailySelfieImage>();
		this.mContext = mContext;
		inflater = LayoutInflater.from(mContext);
	}

	public void add(DailySelfieImage image) {
		mImages.add(image);
		notifyDataSetChanged();
	}

	public void clear() {
		mImages.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mImages.size();
	}

	@Override
	public Object getItem(int position) {

		return mImages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		ImageView image;
		TextView imageName;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View newView = convertView;
		ViewHolder holder;

		DailySelfieImage curr = (DailySelfieImage) getItem(position);

		if (null == convertView) {
			holder = new ViewHolder();
			newView = inflater.inflate(R.layout.daily_selfie_layout, parent, false);
			holder.image = (ImageView) newView.findViewById(R.id.image_layout);
			holder.imageName = (TextView) newView.findViewById(R.id.image_name_layout);
			newView.setTag(holder);
		} else {
			holder = (ViewHolder) newView.getTag();
		}
		
		holder.image.setImageBitmap(curr.getImage());
		holder.imageName.setText(curr.getImageName());

		return newView;
	}

}
