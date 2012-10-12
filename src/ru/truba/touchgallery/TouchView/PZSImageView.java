package ru.truba.touchgallery.TouchView;

import com.halodev.touchgallery.GalleryActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * class Pinch Zoom & Swipe Image View.
 * 
 * @author huewu.yang
 * @date 2012. 08. 23
 */
public class PZSImageView extends ImageView {

	enum ImageScaleType {
		FitCenter, TopCrop, CenterCrop
	};

	public ImageScaleType defaultScaleType = ImageScaleType.FitCenter;
	public ImageScaleType doubleTapScaleType = ImageScaleType.TopCrop;

	private static final String TAG = "GalleryImageView"; // debug tag.

	// wrapped motion event code.
	protected static final int PZS_ACTION_INIT = 100;
	protected static final int PZS_ACTION_SCALE = 1001;
	protected static final int PZS_ACTION_TRANSLATE = 1002;
	protected static final int PZS_ACTION_SCALE_TO_TRANSLATE = 1003;
	protected static final int PZS_ACTION_TRANSLATE_TO_SCALE = 1004;
	protected static final int PZS_ACTION_FIT_CENTER = 1005;
	protected static final int PZS_ACTION_CENTER_CROP = 1006;
	protected static final int PZS_ACTION_TO_LEFT_SIDE = 1007;
	protected static final int PZS_ACTION_TO_RIGHT_SIDE = 1008;
	protected static final int PZS_ACTION_TOP_CROP = 1009;
	protected static final int PZS_ACTION_CANCEL = -1;

	// TODO below 2 values should be able to set from attributes.
	private final static float MAX_SCALE_TO_SCREEN = 2.f;
	private final static float MIN_SCALE_TO_SCREEN = 1.f;

	private static final float MIN_SCALE_SPAN = 10.f;

	// calculated min / max scale ratio based on image & screen size.
	private float mMinScaleFactor = 1.f;
	private float mMaxScaleFactor = 2.f;

	private boolean mIsFirstDraw = true; // check flag to calculate necessary
											// init values.
	private int mImageWidth; // current set image width
	private int mImageHeight; // current set image height

	private Context mContext;

	/**
	 * constructor
	 * 
	 * @param context
	 */
	public PZSImageView(Context context) {
		super(context);
		mContext = context;
		init();

	}

	/**
	 * constructor
	 * 
	 * @param context
	 */
	public PZSImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();

	}

	/**
	 * constructor
	 * 
	 * @param context
	 */
	public PZSImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	private void init() {
		// should use matrix scale type.
		setScaleType(ScaleType.MATRIX);
		Matrix mat = getImageMatrix();
		mat.reset();
		setImageMatrix(mat);

		gd = new GestureDetector(mContext, new SimpleOnGestureListener() {

			@Override
			public boolean onDoubleTap(MotionEvent event) {
				int action = parseDoubleTapMotionEvent(event);
				touchAction(action, event);
				return true; // indicate event was handled
			}

			@Override
			public boolean onSingleTapConfirmed(MotionEvent ev) {
				if (ev.getX() < 200) {
					//TODO connected to demo activity
					((GalleryActivity) mContext).leftBtn();
				} else if (ev.getX() > getWidth() - 200) {
					//TODO connected to demo activity
					((GalleryActivity) mContext).rightBtn();
				}
				return true;
			}

		});
	}

	GestureDetector gd;

	// TODO how to handle bitmaps that set as different ways. (by res or src
	// attr)
	// TODO in that case this view should be worked just as a normal image view.
	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);

		mIsFirstDraw = true;
		mImageWidth = bm.getWidth();
		mImageHeight = bm.getHeight();
	}

	@Override
	protected void onDraw(Canvas canvas) {

		if (mIsFirstDraw == true) {
			mIsFirstDraw = false;
			if (defaultScaleType == ImageScaleType.FitCenter)
				fitCenter();
			else if (defaultScaleType == ImageScaleType.TopCrop)
				topCrop();
			else if (defaultScaleType == ImageScaleType.CenterCrop)
				centerCrop();
			calculateScaleFactorLimit();
			validateMatrix();
		}

		setImageMatrix(mCurrentMatrix);
		// canvas.drawRGB(200, 0, 0);

		super.onDraw(canvas);
	}

	private void calculateScaleFactorLimit() {

		// set max / min scale factor.
		mMaxScaleFactor = Math.max(getHeight() * MAX_SCALE_TO_SCREEN
				/ mImageHeight, getWidth() * MAX_SCALE_TO_SCREEN / mImageWidth);

		mMinScaleFactor = Math.min(getHeight() * MIN_SCALE_TO_SCREEN
				/ mImageHeight, getWidth() * MIN_SCALE_TO_SCREEN / mImageWidth);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gd.onTouchEvent(event)) {
			return true;
		}
		int action = parseMotionEvent(event);
		touchAction(action, event);
		return true; // indicate event was handled
	}

	private void touchAction(int action, MotionEvent event) {
		switch (action) {
		case PZS_ACTION_INIT:
			initGestureAction(event.getX(), event.getY());
			break;
		case PZS_ACTION_SCALE:
			handleScale(event);
			break;
		case PZS_ACTION_TRANSLATE:
			handleTranslate(event);
			break;
		case PZS_ACTION_TRANSLATE_TO_SCALE:
			initGestureAction(event.getX(), event.getY());
			break;
		case PZS_ACTION_SCALE_TO_TRANSLATE:
			int activeIndex = (event.getActionIndex() == 0 ? 1 : 0);
			initGestureAction(event.getX(activeIndex), event.getY(activeIndex));
			break;
		case PZS_ACTION_FIT_CENTER:
			fitCenter();
			initGestureAction(event.getX(), event.getY());
			break;
		case PZS_ACTION_CENTER_CROP:
			centerCrop();
			initGestureAction(event.getX(), event.getY());
			break;
		case PZS_ACTION_TOP_CROP:
			topCrop();
			initGestureAction(event.getX(), event.getY());
			break;
		case PZS_ACTION_TO_LEFT_SIDE:
			toLeftSide();
			break;
		case PZS_ACTION_TO_RIGHT_SIDE:
			toRightSide();
			break;
		case PZS_ACTION_CANCEL:
			break;
		}

		// check current position of bitmap.
		validateMatrix();
		updateMatrix();
	}

	private int parseDoubleTapMotionEvent(MotionEvent ev) {
		float values[] = new float[9];
		mCurrentMatrix.getValues(values);
		float scaleNow = values[Matrix.MSCALE_X];
		float scaleX = (getWidth() - getPaddingLeft() - getPaddingRight())
				/ (float) mImageWidth;
		float scaleY = (getHeight() - getPaddingTop() - getPaddingBottom())
				/ (float) mImageHeight;
		if (scaleNow >= Math.max(scaleX, scaleY))
			return PZS_ACTION_FIT_CENTER;
		else if (scaleNow < Math.max(scaleX, scaleY)) {
			if (doubleTapScaleType == ImageScaleType.FitCenter)
				return PZS_ACTION_FIT_CENTER;
			else if (doubleTapScaleType == ImageScaleType.TopCrop)
				return PZS_ACTION_TOP_CROP;
			else if (doubleTapScaleType == ImageScaleType.CenterCrop)
				return PZS_ACTION_CENTER_CROP;

		}
		return PZS_ACTION_FIT_CENTER;
	}

	private int parseMotionEvent(MotionEvent ev) {

		switch (ev.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			return PZS_ACTION_INIT;
		case MotionEvent.ACTION_POINTER_DOWN:
			// more than one pointer is pressed...
			return PZS_ACTION_TRANSLATE_TO_SCALE;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			if (ev.getPointerCount() == 2) {
				return PZS_ACTION_SCALE_TO_TRANSLATE;
			} else {
				return PZS_ACTION_INIT;
			}
		case MotionEvent.ACTION_MOVE:
			if (ev.getPointerCount() == 1)
				return PZS_ACTION_TRANSLATE;
			else if (ev.getPointerCount() == 2)
				return PZS_ACTION_SCALE;
			return 0;
		}
		return 0;
	}

	// ///////////////////////////////////////////////
	// Related matrix calculation stuffs.
	// ///////////////////////////////////////////////

	private Matrix mCurrentMatrix = new Matrix();
	private Matrix mSavedMatrix = new Matrix();

	// Remember some things for zooming
	private PointF mStartPoint = new PointF();
	private PointF mMidPoint = new PointF();
	private float mInitScaleSpan = 1f;

	protected void initGestureAction(float x, float y) {
		mSavedMatrix.set(mCurrentMatrix);
		mStartPoint.set(x, y);
		mInitScaleSpan = 0.f;
	}

	protected void handleScale(MotionEvent event) {
		float newSpan = spacing(event);

		// if two finger is too close, pointer index is bumped.. so just ignore
		// it.
		if (newSpan < MIN_SCALE_SPAN)
			return;

		if (mInitScaleSpan == 0.f) {
			// init values. scale gesture action is just started.
			mInitScaleSpan = newSpan;
			midPoint(mMidPoint, event);
		} else {
			float scale = normalizeScaleFactor(mSavedMatrix, newSpan,
					mInitScaleSpan);
			mCurrentMatrix.set(mSavedMatrix);
			mCurrentMatrix.postScale(scale, scale, mMidPoint.x, mMidPoint.y);
		}
	}

	private float normalizeScaleFactor(Matrix curMat, float newSpan,
			float stdSpan) {

		float values[] = new float[9];
		curMat.getValues(values);
		float scale = values[Matrix.MSCALE_X];

		if (stdSpan == newSpan) {
			return scale;
		} else {
			float newScaleFactor = newSpan / stdSpan;
			float candinateScale = scale * newScaleFactor;

			if (candinateScale > mMaxScaleFactor) {
				return mMaxScaleFactor / scale;
			} else if (candinateScale < mMinScaleFactor) {
				return mMinScaleFactor / scale;
			} else {
				return newScaleFactor;
			}
		}
	}

	protected void handleTranslate(MotionEvent event) {
		mCurrentMatrix.set(mSavedMatrix);
		mCurrentMatrix.postTranslate(event.getX() - mStartPoint.x, event.getY()
				- mStartPoint.y);
	}

	private RectF mTraslateLimitRect = new RectF(); // reuse instance.

	public boolean getOnLeftSide() {
		float values[] = new float[9];
		mCurrentMatrix.getValues(values);
		float tranX = values[Matrix.MTRANS_X];
		if (tranX >= mTraslateLimitRect.right) {
			return true;
		}
		return false;
	}

	public boolean getOnRightSide() {
		float values[] = new float[9];
		mCurrentMatrix.getValues(values);
		float tranX = values[Matrix.MTRANS_X];
		if (tranX <= mTraslateLimitRect.left) {
			return true;
		}
		return false;
	}

	private void validateMatrix() {
		float values[] = new float[9];
		mCurrentMatrix.getValues(values);

		// get current matrix values.
		float scale = values[Matrix.MSCALE_X];
		float tranX = values[Matrix.MTRANS_X];
		float tranY = values[Matrix.MTRANS_Y];

		int imageHeight = (int) (scale * mImageHeight);
		int imageWidth = (int) (scale * mImageWidth);

		mTraslateLimitRect.setEmpty();
		// don't think about optimize code. first, just write code case by case.

		// check TOP & BOTTOM
		if (imageHeight > getHeight()) {
			// image height is taller than view
			mTraslateLimitRect.top = getHeight() - imageHeight
					- getPaddingTop() - getPaddingBottom();
			mTraslateLimitRect.bottom = 0.f;
		} else {
			mTraslateLimitRect.top = mTraslateLimitRect.bottom = (getHeight()
					- imageHeight - getPaddingTop() - getPaddingBottom()) / 2.f;
		}

		// check LEFT & RIGHT
		if (imageWidth > getWidth()) {
			// image width is longer than view
			mTraslateLimitRect.left = getWidth() - imageWidth
					- getPaddingRight() - getPaddingLeft();
			mTraslateLimitRect.right = 0.f;
		} else {
			mTraslateLimitRect.left = mTraslateLimitRect.right = (getWidth()
					- imageWidth - getPaddingLeft() - getPaddingRight()) / 2.f;
		}

		float newTranX = tranX;
		newTranX = Math.max(newTranX, mTraslateLimitRect.left);
		newTranX = Math.min(newTranX, mTraslateLimitRect.right);

		float newTranY = tranY;
		newTranY = Math.max(newTranY, mTraslateLimitRect.top);
		newTranY = Math.min(newTranY, mTraslateLimitRect.bottom);

		values[Matrix.MTRANS_X] = newTranX;
		values[Matrix.MTRANS_Y] = newTranY;
		mCurrentMatrix.setValues(values);

		if (mTraslateLimitRect.contains(tranX, tranY) == false) {
			// set new start point.
			mStartPoint.offset(tranX - newTranX, tranY - newTranY);
		}
	}

	protected void updateMatrix() {
		setImageMatrix(mCurrentMatrix);
	}

	protected void fitCenter() {
		// move image to center....
		mCurrentMatrix.reset();

		float scaleX = (getWidth() - getPaddingLeft() - getPaddingRight())
				/ (float) mImageWidth;
		float scaleY = (getHeight() - getPaddingTop() - getPaddingBottom())
				/ (float) mImageHeight;
		float scale = Math.min(scaleX, scaleY);

		float dx = (getWidth() - getPaddingLeft() - getPaddingRight() - mImageWidth
				* scale) / 2.f;
		float dy = (getHeight() - getPaddingTop() - getPaddingBottom() - mImageHeight
				* scale) / 2.f;
		mCurrentMatrix.postScale(scale, scale);
		mCurrentMatrix.postTranslate(dx, dy);
		setImageMatrix(mCurrentMatrix);
	}

	public void toLeftSide() {
		float values[] = new float[9];
		mCurrentMatrix.getValues(values);
		float tranX = values[Matrix.MTRANS_X];
		mCurrentMatrix.postTranslate(mTraslateLimitRect.right - tranX, 0);
		setImageMatrix(mCurrentMatrix);
	}

	public void toRightSide() {
		float values[] = new float[9];
		mCurrentMatrix.getValues(values);
		float tranX = values[Matrix.MTRANS_X];
		mCurrentMatrix.postTranslate(mTraslateLimitRect.left - tranX, 0);
		setImageMatrix(mCurrentMatrix);
	}

	protected void centerCrop() {
		mCurrentMatrix.reset();

		float scaleX = (getWidth() - getPaddingLeft() - getPaddingRight())
				/ (float) mImageWidth;
		float scaleY = (getHeight() - getPaddingTop() - getPaddingBottom())
				/ (float) mImageHeight;
		float scale = Math.max(scaleX, scaleY);

		float dx = (getWidth() - getPaddingLeft() - getPaddingRight() - mImageWidth
				* scale) / 2.f;
		float dy = (getHeight() - getPaddingTop() - getPaddingBottom() - mImageHeight
				* scale) / 2.f;

		mCurrentMatrix.postScale(scale, scale);
		mCurrentMatrix.postTranslate(dx, dy);
		setImageMatrix(mCurrentMatrix);
	}

	protected void topCrop() {
		mCurrentMatrix.reset();

		float scaleX = (getWidth() - getPaddingLeft() - getPaddingRight())
				/ (float) mImageWidth;
		float scaleY = (getHeight() - getPaddingTop() - getPaddingBottom())
				/ (float) mImageHeight;
		float scale = Math.max(scaleX, scaleY);

		mCurrentMatrix.postScale(scale, scale);
		mCurrentMatrix.postTranslate(0, 0);
		setImageMatrix(mCurrentMatrix);
	}

	/** Determine the space between the first two fingers */
	private float spacing(MotionEvent event) {
		// ...
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/** Calculate the mid point of the first two fingers */
	private void midPoint(PointF point, MotionEvent event) {
		// ...
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

}// end of class
