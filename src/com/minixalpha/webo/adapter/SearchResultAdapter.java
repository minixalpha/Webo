package com.minixalpha.webo.adapter;

import java.util.List;

import com.minixalpha.webo.R;
import com.minixalpha.webo.WeboApplication;
import com.minixalpha.webo.control.WeiboController;
import com.minixalpha.webo.data.SearchResult;
import com.minixalpha.webo.utils.Utils;
import com.minixalpha.webo.utils.WeiboAPI;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;
import com.sina.weibo.sdk.utils.LogUtil;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchResultAdapter extends ArrayAdapter<SearchResult> {

	private static final String TAG = AtStatusAdapter.class.getName();
	private int resourceId;
	private Context mContext;

	public SearchResultAdapter(Context context, int textViewResourceId,
			List<SearchResult> objects) {
		super(context, textViewResourceId, objects);

		resourceId = textViewResourceId;
		mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = null;
		// 加载布局
		if (convertView == null) {
			view = LayoutInflater.from(mContext).inflate(resourceId, null);
		} else {
			view = convertView;
		}

		SearchResult result = getItem(position);
		TextView screenNameView = (TextView) view
				.findViewById(R.id.screen_name);
		TextView followersNamesView = (TextView) view
				.findViewById(R.id.followers_count);
		screenNameView.setText(result.screen_name);
		followersNamesView.setText(String.valueOf(result.followers_count));

		setAvatar(view, result);

		return view;
	}

	private void setAvatar(View view, SearchResult result) {
		final ImageView avatar = (ImageView) view.findViewById(R.id.avatar);
		if (Utils.isNetworkAvailable()) {
			UsersAPI userAPI = WeiboAPI.getInstance().getUserAPI();
			userAPI.show(Long.parseLong(result.uid), new RequestListener() {
				@Override
				public void onComplete(String response) {
					if (!TextUtils.isEmpty(response)) {
						LogUtil.i(TAG, "user:" + response);
						// 调用 User#parse 将JSON串解析成User对象
						User user = User.parse(response);
						if (user != null) {
							WeboApplication.getImageLoader().displayImage(
									user.avatar_large, avatar);
						}
					} else {
						LogUtil.i(TAG, "user:" + response);
					}

				}

				@Override
				public void onWeiboException(WeiboException arg0) {
					LogUtil.i(TAG, "user:" + arg0.getMessage());
				}

			});
		} else {
			LogUtil.i(TAG, "user:" + "network is not available");
		}
	}

}
