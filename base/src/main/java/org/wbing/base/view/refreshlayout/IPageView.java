package org.wbing.base.view.refreshlayout;


/**
 * Created by zhuangzhuang on 16/1/20.
 * Modify by mobing on 16/2/17 添加 是否空数据方法
 * Modify by ermu on 16/12/06 迁移到ids 项目
 */
public interface IPageView {
    /**
     * 控制是否还有“更多”数据显示，以及“没有更多”的情况下是否显示“已经到底啦”
     *
     * @param isEnd 是否还有更多数据，true 表示没有有更多数据,false 表示有更多数据
     */
    void setIsEnd(boolean isEnd);

    /**
     * 展示loading动画
     */
    void showLoading();

    /**
     * 隐藏loading动画
     */
    void hideLoading();

    /**
     * 加载更多时,网络出错
     */
    void loadMoreError(int errorCode, String errorMessage);

    /**
     * 隐藏的空白view
     */
    void hideEmptyView();
    /**
     * 根据不同到的listType,展示各个页面的空白view
     *
     * @param listType 空白view的类型
     * @param isEmpty  是否显示空白view true 展示空白view, false 不展示空白view
     */
    void showEmptyView(int listType, boolean isEmpty);

    /**
     * 展示错误页view
     */
    void showErrorView(int listType);


    /**
     * 设置空view中提示信息的图标
     *
     * @param iconID
     */
    EmptyRefreshLayout setEmptyIcon(int iconID);

    /**
     * 设置空view中提示信息的文案
     *
     * @param text
     */
    EmptyRefreshLayout setEmptyText(String text);

    /**
     * 设置列表监听 ,刷新,加载更多,重新加载
     * 该listener的实现类在PagePresenter中,和网络库进行交互。
     * 该listener的调用是在IPageView接口的实现类pageRecycleListView中。
     *
     * @param listener
     */
    void setPageLoadListener(PageLoadListener listener);

    /**
     * 设置空view中按钮的文案及跳转链接
     *
     * @param emptyStr       按钮文案
     * @param emptyRouterUrl 跳转url
     */
    void setEmptyBtn(String emptyStr, String emptyRouterUrl);

}
