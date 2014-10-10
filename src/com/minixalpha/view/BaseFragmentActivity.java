package com.minixalpha.view;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import com.minixalpha.util.Utils;
import com.minixalpha.webo.R;

public abstract class BaseFragmentActivity extends FragmentActivity implements
		MenuAdapter.MenuListener {

	private static final String STATE_ACTIVE_POSITION = "net.simonvt.menudrawer.samples.LeftDrawerSample.activePosition";
	protected MenuDrawer mMenuDrawer;
	protected MenuAdapter mAdapter;
	protected ListView mList;
	private int mActivePosition = 0;

	@Override
	protected void onCreate(Bundle inState) {
		super.onCreate(inState);

		if (inState != null) {
			mActivePosition = inState.getInt(STATE_ACTIVE_POSITION);
		}

		mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND,
				getDrawerPosition(), getDragMode());

		View view = LayoutInflater.from(this).inflate(R.layout.left_menu, null);
		mMenuDrawer.setMenuView(view);

		// List<Object> items = new ArrayList<Object>();
		// items.add(new Item(Utils.loadFromResource(R.string.home),
		// R.drawable.home_32));
		// items.add(new Item(Utils.loadFromResource(R.string.search),
		// R.drawable.search_32));
		// items.add(new Item(Utils.loadFromResource(R.string.configure),
		// R.drawable.configure_32));
		// items.add(new Category(""));
		// items.add(new Item(Utils.loadFromResource(R.string.exit),
		// R.drawable.exit_32));
		//
		// mList = new ListView(this);
		//
		// mAdapter = new MenuAdapter(this, items);
		// mAdapter.setListener(this);
		// mAdapter.setActivePosition(mActivePosition);
		//
		// mList.setAdapter(mAdapter);
		// mList.setOnItemClickListener(mItemClickListener);
		//
		// mMenuDrawer.setMenuView(mList);

	}

	protected abstract void onMenuItemClicked(int position, Item item);

	protected abstract int getDragMode();

	protected abstract Position getDrawerPosition();

	private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			mActivePosition = position;
			mMenuDrawer.setActiveView(view, position);
			mAdapter.setActivePosition(position);
			onMenuItemClicked(position, (Item) mAdapter.getItem(position));
		}
	};

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_ACTIVE_POSITION, mActivePosition);
	}

	@Override
	public void onActiveViewChanged(View v) {
		mMenuDrawer.setActiveView(v, mActivePosition);
	}
}
