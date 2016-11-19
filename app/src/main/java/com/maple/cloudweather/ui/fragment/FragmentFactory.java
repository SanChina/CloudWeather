package com.maple.cloudweather.ui.fragment;

import android.util.SparseArray;

/**
 * Created by San on 2016/11/2.
 */

public class FragmentFactory {
    private static SparseArray<BaseFragment> mArray = new SparseArray<>();

    public static BaseFragment createFragment(int position) {
        BaseFragment fragment = mArray.get(position);
        if (fragment == null) {
            if (position == 0) {
                fragment = new HomeFragment();
            } else if (position == 1) {
                fragment = new MoreFragment();
            }
            mArray.put(position, fragment);
        }
        return fragment;
    }
}
