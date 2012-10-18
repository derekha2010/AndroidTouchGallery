package com.halodev.touchgallery.example;

import android.os.Bundle;

import com.halodev.touchgallery.AbstractGalleryActivity;
import com.halodev.touchgallery.example.R;
import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;
import ru.truba.touchgallery.GalleryWidget.LocalPagerAdapter;

public class GalleryActivity extends AbstractGalleryActivity {

	private GalleryViewPager mViewPager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// UrlPagerAdapter
		// String[] urls = {
		// "http://cs407831.userapi.com/v407831207/18f6/jBaVZFDhXRA.jpg",
		// "http://cs407831.userapi.com/v407831207/18fe/4Tz8av5Hlvo.jpg",
		// "http://cs407831.userapi.com/v407831207/1906/oxoP6URjFtA.jpg",
		// "http://cs407831.userapi.com/v407831207/190e/2Sz9A774hUc.jpg",
		// "http://cs407831.userapi.com/v407831207/1916/Ua52RjnKqjk.jpg",
		// "http://cs407831.userapi.com/v407831207/191e/QEQE83Ok0lQ.jpg" };
		// List<String> items = new ArrayList<String>();
		// Collections.addAll(items, urls);
		//
		// UrlPagerAdapter urlPagerAdapter = new UrlPagerAdapter(this, items);

		// LocalPagerAdapter
		// From res
		// int images[] = { R.drawable.chrysanthemum, R.drawable.desert,
		// R.drawable.hydrangeas, R.drawable.penguins };

		// From assets
		String imagesString[] = { "images/chrysanthemum.jpg",
				"images/desert.jpg", "images/hydrangeas.jpg",
				"images/penguins.jpg" };

		// From sdcard
		// String externalStorageDirectory = Environment
		// .getExternalStorageDirectory().toString();
		// String imagesString[] = {
		// externalStorageDirectory + "/Pictures/chrysanthemum.jpg",
		// externalStorageDirectory + "/Pictures/desert.jpg",
		// externalStorageDirectory + "/Pictures/hydrangeas.jpg",
		// externalStorageDirectory + "/Pictures/penguins.jpg" };

		LocalPagerAdapter localPagerAdapter = new LocalPagerAdapter(this,
				imagesString);
		mViewPager = (GalleryViewPager) findViewById(R.id.viewer);
		mViewPager.setPageMargin(10);
		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setAdapter(localPagerAdapter);
	}

	@Override
	public void leftBtn() {
		if (mViewPager.mCurrentView.getOnLeftSide()) {
			mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
		} else {
			mViewPager.mCurrentView.toLeftSide();

		}
	}

	@Override
	public void rightBtn() {
		if (mViewPager.mCurrentView.getOnRightSide()) {
			mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
		} else {
			mViewPager.mCurrentView.toRightSide();

		}

	}

	@Override
	public void topBtn() {

	}

	@Override
	public void bottomBtn() {

	}

	@Override
	public void centerBtn() {

	}
}