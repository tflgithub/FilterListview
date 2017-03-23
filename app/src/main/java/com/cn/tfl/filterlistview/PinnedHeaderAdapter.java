package com.cn.tfl.filterlistview;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Happiness on 2017/3/22.
 */
public class PinnedHeaderAdapter extends BaseAdapter implements IPinnedHeader, AbsListView.OnScrollListener, Filterable {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SECTION = 1;
    private static final int TYPE_MAX_COUNT = TYPE_SECTION + 1;

    LayoutInflater mLayoutInflater;
    int mCurrentSectionPosition = 0, mNextSectionPosition = 0;

    // array list to store section positions
    ArrayList<Integer> mListSectionPos;

    // array list to store list view data
    ArrayList<String> mListItems;

    // context object
    Context mContext;

    public static String searchContent = null;

    public PinnedHeaderAdapter(Context context, ArrayList<String> listItems, ArrayList<Integer> listSectionPos) {
        this.mContext = context;
        this.mListItems = listItems;
        this.mListSectionPos = listSectionPos;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return mListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mListItems.get(position).hashCode();
    }


    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return !mListSectionPos.contains(position);
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return mListSectionPos.contains(position) ? TYPE_SECTION : TYPE_ITEM;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            holder = new ViewHolder();
            switch (type) {
                case TYPE_ITEM:
                    convertView = mLayoutInflater.inflate(R.layout.row_view, null);
                    break;
                case TYPE_SECTION:
                    convertView = mLayoutInflater.inflate(R.layout.section_row_view, null);
                    break;
            }
            holder.textView = (TextView) convertView.findViewById(R.id.row_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(mListItems.get(position).toString());
        String name = mListItems.get(position).toString();
        String temp = name.toLowerCase();
        if (type == TYPE_ITEM) {
            if (searchContent != null) {
                if (temp.indexOf(searchContent) != -1) {
                    SpannableStringBuilder style = new SpannableStringBuilder(name);
                    style.setSpan(
                            new ForegroundColorSpan(Color.parseColor("#00cc00")),
                            temp.indexOf(searchContent),
                            temp.indexOf(searchContent) + searchContent.length(),
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE); // 设置指定位置文字的颜色
                    holder.textView.setText(style);
                }
            }
        }
        return convertView;
    }

    @Override
    public int getPinnedHeaderState(int position) {
        if (getCount() == 0 || position < 0 || mListSectionPos.indexOf(position) != -1) {
            return PINNED_HEADER_GONE;
        }

        // the header should get pushed up if the top item shown
        // is the last item in a section for a particular letter.
        mCurrentSectionPosition = getCurrentSectionPosition(position);
        mNextSectionPosition = getNextSectionPosition(mCurrentSectionPosition);
        if (mNextSectionPosition != -1 && position == mNextSectionPosition - 1) {
            return PINNED_HEADER_PUSHED_UP;
        }
        return PINNED_HEADER_VISIBLE;
    }

    public int getCurrentSectionPosition(int position) {
        String listChar = mListItems.get(position).toString().substring(0, 1).toUpperCase(Locale.getDefault());
        return mListItems.indexOf(listChar);
    }

    public int getNextSectionPosition(int currentSectionPosition) {
        int index = mListSectionPos.indexOf(currentSectionPosition);
        if ((index + 1) < mListSectionPos.size()) {
            return mListSectionPos.get(index + 1);
        }
        return mListSectionPos.get(index);
    }

    @Override
    public void configurePinnedHeader(View v, int position) {
        TextView header = (TextView) v;
        mCurrentSectionPosition = getCurrentSectionPosition(position);
        if (mCurrentSectionPosition != -1) {
            header.setText(mListItems.get(mCurrentSectionPosition));
        }
    }

    @Override
    public void configurePinnedPreview(View view, int position) {
        TextView view1 = (TextView) view;
        String listChar = mListItems.get(position).toString().substring(0, 1);
        view1.setText(listChar);
    }

    private boolean isScroll = false;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        isScroll = scrollState > 0 ? true : false;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (view instanceof PinnedHeaderListView) {
            ((PinnedHeaderListView) view).configureHeaderView(firstVisibleItem);
        }
        if (isScroll) {
            ((PinnedHeaderListView) view).configurePreView(firstVisibleItem);
        }
    }

    @Override
    public Filter getFilter() {
        return ((MainActivity) mContext).new ListFilter();
    }

    public static class ViewHolder {
        public TextView textView;
    }

}
