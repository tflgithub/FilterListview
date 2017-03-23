package com.cn.tfl.filterlistview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Happiness on 2017/3/22.
 */
public class PinnedHeaderListView extends ListView implements IIndexBarFilter {

    IPinnedHeader mAdapter;
    View mHeaderView, mIndexBarView, mPreviewTextView;
    // flags that decide view visibility
    boolean mHeaderVisibility = false;
    boolean mPreviewVisibility = false;
    // initially show index bar view with it's content
    boolean mIndexBarVisibility = true;

    // context object
    Context mContext;

    // view height and width
    int mHeaderViewWidth,
            mHeaderViewHeight,
            mIndexBarViewWidth,
            mIndexBarViewHeight,
            mIndexBarViewMargin,
            mPreviewTextViewWidth,
            mPreviewTextViewHeight;

    // touched index bar Y axis position used to decide preview text view position
    float mIndexBarY;


    public PinnedHeaderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }


    public void setPinnedHeaderView(View headerView) {
        this.mHeaderView = headerView;
        // Disable vertical fading when the pinned header is present
        // TODO change ListView to allow separate measures for top and bottom fading edge;
        // in this particular case we would like to disable the top, but not the bottom edge.
        if (mHeaderView != null) {
            setFadingEdgeLength(0);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHeaderView != null) {
            measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
            mHeaderViewWidth = mHeaderView.getMeasuredWidth();
            mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        }
        if (mIndexBarView != null && mIndexBarVisibility) {
            measureChild(mIndexBarView, widthMeasureSpec, heightMeasureSpec);
            mIndexBarViewWidth = mIndexBarView.getMeasuredWidth();
            mIndexBarViewHeight = mIndexBarView.getMeasuredHeight();
        }

        if (mPreviewTextView != null && mPreviewVisibility) {
            measureChild(mPreviewTextView, widthMeasureSpec, heightMeasureSpec);
            mPreviewTextViewWidth = mPreviewTextView.getMeasuredWidth();
            mPreviewTextViewHeight = mPreviewTextView.getMeasuredHeight();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mHeaderView != null) {
            mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
            configureHeaderView(getFirstVisiblePosition());
        }
        if (mIndexBarView != null && mIndexBarVisibility) {
            mIndexBarView.layout(getMeasuredWidth() - mIndexBarViewMargin - mIndexBarViewWidth, mIndexBarViewMargin
                    , getMeasuredWidth() - mIndexBarViewMargin, getMeasuredHeight() - mIndexBarViewMargin);
        }
        if (mPreviewTextView != null && mPreviewVisibility) {
//            mPreviewTextView.layout(mIndexBarView.getLeft() - mPreviewTextViewWidth, (int) mIndexBarY - (mPreviewTextViewHeight / 2)
//                    , mIndexBarView.getLeft(), (int) (mIndexBarY - (mPreviewTextViewHeight / 2)) + mPreviewTextViewHeight);
            mPreviewTextView.layout(getMeasuredWidth() / 2 - mPreviewTextViewWidth / 2, getMeasuredHeight() / 2 - mPreviewTextViewWidth / 2, getMeasuredWidth() / 2 + mPreviewTextViewWidth / 2, getMeasuredHeight() / 2 + mPreviewTextViewHeight / 2);
        }
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        this.mAdapter = (PinnedHeaderAdapter) adapter;
        super.setAdapter(adapter);
    }

    public void setIndexBarView(View indexBarView) {
        mIndexBarViewMargin = (int) mContext.getResources().getDimension(R.dimen.index_bar_view_margin);
        this.mIndexBarView = indexBarView;
    }


    public void setPreviewView(View previewTextView) {
        this.mPreviewTextView = previewTextView;
    }

    public void configureHeaderView(int position) {
        if (mHeaderView == null) {
            return;
        }

        int state = mAdapter.getPinnedHeaderState(position);

        switch (state) {
            case IPinnedHeader.PINNED_HEADER_GONE:
                mHeaderVisibility = false;
                break;
            case IPinnedHeader.PINNED_HEADER_VISIBLE:
                if (mHeaderView.getTop() != 0) {
                    mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
                }
                mAdapter.configurePinnedHeader(mHeaderView, position);
                mHeaderVisibility = true;
                break;
            case IPinnedHeader.PINNED_HEADER_PUSHED_UP:
                View firstView = getChildAt(0);
                int bottom = firstView.getBottom();
                int headerHeight = mHeaderView.getHeight();
                int y;
                if (bottom < headerHeight) {
                    y = (bottom - headerHeight);
                } else {
                    y = 0;
                }

                if (mHeaderView.getTop() != y) {
                    mHeaderView.layout(0, y, mHeaderViewWidth, mHeaderViewHeight + y);
                }
                mAdapter.configurePinnedHeader(mHeaderView, position);
                mHeaderVisibility = true;
                break;
        }
    }


    public void configurePreView(int position) {
        mAdapter.configurePinnedPreview(mPreviewTextView, position);
        setPreviewTextVisibility(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator animator = ObjectAnimator.ofFloat(mPreviewTextView, "alpha", 1, 0);
                animator.setDuration(1000);
                animator.start();
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        setPreviewTextVisibility(false);
                        super.onAnimationEnd(animation);
                    }
                });
            }
        }, 1000);
        requestLayout();
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);// draw list view elements (zIndex == 1)

        if (mHeaderView != null && mHeaderVisibility) {
            drawChild(canvas, mHeaderView, getDrawingTime()); // draw pinned header view (zIndex == 2)
        }
        if (mIndexBarView != null && mIndexBarVisibility) {
            drawChild(canvas, mIndexBarView, getDrawingTime()); // draw index bar view (zIndex == 3)
        }
        if (mPreviewTextView != null && mPreviewVisibility) {
            drawChild(canvas, mPreviewTextView, getDrawingTime()); // draw preview text view (zIndex == 4)
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mIndexBarView != null && (mIndexBarView).onTouchEvent(ev)) {
            setPreviewTextVisibility(true);
            return true;
        } else {
            setPreviewTextVisibility(false);
            return super.onTouchEvent(ev);
        }
    }

    public void setIndexBarVisibility(Boolean isVisible) {
        mIndexBarVisibility = isVisible;
    }

    private void setPreviewTextVisibility(Boolean isVisible) {
        mPreviewVisibility = isVisible;
    }


    @Override
    public void filterList(float indexBarY, int position, String previewText) {
        this.mIndexBarY = indexBarY;
        if (mPreviewTextView instanceof TextView)
            ((TextView) mPreviewTextView).setText(previewText);
        setSelection(position);
    }
}
