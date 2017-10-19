package org.wbing.base.view.refreshlayout;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.wbing.base.R;
import org.wbing.base.view.empty.EmptyView;


/**
 * 可以显示空view的下拉刷新控件
 *
 * @author suitian on 16/9/12.
 * @see RefreshLayout
 */
public abstract class EmptyRefreshLayout extends RefreshLayout {


    /**
     * 空View和实际View切换时使用的缓存
     */
    private View mCache;
    /**
     * 空View
     */
    protected EmptyView mEmptyView;

    /**
     * 当前是否显示空View
     */
    private boolean isEmpty;

    /**
     * 当前是否显示错误view
     */
    private boolean isError;

    private OnEmptyOrErrorListener emptyOrErrorListener;

    public EmptyRefreshLayout(Context context) {
        super(context);
        init();
    }

    public EmptyRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mEmptyView = new EmptyView(getContext());
    }

    /**
     * 设置空view和错误view的监听器
     *
     * @param emptyOrErrorListener
     */
    public void setEmptyOrErrorListener(OnEmptyOrErrorListener emptyOrErrorListener) {
        this.emptyOrErrorListener = emptyOrErrorListener;
    }

    /**
     * 设置空view的点击事件
     *
     * @param l
     * @return
     */
    public EmptyRefreshLayout setEmptyOnclickListener(OnClickListener l) {
        mEmptyView.setEmptyOnclickListener(l);
        return this;
    }

    /**
     * 设置错误view的点击事件
     *
     * @param l
     * @return
     */
    public EmptyRefreshLayout setErrorOnclickListener(OnClickListener l) {
        mEmptyView.setErrorOnclickListener(l);
        return this;
    }

    /**
     * 显示错误view
     */
    public void showErrorView() {
        showErrorView(mEmptyView);
    }

    /**
     * 显示错误view
     */
    public final void showErrorView(EmptyView errorView) {
        if (isError()) {
            return;
        }

        mEmptyView.showErrorView();

        if (errorView != null && mEmptyView != errorView) {
            mEmptyView = errorView;
        }

        mCache = mRefreshView;
        mRefreshView.setVisibility(GONE);
        mRefreshView = mEmptyView;
        mRefreshView.setVisibility(VISIBLE);
        removeView(mRefreshView);
        addView(mRefreshView);
        isError = true;

        if (emptyOrErrorListener != null) {
            emptyOrErrorListener.onErrorViewShow();
        }
    }

    /**
     * 显示空View
     */
    public void showEmptyView() {
        showEmptyView(mEmptyView);
    }

    /**
     * 以自定义的View作为空View显示
     *
     * @param emptyView 自定义的空view
     */
    public final void showEmptyView(EmptyView emptyView) {
        if (isEmpty()) {
            return;
        }

        mEmptyView.showEmptyView();

        if (emptyView != null && mEmptyView != emptyView) {
            mEmptyView = emptyView;
        }

        mCache = mRefreshView;
        mRefreshView.setVisibility(GONE);
        mRefreshView = mEmptyView;
        mRefreshView.setVisibility(VISIBLE);
        removeView(mRefreshView);
        addView(mRefreshView);
        isEmpty = true;

        if (emptyOrErrorListener != null) {
            emptyOrErrorListener.onEmptyViewShow();
        }
    }

    /**
     * 隐藏空View
     */
    public final void hideEmptyView() {
        if (!isEmpty()) {
            return;
        }

        if (mCache != null && mRefreshView != null) {
            removeView(mRefreshView);
            mRefreshView = mCache;
            mCache = null;
            mRefreshView.setVisibility(VISIBLE);
        }
        isEmpty = false;

        if (emptyOrErrorListener != null) {
            emptyOrErrorListener.onEmptyViewHide();
        }
    }

    /**
     * 隐藏错误View
     */
    public final void hideErrorView() {
        if (!isError()) {
            return;
        }

        if (mCache != null && mRefreshView != null) {
            removeView(mRefreshView);
            mRefreshView = mCache;
            mCache = null;
            mRefreshView.setVisibility(VISIBLE);
        }
        isError = false;

        if (emptyOrErrorListener != null) {
            emptyOrErrorListener.onErrorViewHide();
        }
    }

    /**
     * 设置空View的icon
     *
     * @return
     */
    public EmptyRefreshLayout setEmptyIcon(int iconID) {
        mEmptyView.setEmptyIcon(iconID);
        return this;
    }

    /**
     * 设置错误View的icon
     *
     * @return
     */
    public EmptyRefreshLayout setErrorIcon(int iconID) {
        mEmptyView.setErrorIcon(iconID);

        return this;
    }

    /**
     * 设置空view按钮的文案
     *
     * @param text
     * @return
     */
    public EmptyRefreshLayout setEmptyBtn(int text) {
        if (TextUtils.isEmpty(getResources().getString(text))) {
            return toggleEmptyBtn(false);
        }

        mEmptyView.getEmptyBtn().setText(getResources().getString(text));
        return toggleEmptyBtn(true);
    }

    /**
     * 设置空view按钮的文案
     *
     * @param text
     * @return
     */
    public EmptyRefreshLayout setEmptyBtn(String text) {
        if (TextUtils.isEmpty(text)) {
            return toggleEmptyBtn(false);
        }

        mEmptyView.getEmptyBtn().setText(text);
        return toggleEmptyBtn(true);
    }

    /**
     * 是否显示按钮
     *
     * @param display
     * @return
     */
    public EmptyRefreshLayout toggleEmptyBtn(boolean display) {
        if (null == mEmptyView.getEmptyBtn()) {
            return this;
        }
        if (display) {
            mEmptyView.getEmptyBtn().setVisibility(View.VISIBLE);
        } else {
            mEmptyView.getEmptyBtn().setVisibility(View.INVISIBLE);
        }

        return this;
    }


    /**
     * 设置按钮的点击事件
     *
     * @param onClickListener
     * @return
     */
    public EmptyRefreshLayout setEmptyBtn(View.OnClickListener onClickListener) {
        if (null == mEmptyView.getEmptyBtn()) {
            return toggleEmptyBtn(false);
        }

        mEmptyView.getEmptyBtn().setOnClickListener(onClickListener);
        return toggleSubHead(true);
    }


    /**
     * 设置空view 的subhead的文案
     *
     * @param text
     * @return
     */
    public EmptyRefreshLayout setEmptySubHead(int text) {
        if (TextUtils.isEmpty(getResources().getString(text))) {
            return toggleSubHead(false);
        }

        mEmptyView.setEmptySubText(text);
        return toggleSubHead(true);
    }


    /**
     * 设置空view 的subhead的文案
     *
     * @param text
     * @return
     */
    public EmptyRefreshLayout setEmptySubHead(String text) {
        if (TextUtils.isEmpty(text)) {
            return toggleSubHead(false);
        }

        mEmptyView.setEmptySubText(text);
        return toggleSubHead(true);
    }


    /**
     * 设置空view 的subhead的文案
     *
     * @param text
     * @return
     */
    public EmptyRefreshLayout setErrorSubHead(int text) {
        if (TextUtils.isEmpty(getResources().getString(text))) {
            return toggleSubHead(false);
        }

        mEmptyView.setErrorSubText(text);
        return toggleSubHead(true);
    }


    /**
     * 设置空view 的subhead的文案
     *
     * @param text
     * @return
     */
    public EmptyRefreshLayout setErrorSubHead(String text) {
        if (TextUtils.isEmpty(text)) {
            return toggleSubHead(false);
        }

        mEmptyView.setErrorSubText(text);
        return toggleSubHead(true);
    }


    /**
     * 是否显示subhead
     *
     * @param display
     * @return
     */
    public EmptyRefreshLayout toggleSubHead(boolean display) {
        if (null == mEmptyView.getSubHeadView()) {
            return this;
        }
        if (display) {
            mEmptyView.getSubHeadView().setVisibility(View.VISIBLE);
        } else {
            mEmptyView.getSubHeadView().setVisibility(View.GONE);
        }

        return this;
    }


    /**
     * 设置空view的文案
     *
     * @param text
     * @return
     */
    public EmptyRefreshLayout setEmptyText(String text) {
        mEmptyView.setEmptyText(text);
        return this;
    }


    /**
     * 设置空view的文案
     *
     * @param textID
     * @return
     */
    public EmptyRefreshLayout setEmptyText(int textID) {
        mEmptyView.setEmptyText(textID);
        return this;
    }

    /**
     * 设置空view的文案
     *
     * @param text
     * @return
     */
    public EmptyRefreshLayout setErrorText(String text) {
        mEmptyView.setErrorText(text);
        return this;
    }


    /**
     * 设置空view的文案
     *
     * @param textID
     * @return
     */
    public EmptyRefreshLayout setErrorText(int textID) {
        mEmptyView.setErrorText(textID);
        return this;
    }

    /**
     * 显示默认的空View
     *
     * @return
     */
    public final void showEmptyDefault() {
        setEmptyText(getResources().getString(R.string.empty_otherall));
        setEmptyIcon(R.drawable.icon_empty_net_error);
        setEmptySubHead(getResources().getString(R.string.empty_default_op));
        showEmptyView();
    }

    /**
     * 显示默认的错误View
     *
     * @return
     */
    public final void showErrorDefault() {
        setErrorText(getResources().getString(R.string.error_default));
        setErrorIcon(R.drawable.icon_empty_net_error);
        setErrorSubHead(getResources().getString(R.string.error_default_op));
        showErrorView();
    }
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        if (mEmptyView.getEmptyBtn() != null && mEmptyView.getEmptyBtn().getVisibility() == VISIBLE && inRangeOfView(mEmptyView.getEmptyBtn(), ev)){
//            return super.onInterceptTouchEvent(ev);
//        }if (mEmptyView.getEmptyText() != null && mEmptyView.getEmptyText().getVisibility() == VISIBLE && inRangeOfView(mEmptyView.getEmptyText(), ev)){
//            return super.onInterceptTouchEvent(ev);
//        }else if (inRangeOfView(mEmptyView, ev)){
//            return true;
//        }
//
//        return super.onInterceptTouchEvent(ev);
//    }

    /**
     * 是否正在显示空View
     *
     * @return
     */
    public boolean isEmpty() {
        return isEmpty;
    }

    public boolean isError() {
        return isError;
    }

    public View getEmptyView() {
        return mEmptyView.getEmptyView();
    }

    public ImageView getEmptyIcon() {
        return mEmptyView.getEmptyIcon();
    }

    public TextView getEmptyText() {
        return mEmptyView.getEmptyText();
    }

    public TextView getEmptyBtn() {
        return mEmptyView.getEmptyBtn();
    }

    private boolean inRangeOfView(View view, MotionEvent ev) {
        if (!isEmpty()) {
            return false;
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if (ev.getRawX() < x
                || ev.getRawX() > (x + view.getWidth())
                || ev.getRawY() < y
                || ev.getRawY() > (y + view.getHeight())) {
            return false;
        }
        return true;
    }

    public static class OnEmptyOrErrorListener {
        public void onEmptyViewShow() {
        }

        public void onEmptyViewHide() {
        }

        public void onErrorViewShow() {
        }

        public void onErrorViewHide() {
        }
    }
}
