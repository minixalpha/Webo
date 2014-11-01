package com.minixalpha.webo;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.minixalpha.model.AtStatusAdapter;
import com.minixalpha.model.Cache;
import com.minixalpha.model.CommentsAdapter;
import com.minixalpha.model.WeiboItemAdapter;
import com.minixalpha.util.Utils;
import com.minixalpha.util.WeiboAPI;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.CommentList;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

public class ViewAtFragment extends Fragment {
	private static final String TAG = ViewAtFragment.class.getName();

	private PullToRefreshListView mAtsListView;
	private LinkedList<Status> mAtsList;
	private AtStatusAdapter mAtsAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = initFragement(inflater, container,
				R.layout.fragment_new_ats);

		mAtsListView = (PullToRefreshListView) view.findViewById(R.id.at_list);

		mAtsList = new LinkedList<>();
		mAtsAdapter = new AtStatusAdapter(getActivity(), R.layout.comment_item,
				mAtsList);
		mAtsListView.setAdapter(mAtsAdapter);
		mAtsListView.setOnRefreshListener(getOnRefreshListener());
		setAtList();
		return view;
	}

	private void setAtList() {
		String response = Cache.getAts();
		boolean hasCache = !TextUtils.isEmpty(response);

		if (hasCache) {
			displayAts(response);
			Log.d(TAG, "has cache:" + response);
		} else {
			requestAtList();
			Log.d(TAG, "no cache");
		}
	}

	private void displayAts(String jsonContent) {
		if (!TextUtils.isEmpty(jsonContent)) {
			StatusList Ats = StatusList.parse(jsonContent);
			if (Ats != null && Ats.total_number > 0) {
				List<Status> AtsList = Ats.statusList;
				ListIterator<Status> iter = AtsList
						.listIterator(AtsList.size());
				mAtsList.clear();
				while (iter.hasPrevious()) {
					mAtsList.addFirst(iter.previous());
				}
				mAtsAdapter.notifyDataSetChanged();
			}
		}
	}

	private void requestAtList() {
		boolean needRefresh = true;
		if (Utils.isNetworkAvailable()) {
			if (WeiboAPI.getInstance().isTokenAvailable()) {
				needRefresh = false;
				StatusesAPI statusesAPI = WeiboAPI.getInstance()
						.getStatusesAPI();
				statusesAPI.mentions(0L, 0L, 10, 1, 0, 0, 0, false,
						new RequestListener() {

							@Override
							public void onComplete(String response) {
								Log.d(TAG, "response:" + response);
								displayAts(response);
								Cache.updateAt(response);
								mAtsListView.onRefreshComplete();
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
			mAtsListView.onRefreshComplete();
		}
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
			requestAtList();
			super.onPostExecute(result);
		}

		@Override
		protected String[] doInBackground(Void... params) {
			return null;
		}
	}
}
