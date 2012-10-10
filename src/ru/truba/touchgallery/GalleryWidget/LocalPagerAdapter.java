package ru.truba.touchgallery.GalleryWidget;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
		LayoutParams p = new LayoutParams();
		p.width = LayoutParams.MATCH_PARENT;
		p.height = LayoutParams.MATCH_PARENT;
		iv.setLayoutParams(p);

		((ViewPager) collection).addView(iv, 0);

		new ImageTask(iv).execute(position);

		return iv;
	}

	class ImageTask extends AsyncTask<Integer, Object, Bitmap> {
		PZSImageView v;

		public ImageTask(PZSImageView v) {
			this.v = v;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null)
				v.setImageBitmap(result);
		}

		@Override
		protected Bitmap doInBackground(Integer... params) {
			Bitmap b = null;
			if (mImagesInt != null) {
				b = BitmapFactory.decodeResource(mContext.getResources(),
						mImagesInt[params[0]]);

			} else if (mImagesString != null) {
				try {
					if (mImagesString[params[0]].contains(Environment
							.getExternalStorageDirectory().toString())) {
						b = BitmapFactory.decodeStream(new FileInputStream(
								new File((mImagesString[params[0]]))));
					} else {
						b = BitmapFactory.decodeStream(mContext.getAssets()
								.open(mImagesString[params[0]]));
					}
				} catch (IOException e) {
					Log.e("IOException", mImagesString[params[0]]);
				}
			}
			return b;
		}
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