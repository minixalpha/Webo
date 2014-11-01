package com.minixalpha.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.minixalpha.model.Weibo;
import com.minixalpha.webo.R;
import com.minixalpha.webo.WeboApplication;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.CommentList;
import com.sina.weibo.sdk.openapi.models.Geo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.openapi.models.User;
import com.sina.weibo.sdk.openapi.models.Visible;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

public class Utils {
	private static final String TAG = Utils.class.getName();
	private static final Context mContext = WeboApplication.getContext();

	public static void saveToken(Oauth2AccessToken token) {
		AccessTokenKeeper.writeAccessToken(mContext, token);
	}

	public static Oauth2AccessToken readToken() {
		return AccessTokenKeeper.readAccessToken(mContext);
	}

	public static void clearToken() {
		AccessTokenKeeper.clear(mContext);
	}

	/**
	 * 返回人类友好的时间格式
	 * 
	 * @param time
	 *            原始时间格式: Mon Sep 29 10:30:51 +0800 2014
	 * @return 人类友好的时间格式 如果是同一天，返回 10:30，否则，返回　9月29日 10:30
	 */
	public static String getFormatTime(String time) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
		try {
			Date date = dateFormat.parse(time);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			Calendar rightNow = Calendar.getInstance();

			boolean sameDay = rightNow.get(Calendar.YEAR) == calendar
					.get(Calendar.YEAR)
					&& rightNow.get(Calendar.DAY_OF_YEAR) == calendar
							.get(Calendar.DAY_OF_YEAR);

			SimpleDateFormat simpleFormat = null;
			if (sameDay) {
				simpleFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
			} else {
				simpleFormat = new SimpleDateFormat("MM月dd日 HH:mm",
						Locale.CHINA);
			}

			return simpleFormat.format(date);
		} catch (ParseException e) {
			Log.e(TAG, "time format error:" + time);
		}
		return "null";
	}

	/**
	 * 检查网络状态是否可用
	 * 
	 * @param context
	 *            当前环境
	 * @return true 如果网络可用，否则返回 false
	 */
	public static boolean isNetworkAvailable() {
		ConnectivityManager manager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = manager.getActiveNetworkInfo();
		if (network != null) {
			return network.isAvailable();
		}
		return false;
	}

	public static List<Status> getStatusList(String data) {
		StatusList statuses = StatusList.parse(data);
		List<Status> statusList = new ArrayList<>();
		if (statuses != null && statuses.total_number > 0) {
			statusList = statuses.statusList;
		}
		return statusList;
	}

	private static JSONObject toJsonObject(Comment comment) {
		JSONObject jsonObject = new JSONObject();
		if (comment == null) {
			return jsonObject;
		}

		try {
			jsonObject.put("created_at", comment.created_at);
			jsonObject.put("id", comment.id);
			jsonObject.put("text", comment.text);
			jsonObject.put("source", comment.source);
			jsonObject.put("user", toJsonObject(comment.user));
			jsonObject.put("mid", comment.mid);
			jsonObject.put("idstr", comment.idstr);
			jsonObject.put("status", toJsonObject(comment.status));
			jsonObject
					.put("reply_comment", toJsonObject(comment.reply_comment));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

	public static String toJson(User user) {
		return toJsonObject(user).toString();
	}

	// 将　User 转化为 Json 对象
	public static JSONObject toJsonObject(User user) {
		JSONObject jsonObject = new JSONObject();
		if (user == null) {
			return jsonObject;
		}
		try {
			jsonObject.put("id", user.id);
			jsonObject.put("idstr", user.idstr);
			jsonObject.put("screen_name", user.screen_name);
			jsonObject.put("name", user.name);
			jsonObject.put("province", user.province);
			jsonObject.put("city", user.city);
			jsonObject.put("location", user.location);
			jsonObject.put("description", user.description);
			jsonObject.put("url", user.url);
			jsonObject.put("profile_image_url", user.profile_image_url);
			jsonObject.put("profile_url", user.profile_url);
			jsonObject.put("domain", user.domain);
			jsonObject.put("weihao", user.weihao);
			jsonObject.put("gender", user.gender);
			jsonObject.put("followers_count", user.followers_count);
			jsonObject.put("friends_count", user.friends_count);
			jsonObject.put("statuses_count", user.statuses_count);
			jsonObject.put("favourites_count", user.favourites_count);
			jsonObject.put("created_at", user.created_at);
			jsonObject.put("following", user.following);
			jsonObject.put("allow_all_act_msg", user.allow_all_act_msg);
			jsonObject.put("geo_enabled", user.geo_enabled);
			jsonObject.put("verified", user.verified);
			jsonObject.put("verified_type", user.verified_type);
			jsonObject.put("remark", user.remark);
			jsonObject.put("allow_all_comment", user.allow_all_comment);
			jsonObject.put("avatar_large", user.avatar_large);
			jsonObject.put("avatar_hd", user.avatar_hd);
			jsonObject.put("verified_reason", user.verified_reason);
			jsonObject.put("follow_me", user.follow_me);
			jsonObject.put("online_status", user.online_status);
			jsonObject.put("bi_followers_count", user.bi_followers_count);
			jsonObject.put("lang", user.lang);

			// 注意：以下字段暂时不清楚具体含义，OpenAPI 说明文档暂时没有同步更新对应字段含义
			jsonObject.put("star", user.star);
			jsonObject.put("mbtype", user.mbtype);
			jsonObject.put("mbrank", user.mbrank);
			jsonObject.put("block_word", user.block_word);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

	// 将 Geo 转化为Json对象
	private static JSONObject toJsonObject(Geo geo) {
		JSONObject jsonObject = new JSONObject();
		if (geo == null) {
			return jsonObject;
		}
		try {
			jsonObject.put("longitude", geo.longitude);
			jsonObject.put("latitude", geo.latitude);
			jsonObject.put("city", geo.city);
			jsonObject.put("province", geo.province);
			jsonObject.put("city_name", geo.city_name);
			jsonObject.put("province_name", geo.province_name);
			jsonObject.put("address", geo.address);
			jsonObject.put("pinyin", geo.pinyin);
			jsonObject.put("more", geo.more);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

	public static String toJson(Status status) {
		return toJsonObject(status).toString();
	}

	// 将 Status 转化为 Json　对象
	private static JSONObject toJsonObject(Status status) {
		JSONObject jsonObject = new JSONObject();
		if (status == null) {
			return jsonObject;
		}
		try {
			jsonObject.put("created_at", status.created_at);
			jsonObject.put("id", status.id);
			jsonObject.put("mid", status.mid);
			jsonObject.put("idstr", status.idstr);
			jsonObject.put("text", status.text);
			jsonObject.put("source", status.source);
			jsonObject.put("favorited", status.favorited);
			jsonObject.put("truncated", status.truncated);

			// Have NOT supported
			jsonObject.put("in_reply_to_status_id",
					status.in_reply_to_status_id);
			jsonObject.put("in_reply_to_user_id", status.in_reply_to_user_id);
			jsonObject.put("in_reply_to_screen_name",
					status.in_reply_to_screen_name);

			jsonObject.put("thumbnail_pic", status.thumbnail_pic);
			jsonObject.put("bmiddle_pic", status.bmiddle_pic);
			jsonObject.put("original_pic", status.original_pic);

			jsonObject.put("geo", toJsonObject(status.geo));
			jsonObject.put("user", toJsonObject(status.user));
			jsonObject.put("retweeted_status",
					toJsonObject(status.retweeted_status));

			jsonObject.put("reposts_count", status.reposts_count);
			jsonObject.put("comments_count", status.comments_count);
			jsonObject.put("attitudes_count", status.attitudes_count);
			jsonObject.put("mlevel", status.mlevel); // Have NOT
														// supported
			status.visible = Visible.parse(jsonObject.optJSONObject("visible"));

			JSONArray picUrlsArray = new JSONArray();

			if (status.pic_urls != null) {
				for (String pic : status.pic_urls) {
					JSONObject tmpObject = new JSONObject();
					tmpObject.put("thumbnail_pic", pic);
					picUrlsArray.put(tmpObject);
				}
			}

			jsonObject.put("pic_urls", picUrlsArray);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

	public static String toJson(CommentList comments) {
		JSONObject jsonObject = new JSONObject();
		if (comments == null) {
			return jsonObject.toString();
		}
		try {
			jsonObject.put("previous_cursor", comments.previous_cursor);
			jsonObject.put("next_cursor", comments.next_cursor);
			jsonObject.put("total_number", comments.total_number);

			JSONArray jsonArray = new JSONArray();
			for (Comment comment : comments.commentList) {
				jsonArray.put(toJsonObject(comment));
			}

			jsonObject.put("comments", jsonArray);
			return jsonObject.toString();

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject.toString();
	}

	public static String toJson(StatusList statuses) {
		JSONObject jsonObject = new JSONObject();
		if (statuses == null) {
			return jsonObject.toString();
		}
		try {
			jsonObject.put("hasvisible", statuses.hasvisible);
			jsonObject.put("previous_cursor", statuses.previous_cursor);
			jsonObject.put("next_cursor", statuses.next_cursor);
			jsonObject.put("total_number", statuses.total_number);

			JSONArray jsonArray = new JSONArray();
			for (Status status : statuses.statusList) {
				jsonArray.put(toJsonObject(status));
			}

			jsonObject.put("statuses", jsonArray);
			return jsonObject.toString();

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return "";
	}

	// 　根据新的 statusList 生成 json 格式字符串
	public static String toJson(StatusList statuses, List<Status> statusList) {
		JSONObject jsonObject = new JSONObject();
		if (statuses == null || statusList == null) {
			return jsonObject.toString();
		}
		try {
			jsonObject.put("hasvisible", statuses.hasvisible);
			jsonObject.put("previous_cursor", statuses.previous_cursor);
			jsonObject.put("next_cursor", statuses.next_cursor);
			jsonObject.put("total_number", statuses.total_number);

			JSONArray jsonArray = new JSONArray();
			for (Status status : statusList) {
				jsonArray.put(toJsonObject(status));
			}

			jsonObject.put("statuses", jsonArray);
			return jsonObject.toString();

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return "";
	}

	private static String getReplacedStr(String regex, String source,
			OperationOnStr op) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(source);
		String procSource = source;
		String preResult = null;
		while (matcher.find()) {
			String result = matcher.group(1);
			if (result.equals(preResult) == false) {
				procSource = procSource.replaceAll(result, op.action(result));
			}
			preResult = result;
		}
		return procSource;
	}

	private static List<String> getPatternList(String regex, String source) {
		List<String> resutlList = new ArrayList<>();

		/* TODO: 避免反复编译 */
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(source);
		String preResult = null;
		while (matcher.find()) {
			String result = matcher.group(1);
			if (result.equals(preResult) == false) {
				resutlList.add(result);
			}
			preResult = result;
		}
		return resutlList;
	}

	private static final String URL_REGEX = "[^\"](http://[a-zA-Z0-9./]+)";

	/**
	 * 将文本中的链接替换成 href 格式
	 * 
	 * @param text
	 *            文本数据
	 * @return 替换后的文本
	 */
	public static String getHrefLink(String text) {
		return getReplacedStr(URL_REGEX, text, new OperationOnStr() {

			@Override
			public String action(String str) {
				return "<a href=\"" + str + "\">" + str + "</a>";
			}

		});
	}

	public static List<String> getAtList(String text) {
		return getPatternList(Weibo.AT_REGEX, text);
	}

	private static int notification_id = 1;

	public static void sendNotification(String tickerText, int icon,
			String title, String content, PendingIntent pi) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				mContext).setContentTitle(title).setContentText(content)
				.setSmallIcon(icon).setTicker(tickerText).setAutoCancel(true);
		Notification notification = builder.build();
		NotificationManager mNotificationManager = (NotificationManager) WeboApplication
				.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(notification_id, notification);
		mNotificationManager.cancel(notification_id);
		notification_id++;
	}

	public static String loadFromResource(int id) {
		return WeboApplication.getContext().getResources().getString(id);
	}

	public static void showMsg(String msg) {
		Toast.makeText(WeboApplication.getContext(), msg, Toast.LENGTH_SHORT)
				.show();
	}

	public static void showMsg(int rid) {
		Toast.makeText(WeboApplication.getContext(), rid, Toast.LENGTH_SHORT)
				.show();
	}

	public static int getScreenWidth() {
		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size.x;
	}

	public static String getMiddlePicURL(String thumbnailPicURL) {
		return thumbnailPicURL.replace("thumbnail", "bmiddle");
	}
}
