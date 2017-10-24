package org.wbing.base.view.empty;

import android.view.View;

/**
 * Created by WBing on 17/10/18.
 */

public interface IEmptyLayout {
    /**
     * 设置空view的点击事件
     */
    void setEmptyOnclickListener(View.OnClickListener l);

    /**
     * 设置空view的点击事件
     */
    void setErrorOnclickListener(View.OnClickListener l);

    /**
     * 显示错误的view
     */
    void showErrorView(int errorCode, String errorMessage);

    /**
     * 显示空view
     */
    void showEmptyView(String errorMessage);

    /**
     * 隐藏view
     */
    void hideView();

    View getView();
}
