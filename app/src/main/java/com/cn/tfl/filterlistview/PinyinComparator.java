package com.cn.tfl.filterlistview;

import java.util.Comparator;

/**
 * Created by Happiness on 2017/3/23.
 */
public class PinyinComparator implements Comparator<SortModel> {
    public int compare(SortModel o1, SortModel o2) {
        if (o1.getSortLetters().equals("@")
                || o2.getSortLetters().equals("#")) {
            return -1;
        } else if (o1.getSortLetters().equals("#")
                || o2.getSortLetters().equals("@")) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }
}
