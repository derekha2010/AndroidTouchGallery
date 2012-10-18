package com.halodev.touchgallery;

import android.app.Activity;

public abstract class AbstractGalleryActivity extends Activity {

	public abstract void leftBtn();

	public abstract void rightBtn();
	
	public abstract void topBtn();
	
	public abstract void bottomBtn();
	
	public abstract void centerBtn();
}