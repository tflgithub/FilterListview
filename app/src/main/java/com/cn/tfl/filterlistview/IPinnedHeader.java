package com.cn.tfl.filterlistview;

import android.view.View;

/**
 * Created by Happiness on 2017/3/22.
 */
public interface IPinnedHeader {
    /**
     * Pinned header state: don't show the header.
     */
    int PINNED_HEADER_GONE = 0;

    /**
     * Pinned header state: show the header at the top of the list.
     */
    int PINNED_HEADER_VISIBLE = 1;

    /**
     * Pinned header state: show the header. If the header extends beyond
     * the bottom of the first shown element, push it up and clip.
     */
    int PINNED_HEADER_PUSHED_UP = 2;

    /**
     * Computes the desired state of the pinned header for the given
     * position of the first visible list item. Allowed return values are
     * {@link #PINNED_HEADER_GONE}, {@link #PINNED_HEADER_VISIBLE} or
     * {@link #PINNED_HEADER_PUSHED_UP}.
     */
    int getPinnedHeaderState(int position);

    /**
     * Configures the pinned header view to match the first visible list item.
     *
     * @param header   pinned header view.
     * @param position position of the first visible list item.
     */
    void configurePinnedHeader(View header, int position);

    void configurePinnedPreview(View view, int position);
}
