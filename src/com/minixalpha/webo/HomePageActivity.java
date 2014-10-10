package com.minixalpha.webo;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.minixalpha.control.ViewWeiboHelper;
import com.minixalpha.control.WeiboController;
import com.minixalpha.model.Cache;
import com.minixalpha.model.StatusAdapter;
import com.minixalpha.util.WeiboAPI;
import com.minixalpha.view.UserInfoView;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class HomePageActivity extends Activity implements ViewWeiboHelper {
	private static final String TAG = HomePageActivity.class.getName();

	/* 时间线　UI */
	private PullToRefreshListView mTimeLineView;

	/* 时间线控制器 */
	private WeiboController mTimeLineControllor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_page);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		UserInfoView.initUserInfo(this.getWindow().getDecorView());
		initUserTimeline();
	}

	private void initUserTimeline() {
		mTimeLineControllor = new WeiboController(this);

		mTimeLineView = (PullToRefreshListView) findViewById(R.id.timeline);

		StatusAdapter statusAdapter = mTimeLineControllor.getAdapter();
		Log.d(TAG, statusAdapter.toString());
		mTimeLineView.setAdapter(statusAdapter);

		mTimeLineView.setOnRefreshListener(mTimeLineControllor
				.getOnRefreshListener());

		/* 初始化　微博列表 */
		mTimeLineControllor.initTimeLine();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_page, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public Activity getViewActivity() {
		return this;
	}

	@Override
	public PullToRefreshListView getTimeLineView() {
		return mTimeLineView;
	}

	@Override
	public ViewWeiboHelper getInstance() {
		return this;
	}

	@Override
	public void getTimeline(long since_id, long max_id, int count, int page,
			boolean base_app, int featureType, boolean trim_user,
			RequestListener listener) {

		StatusesAPI statusesAPI = WeiboAPI.getInstance().getStatusesAPI();
		statusesAPI.userTimeline(since_id, max_id, count, page, base_app,
				featureType, trim_user, listener);

	}

	@Override
	public String getCache() {
		return Cache.getUserTimeLine();
	}

	// 在 onPause 中更新缓存，因为 onPause 调用之后，在系统资源不足时可能被回收
	@Override
	public void onPause() {
		Cache.updateUserTimeLine(mTimeLineControllor.getLastStatus(),
				mTimeLineControllor.getWeiboList());
		Log.d(TAG, "onPause");
		super.onPause();
	}

	@Override
	public void actionAfterUpdate() {
		// TODO Auto-generated method stub

	}
}
