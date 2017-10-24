package com.yaolan.common.ui;

import android.os.Bundle;

import com.yaolan.common.R;
import com.yaolan.common.databinding.FragmentUnknownBinding;

import org.wbing.base.ui.WFragment;

/**
 * Created by 王冰 on 2017/8/18.
 */

public class UnknownFragment extends WFragment<FragmentUnknownBinding> {

    public static UnknownFragment newInstance() {
        UnknownFragment fragment = new UnknownFragment();
        return fragment;
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected void onCreateView(Bundle savedInstanceState) {

    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_unknown;
    }
}
