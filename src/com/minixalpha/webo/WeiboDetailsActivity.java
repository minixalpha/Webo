package com.minixalpha.webo;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.minixalpha.webo.adapter.WeiboItemAdapter;
import com.minixalpha.webo.control.ViewCommentHelper;
import com.minixalpha.webo.data.Cache;
import com.minixalpha.webo.data.LocalEvent;
import com.minixalpha.webo.data.Weibo;
import com.minixalpha.webo.utils.Utils;
import com.minixalpha.webo.utils.WeiboAPI;
import com.minixalpha.webo.view.CommentView;
import com.minixalpha.webo.view.ProgressBarFactory;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;

/**
 * 微博详情界面，包含微博内容和所有评论
 * 
 * @author zhaoxk
 * 
 */
public class WeiboDetailsActivity extends Activity implements ViewCommentHelper {
	private Status mCurWeibo;
	private PullToRefreshListView mCommentsListView;
	private ProgressBar mProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weibo_details);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// 获取微博内容
		Intent intent = getIntent();
		String jsonStatus = intent.getStringExtra(LocalEvent.STATUS);
		mCurWeibo = Status.parse(jsonStatus);

		// 设置微博内容
		View weibo = getLayoutInflater().inflate(R.layout.item_weibo_no_footer,
				null);
		setUserInfo(weibo);
		setWeiboContent(weibo);

		// 初始化控件
		mCommentsListView = (PullToRefreshListView) findViewById(R.id.comment_list);
		mProgressBar = ProgressBarFactory.getProgressBar(mCommentsListView);
		mCommentsListView.getRefreshableView().addHeaderView(weibo);
		CommentView.setListView(mCommentsListView,
				R.layout.item_comment_no_repost, this);
	}

	private void setUserInfo(View view) {
		User user = mCurWeibo.user;
		WeiboItemAdapter.setAvatar(view, R.id.avatar, user.avatar_large);
		WeiboItemAdapter.setTextViewLink(view, R.id.screen_name,
				user.screen_name);
		WeiboItemAdapter.setTextViewLink(view, R.id.create_at,
				Utils.getFormatTime(mCurWeibo.created_at));
	}

	private void setWeiboContent(View view) {
		WeiboItemAdapter.setTextViewContent(view, R.id.main_content,
				mCurWeibo.text);

		String retweetText = Weibo
				.getRetweetContent(mCurWeibo.retweeted_status);
		if (TextUtils.isEmpty(retweetText) == false) {
			User user = mCurWeibo.retweeted_status.user;
			Cache.updateUserId(user.screen_name, user.id);
		}
		WeiboItemAdapter.setTextViewContent(view, R.id.repost_content,
				retweetText);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.weibo_details, menu);
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
	public void requestComment(RequestListener listener) {
		CommentsAPI commentAPI = WeiboAPI.getInstance().getCommentsAPI();
		commentAPI.show(Long.parseLong(mCurWeibo.id), 0L, 0L, 10, 1,
				CommentsAPI.AUTHOR_FILTER_ALL, listener);
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	/* 微博详情一般不会多次点开，不使用缓存 */
	@Override
	public String getCache() {
		// NO NOTHING
		return null;
	}

	@Override
	public void updateCache(String response) {
		// NO NOTHING
	}

	@Override
	public void onRequestComplete() {
		mProgressBar.setVisibility(View.GONE);
	}

	@Override
	public void beforeRequest() {
		mProgressBar.setVisibility(View.VISIBLE);
	}

}
