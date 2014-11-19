package com.minixalpha.webo.view;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.minixalpha.webo.R;
import com.minixalpha.webo.data.Cache;
import com.minixalpha.webo.utils.Utils;
import com.minixalpha.webo.utils.WeiboAPI;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;
import com.sina.weibo.sdk.utils.LogUtil;

public class UserInfoView {
	private static final String TAG = UserInfoView.class.getName();

	private static void initUserInfo(View menuView, User user) {
		final TextView screenName = (TextView) menuView
				.findViewById(R.id.screen_name);
		final TextView followersCount = (TextView) menuView
				.findViewById(R.id.followers_count);
		final TextView starredCount = (TextView) menuView
				.findViewById(R.id.starred_count);
		final TextView weiboCount = (TextView) menuView
				.findViewById(R.id.weibo_count);

		screenName.setText(user.screen_name);
		followersCount.setText(String.valueOf(user.followers_count));
		starredCount.setText(String.valueOf(user.friends_count));
		weiboCount.setText(String.valueOf(user.statuses_count));
	}

	public static void initUserInfo(final View menuView) {
		WeiboAPI weiboAPI = WeiboAPI.getInstance();
		final long uid = weiboAPI.getCurUserId();
		String cachedUser = Cache.getUserInfo(uid);
		boolean hasCache = !TextUtils.isEmpty(cachedUser);
		if (hasCache) {
			Log.d(TAG, "has cache");
			User user = User.parse(cachedUser);
			initUserInfo(menuView, user);
		} else {
			Log.d(TAG, "no cache");
			if (Utils.isNetworkAvailable()) {
				UsersAPI userAPI = weiboAPI.getUserAPI();
				userAPI.show(uid, new RequestListener() {
					@Override
					public void onComplete(String response) {
						if (!TextUtils.isEmpty(response)) {
							LogUtil.i(TAG, "user:" + response);
							// 调用 User#parse 将JSON串解析成User对象
							User user = User.parse(response);
							if (user != null) {
								initUserInfo(menuView, user);
								Cache.updateUserInfo(uid, Utils.toJson(user));
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
}
