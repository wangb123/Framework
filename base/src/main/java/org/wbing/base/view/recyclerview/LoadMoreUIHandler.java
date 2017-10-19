package org.wbing.base.view.recyclerview;

import android.view.View;

/**
 * 上拉加载ui回调的通用接口
 *
 * @author suitian on 16/9/12.
 */
public interface LoadMoreUIHandler {

    View getLoadView();

    /**
     * 正在加载中的样式
     * @param container
     */
    void onLoading(ILoadMore container);

    /**
     * 加载结束的状态
     * @param container
     * @param hasMore 是否还有更多
     * @param isDisplay 没有更多的情况下是否显示“已经到底啦”, 只有在hasMore为false的情况下,这个参数才有意义
     */
    void onLoadFinish(ILoadMore container, boolean hasMore, boolean isDisplay);

    /**
     * 不自动加载更多的情况下,需要点击加载更多。等待用户点击时的样式
     * @param container
     */
    void onWaitToLoadMore(ILoadMore container);

    /**
     * 加载发生错误时的样式
     * @param container
     * @param errorCode
     * @param errorMessage
     */
    void onLoadError(ILoadMore container, int errorCode, String errorMessage);
}