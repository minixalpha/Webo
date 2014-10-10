package com.minixalpha.webo;

import com.minixalpha.control.UpdateWeiboCallback;
import com.minixalpha.model.LocalEvent;
import com.minixalpha.util.Utils;
import com.minixalpha.util.WeiboAPI;
import com.sina.weibo.sdk.openapi.StatusesAPI;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class CreateWeiboFragment extends Fragment {
	private static final String TAG = CreateWeiboFragment.class.getName();
	private static int mWordsLimit;
	private EditText mEditWeibo;
	private TextView mTextWordLimit;
	private int mLeftWordsCount;

	private ImageButton mBtnClear;
	private ImageButton mBtnSend;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = initFragement(inflater, container,
				R.layout.fragment_create_weibo);

		mEditWeibo = (EditText) view.findViewById(R.id.create_weibo);
		mTextWordLimit = (TextView) view.findViewById(R.id.words_limit);
		mWordsLimit = Integer.valueOf(mTextWordLimit.getText().toString());
		mLeftWordsCount = mWordsLimit;
		mEditWeibo.addTextChangedListener(new WeiboTextChange());

		mBtnClear = (ImageButton) view.findViewById(R.id.btn_clear);
		mBtnClear.setOnClickListener(new ClickClear());
		mBtnSend = (ImageButton) view.findViewById(R.id.btn_send);
		mBtnSend.setOnClickListener(new ClickSend());
		return view;
	}

	private class ClickClear implements OnClickListener {

		@Override
		public void onClick(View v) {
			mEditWeibo.setText("");
			mTextWordLimit.setText(String.valueOf(mWordsLimit));
		}

	}

	private class ClickSend implements OnClickListener {
		@Override
		public void onClick(View v) {
			
			if (mLeftWordsCount == mWordsLimit) {
				// 字数太少
				String hint = getResources().getString(R.string.words_null);
				Toast.makeText(WeboApplication.getContext(), hint,
						Toast.LENGTH_SHORT).show();
			} else if (mLeftWordsCount < 0) {
				// 字数太多
				String hint = getResources().getString(R.string.words_overflow);
				Toast.makeText(WeboApplication.getContext(), hint,
						Toast.LENGTH_SHORT).show();
			} else {
				if (Utils.isNetworkAvailable()) {
					String sendHint = getResources().getString(
							R.string.send_ing);
					String text = mEditWeibo.getText().toString();
					Utils.sendNotification(sendHint, R.drawable.send_48,
							sendHint, text, null);
					StatusesAPI statusesAPI = WeiboAPI.getInstance()
							.getStatusesAPI();
					statusesAPI.update(text, "0.0", "0.0",
							new UpdateWeiboCallback(mEditWeibo));
				} else {
					String networkHint = getResources().getString(
							R.string.network_not_available);
					Toast.makeText(WeboApplication.getContext(), networkHint,
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	private class WeiboTextChange implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			mLeftWordsCount = mWordsLimit - s.length();
			mTextWordLimit.setText(String.valueOf(mLeftWordsCount));

			if (mLeftWordsCount < 0) {
				mTextWordLimit.setTextColor(android.graphics.Color.RED);
			} else {
				mTextWordLimit.setTextColor(android.graphics.Color.BLACK);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
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

}