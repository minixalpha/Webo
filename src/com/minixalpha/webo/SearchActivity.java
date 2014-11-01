package com.minixalpha.webo;

import java.util.ArrayList;
import java.util.List;

import com.minixalpha.model.Cache;
import com.minixalpha.model.SearchResult;
import com.minixalpha.model.SearchResultAdapter;
import com.minixalpha.model.WeiboItemAdapter;
import com.minixalpha.model.Weibo;
import com.minixalpha.util.Utils;
import com.minixalpha.util.WeiboAPI;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.SearchAPI;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends Activity {
	private static final int SEARCH_COUNT = 5;
	protected static final String TAG = SearchActivity.class.getName();
	private ListView mSearchResultListView;
	private SearchResultAdapter mSearchResultAdapter;
	private List<SearchResult> mSearchResult;

	private TextView mScreenNameTitle;
	private TextView mFollowersCountTitle;
	
	private ProgressBar mProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		mSearchResultListView = (ListView) findViewById(R.id.search_result);
		mSearchResult = new ArrayList<>();
		mSearchResultAdapter = new SearchResultAdapter(SearchActivity.this,
				R.layout.search_result_item, mSearchResult);
		mSearchResultListView.setAdapter(mSearchResultAdapter);

		mScreenNameTitle = (TextView) findViewById(R.id.screen_name);
		mFollowersCountTitle = (TextView) findViewById(R.id.followers_count);
		
		mProgressBar = (ProgressBar) findViewById(R.id.search_progress_bar);
		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {

		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			mProgressBar.setVisibility(ProgressBar.VISIBLE);
			String query = intent.getStringExtra(SearchManager.QUERY);
			SearchAPI searchAPI = WeiboAPI.getInstance().getSearchAPI();
			if (Utils.isNetworkAvailable()) {
				searchAPI.users(query, SEARCH_COUNT, new RequestListener() {

					@Override
					public void onComplete(String response) {
						List<SearchResult> resultList = SearchResult
								.parseTotal(response);
						if (resultList != null && resultList.size() > 0) {
							mSearchResult.clear();
							mSearchResult.addAll(resultList);
							mScreenNameTitle.setVisibility(ListView.VISIBLE);
							mFollowersCountTitle
									.setVisibility(ListView.VISIBLE);
							mSearchResultAdapter.notifyDataSetChanged();
						} else {
							Toast.makeText(
									WeboApplication.getContext(),
									Utils.loadFromResource(R.string.no_user_find),
									Toast.LENGTH_SHORT).show();
						}
						mProgressBar.setVisibility(ProgressBar.GONE);
					}

					@Override
					public void onWeiboException(WeiboException response) {
						mProgressBar.setVisibility(ProgressBar.GONE);
						Log.d(TAG, "search user failure");
					}

				});
			} else {
				Toast.makeText(WeboApplication.getContext(),
						Utils.loadFromResource(R.string.network_not_available),
						Toast.LENGTH_SHORT).show();
				mProgressBar.setVisibility(ProgressBar.GONE);
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		searchView.setIconified(false);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.search:
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
