package com.minixalpha.webo;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.minixalpha.control.ViewCommentHelper;
import com.minixalpha.model.Cache;
import com.minixalpha.model.LocalEvent;
import com.minixalpha.model.StatusAdapter;
import com.minixalpha.model.Weibo;
import com.minixalpha.util.Utils;
import com.minixalpha.util.WeiboAPI;
import com.minixalpha.view.CommentView;
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

public class WeiboDetailsActivity extends Activity implements ViewCommentHelper {
	private Status mStatus;
	PullToRefreshListView mCommentsListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weibo_details);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		Intent intent = getIntent();
		String jsonStatus = intent.getStringExtra(LocalEvent.STATUS);
		mStatus = Status.parse(jsonStatus);
		View view = getWindow().getDecorView();

		setUserInfo(view);
		setWeiboContent(view);
		mCommentsListView = (PullToRefreshListView) findViewById(R.id.comment_list);
		CommentView.setListView(mCommentsListView,
				R.layout.simple_comment_item, this);
	}

	private void setUserInfo(View view) {
		User user = mStatus.user;
		StatusAdapter.setAvatar(view, R.id.avatar, user.avatar_large);
		StatusAdapter.setTextViewLink(view, R.id.screen_name, user.screen_name);
		StatusAdapter.setTextViewLink(view, R.id.create_at,
				Utils.getFormatTime(mStatus.created_at));
	}

	private void setWeiboContent(View view) {
		StatusAdapter.setTextViewContent(view, R.id.main_content, mStatus.text);

		String retweetText = Weibo.getRetweetContent(mStatus.retweeted_status);
		if (TextUtils.isEmpty(retweetText) == false) {
			User user = mStatus.retweeted_status.user;
			Cache.updateUserId(user.screen_name, user.id);
		}
		StatusAdapter
				.setTextViewContent(view, R.id.repost_content, retweetText);
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
		commentAPI.show(Long.parseLong(mStatus.id), 0L, 0L, 10, 1,
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

}
