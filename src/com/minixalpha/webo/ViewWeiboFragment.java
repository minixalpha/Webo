package com.minixalpha.webo;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.minixalpha.webo.adapter.WeiboItemAdapter;
import com.minixalpha.webo.control.ViewWeiboHelper;
import com.minixalpha.webo.control.WeiboController;
import com.minixalpha.webo.data.Cache;
import com.minixalpha.webo.utils.Utils;
import com.minixalpha.webo.utils.WeiboAPI;
import com.minixalpha.webo.view.ProgressBarFactory;
import com.sina.weibo.sdk.net.RequestListener;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.AbsListView.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

/**
 * 查看时间线上的微博
 * 
 * @author minixalpha
 * 
 */
public class ViewWeiboFragment extends Fragment implements ViewWeiboHelper {
	private static final String TAG = ViewWeiboFragment.class.getName();

	private PullToRefreshListView mTimeLineView;
	private WeiboController mTimeLineControllor;
	private ProgressBar mProgressBar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = initFragement(inflater, container);

		mTimeLineControllor = new WeiboController(this);
		mTimeLineView = (PullToRefreshListView) view
				.findViewById(R.id.timeline);
		mProgressBar = ProgressBarFactory.getProgressBar(mTimeLineView);

		WeiboItemAdapter statusAdapter = mTimeLineControllor.getAdapter();
		Log.d(TAG, statusAdapter.toString());
		mTimeLineView.setAdapter(statusAdapter);

		mTimeLineView.setOnRefreshListener(mTimeLineControllor
				.getOnRefreshListener());
		mTimeLineControllor.initTimeLine();

		return view;
	}

	public PullToRefreshListView getTimeLineView() {
		return mTimeLineView;
	}

	// 在 onPause 中更新缓存，因为 onPause 调用之后，在系统资源不足时可能被回收
	@Override
	public void onPause() {
		Cache.updateAllTimeLine(mTimeLineControllor.getLastStatus(),
				mTimeLineControllor.getWeiboList());
		Log.d(TAG, "onPause");
		super.onPause();
	}

	private View initFragement(LayoutInflater inflater, ViewGroup container) {
		FrameLayout frameLayout = new FrameLayout(getActivity());
		frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER));
		View view = inflater.inflate(R.layout.fragment_view_weibo, container,
				false);
		frameLayout.addView(view);
		return frameLayout;
	}

	@Override
	public Activity getViewActivity() {
		return getActivity();
	}

	@Override
	public ViewWeiboHelper getInstance() {
		return ViewWeiboFragment.this;
	}

	@Override
	public void getTimeline(long since_id, long max_id, int count, int page,
			boolean base_app, int featureType, boolean trim_user,
			RequestListener listener) {

		WeiboAPI.getInstance()
				.getStatusesAPI()
				.friendsTimeline(since_id, max_id, count, page, base_app,
						featureType, trim_user, listener);

	}

	@Override
	public String getCache() {
		return Cache.getAllTimeLine();
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
