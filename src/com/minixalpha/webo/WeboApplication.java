package com.minixalpha.webo;

import com.minixalpha.control.Configure;
import com.nostra13.universalimageloader.core.ImageLoader;

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

	/* 图片缓存　 */
	private static ImageLoader mImageLoader;

	@Override
	public void onCreate() {
		mContext = getApplicationContext();
		mImageLoader = ImageLoader.getInstance();
		mImageLoader.init(Configure.getImageCacheConfig());
	}

	public static Context getContext() {
		return mContext;
	}
	
	public static ImageLoader getImageLoader() {
		return mImageLoader;
	}
}
