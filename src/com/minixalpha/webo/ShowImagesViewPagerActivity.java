package com.minixalpha.webo;

import java.util.List;

import com.minixalpha.control.Configure;
import com.minixalpha.model.LocalEvent;
import com.minixalpha.util.Utils;

import uk.co.senab.photoview.PhotoView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class ShowImagesViewPagerActivity extends Activity {
	ViewPager mImagesViewPager;
	List<String> mImgesURLList;
	int mImagePos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_images_view_pager);

		Intent intent = getIntent();
		mImgesURLList = intent.getStringArrayListExtra(LocalEvent.IMAGES_URL);
		mImagePos = intent.getIntExtra(LocalEvent.IMAGE_POS, 0);
		mImagesViewPager = (ViewPager) findViewById(R.id.images_view_pager);
		mImagesViewPager.setAdapter(new ImagesPagerAdapter());
		mImagesViewPager.setCurrentItem(mImagePos);
	}

	private class ImagesPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mImgesURLList.size();
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {
			PhotoView photoView = new PhotoView(container.getContext());

			WeboApplication.getImageLoader().displayImage(
					Utils.getMiddlePicURL(mImgesURLList.get(position)),
					photoView,
					Configure.getFullScreenImageDisplayImageOptions());

			// Now just add PhotoView to ViewPager and return it
			container.addView(photoView, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);

			return photoView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_images_view_pager, menu);
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
