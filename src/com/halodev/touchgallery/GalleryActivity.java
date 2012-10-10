package com.halodev.touchgallery;

import android.app.Activity;
import android.os.Bundle;
import com.halodev.touchgallery.R;
import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;
import ru.truba.touchgallery.GalleryWidget.LocalPagerAdapter;
import ru.truba.touchgallery.GalleryWidget.UrlPagerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GalleryActivity extends Activity {

	private GalleryViewPager mViewPager;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
/*
		// UrlPagerAdapter
		String[] urls = {
				"http://cs407831.userapi.com/v407831207/18f6/jBaVZFDhXRA.jpg",
				"http://cs407831.userapi.com/v407831207/18fe/4Tz8av5Hlvo.jpg",
				"http://cs407831.userapi.com/v407831207/1906/oxoP6URjFtA.jpg",
				"http://cs407831.userapi.com/v407831207/190e/2Sz9A774hUc.jpg",
				"http://cs407831.userapi.com/v407831207/1916/Ua52RjnKqjk.jpg",
				"http://cs407831.userapi.com/v407831207/191e/QEQE83Ok0lQ.jpg" };
		List<String> items = new ArrayList<String>();
		Collections.addAll(items, urls);

		UrlPagerAdapter urlPagerAdapter = new UrlPagerAdapter(this, items);
*/
		// LocalPagerAdapter
		
		//From res
		int images[] = { R.drawable.chrysanthemum, R.drawable.desert,
				R.drawable.hydrangeas, R.drawable.penguins };
		
		//From assets
		String imagesString[] = { "images/chrysanthemum.jpg", "images/desert.jpg",
				"images/hydrangeas.jpg","images/penguins.jpg" };
		
		LocalPagerAdapter localPagerAdapter = new LocalPagerAdapter(this,
				imagesString);
		mViewPager = (GalleryViewPager) findViewById(R.id.viewer);
		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setAdapter(localPagerAdapter);
	}

}