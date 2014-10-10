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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sina.weibo.sdk.openapi.legacy.SearchAPI;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.User;

public class StatusAdapter extends ArrayAdapter<Status> {
	private static final String TAG = StatusAdapter.class.getName();
	private int resourceId;
	private Activity mActivity;

	public StatusAdapter(Activity activity, int textViewReourceId,
			List<Status> objects) {
		super(activity, textViewReourceId, objects);
		resourceId = textViewReourceId;
		mActivity = activity;
	}

	/**
	 * 子项被滚入屏幕时会被调用
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = null;
		// 加载布局
		if (convertView == null) {
			view = LayoutInflater.from(mActivity).inflate(resourceId, null);
		} else {
			view = convertView;
		}
		Status status = getItem(position);
		Log.d(TAG, String.valueOf(position) + "," + status.user.screen_name);

		// 设置头像
		setAvatar(view, R.id.avatar, status.user.avatar_large);
		setTextViewLink(view, R.id.screen_name, status.user.screen_name);

		// 点击文本区时的动作
		OnClickListener listener = new ClickItemAction(status);

		// 设置内容
		setTextViewContent(view, R.id.main_content, status.text);
		setTextViewAction(view, R.id.main_content, listener);
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
		setTextViewAction(view, R.id.repost_content, listener);
		setButton(view, status);

		return view;
	}

	private void setTextViewAction(View view, int id, OnClickListener listener) {
		TextView tv = (TextView) view.findViewById(id);
		tv.setOnClickListener(listener);
	}

	private class ClickItemAction implements OnClickListener {
		private Status mStatus;

		public ClickItemAction(Status status) {
			mStatus = status;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(mActivity, WeiboDetailsActivity.class);
			intent.putExtra(LocalEvent.STATUS, Utils.toJson(mStatus));
			mActivity.startActivity(intent);
		}

	}

	private void setButton(View view, final Status status) {
		initButton(view, R.id.btn_comment,
				String.valueOf(status.comments_count), new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mActivity,
								RetweetReplyActivity.class);
						intent.putExtra(LocalEvent.REPLY_EVENT,
								LocalEvent.COMMENT);
						intent.putExtra(LocalEvent.REPLY_SOURCE,
								Utils.toJson(status));
						mActivity.startActivity(intent);
					}

				});

		initButton(view, R.id.btn_repost, String.valueOf(status.reposts_count),
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mActivity,
								RetweetReplyActivity.class);
						intent.putExtra(LocalEvent.REPLY_EVENT,
								LocalEvent.REPOST);
						intent.putExtra(LocalEvent.REPLY_SOURCE,
								Utils.toJson(status));
						mActivity.startActivity(intent);
					}

				});

		initButton(view, R.id.btn_favour,
				String.valueOf(status.attitudes_count), new OnClickListener() {

					@Override
					public void onClick(View v) {
						Utils.showMsg(R.string.no_api);
					}

				});

	}

	/**
	 * 为包含有 at 的 TextView 设置内容
	 * 
	 * @param view
	 *            上层 view
	 * @param id
	 *            资源 id
	 * @param text
	 *            文本内容
	 */
	public static void setTextViewContent(View view, int id, String text) {
		TextView tv = (TextView) view.findViewById(id);
		if (tv == null || tv.getVisibility() == View.GONE) {
			return;
		}

		SearchAPI searchAPI = WeiboAPI.getInstance().getSearchAPI();

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

	private void initButton(View view, int id, String content,
			OnClickListener listener) {
		Button btn = (Button) view.findViewById(id);
		btn.setText(content);
		btn.setOnClickListener(listener);
	}

	public static void setAvatar(View view, int id, String addr) {
		ImageView imageView = (ImageView) view.findViewById(id);
		WeiboController.getImageLoader().displayImage(addr, imageView,
				Configure.getAvatarDisplayOptions());
		Log.d(TAG, "setAvatar");
	}

}
