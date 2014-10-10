package com.minixalpha.model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import com.minixalpha.control.Configure;
import com.minixalpha.control.SearchIdCallback;
import com.minixalpha.control.WeiboController;
import com.minixalpha.util.Utils;
import com.minixalpha.util.WeiboAPI;
import com.minixalpha.webo.*;

import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sina.weibo.sdk.openapi.legacy.SearchAPI;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.User;

public class AtStatusAdapter extends ArrayAdapter<Status> {
	private static final String TAG = AtStatusAdapter.class.getName();
	private int resourceId;
	private Context mContext;

	public AtStatusAdapter(Context context, int textViewReourceId,
			List<Status> objects) {
		super(context, textViewReourceId, objects);
		resourceId = textViewReourceId;
		mContext = context;
	}

	/**
	 * 子项被滚入屏幕时会被调用
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = null;
		// 加载布局
		if (convertView == null) {
			view = LayoutInflater.from(mContext).inflate(resourceId, null);
		} else {
			view = convertView;
		}
		Status status = getItem(position);
		Log.d(TAG, String.valueOf(position) + "," + status.user.screen_name);

		// 设置头像
		setAvatar(view, R.id.avatar, status.user.avatar_large);
		setTextViewLink(view, R.id.screen_name, status.user.screen_name);

		// 设置内容
		setTextViewContent(view, R.id.main_content, status.text);
		setTextViewLink(view, R.id.create_at,
				Utils.getFormatTime(status.created_at));
		// setTextViewContent(view, R.id.source, status.source);

		// 设置转发内容
		String retweetText = Weibo.getRetweetContent(status.retweeted_status);
		if (TextUtils.isEmpty(retweetText) == false) {
			User user = status.retweeted_status.user;
			Cache.updateUserId(user.screen_name, user.id);
		}
		setTextViewContent(view, R.id.repost_content, retweetText);


		return view;
	}

	public static void setTextViewContent(View view, int id, String text) {
		TextView tv = (TextView) view.findViewById(id);
	

		List<String> atList = Utils.getAtList(text);
		String curtext = text;
		int completeN = 0, totalN = atList.size();
		for (final String atName : atList) {
			String uid = Cache.getUserId(atName);
			if (TextUtils.isEmpty(uid) == false) {
				curtext = curtext.replaceAll("@" + atName,
						Weibo.getHomeLink(atName, uid));
				completeN++;
			}
		}
		setTextViewLink(view, id, curtext);
		if (completeN == totalN) {
			return;
		}

		SearchIdCallback searchIdCallback = new SearchIdCallback(tv, curtext,
				completeN, totalN);
		SearchAPI searchAPI = WeiboAPI.getInstance().getSearchAPI();
		for (final String atName : atList) {
			String uid = Cache.getUserId(atName);
			if (TextUtils.isEmpty(uid)) {
				boolean isNetworkAvailable = Utils.isNetworkAvailable();
				if (isNetworkAvailable) {
					/* TODO: need URLEncode or not */
					String encodeName = atName;
					try {
						encodeName = URLEncoder.encode(atName, "utf-8");
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}
					Log.d(TAG, "search:" + atName);
					searchAPI.users(atName, 1, searchIdCallback);
				} else {
					Log.d(TAG, "network is not available");
				}
			}
		}
	}

	public static void setTextViewLink(TextView tv, String content) {
		if (TextUtils.isEmpty(content)) {
			tv.setVisibility(View.GONE);
			return;
		}

		tv.setVisibility(View.VISIBLE);

		String addHrefContent = Utils.getHrefLink(content);
		tv.setText(Html.fromHtml(addHrefContent));
		tv.setMovementMethod(LinkMovementMethod.getInstance());

		CharSequence text = tv.getText();
		if (text instanceof Spannable) {
			int end = text.length();
			Spannable sp = (Spannable) tv.getText();
			URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
			SpannableStringBuilder style = new SpannableStringBuilder(text);
			// clear old spans
			style.clearSpans();
			for (URLSpan url : urls) {
				WeiboSpan myURLSpan = new WeiboSpan(url.getURL());
				style.setSpan(myURLSpan, sp.getSpanStart(url),
						sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			tv.setText(style);
		}
	}

	public static void setTextViewLink(View view, int id, String content) {
		TextView tv = (TextView) view.findViewById(id);
		setTextViewLink(tv, content);
	}


	public static void setAvatar(View view, int id, String addr) {
		ImageView imageView = (ImageView) view.findViewById(id);
		WeiboController.getImageLoader().displayImage(addr, imageView,
				Configure.getAvatarDisplayOptions());
		Log.d(TAG, "setAvatar");
	}

}
