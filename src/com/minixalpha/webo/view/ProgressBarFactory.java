package com.minixalpha.webo.view;

import com.minixalpha.webo.utils.Utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.AbsListView.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class ProgressBarFactory {
	private static final String TAG = ProgressBarFactory.class.getName();

	public static ProgressBar getProgressBar(final View view) {

		final ProgressBar progressBar = new ProgressBar(view.getContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		progressBar.setLayoutParams(params);
		progressBar.setIndeterminate(true);
		ViewGroup root = (ViewGroup) view.getRootView();
		root.addView(progressBar);
		progressBar.setVisibility(View.GONE);

		view.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@SuppressLint("NewApi")
					@Override
					public void onGlobalLayout() {
						int[] location = new int[2];
						ViewGroup.MarginLayoutParams params = (MarginLayoutParams) progressBar
								.getLayoutParams();
						params.leftMargin = location[0]
								+ (view.getWidth() - progressBar
										.getMeasuredWidth()) / 2;
						params.topMargin = location[1]
								+ (view.getHeight() / 2 + progressBar
										.getMeasuredHeight()) / 2;
						progressBar.setLayoutParams(params);

						if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
							progressBar.getViewTreeObserver()
									.removeOnGlobalLayoutListener(this);
						} else {
							progressBar.getViewTreeObserver()
									.removeGlobalOnLayoutListener(this);
						}

					}
				});

		return progressBar;
	}
}
