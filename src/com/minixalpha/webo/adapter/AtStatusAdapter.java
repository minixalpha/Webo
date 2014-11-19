package com.minixalpha.webo.adapter;

import java.util.List;
import com.minixalpha.webo.*;
import com.minixalpha.webo.data.Weibo;
import com.minixalpha.webo.utils.Utils;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.sina.weibo.sdk.openapi.models.Status;

public class AtStatusAdapter extends ArrayAdapter<Status> {
	private static final String TAG = AtStatusAdapter.class.getName();
	private int resourceId;
	private Context mContext;
	private ViewHolder mViewHolder;

	public AtStatusAdapter(Context context, int textViewReourceId,
			List<Status> objects) {
		super(context, textViewReourceId, objects);
		resourceId = textViewReourceId;
		mContext = context;
	}

	/**
	 * 子项被滚入屏幕时会被调用
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		// 加载布局
		if (convertView == null) {
			view = LayoutInflater.from(mContext).inflate(resourceId, null);
			mViewHolder = new ViewHolder(view);
			view.setTag(mViewHolder);
		} else {
			view = convertView;
			mViewHolder = (ViewHolder) view.getTag();
		}
		Status status = getItem(position);
		WeiboItemAdapter.setAvatar(mViewHolder.mAvatar,
				status.user.avatar_large);
		WeiboItemAdapter.setTextWithLink(mViewHolder.mScreenName,
				status.user.screen_name);
		WeiboItemAdapter.setTextWithLink(mViewHolder.mCreateAt,
				Utils.getFormatTime(status.created_at));

		WeiboItemAdapter.setTextWithAtAndLink(mViewHolder.mMainContent,
				status.text);

		String retweetText = Weibo.getRetweetContent(status.retweeted_status);
		if (TextUtils.isEmpty(retweetText) == false) {
			WeiboItemAdapter.setTextWithAtAndLink(mViewHolder.mRepostContent,
					Utils.loadFromResource(R.string.comment_on_my_weibo)
							+ retweetText);
		}
		return view;
	}

	/**
	 * 用于保存一个评论项中的各种控件，避免反复获取
	 * 
	 * @author minixalpha
	 * 
	 */
	private class ViewHolder {
		// Weibo Item Header
		ImageView mAvatar;
		TextView mScreenName;
		TextView mCreateAt;

		// Weibo Item Body
		TextView mMainContent;
		TextView mRepostContent;

		public ViewHolder(View view) {
			mAvatar = (ImageView) view.findViewById(R.id.avatar);
			mScreenName = (TextView) view.findViewById(R.id.screen_name);
			mCreateAt = (TextView) view.findViewById(R.id.create_at);

			mMainContent = (TextView) view.findViewById(R.id.main_content);
			mRepostContent = (TextView) view.findViewById(R.id.repost_content);
		}
	}
}
