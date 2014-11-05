package com.minixalpha.webo;

import com.minixalpha.webo.data.Configure;
import com.minixalpha.webo.utils.Utils;
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

/**
 * 登录界面
 * 
 * 如果找不到 Token，显示登录界面，否则进入客户端主界面
 * 
 * @author minixalpha
 * 
 */
public class LoginActivity extends BaseActivity {
	private static final String TAG = LoginActivity.class.getName();

	/** 微博 Web 授权类，提供登陆等功能 */
	private WeiboAuth mWeiboAuth;

	/** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能 */
	private Oauth2AccessToken mAccessToken;

	private Button btnLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		btnLogin = (Button) findViewById(R.id.btn_login);
		btnLogin.setOnClickListener(new ClickLogin());

		mAccessToken = Utils.readToken();

		if (mAccessToken != null && mAccessToken.isSessionValid()) {
			startMainPage();
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

	private void requestAuth() {
		// 创建微博实例
		mWeiboAuth = new WeiboAuth(this, Configure.getAppKey(),
				Configure.getRedirectURL(), Configure.getScope());

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
				startMainPage();
			}

			@Override
			public void onWeiboException(WeiboException exception) {
				Log.d(TAG, "auth exception" + exception.getMessage());
			}
		});
	}

	private void startMainPage() {
		Intent intent = new Intent(LoginActivity.this, MainPageActivity.class);
		startActivity(intent);
	}
}
