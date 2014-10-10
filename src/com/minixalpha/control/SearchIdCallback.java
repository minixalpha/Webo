package com.minixalpha.control;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.minixalpha.model.Cache;
import com.minixalpha.model.SearchResult;
import com.minixalpha.model.StatusAdapter;
import com.minixalpha.model.Weibo;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

/**
 * 搜索回调类
 * 
 * @author zhaoxk
 * 
 */
public class SearchIdCallback implements RequestListener {
	private static final String TAG = SearchIdCallback.class.getName();
	private TextView mTextview;
	private String mCurtext;
	private int mCompleteN;
	private int mTotalN;

	public SearchIdCallback(TextView textview, String curtext, int completeN,
			int totalN) {
		mTextview = textview;
		mCurtext = curtext;
		mTotalN = totalN;
	}

	@Override
	public void onComplete(String response) {
		Log.d(TAG, mCurtext );
		Log.d(TAG, response);
		List<SearchResult> resultList = SearchResult.parseTotal(response);
		if (resultList != null && resultList.size() > 0) {
			SearchResult result = resultList.get(0);
			Cache.updateUserId(result.screen_name,
					result.uid);
			mCurtext = mCurtext.replaceAll("@" + result.screen_name,
					Weibo.getHomeLink(result.screen_name, result.uid));
			mCompleteN++;
			
			if (mCompleteN == mTotalN) {
				StatusAdapter.setTextViewLink(mTextview, mCurtext);
			}
		}
	}

	@Override
	public void onWeiboException(WeiboException arg0) {
		Log.d(TAG, "searchAPI exception:" + arg0.toString());
	}

}
