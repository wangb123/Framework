package org.wbing.base.view.refreshlayout.impl;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import org.wbing.base.view.refreshlayout.EmptyRefreshLayout;
import org.wbing.base.view.scrollablelayout.ScrollableLayout;


/**
 * Created by mier on 2017/02/07.
 */
public class PullScrollableLayout extends EmptyRefreshLayout {

    private ScrollableLayout contentView;

    public PullScrollableLayout(Context context) {
        this(context, null);
    }

    public PullScrollableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setErrorOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mRefreshListener) {
                    mRefreshListener.onRefresh();
                }
            }
        });
        setLoadingHeaderEnable(true);
        contentView = (ScrollableLayout) mRefreshView;
    }

    @Override
    protected ScrollableLayout createRefreshView() {
        return new ScrollableLayout(getContext());
    }

    @Override
    public ScrollableLayout getRefreshView() {
        return contentView;
    }

    @Override
    protected boolean childIsOnTop() {
        return contentView.getCurrentY() <= 0;
    }

    @Override
    public void addView(View child) {
        if (child == mRefreshHeaderView || child == mRefreshView || child == mEmptyView) {
            super.addView(child);
            return;
        }
        contentView.addView(child);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child == mRefreshHeaderView || child == mRefreshView || child == mEmptyView) {
            super.addView(child, index, params);
            return;
        }
        contentView.addView(child, index, params);
    }

    @Override
    public void addView(View child, int width, int height) {
        if (child == mRefreshHeaderView || child == mRefreshView || child == mEmptyView) {
            super.addView(child, width, height);
            return;
        }
        contentView.addView(child, width, height);
    }

    @Override
    public void addView(View child, int index) {
        if (child == mRefreshHeaderView || child == mRefreshView || child == mEmptyView) {
            super.addView(child, index);
            return;
        }
        contentView.addView(child, index);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (child == mRefreshHeaderView || child == mRefreshView || child == mEmptyView) {
            super.addView(child, params);
            return;
        }
        contentView.addView(child, params);
    }

    @Override
    public void removeAllViews() {
        contentView.removeAllViews();
    }

    @Override
    public void removeAllViewsInLayout() {
        contentView.removeAllViewsInLayout();
    }

    @Override
    public void removeView(View view) {
        if (view == mRefreshHeaderView || view == mRefreshView || view == mEmptyView) {
            super.removeView(view);
            return;
        }
        contentView.removeView(view);
    }

    @Override
    public void removeViewInLayout(View view) {
        if (view == mRefreshHeaderView || view == mRefreshView || view == mEmptyView) {
            super.removeViewInLayout(view);
            return;
        }
        contentView.removeViewInLayout(view);
    }

}
