package com.minixalpha.webo;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.minixalpha.webo.control.ViewCommentHelper;
import com.minixalpha.webo.data.Cache;
import com.minixalpha.webo.utils.WeiboAPI;
import com.minixalpha.webo.view.CommentView;
import com.minixalpha.webo.view.ProgressBarFactory;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

public class ViewCommentFragment extends Fragment implements ViewCommentHelper {
	private static final String TAG = ViewCommentFragment.class.getName();
	private PullToRefreshListView mCommentsListView;
	private ProgressBar mProgressBar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = initFragement(inflater, container,
				R.layout.fragment_new_comments);
		mCommentsListView = (PullToRefreshListView) view
				.findViewById(R.id.comment_list);
		mProgressBar = ProgressBarFactory.getProgressBar(mCommentsListView);
		CommentView.setListView(mCommentsListView, this);

		return view;
	}

	private View initFragement(LayoutInflater inflater, ViewGroup container,
			int resourceId) {
		FrameLayout frameLayout = new FrameLayout(getActivity());
		frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER));
		View view = inflater.inflate(resourceId, container, false);
		frameLayout.addView(view);
		return frameLayout;
	}

	@Override
	public void requestComment(RequestListener listener) {
		CommentsAPI commentAPI = WeiboAPI.getInstance().getCommentsAPI();
		commentAPI.toME(0L, 0L, 10, 1, CommentsAPI.AUTHOR_FILTER_ALL,
				CommentsAPI.SRC_FILTER_ALL, listener);
	}

	@Override
	public String getCache() {
		return Cache.getComments();
	}

	@Override
	public void updateCache(String response) {
		Cache.updateComment(response);
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
