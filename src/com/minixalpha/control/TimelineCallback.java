package com.minixalpha.control;

import android.text.TextUtils;
import android.widget.Toast;

import com.minixalpha.util.Utils;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.utils.LogUtil;

/**
 * 请求微博后的回调函数
 * 
 * @author zhaoxk
 * 
 */
public class TimelineCallback implements RequestListener {
	private static final String TAG = TimelineCallback.class.getName();
	private WeiboController mControllor;

	public TimelineCallback(WeiboController controllor) {
		mControllor = controllor;
	}

	@Override
	public void onComplete(String response) {

		boolean needNotify = true;
		if (!TextUtils.isEmpty(response)) {
			LogUtil.i(TAG, response);
			if (response.startsWith("{\"statuses\"")) {
				needNotify = false;
				// 更新时间线
				StatusList statusList = mControllor.updateTimeLine(response);

				mControllor.setLastStatus(statusList);

				// 更新后，刷新时间线UI，去除刷新 header
				mControllor.getTimeLineView().onRefreshComplete();

			} else if (response.startsWith("{\"created_at\"")) {
				// 调用 Status#parse 解析字符串成微博对象
				Status status = Status.parse(response);
				Toast.makeText(mControllor.getViewWeiboActivity(),
						"发送一送微博成功, id = " + status.id, Toast.LENGTH_LONG)
						.show();
			} else {
				Toast.makeText(mControllor.getViewWeiboActivity(), response,
						Toast.LENGTH_LONG).show();
			}
		}
		if (needNotify) {
			mControllor.actionAfterUpdate();
		}
	}

	@Override
	public void onWeiboException(WeiboException e) {
		LogUtil.e(TAG, e.getMessage());
		ErrorInfo info = ErrorInfo.parse(e.getMessage());
		Utils.showMsg(info.toString());
		mControllor.getTimeLineView().onRefreshComplete();
		mControllor.actionAfterUpdate();
	}

}
