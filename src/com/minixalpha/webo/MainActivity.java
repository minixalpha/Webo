package com.minixalpha.webo;

import com.minixalpha.util.Utils;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends BaseActivity {
	private static final String TAG = MainActivity.class.getName();

	/** 微博 Web 授权类，提供登陆等功能 */
	private WeiboAuth mWeiboAuth;

	/** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能 */
	private Oauth2AccessToken mAccessToken;

	private Button btnLogin;

	private void requestAuth() {
		// 创建微博实例
		mWeiboAuth = new WeiboAuth(this, Constants.APP_KEY,
				Constants.REDIRECT_URL, Constants.SCOPE);

		mWeiboAuth.anthorize(new WeiboAuthListener() {

			@Override
			public void onCancel() {
				Log.d(TAG, "cancel auth");
				finish();
			}

			@Override
			public void onComplete(Bundle values) {

				// 从 Bundle 中解析 Token
				mAccessToken = Oauth2AccessToken.parseAccessToken(values);
				Utils.saveToken(mAccessToken);
				Log.d(TAG, "auth complete");
				startView();
			}

			@Override
			public void onWeiboException(WeiboException exception) {
				Log.d(TAG, "auth exception" + exception.getMessage());
			}
		});
	}

	private void startView() {
		// Intent intent = new Intent(MainActivity.this, ViewActivity.class);
		// Intent intent = new Intent(MainActivity.this,
		// ViewWeiboActivity.class);
		Intent intent = new Intent(MainActivity.this, ViewPagerActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(Constants.APP_DEBUG_FLAG, "Main Activity onCreate");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btnLogin = (Button) findViewById(R.id.btn_login);
		btnLogin.setOnClickListener(new ClickLogin());

		mAccessToken = Utils.readToken();

		if (mAccessToken != null && mAccessToken.isSessionValid()) {
			startView();
		}
	}

	private class ClickLogin implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (Utils.isNetworkAvailable()) {
				requestAuth();
			} else {
				Toast.makeText(WeboApplication.getContext(),
						Utils.loadFromResource(R.string.network_not_available),
						Toast.LENGTH_LONG).show();
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
