package org.wbing.base.view.refreshlayout;

/**
 * Created by zhuangzhuang on 16/1/20.
 * Modify by ermu on 16/12/06 迁移到ids 项目
 */
public interface PageLoadListener {
    /**
     * 加载更多
     */
    void loadMore();

    /**
     * 刷新
     */
    void refresh();

    /**
     * 重新加载
     */
    void reLoad();
}
