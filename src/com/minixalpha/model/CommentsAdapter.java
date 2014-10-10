package com.minixalpha.model;

import java.util.List;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.minixalpha.util.Utils;
import com.minixalpha.webo.R;
import com.sina.weibo.sdk.openapi.models.Comment;

public class CommentsAdapter extends ArrayAdapter<Comment> {
	private static final String TAG = CommentsAdapter.class.getName();
	private int resourceId;
	private Context mContext;

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
		} else {
			view = convertView;
		}
		Log.d(TAG, "display" + position);
		Comment comment = getItem(position);
		StatusAdapter.setAvatar(view, R.id.avatar, comment.user.avatar_large);
		StatusAdapter.setTextViewLink(view, R.id.screen_name,
				comment.user.screen_name);
		StatusAdapter.setTextViewLink(view, R.id.create_at,
				Utils.getFormatTime(comment.created_at));
		StatusAdapter.setTextViewContent(view, R.id.main_content, comment.text);

		if (TextUtils.isEmpty(comment.status.text) == false) {
			StatusAdapter.setTextViewContent(view, R.id.repost_content,
					"评论我的微博:" + comment.status.text);
		}

		return view;
	}
}
