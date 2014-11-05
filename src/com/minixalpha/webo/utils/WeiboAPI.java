package com.minixalpha.webo.utils;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.LogoutAPI;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.legacy.SearchAPI;

public class WeiboAPI {
	/* 当前 Token 信息 */
	private Oauth2AccessToken mAccessToken;

	/* 用于获取微博信息流等操作的API */
	private StatusesAPI mStatusesAPI;

	/* 搜索 API */
	private SearchAPI mSearchAPI;

	/* 微博评论信息 API */
	private CommentsAPI mCommentsAPI;

	/* 用户信息接口 */
	private UsersAPI mUsersAPI;

	/* 退出登录接口　 */
	private LogoutAPI mLogoutAPI;

	private static final WeiboAPI instance = new WeiboAPI();

	private WeiboAPI() {
		// 获取当前已保存过的 Token
		mAccessToken = Utils.readToken();

		mStatusesAPI = new StatusesAPI(mAccessToken);

		mSearchAPI = new SearchAPI(mAccessToken);

		mCommentsAPI = new CommentsAPI(mAccessToken);

		// 获取用户信息接口
		mUsersAPI = new UsersAPI(mAccessToken);

		mLogoutAPI = new LogoutAPI(mAccessToken);
	}

	public static WeiboAPI getInstance() {
		return instance;
	}

	public CommentsAPI getCommentsAPI() {
		return mCommentsAPI;
	}

	public SearchAPI getSearchAPI() {
		return mSearchAPI;
	}

	public StatusesAPI getStatusesAPI() {
		return mStatusesAPI;
	}

	public UsersAPI getUserAPI() {
		return mUsersAPI;
	}

	public boolean isTokenAvailable() {
		return mAccessToken != null && mAccessToken.isSessionValid();
	}

	public long getCurUserId() {
		return Long.parseLong(mAccessToken.getUid());
	}

	public void logout(RequestListener listener) {
		mLogoutAPI.logout(listener);
	}
}
