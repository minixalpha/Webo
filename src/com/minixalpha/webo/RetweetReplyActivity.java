package com.minixalpha.webo;

import com.minixalpha.webo.control.UpdateWeiboCallback;
import com.minixalpha.webo.data.LocalEvent;
import com.minixalpha.webo.utils.Utils;
import com.minixalpha.webo.utils.WeiboAPI;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.Status;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 转发及评论模块
 * 
 * @author minixalpha
 *
 */
public class RetweetReplyActivity extends Activity {
	private static final String TAG = RetweetReplyActivity.class.getName();
	private static int mWordsLimit;
	private EditText mEditWeibo;
	private TextView mTextWordLimit;
	private int mLeftWordsCount;

	private ImageButton mBtnClear;
	private ImageButton mBtnSend;
	private CheckBox mCheckAddAction;
	private Status mStatus;

	private LocalBroadcastManager mLocalBroadcastManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_retweet_reply);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		mEditWeibo = (EditText) findViewById(R.id.create_weibo);
		mTextWordLimit = (TextView) findViewById(R.id.words_limit);
		mWordsLimit = Integer.valueOf(mTextWordLimit.getText().toString());
		mLeftWordsCount = mWordsLimit;
		mEditWeibo.addTextChangedListener(new WeiboTextChange());

		mBtnClear = (ImageButton) findViewById(R.id.btn_clear);
		mBtnClear.setOnClickListener(new ClickClear());
		mBtnSend = (ImageButton) findViewById(R.id.btn_send);
		mCheckAddAction = (CheckBox) findViewById(R.id.add_action);

		initLocalBroadcast();

		Intent intent = getIntent();
		String event = intent.getStringExtra(LocalEvent.REPLY_EVENT);
		String jsonData = intent.getStringExtra(LocalEvent.REPLY_SOURCE);
		mStatus = Status.parse(jsonData);
		if (LocalEvent.COMMENT.equals(event)) {
			setTitle(R.string.comment_title);
			mCheckAddAction.setText(R.string.add_reply);
			initComment();
		} else if (LocalEvent.REPOST.equals(event)) {
			setTitle(R.string.repost_title);
			mCheckAddAction.setText(R.string.add_comment);
			initRepost();
		}

	}

	private void initLocalBroadcast() {
		mLocalBroadcastManager = LocalBroadcastManager
				.getInstance(WeboApplication.getContext());
		IntentFilter intentFiler = new IntentFilter();
		intentFiler.addAction(LocalEvent.SEND_WEIBO_SUCCEED);
		BroadcastReceiver broadcastReceiver = new LocalReceiver();
		mLocalBroadcastManager.registerReceiver(broadcastReceiver, intentFiler);
	}

	// 局部广播，接收来自 Fragment 的事件，并处理
	private class LocalReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			NavUtils.navigateUpFromSameTask(RetweetReplyActivity.this);
		}

	}

	private void initComment() {
		mBtnSend.setOnClickListener(new ClickComment());
	}

	private void initRepost() {
		mBtnSend.setOnClickListener(new ClickRepost());
		String initContent = "//@" + mStatus.user.screen_name + ":"
				+ mStatus.text;
		mEditWeibo.setText(initContent);
		mLeftWordsCount = mWordsLimit - initContent.length();
		setWordsLimit();

	}

	private class ClickClear implements OnClickListener {

		@Override
		public void onClick(View v) {
			mEditWeibo.setText("");
			mTextWordLimit.setText(String.valueOf(mWordsLimit));

		}

	}

	private boolean canSend() {
		if (mLeftWordsCount == mWordsLimit) {
			// 字数太少
			String hint = getResources().getString(R.string.words_null);
			Toast.makeText(WeboApplication.getContext(), hint,
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (mLeftWordsCount < 0) {
			// 字数太多
			String hint = getResources().getString(R.string.words_overflow);
			Toast.makeText(WeboApplication.getContext(), hint,
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (!Utils.isNetworkAvailable()) {
			String networkHint = getResources().getString(
					R.string.network_not_available);
			Toast.makeText(WeboApplication.getContext(), networkHint,
					Toast.LENGTH_SHORT).show();
			return false;
		} else {
			return true;
		}

	}

	private class ClickRepost implements OnClickListener {
		@Override
		public void onClick(View v) {

			if (canSend()) {
				String sendHint = getResources().getString(R.string.send_ing);
				String text = mEditWeibo.getText().toString();
				Utils.sendNotification(sendHint, R.drawable.send_48, sendHint,
						text, null);

				StatusesAPI statusesAPI = WeiboAPI.getInstance()
						.getStatusesAPI();
				boolean addComment = mCheckAddAction.isChecked();
				statusesAPI.repost(text, Long.parseLong(mStatus.id),
						addComment, new UpdateWeiboCallback(mEditWeibo));

			}
		}
	}

	private class ClickComment implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (canSend()) {
				String sendHint = getResources()
						.getString(R.string.comment_ing);
				String text = mEditWeibo.getText().toString();
				Utils.sendNotification(sendHint, R.drawable.send_48, sendHint,
						text, null);
				CommentsAPI commentAPI = WeiboAPI.getInstance()
						.getCommentsAPI();
				boolean addReply = mCheckAddAction.isChecked();
				commentAPI.create(text, Long.parseLong(mStatus.id), addReply,
						new UpdateWeiboCallback(mEditWeibo));
			}

		}
	}

	private void setWordsLimit() {
		mTextWordLimit.setText(String.valueOf(mLeftWordsCount));
		if (mLeftWordsCount < 0) {
			mTextWordLimit.setTextColor(android.graphics.Color.RED);
		} else {
			mTextWordLimit.setTextColor(android.graphics.Color.BLACK);
		}
	}

	private class WeiboTextChange implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			mLeftWordsCount = mWordsLimit - s.length();

			setWordsLimit();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.retweet_reply, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
