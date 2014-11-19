package com.minixalpha.webo;

import com.minixalpha.webo.data.Cache;
import com.minixalpha.webo.data.Setting;
import com.minixalpha.webo.utils.Utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {

	private ListPreference mFontSize;
	private ListPreference mImageSize;
	private SharedPreferences mPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.app_settings);
		mPreferences = getPreferenceScreen().getSharedPreferences();
		init();
	}

	private void init() {
		initPreferenceWidgets();
		initButton();
	}

	private void initPreferenceWidgets() {
		mFontSize = (ListPreference) findPreference(Setting.FONT_SIZE);
		mImageSize = (ListPreference) findPreference(Setting.IMAGE_SIZE);

		mFontSize.setSummary(mPreferences.getString(Setting.FONT_SIZE,
				Setting.MID_FONT));
		mImageSize.setSummary(mPreferences.getString(Setting.IMAGE_SIZE,
				Setting.MID_IMAGE));

	}

	private void initButton() {
		// 清除缓存
		Preference clearCacheBtn = findPreference(Utils
				.loadFromResource(R.string.clear_cache));
		clearCacheBtn
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						AlertDialog.Builder dialog = new AlertDialog.Builder(
								SettingsFragment.this.getActivity());
						dialog.setTitle(Utils
								.loadFromResource(R.string.prompt_title));
						dialog.setMessage(R.string.clear_cache_prompt);
						dialog.setPositiveButton(R.string.ok,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Cache.clearAll();
									}
								});
						dialog.setNegativeButton(R.string.cancel,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});
						dialog.show();
						return true;
					}

				});

		// 关于
		Preference aboutBtn = findPreference(Utils
				.loadFromResource(R.string.about));
		aboutBtn.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(SettingsFragment.this.getActivity(),
						AboutActivity.class);
				startActivity(intent);
				return true;
			}

		});

	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Setting.setChange();
		if (key.equals(Setting.FONT_SIZE)) {
			mFontSize.setSummary(sharedPreferences.getString(key,
					Setting.FONT_SIZE));
		} else if (key.equals(Setting.IMAGE_SIZE)) {
			mImageSize.setSummary(sharedPreferences.getString(key,
					Setting.IMAGE_SIZE));
		}
	}
}
