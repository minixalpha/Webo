package com.minixalpha.webo;

import android.app.Application;
import android.content.Context;

/**
 * 自定义的　Application, 用于统一获取全局的 Contex
 * 
 * @author zhaoxk
 * 
 */
public class WeboApplication extends Application {
	private static Context mContext;

	@Override
	public void onCreate() {
		mContext = getApplicationContext();
	}

	public static Context getContext() {
		return mContext;
	}
}
