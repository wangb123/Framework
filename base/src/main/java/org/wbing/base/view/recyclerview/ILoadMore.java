package org.wbing.base.view.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 包含有上拉加载功能RecyclerView的通用接口
 *
 * @author suitian on 16/9/12.
 */
public interface ILoadMore {

    /**
     * 设置上拉是否自动加载更多,默认为true
     *
     * @param autoLoadMore
     */
    void setAutoLoadMore(boolean autoLoadMore);

    /**
     * 设置上拉加载的底部view,这个view必须实现LoadMoreUIHandler接口
     *
     * @param view
     */
    void setLoadMoreHandler(LoadMoreUIHandler view);

    /**
     * 上拉加载更多的业务接口设置
     *
     * @param listener
     */
    void setOnLoadMoreListener(OnLoadMoreListener listener);

    /**
     * 上拉加载完成时必须调用此方法
     *
     * @param hasMore
     * @param isDisplay 是否展示"已经到底了", 仅当hasMore为false时有效
     */
    void loadMoreFinish(boolean hasMore, boolean isDisplay);

    /**
     * 上拉加载出错时调用此方法。
     * 目前暂未使用,预留方法 2016.9.12
     *
     * @param errorCode
     * @param errorMessage
     */
    void loadMoreError(int errorCode, String errorMessage);

    /**
     * 设置是否显示底部
     *
     * @param enableLoadMore
     */
    void setEnableLoadMore(boolean enableLoadMore);

    /**
     * 是否展示上拉加载View
     * @param show
     */
    void showLoadMoreView(boolean show);
}
