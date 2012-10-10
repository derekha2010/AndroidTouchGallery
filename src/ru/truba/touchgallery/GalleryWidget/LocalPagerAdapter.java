package ru.truba.touchgallery.GalleryWidget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import ru.truba.touchgallery.TouchView.PZSImageView;

public class LocalPagerAdapter extends PagerAdapter {

	private int mImages[];

	private Context mContext;

	public LocalPagerAdapter(Context context) {
		this.mContext = context;
	}

	public LocalPagerAdapter(Context context, int[] images) {
		this.mContext = context;
		mImages = images;
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		super.setPrimaryItem(container, position, object);
		((GalleryViewPager) container).mCurrentView = ((PZSImageView) object);
	}

	@Override
	public Object instantiateItem(View collection, int position) {
		PZSImageView iv = new PZSImageView(mContext);
		Bitmap b = BitmapFactory.decodeResource(mContext.getResources(),
				mImages[position]);
		iv.setImageBitmap(b);
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
		return mImages.length;
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