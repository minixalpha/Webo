package com.minixalpha.webo;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;

public class ActivityCollector {
	public static final List<Activity> activities = new LinkedList<>();

	public static void add(Activity activity) {
		activities.add(activity);
	}

	public static void remove(Activity activity) {
		activities.remove(activity);
	}

	public static void finishAll() {
		for (Activity activity : activities) {
			if (activity.isFinishing() == false) {
				activity.finish();
			}
		}
	}
}
