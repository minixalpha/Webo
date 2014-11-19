package com.minixalpha.webo.data;

import android.text.TextUtils;

import com.sina.weibo.sdk.openapi.models.Status;

public class Weibo {
	private static final String WEIBO_HOME_PREFIX = "http://weibo.com/u/";
	private static final String WEIBO_HOME_FROM_AT_PREFIX = "http://weibo.com/n/";
	private static final String WEIBO_HOME_FROM_AT_SUFFIX = "?from=feed&loc=at";
	public static final String AT_REGEX = "@([\\w\u4e00-\u9fa5-]+)\\W";

	public static String getRetweetContent(Status status) {
		if (status == null) {
			return "";
		}

		StringBuilder builder = new StringBuilder();

		if (status.user != null
				&& TextUtils.isEmpty(status.user.screen_name) == false) {
			builder.append("@");
			builder.append(status.user.screen_name);
			builder.append(":");
			builder.append(status.text);
		}
		return builder.toString();
	}

	public static String getHomeLink(String screen_name, String uid) {
		return "<a href=\"" + WEIBO_HOME_PREFIX + uid + "\">@" + screen_name
				+ "</a>";
	}

	public static String getHomeLinkFromAt(String screen_name) {
		return "<a href=\"" + WEIBO_HOME_FROM_AT_PREFIX + screen_name
				+ WEIBO_HOME_FROM_AT_SUFFIX + "\">@" + screen_name + "</a>";
	}
}
