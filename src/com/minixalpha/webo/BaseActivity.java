package com.minixalpha.webo;

import android.app.Activity;
import android.os.Bundle;

/**
 * 统一管理所有 Activity. 其子类，都会放入 ActivityCollector 以便统一销毁
 * 
 * @author minixalpha
 * 
 */
public class BaseActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityCollector.add(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityCollector.remove(this);
	}
}
