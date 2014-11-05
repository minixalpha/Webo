package com.minixalpha.webo.data;

import com.minixalpha.webo.R;
import com.minixalpha.webo.WeboApplication;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * 各种配置信息
 * 
 * @author minixalpha
 * 
 */
public class Configure {

	/** 从新浪微博开放平台申请的 APP_KEY */
	private static final String APP_KEY = "1366822403";

	/**
	 * 当前应用的回调页
	 * 
	 * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
	 * 
	 */
	private static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

	/**
	 * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博
	 * 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利 选择赋予应用的功能。
	 * 
	 * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的 使用权限，高级权限需要进行申请。
	 * 
	 * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
	 * 
	 * 有关哪些 OpenAPI 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
	 * 关于 Scope 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
	 */
	private static final String SCOPE = "email,direct_messages_read,direct_messages_write,"
			+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
			+ "follow_app_official_microblog," + "invitation_write";

	/**
	 * 调试标记
	 */
	public static final String APP_DEBUG_FLAG = "webo";

	/**
	 * Image Loader 配置
	 */
	private static final ImageLoaderConfiguration mImageLoaderConfig = new ImageLoaderConfiguration.Builder(
			WeboApplication.getContext())
			.threadPriority(Thread.NORM_PRIORITY - 2)
			.denyCacheImageMultipleSizesInMemory()
			.diskCacheFileNameGenerator(new Md5FileNameGenerator())
			// 50 Mb
			.diskCacheSize(50 * 1024 * 1024)
			.tasksProcessingOrder(QueueProcessingType.LIFO).writeDebugLogs()
			.build();

	/**
	 * 微博头像显示配置
	 */
	private static final DisplayImageOptions mAvatarDisplayOptions = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.avatar)
			.showImageOnFail(R.drawable.avatar)
			.showImageOnLoading(R.drawable.avatar).cacheInMemory(true)
			.cacheOnDisk(true).build();

	/**
	 * 微博配图配置
	 */
	private static final DisplayImageOptions mWeiboImageDisplayImageOptions = new DisplayImageOptions.Builder()
			.showImageOnFail(R.drawable.weibo_image_holder).cacheOnDisk(true)
			.build();

	/**
	 * 图片全屏显示时的配置
	 */
	private static final DisplayImageOptions mFullScreenImageDisplayImageOptions = new DisplayImageOptions.Builder()
			.cacheOnDisk(true).build();

	public static ImageLoaderConfiguration getImageCacheConfig() {
		return mImageLoaderConfig;
	}

	public static DisplayImageOptions getAvatarDisplayOptions() {
		return mAvatarDisplayOptions;
	}

	public static DisplayImageOptions getWeiboImageDisplayImageOptions() {
		return mWeiboImageDisplayImageOptions;
	}

	public static DisplayImageOptions getFullScreenImageDisplayImageOptions() {
		return mFullScreenImageDisplayImageOptions;
	}

	public static String getAppKey() {
		return APP_KEY;
	}

	public static String getRedirectURL() {
		return REDIRECT_URL;
	}

	public static String getScope() {
		return SCOPE;
	}
	
	public static String getAppDebugFlag() {
		return APP_DEBUG_FLAG;
	}
}
