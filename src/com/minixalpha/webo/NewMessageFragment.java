package com.minixalpha.webo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public class NewMessageFragment extends Fragment {
	private static final String TAG = NewMessageFragment.class.getName();
	private View mView;

	private ImageButton mAtBtn;
	private ImageButton mCommentBtn;

	private Fragment mAtFragment;
	private Fragment mCommentFragment;
	private android.support.v4.app.FragmentManager mFragmentManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = initFragement(inflater, container,
				R.layout.fragment_new_message);
		mAtBtn = (ImageButton) mView.findViewById(R.id.newmsg_btn_at);
		mAtBtn.setOnClickListener(new ClickAt());

		mCommentBtn = (ImageButton) mView.findViewById(R.id.newmsg_btn_comment);
		mCommentBtn.setOnClickListener(new ClickComment());

		mAtFragment = new ViewAtFragment();
		mCommentFragment = new ViewCommentFragment();
		mFragmentManager = getFragmentManager();

		replaceFragment(mCommentFragment);
		return mView;
	}

	private void replaceFragment(Fragment fragment) {
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		transaction.replace(R.id.newmsg_area, fragment);
		transaction.commit();
	}

	private class ClickAt implements OnClickListener {

		@Override
		public void onClick(View v) {
			replaceFragment(mAtFragment);
			mCommentBtn.setImageResource(R.drawable.comments_32);
			mAtBtn.setImageResource(R.drawable.at_thin_red_50);
		}

	}

	private class ClickComment implements OnClickListener {

		@Override
		public void onClick(View v) {
			replaceFragment(mCommentFragment);
			mCommentBtn.setImageResource(R.drawable.comments_red_32);
			mAtBtn.setImageResource(R.drawable.at_thin_48);
		}

	}

	private View initFragement(LayoutInflater inflater, ViewGroup container,
			int resourceId) {
		FrameLayout frameLayout = new FrameLayout(getActivity());
		frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER));
		View view = inflater.inflate(resourceId, container, false);
		frameLayout.addView(view);
		return frameLayout;
	}

}
