package com.minixalpha.webo.control;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.minixalpha.webo.R;
import com.minixalpha.webo.WeboApplication;
import com.minixalpha.webo.data.LocalEvent;
import com.minixalpha.webo.utils.Utils;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.models.Status;

public class UpdateWeiboCallback implements RequestListener {
	private static final String TAG = UpdateWeiboCallback.class.getName();

	private EditText mEditTextWeibo;

	public UpdateWeiboCallback(EditText editText) {
		mEditTextWeibo = editText;
	}
	@Override
	public void onComplete(String response) {
		if (!TextUtils.isEmpty(response)) {
			if (response.startsWith("{\"created_at\"")) {
				// 调用 Status#parse 解析字符串成微博对象
				Status status = Status.parse(response);
				String sendHint = WeboApplication.getContext().getResources()
						.getString(R.string.send_success);
				Utils.sendNotification(sendHint, R.drawable.done_48, sendHint,
						"", null);
				mEditTextWeibo.setText("");
				sentCompleteEvent();
				Log.d(TAG, status.id + ":" + status.text);
			} else {
				String sendHint = WeboApplication.getContext().getResources()
						.getString(R.string.send_failure);
				Utils.sendNotification(sendHint, R.drawable.clear_48, sendHint,
						"", null);
				Log.d(TAG, "update failure: not create");
			}
		} else {
			Log.d(TAG, "update failure: response null");
		}
	}

	@Override
	public void onWeiboException(WeiboException arg0) {

	}

	private void sentCompleteEvent() {
		LocalBroadcastManager localBroadcastManager = LocalBroadcastManager
				.getInstance(WeboApplication.getContext());
		Intent intent = new Intent(LocalEvent.SEND_WEIBO_SUCCEED);
		localBroadcastManager.sendBroadcast(intent);
	}
}
