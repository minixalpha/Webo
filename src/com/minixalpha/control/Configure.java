package com.minixalpha.control;

import com.minixalpha.webo.R;
import com.minixalpha.webo.WeboApplication;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class Configure {

	public static ImageLoaderConfiguration getImageCacheConfig() {
		return new ImageLoaderConfiguration.Builder(
				WeboApplication.getContext())
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				// 50 Mb
				.diskCacheSize(50 * 1024 * 1024)
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
	}

	public static DisplayImageOptions getAvatarDisplayOptions() {
		return new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.avatar)
				.showImageOnFail(R.drawable.avatar)
				.showImageOnLoading(R.drawable.avatar).cacheInMemory(true)
				.cacheOnDisk(true).build();
	}

	public static DisplayImageOptions getWeiboImageDisplayImageOptions() {
		return new DisplayImageOptions.Builder()
				.showImageOnFail(R.drawable.weibo_image_holder)
				.cacheOnDisk(true).build();
	}

	public static DisplayImageOptions getFullScreenImageDisplayImageOptions() {
		return new DisplayImageOptions.Builder().cacheOnDisk(true).build();
	}
}
