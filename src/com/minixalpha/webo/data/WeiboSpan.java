package com.minixalpha.webo.data;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;

public class WeiboSpan extends ClickableSpan {
	private static final String TAG = WeiboSpan.class.getName();

	String text;

	public WeiboSpan(String text) {
		super();
		this.text = text;
	}

	@Override
	public void updateDrawState(TextPaint ds) {
		ds.setColor(ds.linkColor);
		ds.setUnderlineText(false);
	}

	@Override
	public void onClick(View widget) {
		Log.d(TAG, text);
	}
}