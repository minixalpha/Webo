package com.minixalpha.model;

import java.util.ArrayList;
import java.util.List;

import com.minixalpha.util.Utils;
import com.minixalpha.util.WeiboAPI;
import com.minixalpha.webo.WeboApplication;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.CommentList;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;

public class Cache {
	private static final String CUR_USRE_ID = String.valueOf(WeiboAPI
			.getInstance().getCurUserId());
	private static final String TAG = Cache.class.getName();
	private static final String TIMELINE_FILE = "TIMELINE_DATA";
	private static final String TIMELINE_KEY = "TIMELINE" + CUR_USRE_ID;;
	private static final String USER_TIMELINE_KEY = "USER_TIMELINE"
			+ CUR_USRE_ID;;
	private static final String COMMENTS_KEY = "COMMENTS" + CUR_USRE_ID;;
	private static final String USER_INFO_FILE = "USER_INFO_DATA";
	private static final String AT_KEY = "ATS";
	private static final int LIMIT = 20;

	private static final Context mContext = WeboApplication.getContext();

	public static void updateUserInfo(long uid, String jsonData) {
		update(USER_INFO_FILE, String.valueOf(uid), jsonData);
	}

	public static String getUserInfo(long uid) {
		return get(USER_INFO_FILE, String.valueOf(uid));
	}

	private static String update(String file, String key, String data) {
		SharedPreferences pref = mContext.getSharedPreferences(file,
				Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString(key, data);
		editor.commit();
		return data;
	}

	private static String updateTimeLine(StatusList statuses,
			List<Status> statusList, String key) {
		if (statuses == null || statusList == null) {
			return "";
		}

		List<Status> cachedList = null;
		if (statusList.size() < LIMIT) {
			cachedList = statusList;
		} else {
			cachedList = statusList.subList(0, LIMIT);
		}

		return update(TIMELINE_FILE, key, Utils.toJson(statuses, cachedList));
	}

	public static String updateAllTimeLine(StatusList statuses,
			List<Status> statusList) {
		return updateTimeLine(statuses, statusList, TIMELINE_KEY);
	}

	public static String updateUserTimeLine(StatusList statuses,
			List<Status> statusList) {
		return updateTimeLine(statuses, statusList, USER_TIMELINE_KEY);
	}

	// 更新 comments 缓存，保存最新的 LIMIT 条评论
	public static String updateComment(String response) {
		String oldCache = getComments();
		if (TextUtils.isEmpty(oldCache)) {
			return update(TIMELINE_FILE, COMMENTS_KEY, response);
		} else {
			CommentList newCommentList = CommentList.parse(response);
			if (newCommentList == null) {
				return oldCache;
			}
			CommentList oldCommentList = CommentList.parse(oldCache);
			ArrayList<Comment> newList = newCommentList.commentList;
			if (newList == null || newList.size() == 0) {
				return oldCache;
			} else if (newList.size() > LIMIT) {
				newList = (ArrayList<Comment>) newList.subList(0, LIMIT);
			} else if (newList.size() < LIMIT) {
				Comment lastComment = newList.get(newList.size() - 1);
				for (Comment comment : oldCommentList.commentList) {
					if (Long.valueOf(comment.id) < Long.valueOf(lastComment.id)) {
						newList.add(comment);
					}
				}
			}

			newCommentList.commentList = newList;
			return update(TIMELINE_FILE, COMMENTS_KEY,
					Utils.toJson(newCommentList));
		}
	}

	public static String updateAt(String response) {
		String oldCache = getAts();
		if (TextUtils.isEmpty(oldCache)) {
			return update(TIMELINE_FILE, AT_KEY, response);
		} else {
			StatusList newStatusList = StatusList.parse(response);
			if (newStatusList == null) {
				return oldCache;
			}
			StatusList oldStatusList = StatusList.parse(oldCache);
			ArrayList<Status> newList = newStatusList.statusList;
			if (newList == null || newList.size() == 0) {
				return oldCache;
			} else if (newList.size() > LIMIT) {
				newList = (ArrayList<Status>) newList.subList(0, LIMIT);
			} else if (newList.size() < LIMIT
					&& oldStatusList.statusList != null) {
				Status lastStatus = newList.get(newList.size() - 1);
				for (Status status : oldStatusList.statusList) {
					if (Long.valueOf(status.id) < Long.valueOf(lastStatus.id)) {
						newList.add(status);
					}
				}
			}

			newStatusList.statusList = newList;
			return update(TIMELINE_FILE, AT_KEY, Utils.toJson(newStatusList));
		}
	}

	private static String get(String file, String key) {
		SharedPreferences pref = mContext.getSharedPreferences(file,
				Context.MODE_APPEND);
		return pref.getString(key, "");
	}

	/**
	 * 获取缓存数据
	 * 
	 * @param context
	 *            　需要获取缓存数据的　context
	 * 
	 * @return 缓存数据
	 */
	public static String getAllTimeLine() {
		return get(TIMELINE_FILE, TIMELINE_KEY);
	}

	public static String getUserTimeLine() {
		return get(TIMELINE_FILE, USER_TIMELINE_KEY);
	}

	public static String getComments() {
		return get(TIMELINE_FILE, COMMENTS_KEY);
	}

	public static String getAts() {
		return get(TIMELINE_FILE, AT_KEY);
	}

	public static void clearAll() {
		SharedPreferences pref = mContext.getSharedPreferences(TIMELINE_FILE,
				Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}

	private static final String NAME_ID = "SCREEN_NAME_ID";

	public static void updateUserId(String screen_name, String uid) {
		SharedPreferences pref = mContext.getSharedPreferences(NAME_ID,
				Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString(screen_name, uid);
		Log.d(TAG, screen_name + ":" + uid);
		editor.commit();
	}

	public static String getUserId(String screen_name) {
		SharedPreferences pref = mContext.getSharedPreferences(NAME_ID,
				Context.MODE_APPEND);

		String uid = pref.getString(screen_name, null);
		Log.d(TAG, screen_name + ":" + uid);
		return uid;
	}

}
