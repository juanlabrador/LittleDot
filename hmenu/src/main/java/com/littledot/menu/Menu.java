package com.littledot.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Menu extends RelativeLayout {

	private MenuLayout mMenuLayout;

	private HorizontalScrollView mScroll;

	private ImageView mHintView;

	private ViewGroup controlLayout;

	public Menu(Context context) {
		super(context);
		init(context);
	}

	public Menu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		setClipChildren(false);

		LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.menu, this);

		mScroll = (HorizontalScrollView) findViewById(R.id.scroll);

		mMenuLayout = (MenuLayout) findViewById(R.id.item_layout);

		controlLayout = (ViewGroup) findViewById(R.id.control_layout);
		controlLayout.setClickable(true);
		controlLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mHintView.startAnimation(createHintSwitchAnimation(mMenuLayout.isExpanded()));
					mMenuLayout.switchState(true);
				}

				return false;
			}
		});

		mHintView = (ImageView) findViewById(R.id.control_hint);
	}

	public void openMenu() {
		mHintView.startAnimation(createHintSwitchAnimation(mMenuLayout.isExpanded()));
		mMenuLayout.switchState(true);
	}

	public void addItem(View item, OnClickListener listener) {
		mMenuLayout.addView(item);
		item.setOnClickListener(getItemClickListener(listener));
	}

	private OnClickListener getItemClickListener(final OnClickListener listener) {
		return new OnClickListener() {

			@Override
			public void onClick(final View viewClicked) {
				Animation animation = bindItemAnimation(viewClicked, true, 400);
				animation.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						postDelayed(new Runnable() {

							@Override
							public void run() {
								itemDidDisappear();
							}
						}, 0);
					}
				});

				final int itemCount = mMenuLayout.getChildCount();
				for (int i = 0; i < itemCount; i++) {
					View item = mMenuLayout.getChildAt(i);
					if (viewClicked != item) {
						bindItemAnimation(item, false, 300);
					}
				}

				mMenuLayout.invalidate();
				mHintView.startAnimation(createHintSwitchAnimation(true));

				if (listener != null) {
					listener.onClick(viewClicked);
				}
			}
		};
	}

	private Animation bindItemAnimation(final View child, final boolean isClicked, final long duration) {
		Animation animation = createItemDisapperAnimation(duration, isClicked);
		child.setAnimation(animation);

		return animation;
	}

	private void itemDidDisappear() {
		final int itemCount = mMenuLayout.getChildCount();
		for (int i = 0; i < itemCount; i++) {
			View item = mMenuLayout.getChildAt(i);
			item.clearAnimation();
		}

		mMenuLayout.switchState(false);
	}

	private static Animation createItemDisapperAnimation(final long duration, final boolean isClicked) {
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(new ScaleAnimation(1.0f, isClicked ? 2.0f : 0.0f, 1.0f, isClicked ? 2.0f : 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
		animationSet.addAnimation(new AlphaAnimation(1.0f, 0.0f));

		animationSet.setDuration(duration);
		animationSet.setInterpolator(new DecelerateInterpolator());
		animationSet.setFillAfter(true);

		return animationSet;
	}

	private Animation createHintSwitchAnimation(final boolean expanded) {
		Animation animation = new RotateAnimation(expanded ? 45 : 0, expanded ? 0 : 45, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setStartOffset(0);
		animation.setDuration(500);
		animation.setInterpolator(new DecelerateInterpolator());
		animation.setFillAfter(true);

		if (expanded == false) {
			mScroll.fullScroll(HorizontalScrollView.FOCUS_LEFT);
		}
		return animation;
	}

}
