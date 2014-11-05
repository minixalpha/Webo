package com.minixalpha.webo;

import java.util.ArrayList;

import com.minixalpha.webo.data.LocalEvent;
import com.minixalpha.webo.utils.Utils;
import com.minixalpha.webo.utils.WeiboAPI;
import com.minixalpha.webo.view.UserInfoView;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * 客户端主界面，用侧栏，主内容区组成，主内容区又由时间线，发微博，新消息三部分构成
 * 
 * @author minixalpha
 * 
 */
public class MainPageActivity extends FragmentActivity {
	private static final String TAG = MainPageActivity.class.getName();
	private PagerAdapter mPagerAdapter;
	private ViewPager mViewPager;
	private int mPagerOffsetPixels;
	private int mPagerPosition;
	private Menu mMenu;
	private LocalBroadcastManager mLocalBroadcastManager;
	private MenuDrawer mMenuDrawer;

	private Class<?>[] mTabClasses = { ViewWeiboFragment.class,
			CreateWeiboFragment.class, NewMessageFragment.class };

	@Override
	protected void onCreate(Bundle inState) {
		super.onCreate(inState);
		ActivityCollector.add(this);

		// 初始化侧栏
		initMenuDrawer();

		// 初始化 View Pager
		initViewPager();

		// 初始化局部广播
		initLocalBroadcast();

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mMenuDrawer != null) {
			// 进入可见状态时，menu drawer 要收回
			mMenuDrawer.closeMenu();
		}

	}

	// 初始化侧栏
	private void initMenuDrawer() {
		mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND,
				Position.LEFT, MenuDrawer.MENU_DRAG_CONTENT);
		View view = LayoutInflater.from(this).inflate(R.layout.left_menu, null);
		mMenuDrawer.setMenuView(view);
		mMenuDrawer.setContentView(R.layout.activity_main_page);
		mMenuDrawer.setSlideDrawable(R.drawable.ic_drawer_dark);
		mMenuDrawer.setDrawerIndicatorEnabled(true);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}

		// 拖拽屏幕边缘时才会弹出
		mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_BEZEL);
		mMenuDrawer
				.setOnInterceptMoveEventListener(new MenuDrawer.OnInterceptMoveEventListener() {
					@Override
					public boolean isViewDraggable(View v, int dx, int x, int y) {
						if (v == mViewPager) {
							return !(mPagerPosition == 0 && mPagerOffsetPixels == 0)
									|| dx < 0;
						}

						return false;
					}
				});

		initLefMenu(mMenuDrawer.getMenuView());
	}

	// 初始化侧栏的按钮
	private void initLeftMenuButton(View menuView) {
		Button btnHome = (Button) menuView.findViewById(R.id.btn_home);
		btnHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainPageActivity.this,
						HomePageActivity.class);
				startActivity(intent);
			}

		});

		Button btnSearch = (Button) menuView.findViewById(R.id.btn_search);
		btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainPageActivity.this,
						SearchActivity.class);
				startActivity(intent);

			}

		});

		Button btnConf = (Button) menuView.findViewById(R.id.btn_configure);
		btnConf.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainPageActivity.this,
						ConfigureActivity.class);
				startActivity(intent);

			}

		});

		initLogoutButton(menuView);
	}

	private void initLogoutButton(View menuView) {
		Button btnExit = (Button) menuView.findViewById(R.id.btn_exit);
		btnExit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						MainPageActivity.this);
				dialog.setTitle(Utils.loadFromResource(R.string.prompt_title));
				dialog.setMessage(R.string.logout_prompt);
				dialog.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								WeiboAPI.getInstance().logout(
										new RequestListener() {

											@Override
											public void onComplete(String arg0) {

											}

											@Override
											public void onWeiboException(
													WeiboException arg0) {

											}
										});
								Utils.clearToken();
								Intent intent = new Intent(
										MainPageActivity.this,
										LoginActivity.class);
								startActivity(intent);
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
			}

		});
	}

	// 初始化侧栏
	private void initLefMenu(View menuView) {
		UserInfoView.initUserInfo(menuView);
		initLeftMenuButton(menuView);
	}

	private void initViewPager() {
		mViewPager = (ViewPager) findViewById(R.id.view_pager);
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageScrolled(int position,
							float positionOffset, int positionOffsetPixels) {
						mPagerPosition = position;
						mPagerOffsetPixels = positionOffsetPixels;
						Log.d(TAG, position + "," + positionOffsetPixels);
					}

					@Override
					public void onPageSelected(int position) {
						// When swiping between pages, select the
						// corresponding tab.
						// getActionBar().setSelectedNavigationItem(position);
						if (mMenu != null) {
							changeMenuState(position);
						}

					}
				});

		mPagerAdapter = new PagerAdapter(this);

		for (int i = 0; i < mTabClasses.length; i++) {
			mPagerAdapter.addTab(mTabClasses[i], null);
		}

		mViewPager.setAdapter(mPagerAdapter);
	}

	private void initLocalBroadcast() {
		mLocalBroadcastManager = LocalBroadcastManager
				.getInstance(WeboApplication.getContext());
		IntentFilter intentFiler = new IntentFilter();
		intentFiler.addAction(LocalEvent.SEND_WEIBO_SUCCEED);
		BroadcastReceiver broadcastReceiver = new LocalReceiver();
		mLocalBroadcastManager.registerReceiver(broadcastReceiver, intentFiler);
	}

	// 局部广播，接收来自 Fragment 的事件，并处理
	private class LocalReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			mViewPager.setCurrentItem(0, true);
		}

	}

	// Menu Item 状态切换
	private void changeMenuState(int position) {
		switch (position) {
		case 0:
			mMenu.getItem(0).setIcon(R.drawable.weibo_24);
			mMenu.getItem(1).setIcon(R.drawable.write_grey_24);
			mMenu.getItem(2).setIcon(R.drawable.bell_grey_32);
			setTitle(R.string.app_name);
			closeSoftKeyboard();
			break;
		case 1:
			mMenu.getItem(0).setIcon(R.drawable.weibo_grey_24);
			mMenu.getItem(1).setIcon(R.drawable.write_24);
			mMenu.getItem(2).setIcon(R.drawable.bell_grey_32);
			setTitle(R.string.write_title);
			openSoftKeyboard();
			break;
		case 2:
			mMenu.getItem(0).setIcon(R.drawable.weibo_grey_24);
			mMenu.getItem(1).setIcon(R.drawable.write_grey_24);
			mMenu.getItem(2).setIcon(R.drawable.bell_32);
			setTitle(R.string.newmsg_title);
			closeSoftKeyboard();
			break;
		default:
			break;
		}
	}

	private void openSoftKeyboard() {
		EditText edittext = (EditText) findViewById(R.id.create_weibo);
		edittext.requestFocus();
		InputMethodManager inputMethod = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		Log.d(TAG, edittext.toString());
		inputMethod.showSoftInput(edittext, InputMethodManager.SHOW_IMPLICIT);
	}

	private void closeSoftKeyboard() {
		InputMethodManager inputMethod = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		View focusView = MainPageActivity.this.getCurrentFocus();
		if (focusView != null) {
			inputMethod.hideSoftInputFromWindow(focusView.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	public void onBackPressed() {
		final int drawerState = mMenuDrawer.getDrawerState();
		if (drawerState == MenuDrawer.STATE_OPEN
				|| drawerState == MenuDrawer.STATE_OPENING) {
			mMenuDrawer.closeMenu();
			return;
		}
		ActivityCollector.finishAll();
		super.onBackPressed();
	}

	/**
	 * This is a helper class that implements the management of tabs and all
	 * details of connecting a ViewPager with associated TabHost. It relies on a
	 * trick. Normally a tab host has a simple API for supplying a View or
	 * Intent that each tab will show. This is not sufficient for switching
	 * between pages. So instead we make the content part of the tab host 0dp
	 * high (it is not shown) and the TabsAdapter supplies its own dummy view to
	 * show as the tab content. It listens to changes in tabs, and takes care of
	 * switch to the correct paged in the ViewPager whenever the selected tab
	 * changes.
	 */
	public static class PagerAdapter extends FragmentPagerAdapter {

		private final Context mContext;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		static final class TabInfo {

			private final Class<?> mClss;
			private final Bundle mArgs;

			TabInfo(Class<?> aClass, Bundle args) {
				mClss = aClass;
				mArgs = args;
			}
		}

		public PagerAdapter(FragmentActivity activity) {
			super(activity.getSupportFragmentManager());
			mContext = activity;
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			return Fragment.instantiate(mContext, info.mClss.getName(),
					info.mArgs);
		}

		public void addTab(Class<?> clss, Bundle args) {
			TabInfo info = new TabInfo(clss, args);
			mTabs.add(info);
			notifyDataSetChanged();
		}

	}

	public static class TextViewFragment extends Fragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			FrameLayout frameLayout = new FrameLayout(getActivity());
			frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.MATCH_PARENT,
					FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER));

			TextView tv = new TextView(getActivity());
			tv.setText("This is an example of a Fragment in a View Pager");
			frameLayout.addView(tv);
			return frameLayout;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_pager, menu);
		mMenu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mMenuDrawer.toggleMenu();
			return true;
		case R.id.weibo:
			mViewPager.setCurrentItem(0, true);
			return true;
		case R.id.write:
			mViewPager.setCurrentItem(1, true);
			return true;
		case R.id.message:
			mViewPager.setCurrentItem(2, true);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
