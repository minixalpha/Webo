package com.minixalpha.webo.adapter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.minixalpha.webo.*;
import com.minixalpha.webo.control.SearchIdCallback;
import com.minixalpha.webo.data.Cache;
import com.minixalpha.webo.data.Configure;
import com.minixalpha.webo.data.LocalEvent;
import com.minixalpha.webo.data.Weibo;
import com.minixalpha.webo.data.WeiboSpan;
import com.minixalpha.webo.utils.Utils;
import com.minixalpha.webo.utils.WeiboAPI;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.sina.weibo.sdk.openapi.legacy.SearchAPI;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.User;

/**
 * ListView 的 Adapter, 控制单条微博滚入屏幕时的行为
 * 
 * @author minixalpha
 * 
 */
public class WeiboItemAdapter extends ArrayAdapter<Status> {
	private static final String TAG = WeiboItemAdapter.class.getName();
	private int resourceId;
	private Activity mActivity;
	private ViewHolder mViewHolder;

	public WeiboItemAdapter(Activity activity, int textViewReourceId,
			List<Status> objects) {
		super(activity, textViewReourceId, objects);
		resourceId = textViewReourceId;
		mActivity = activity;
	}

	/**
	 * 子项被滚入屏幕时会被调用
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = null;

		// 加载布局
		if (convertView == null) {
			view = LayoutInflater.from(mActivity).inflate(resourceId, null);
			mViewHolder = new ViewHolder(view);
			view.setTag(mViewHolder);
		} else {
			view = convertView;
			mViewHolder = (ViewHolder) view.getTag();
			// mViewHolder = new ViewHolder(view);
		}
		Status status = getItem(position);

		// 设置头像
		setAvatar(mViewHolder.mAvatar, status.user.avatar_large);
		setTextViewLink(mViewHolder.mScreenName, status.user.screen_name);

		// 点击文本区时的动作
		OnClickListener listener = new ClickItemAction(status);

		// 设置内容
		setTextViewContent(mViewHolder.mMainContent, status.text);
		setTextViewAction(mViewHolder.mMainContent, listener);
		setTextViewLink(mViewHolder.mCreateAt,
				Utils.getFormatTime(status.created_at));
		setImages(mViewHolder.mMainContentImages, status);

		// setTextViewContent(view, R.id.source, status.source);

		// 设置转发内容
		String retweetText = Weibo.getRetweetContent(status.retweeted_status);
		if (TextUtils.isEmpty(retweetText) == false) {
			User user = status.retweeted_status.user;
			Cache.updateUserId(user.screen_name, user.id);
		}

		setTextViewContent(mViewHolder.mRepostContent, retweetText);
		setTextViewAction(mViewHolder.mRepostContent, listener);
		setImages(mViewHolder.mRepostContentImages, status.retweeted_status);

		// 设置微博项底部按钮
		setButton(view, status);
		// 设置操作数目
		setCount(view, status);

		return view;
	}

	// TODO: fixbug: image slide to others
	private void setImages(GridView gridView, Status status) {
		if (!isValid(gridView)) {
			return;
		}

		if (isValid(gridView) && status == null) {
			gridView.setVisibility(View.GONE);
			return;
		}

		ArrayList<String> pic_urlss = status.pic_urls;
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));

		if (pic_urlss != null && pic_urlss.size() > 0) {
			// 动态设置 GridView 高度
			int rowN = (pic_urlss.size() - 1) / ImageAdapter.IMAGE_ROW + 1;
			Log.d(TAG, "row:" + rowN);
			ViewGroup.LayoutParams params = gridView.getLayoutParams();
			if (pic_urlss.size() == 1 && status.bmiddle_pic != null) {
				gridView.setAdapter(new ImageAdapter(mActivity,
						status.bmiddle_pic));
				params.height = (ImageAdapter.MID_IMAGE_HEIGHT + ImageAdapter.IMAGE_VERTICAL_SPACING)
						* rowN;
			} else {
				gridView.setAdapter(new ImageAdapter(mActivity, pic_urlss));
				params.height = (ImageAdapter.MIN_IMAGE_HEIGHT + ImageAdapter.IMAGE_VERTICAL_SPACING)
						* rowN;
			}
			gridView.setLayoutParams(params);
			gridView.setVisibility(View.VISIBLE);
		} else {
			gridView.setVisibility(View.GONE);
		}
	}

	private class ImageAdapter extends BaseAdapter {
		private Context mContext;
		private ArrayList<String> mPicUrls;
		private static final int MIN_IMAGE_WIDTH = 200;
		private static final int MIN_IMAGE_HEIGHT = 150;

		private static final int MID_IMAGE_WIDTH = 400;
		private static final int MID_IMAGE_HEIGHT = 300;

		private static final int IMAGE_ROW = 3;
		private static final int IMAGE_VERTICAL_SPACING = 5;

		private boolean mMultiPic;

		public ImageAdapter(Context c, ArrayList<String> pic_urlss) {
			mContext = c;
			mPicUrls = pic_urlss;
			mMultiPic = true;
		}

		public ImageAdapter(Context c, String picurl) {
			mContext = c;
			mPicUrls = new ArrayList<>();
			mPicUrls.add(picurl);
			mMultiPic = false;
		}

		@Override
		public int getCount() {
			if (mPicUrls != null) {
				return mPicUrls.size();
			} else {
				return 0;
			}
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView,
				final ViewGroup parent) {
			final ImageView imageView;
			final int finalPos = position;
			if (convertView == null) {
				imageView = new ImageView(mContext);
				if (mMultiPic) {
					// 多小图
					imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
					GridView.LayoutParams params = new GridView.LayoutParams(
							MIN_IMAGE_WIDTH, MIN_IMAGE_HEIGHT);
					imageView.setLayoutParams(params);
				} else {
					// 单中图
					imageView.setScaleType(ImageView.ScaleType.FIT_START);
					GridView.LayoutParams params = new GridView.LayoutParams(
							MID_IMAGE_WIDTH, MID_IMAGE_HEIGHT);
					imageView.setLayoutParams(params);
				}

			} else {
				imageView = (ImageView) convertView;
			}

			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mActivity,
							ShowImagesViewPagerActivity.class);
					intent.putExtra(LocalEvent.IMAGE_POS, finalPos);
					intent.putStringArrayListExtra(LocalEvent.IMAGES_URL,
							mPicUrls);
					mActivity.startActivity(intent);
				}

			});

			if (mMultiPic) {
				WeboApplication.getImageLoader().displayImage(
						mPicUrls.get(position), imageView,
						Configure.getWeiboImageDisplayImageOptions());
			} else {
				// 单图的长和宽需要仔细考虑
				final DisplayImageOptions options = Configure
						.getWeiboImageDisplayImageOptions();
				WeboApplication.getImageLoader().loadImage(
						mPicUrls.get(position), options,
						new ImageLoadingListener() {

							@Override
							public void onLoadingStarted(String arg0, View arg1) {
							}

							@Override
							public void onLoadingFailed(String arg0, View arg1,
									FailReason arg2) {
								Drawable failDrawable = options
										.getImageOnFail(mActivity
												.getResources());
								imageView.setImageDrawable(failDrawable);

								int actualH = failDrawable.getIntrinsicHeight(), actualW = failDrawable
										.getIntrinsicWidth();
								setParent(actualH, actualW, parent);
							}

							@Override
							public void onLoadingComplete(String arg0,
									View arg1, Bitmap bitmap) {
								imageView.setImageBitmap(bitmap);
								int actualH = bitmap.getHeight(), actualW = bitmap
										.getWidth();
								setParent(actualH, actualW, parent);
							}

							@Override
							public void onLoadingCancelled(String arg0,
									View arg1) {
							}
						});
			}

			return imageView;
		}

		private void setParent(int actualH, int actualW, ViewGroup parent) {

			int setH = MID_IMAGE_HEIGHT, setW = MID_IMAGE_WIDTH;
			if (actualW > actualH) {
				setH = (int) (1.0 * setW / actualW * actualH);
				ViewGroup.LayoutParams params = parent.getLayoutParams();

				params.height = setH;

				parent.setLayoutParams(params);
			}
		}
	}

	private void setTextViewAction(TextView tv, OnClickListener listener) {
		if (!isValid(tv)) {
			return;
		}
		tv.setOnClickListener(listener);
	}

	private class ClickItemAction implements OnClickListener {
		private Status mStatus;

		public ClickItemAction(Status status) {
			mStatus = status;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(mActivity, WeiboDetailsActivity.class);
			intent.putExtra(LocalEvent.STATUS, Utils.toJson(mStatus));
			mActivity.startActivity(intent);
		}

	}

	private void setCount(View view, Status status) {
		initCount(mViewHolder.mCommentCount,
				String.valueOf(status.comments_count));
		initCount(mViewHolder.mRepostCount,
				String.valueOf(status.reposts_count));
		initCount(mViewHolder.mFavourCount,
				String.valueOf(status.attitudes_count));
	}

	private void setButton(View view, final Status status) {
		initButton(mViewHolder.mBtnComment, new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity,
						RetweetReplyActivity.class);
				intent.putExtra(LocalEvent.REPLY_EVENT, LocalEvent.COMMENT);
				intent.putExtra(LocalEvent.REPLY_SOURCE, Utils.toJson(status));
				mActivity.startActivity(intent);
			}

		});

		initButton(mViewHolder.mBtnRepost, new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity,
						RetweetReplyActivity.class);
				intent.putExtra(LocalEvent.REPLY_EVENT, LocalEvent.REPOST);
				intent.putExtra(LocalEvent.REPLY_SOURCE, Utils.toJson(status));
				mActivity.startActivity(intent);
			}

		});

		initButton(mViewHolder.mBtnFavour, new OnClickListener() {

			@Override
			public void onClick(View v) {
				Utils.showMsg(R.string.no_api);
			}

		});

	}

	public static void setTextViewContent(TextView tv, String text) {
		if (!isValid(tv)) {
			return;
		}

		SearchAPI searchAPI = WeiboAPI.getInstance().getSearchAPI();

		List<String> atList = Utils.getAtList(text);
		String curtext = text;
		int completeN = 0, totalN = atList.size();
		for (final String atName : atList) {
			String uid = Cache.getUserId(atName);
			if (TextUtils.isEmpty(uid) == false) {
				curtext = curtext.replaceAll("@" + atName,
						Weibo.getHomeLink(atName, uid));
				completeN++;
			}
		}
		setTextViewLink(tv, curtext);

		// 如果缓存中的数据已经够用，不用通过搜索去获取新数据了
		if (completeN == totalN) {
			return;
		}

		SearchIdCallback searchIdCallback = new SearchIdCallback(tv, curtext,
				completeN, totalN);
		for (final String atName : atList) {
			String uid = Cache.getUserId(atName);
			if (TextUtils.isEmpty(uid)) {
				boolean isNetworkAvailable = Utils.isNetworkAvailable();
				if (isNetworkAvailable) {
					/* TODO: need URLEncode or not */
					String encodeName = atName;
					try {
						encodeName = URLEncoder.encode(atName, "utf-8");
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}
					searchAPI.users(atName, 1, searchIdCallback);
				} else {
					Log.d(TAG, "network is not available");
				}
			}
		}
	}

	/**
	 * 为包含有 at 的 TextView 设置内容
	 * 
	 * @param view
	 *            上层 view
	 * @param id
	 *            资源 id
	 * @param text
	 *            文本内容
	 */
	public static void setTextViewContent(View view, int id, String text) {
		TextView tv = (TextView) view.findViewById(id);
		setTextViewContent(tv, text);
	}

	public static void setTextViewLink(TextView tv, String content) {
		if (!isValid(tv)) {
			return;
		}

		if (TextUtils.isEmpty(content)) {
			tv.setVisibility(View.GONE);
			return;
		}

		tv.setVisibility(View.VISIBLE);
		String addHrefContent = Utils.getHrefLink(content);
		tv.setText(Html.fromHtml(addHrefContent));
		tv.setMovementMethod(LinkMovementMethod.getInstance());

		CharSequence text = tv.getText();
		if (text instanceof Spannable) {
			int end = text.length();
			Spannable sp = (Spannable) tv.getText();
			URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
			SpannableStringBuilder style = new SpannableStringBuilder(text);
			// clear old spans
			style.clearSpans();
			for (URLSpan url : urls) {
				WeiboSpan myURLSpan = new WeiboSpan(url.getURL());
				style.setSpan(myURLSpan, sp.getSpanStart(url),
						sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			tv.setText(style);
		}
	}

	public static void setTextViewLink(View view, int id, String content) {
		TextView tv = (TextView) view.findViewById(id);
		setTextViewLink(tv, content);
	}

	private void initButton(View btn, OnClickListener listener) {
		if (isValid(btn)) {
			btn.setOnClickListener(listener);
		}
	}

	private void initCount(TextView btnText, String content) {
		if (isValid(btnText)) {
			btnText.setText(content);
		}
	}

	public static void setAvatar(View view, int id, String addr) {
		ImageView imageView = (ImageView) view.findViewById(id);
		setAvatar(imageView, addr);
	}

	public static void setAvatar(ImageView imageView, String addr) {
		if (!isValid(imageView)) {
			return;
		}
		WeboApplication.getImageLoader().displayImage(addr, imageView,
				Configure.getAvatarDisplayOptions());
	}

	/**
	 * 检测控件是否可用
	 * 
	 * @param view
	 *            被检测的控件
	 * @return 如果可用，返回 true, 否则返回 false
	 */
	private static boolean isValid(View view) {
		return view != null;
	}

	/**
	 * 用于保存一个微博项中的各种控件，避免反复获取
	 * 
	 * @author minixalpha
	 * 
	 */
	private class ViewHolder {
		// Weibo Item Header
		ImageView mAvatar;
		TextView mScreenName;
		TextView mCreateAt;
		TextView mSource;

		// Weibo Item Body
		TextView mMainContent;
		GridView mMainContentImages;
		TextView mRepostContent;
		GridView mRepostContentImages;

		// Weibo Footer
		View mBtnRepost;
		View mBtnComment;
		View mBtnFavour;

		TextView mRepostCount;
		TextView mCommentCount;
		TextView mFavourCount;

		public ViewHolder(View view) {
			mAvatar = (ImageView) view.findViewById(R.id.avatar);
			mScreenName = (TextView) view.findViewById(R.id.screen_name);
			mCreateAt = (TextView) view.findViewById(R.id.create_at);
			mSource = (TextView) view.findViewById(R.id.source);

			mMainContent = (TextView) view.findViewById(R.id.main_content);
			mMainContentImages = (GridView) view
					.findViewById(R.id.main_content_images);
			mRepostContent = (TextView) view.findViewById(R.id.repost_content);
			mRepostContentImages = (GridView) view
					.findViewById(R.id.repost_content_images);

			mBtnRepost = (View) view.findViewById(R.id.btn_repost);
			mBtnComment = (View) view.findViewById(R.id.btn_comment);
			mBtnFavour = (View) view.findViewById(R.id.btn_favour);
			mRepostCount = (TextView) view.findViewById(R.id.repost_count);
			mCommentCount = (TextView) view.findViewById(R.id.comment_count);
			mFavourCount = (TextView) view.findViewById(R.id.favor_count);
		}
	}

}
