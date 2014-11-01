package com.minixalpha.webo;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import com.minixalpha.control.Configure;
import com.minixalpha.model.LocalEvent;
import com.minixalpha.util.Utils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ShowImageActivity extends Activity {
	private static final String TAG = "ShowImageActivity";
	PhotoViewAttacher mAttacher;
	PhotoView mImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_image);
		
		Intent intent = getIntent();
		String imageURL = intent.getStringExtra(LocalEvent.IMAGES_URL);
		String imageMidURL = Utils.getMiddlePicURL(imageURL);
		Log.d(TAG, imageURL);
		Log.d(TAG, imageMidURL);
		mImageView = (PhotoView) findViewById(R.id.full_img);

		// 使用 loadImage 而不使用 displayImage 原因：图片位置不居中
		WeboApplication.getImageLoader().loadImage(imageMidURL,
				Configure.getFullScreenImageDisplayImageOptions(),
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {

					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {

					}

					@Override
					public void onLoadingComplete(String arg0, View view,
							Bitmap bitmap) {
						mImageView.setImageBitmap(bitmap);
						mAttacher = new PhotoViewAttacher(mImageView);

					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
					}
				});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.show_image, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
