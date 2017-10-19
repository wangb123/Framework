package org.wbing.base.view.recyclerview;

import android.view.View;

/**
 * 上拉加载业务逻辑回调的通用接口
 *
 * @author suitian on 16/9/12.
 */
public interface OnLoadMoreListener {

    /**
     * 开始加载下一页
     *
     * @param view
     */
    void onLoadNMore(View view);
}
