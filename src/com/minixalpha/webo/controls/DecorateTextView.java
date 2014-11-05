package com.minixalpha.webo.controls;

import com.minixalpha.webo.R;
import com.minixalpha.webo.utils.Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * 自定义的 TextView 控件，用于在 TextView 上加上前后缀
 * 
 * @author minixalpha
 * 
 */
public class DecorateTextView extends TextView {
	private static final String TAG = DecorateTextView.class.getName();
	private String mPrefix;
	private String mSuffix;

	public DecorateTextView(Context context) {
		super(context);
		init(null);
	}

	public DecorateTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);

	}

	public DecorateTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	private void init(AttributeSet attrs) {
		if (attrs == null) {
			Log.d(TAG, "null");
			return;
		}

		TypedArray typeArray = getContext().obtainStyledAttributes(attrs,
				R.styleable.DecorateTextView);
		mPrefix = typeArray.getString(R.styleable.DecorateTextView_prefix);
		mSuffix = typeArray.getString(R.styleable.DecorateTextView_suffix);
		typeArray.recycle();
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		super.setText(mPrefix + text + mSuffix, type);
	}

}
