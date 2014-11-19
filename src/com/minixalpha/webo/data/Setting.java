package com.minixalpha.webo.data;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.minixalpha.webo.R;
import com.minixalpha.webo.WeboApplication;
import com.minixalpha.webo.utils.Utils;

public class Setting {
	public static final String FONT_SIZE = Utils
			.loadFromResource(R.string.font_size);
	public static final String SMALL_FONT = Utils
			.loadFromResource(R.string.small_font);
	public static final String MID_FONT = Utils
			.loadFromResource(R.string.mid_font);
	public static final String LARGE_FONT = Utils
			.loadFromResource(R.string.large_font);

	public static final String IMAGE_SIZE = Utils
			.loadFromResource(R.string.image_size);
	public static final String NO_IMAGE = Utils
			.loadFromResource(R.string.no_image);
	public static final String SMALL_IMAGE = Utils
			.loadFromResource(R.string.small_image);
	public static final String MID_IMAGE = Utils
			.loadFromResource(R.string.mid_image);

	public static final int MIN_IMAGE_WIDTH = 200;
	public static final int MIN_IMAGE_HEIGHT = 150;

	public static final int MID_IMAGE_WIDTH = 400;
	public static final int MID_IMAGE_HEIGHT = 300;

	public static final int IMAGE_ROW = 3;
	public static final int IMAGE_VERTICAL_SPACING = 5;

	public static SharedPreferences mSettings = PreferenceManager
			.getDefaultSharedPreferences(WeboApplication.getContext());
	public static boolean isChanged = false;

	/**
	 * 根据字体的正常大小及字体等级，获取设置后字体大小
	 * 
	 * @param normalSize
	 *            字体的正常大小
	 * @return 设置后字体大小
	 */
	public static float getFontSize(float normalSize) {
		String fontSize = mSettings.getString(FONT_SIZE, MID_FONT);

		if (fontSize.equals(SMALL_FONT)) {
			return normalSize - 3.0f;
		} else if (fontSize.equals(MID_FONT)) {
			return normalSize;
		} else {
			return normalSize + 3.0f;
		}
	}

	/**
	 * 根据当前字体大小及字体等级，获取正常字体大小
	 * 
	 * @param curSize
	 *            当前字体大小
	 * @return 正常字体大小
	 */
	public static float getNormalFontSize(float curSize) {
		String fontSize = mSettings.getString(FONT_SIZE, MID_FONT);
		Log.d("bug", fontSize);

		if (fontSize.equals(SMALL_FONT)) {
			return curSize + 3.0f;
		} else if (fontSize.equals(MID_FONT)) {
			return curSize;
		} else {
			return curSize - 3.0f;
		}
	}

	/**
	 * 获取当前设置中的图片大小
	 * 
	 * @return 当前设置中的图片大小
	 */
	public static ImageSize getImageSize() {
		String imageLevel = mSettings.getString(IMAGE_SIZE, MID_IMAGE);
		ImageSize size = new ImageSize(0, 0);
		if (imageLevel.equals(MID_IMAGE)) {
			size = new ImageSize(MID_IMAGE_WIDTH, MID_IMAGE_HEIGHT);
		} else if (imageLevel.equals(SMALL_IMAGE)) {
			size = new ImageSize(MIN_IMAGE_WIDTH, MIN_IMAGE_HEIGHT);
		}
		return size;
	}

	public static boolean isNoneImageMode() {
		String imageLevel = mSettings.getString(IMAGE_SIZE, MID_IMAGE);
		return imageLevel.equals(NO_IMAGE);
	}

	public static void setChange() {
		isChanged = true;
	}

	public static void recover() {
		isChanged = false;
	}

	public static boolean isChanged() {
		return isChanged;
	}
}
