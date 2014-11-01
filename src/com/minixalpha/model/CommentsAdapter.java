package com.minixalpha.model;

import java.util.List;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.minixalpha.util.Utils;
import com.minixalpha.webo.R;
import com.sina.weibo.sdk.openapi.models.Comment;

public class CommentsAdapter extends ArrayAdapter<Comment> {
	private static final String TAG = CommentsAdapter.class.getName();
	private int resourceId;
	private Context mContext;
	private ViewHolder mViewHolder;

	public CommentsAdapter(Context context, int textViewReourceId,
			List<Comment> objects) {
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
		Log.d(TAG, "display" + position);
		Comment comment = getItem(position);
		WeiboItemAdapter.setAvatar(mViewHolder.mAvatar,
				comment.user.avatar_large);
		WeiboItemAdapter.setTextViewLink(mViewHolder.mScreenName,
				comment.user.screen_name);
		WeiboItemAdapter.setTextViewLink(mViewHolder.mCreateAt,
				Utils.getFormatTime(comment.created_at));
		WeiboItemAdapter.setTextViewContent(mViewHolder.mMainContent,
				comment.text);

		if (TextUtils.isEmpty(comment.status.text) == false) {
			WeiboItemAdapter.setTextViewContent(mViewHolder.mRepostContent,
					Utils.loadFromResource(R.string.comment_on_my_weibo)
							+ comment.status.text);
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
