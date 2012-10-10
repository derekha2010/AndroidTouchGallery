package ru.truba.touchgallery.GalleryWidget;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import ru.truba.touchgallery.TouchView.PZSImageView;

public class LocalPagerAdapter extends PagerAdapter {

	private int mImagesInt[];
	private String mImagesString[];

	private Context mContext;

	public LocalPagerAdapter(Context context) {
		this.mContext = context;
	}

	public LocalPagerAdapter(Context context, int[] images) {
		this.mContext = context;
		mImagesInt = images;
	}

	public LocalPagerAdapter(Context context, String[] images) {
		this.mContext = context;
		mImagesString = images;
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		super.setPrimaryItem(container, position, object);
		((GalleryViewPager) container).mCurrentView = ((PZSImageView) object);
	}

	@Override
	public Object instantiateItem(View collection, int position) {
		PZSImageView iv = new PZSImageView(mContext);

		Bitmap b = null;
		if (mImagesInt != null) {
			b = BitmapFactory.decodeResource(mContext.getResources(),
					mImagesInt[position]);

		} else if (mImagesString != null) {
			try {
				if (mImagesString[position].contains(Environment
						.getExternalStorageDirectory().toString())) {
					b = BitmapFactory.decodeStream(new FileInputStream(
							new File((mImagesString[position]))));
				} else {
					b = BitmapFactory.decodeStream(mContext.getAssets().open(
							mImagesString[position]));
				}
				iv.setImageBitmap(b);
			} catch (IOException e) {
				Log.e("IOException", mImagesString[position]);
			}
		}
		LayoutParams p = new LayoutParams();
		p.width = LayoutParams.MATCH_PARENT;
		p.height = LayoutParams.MATCH_PARENT;
		iv.setLayoutParams(p);

		((ViewPager) collection).addView(iv, 0);
		return iv;
	}

	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView((View) view);
	}

	@Override
	public int getCount() {
		if (mImagesInt != null)
			return mImagesInt.length;
		else if (mImagesString != null)
			return mImagesString.length;
		else
			return 0;

	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	@Override
	public void finishUpdate(View arg0) {
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}

}