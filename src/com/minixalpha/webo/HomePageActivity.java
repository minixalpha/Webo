package com.minixalpha.webo;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.minixalpha.webo.adapter.WeiboItemAdapter;
import com.minixalpha.webo.control.ViewWeiboHelper;
import com.minixalpha.webo.control.WeiboController;
import com.minixalpha.webo.data.Cache;
import com.minixalpha.webo.utils.WeiboAPI;
import com.minixalpha.webo.view.ProgressBarFactory;
import com.minixalpha.webo.view.UserInfoView;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

public class HomePageActivity extends Activity implements ViewWeiboHelper {
	private static final String TAG = HomePageActivity.class.getName();
	private PullToRefreshListView mTimeLineView;
	private WeiboController mTimeLineControllor;
	private ProgressBar mProgressBar;
	private View mHomeHeader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_page);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		mHomeHeader = getLayoutInflater().inflate(
				R.layout.user_info_light_layout, null);
		UserInfoView.initUserInfo(mHomeHeader);
		initUserTimeline();
	}

	private void initUserTimeline() {
		mTimeLineControllor = new WeiboController(this,
				R.layout.item_weibo_no_header);

		mTimeLineView = (PullToRefreshListView) findViewById(R.id.timeline);
		mTimeLineView.getRefreshableView().addHeaderView(mHomeHeader);

		mProgressBar = ProgressBarFactory.getProgressBar(mTimeLineView);
		WeiboItemAdapter statusAdapter = mTimeLineControllor.getAdapter();
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
		mProgressBar.setVisibility(ProgressBar.GONE);
	}

	@Override
	public void actionBeforeLoad() {
		mProgressBar.setVisibility(View.VISIBLE);
	}
}
