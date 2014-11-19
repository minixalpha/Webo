package com.minixalpha.webo.adapter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.minixalpha.webo.*;
import com.minixalpha.webo.control.SearchIdCallback;
import com.minixalpha.webo.data.Cache;
import com.minixalpha.webo.data.Configure;
import com.minixalpha.webo.data.ImageSize;
import com.minixalpha.webo.data.LocalEvent;
import com.minixalpha.webo.data.Setting;
import com.minixalpha.webo.data.Weibo;
import com.minixalpha.webo.data.WeiboSpan;
import com.minixalpha.webo.utils.Utils;
import com.minixalpha.webo.utils.WeiboAPI;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

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
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
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
	 * 设置微博项的内容
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		Log.d(TAG, "pos:" + position);
		// 加载布局
		if (convertView == null) {
			view = LayoutInflater.from(mActivity).inflate(resourceId, null);
			mViewHolder = new ViewHolder(view);
			view.setTag(mViewHolder);
		} else {
			view = convertView;
			mViewHolder = (ViewHolder) view.getTag();
		}

		Status status = getItem(position);
		setHeader(status);
		setBody(status);
		setFooter(view, status);

		return view;
	}

	private void setHeader(Status status) {
		setAvatar(mViewHolder.mAvatar, status.user.avatar_large);
		setText(mViewHolder.mScreenName, status.user.screen_name);
		setText(mViewHolder.mCreateAt, Utils.getFormatTime(status.created_at));
	}

	private void setBody(Status status) {
		// 设置微博主要内容
		OnClickListener mainListener = new ClickItemAction(status);
		setOnClickAction(mViewHolder.mMain, mainListener);
		setTextWithAtAndLink(mViewHolder.mMainContent, status.text);
		setImages(mViewHolder.mMainContentImages, status);

		// 设置转发内容
		OnClickListener repostListener = new ClickItemAction(
				status.retweeted_status);
		setOnClickAction(mViewHolder.mRepost, repostListener);
		String retweetText = Weibo.getRetweetContent(status.retweeted_status);
		if (TextUtils.isEmpty(retweetText) == false) {
			User user = status.retweeted_status.user;
			Cache.updateUserId(user.screen_name, user.id);
		}
		setTextWithAtAndLink(mViewHolder.mRepostContent, retweetText);
		setImages(mViewHolder.mRepostContentImages, status.retweeted_status);
	}

	private void setFooter(View view, Status status) {
		setFooterAction(status);
		setFooterContent(status);
	}

	// 检查图片相关参数，判断是否需要显示图片
	private boolean needDisplayImage(GridView gridView, Status status) {
		if (gridView == null || status == null || status.pic_urls == null
				|| status.pic_urls.size() <= 0 || Setting.isNoneImageMode()) {
			return false;
		} else {
			return true;
		}
	}

	// 注意返回前把 gridView 设置为 GONE，否则会引起图串行
	private void setImages(GridView gridView, Status status) {
		if (needDisplayImage(gridView, status)) {
			ArrayList<String> pic_urlss = status.pic_urls;
			// 动态设置 GridView 高度
			int rowN = (pic_urlss.size() - 1) / Setting.IMAGE_ROW + 1;
			ViewGroup.LayoutParams params = gridView.getLayoutParams();

			if (pic_urlss.size() == 1 && status.bmiddle_pic != null) {
				// 单图
				gridView.setAdapter(new ImageAdapter(mActivity,
						status.bmiddle_pic));
				ImageSize imageSize = Setting.getImageSize();
				params.height = (imageSize.height + Setting.IMAGE_VERTICAL_SPACING)
						* rowN;
			} else {
				// 多图
				gridView.setAdapter(new ImageAdapter(mActivity, pic_urlss));
				params.height = (Setting.MIN_IMAGE_HEIGHT + Setting.IMAGE_VERTICAL_SPACING)
						* rowN;
				params.width = LayoutParams.MATCH_PARENT;
			}
			gridView.setLayoutParams(params);
			gridView.setVisibility(View.VISIBLE);
			gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		} else {
			if (gridView != null) {
				gridView.setVisibility(View.GONE);
			}
		}
	}

	private class ImageAdapter extends BaseAdapter {
		private Context mContext;
		private ArrayList<String> mPicUrls;
		private boolean mMultiPic;
		private ImageSize mImageSize;

		public ImageAdapter(Context c, ArrayList<String> pic_urlss) {
			mContext = c;
			mPicUrls = pic_urlss;
			mMultiPic = true;
			mImageSize = Setting.getImageSize();
		}

		public ImageAdapter(Context c, String picurl) {
			mContext = c;
			mPicUrls = new ArrayList<>();
			mPicUrls.add(picurl);
			mMultiPic = false;
			mImageSize = Setting.getImageSize();
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
							Setting.MIN_IMAGE_WIDTH, Setting.MIN_IMAGE_HEIGHT);
					imageView.setLayoutParams(params);
				} else {
					// 单中图
					imageView.setScaleType(ImageView.ScaleType.MATRIX);
					GridView.LayoutParams params = new GridView.LayoutParams(
							mImageSize.width, mImageSize.height);
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
						.getWeiboImageDisplayImageOptions(new BitmapProcessor() {

							@Override
							public Bitmap process(Bitmap bitmap) {
								final int actualH = bitmap.getHeight(), actualW = bitmap
										.getWidth();
								parent.post(new Runnable() {

									@Override
									public void run() {
										setParent(actualH, actualW, parent);
									}
								});
								return bitmap;
							}
						});
				WeboApplication.getImageLoader().displayImage(
						mPicUrls.get(position), imageView, options);
			}

			return imageView;
		}

		// 设置父控件大小，以适应子控件大小
		private void setParent(int actualH, int actualW, ViewGroup parent) {
			int setH = mImageSize.height, setW = mImageSize.width;
			ViewGroup.LayoutParams params = parent.getLayoutParams();
			params.height = Math.min(setH, actualH);
			params.width = Math.min(setW, actualW);
			parent.setLayoutParams(params);
		}
	}

	private void setOnClickAction(View tv, OnClickListener listener) {
		if (isValid(tv)) {
			tv.setOnClickListener(listener);
		}
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

	private void setFooterContent(Status status) {
		initCount(mViewHolder.mCommentCount,
				String.valueOf(status.comments_count));
		initCount(mViewHolder.mRepostCount,
				String.valueOf(status.reposts_count));
		initCount(mViewHolder.mFavourCount,
				String.valueOf(status.attitudes_count));
	}

	private void setFooterAction(final Status status) {
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

	/**
	 * 1. 搜索 at 信息
	 * 
	 * 2. 缓存 at 信息
	 * 
	 * 3. 根据缓存的 at 信息更新 content
	 * 
	 * 4. 将 content 送入 setTextWithLink
	 * 
	 * @param tv
	 *            文本所在控件
	 * @param content
	 *            文本内容
	 */
	public static void setTextWithAtAndLink(TextView tv, String content) {
		List<String> atList = Utils.getAtList(content);
		for (final String atName : atList) {
			content = content.replaceAll("@" + atName,
					Weibo.getHomeLinkFromAt(atName));
		}
		setTextWithLink(tv, content);
	}

	private static boolean needSetText(TextView tv, CharSequence content) {
		if (tv == null || TextUtils.isEmpty(content)) {
			return false;
		} else {
			return true;
		}
	}

	public static void setTextWithLink(TextView tv, String content) {
		CharSequence styledContent = getLinkedContentWithoutUnderline(content);
		setText(tv, styledContent);
	}

	public static void setText(TextView tv, CharSequence content) {
		if (needSetText(tv, content)) {
			setTextSize(tv);
			tv.setVisibility(View.VISIBLE);
			tv.setText(content);
		} else {
			if (tv != null) {
				tv.setVisibility(View.GONE);
			}
		}
	}

	// 在文本中添加链接形式(表现为蓝色)，并去掉下划线
	private static CharSequence getLinkedContentWithoutUnderline(String content) {
		Spannable sp = null;
		if (content != null) {
			String addHrefContent = Utils.getHrefLink(content);
			sp = (Spannable) Html.fromHtml(addHrefContent);
			URLSpan[] urls = sp.getSpans(0, sp.length(), URLSpan.class);
			for (URLSpan url : urls) {
				int start = sp.getSpanStart(url);
				int end = sp.getSpanEnd(url);
				sp.removeSpan(url);
				WeiboSpan myURLSpan = new WeiboSpan(url.getURL());
				sp.setSpan(myURLSpan, start, end, 0);
			}
		}
		return sp;
	}

	// 保存正常状态下字体，并根据字体等级设置当前字体大小
	private static void setTextSize(TextView tv) {
		Float normalFontSize = (Float) tv.getTag();
		if (normalFontSize == null) {
			normalFontSize = Utils.pixelsToSp(tv.getTextSize());
			tv.setTag(normalFontSize);
		}
		Float curFontSize = Setting.getFontSize(normalFontSize);
		tv.setTextSize(curFontSize);
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

	public static void setAvatar(ImageView imageView, String addr) {
		if (isValid(imageView)) {
			WeboApplication.getImageLoader().displayImage(addr, imageView,
					Configure.getAvatarDisplayOptions());
		}
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
		View mMain;
		TextView mMainContent;
		GridView mMainContentImages;
		View mRepost;
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

			mMain = view.findViewById(R.id.main);
			mMainContent = (TextView) view.findViewById(R.id.main_content);
			mMainContentImages = (GridView) view
					.findViewById(R.id.main_content_images);
			mRepost = view.findViewById(R.id.repost);
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
