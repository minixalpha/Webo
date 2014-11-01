package com.minixalpha.view;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.minixalpha.control.ViewCommentHelper;
import com.minixalpha.model.CommentsAdapter;
import com.minixalpha.util.Utils;
import com.minixalpha.util.WeiboAPI;
import com.minixalpha.webo.R;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.CommentList;

public class CommentView {
	private static final String TAG = CommentView.class.getName();

	private PullToRefreshListView mCommentsListView;
	private ViewCommentHelper mViewCommentHelper;
	private LinkedList<Comment> mCommentsList;
	private CommentsAdapter mCommentsAdapter;

	private CommentView(PullToRefreshListView commentsListView, int itemId,
			ViewCommentHelper viewCommentHelper) {
		mViewCommentHelper = viewCommentHelper;
		mCommentsListView = commentsListView;

		mCommentsList = new LinkedList<>();
		mCommentsAdapter = new CommentsAdapter(
				mViewCommentHelper.getActivity(), itemId, mCommentsList);
		mCommentsListView.setAdapter(mCommentsAdapter);
		mCommentsListView.setOnRefreshListener(getOnRefreshListener());
		setCommentList();
	}

	public static void setListView(PullToRefreshListView commentsListView,
			ViewCommentHelper viewCommentHelper) {
		new CommentView(commentsListView, R.layout.comment_item,
				viewCommentHelper);
	}

	public static void setListView(PullToRefreshListView commentsListView,
			int itemId, ViewCommentHelper viewCommentHelper) {
		new CommentView(commentsListView, itemId, viewCommentHelper);
	}

	private void setCommentList() {
		String response = mViewCommentHelper.getCache();
		boolean hasCache = !TextUtils.isEmpty(response);

		if (hasCache) {
			displayComments(response);
			Log.d(TAG, "has cache:" + response);
		} else {
			requestCommentList();
			Log.d(TAG, "no cache");
		}
	}

	private void displayComments(String jsonContent) {
		if (!TextUtils.isEmpty(jsonContent)) {
			CommentList comments = CommentList.parse(jsonContent);
			if (comments != null && comments.total_number > 0) {
				List<Comment> commentsList = comments.commentList;
				ListIterator<Comment> iter = commentsList
						.listIterator(commentsList.size());
				mCommentsList.clear();
				while (iter.hasPrevious()) {
					mCommentsList.addFirst(iter.previous());
				}
				mCommentsAdapter.notifyDataSetChanged();
			}
		}
	}

	/**
	 * 通过网络发起评论列表请求
	 */
	private void requestCommentList() {
		boolean needRefresh = true;
		if (Utils.isNetworkAvailable()) {
			if (WeiboAPI.getInstance().isTokenAvailable()) {
				needRefresh = false;
				mViewCommentHelper.beforeRequest();
				mViewCommentHelper.requestComment(new RequestListener() {

					@Override
					public void onComplete(String response) {
						Log.d(TAG, "response:" + response);
						mViewCommentHelper.onRequestComplete();
						displayComments(response);
						mViewCommentHelper.updateCache(response);
						mCommentsListView.onRefreshComplete();
					}

					@Override
					public void onWeiboException(WeiboException arg0) {
					}

				});

			} else {
				Log.d(TAG, "Token is not valid");
			}
		} else {
			Log.d(TAG, "Network is not available");
		}

		if (needRefresh) {
			mCommentsListView.onRefreshComplete();
		}
	}

	private OnRefreshListener<ListView> getOnRefreshListener() {
		return new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// 任务要放在异步Task中，不能放在这里
				// onRefreshComplete 放在这里也不行
				new GetDataTask().execute();
			}

		};
	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {
		@Override
		protected void onPostExecute(String[] result) {
			requestCommentList();
			super.onPostExecute(result);
		}

		@Override
		protected String[] doInBackground(Void... params) {
			return null;
		}
	}
}
