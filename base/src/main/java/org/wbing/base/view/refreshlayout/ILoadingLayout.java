package org.wbing.base.view.refreshlayout;


import org.wbing.base.view.refreshlayout.utils.RefreshLayoutIndicator;

/**
 * Created by 6a209 on 14/10/19.
 */
public interface ILoadingLayout {

    /**
     * When the content view has reached top and refresh has been completed, view will be reset.
     *
     * @param frame
     */
    void onUIReset(RefreshLayout frame);

    /**
     * prepare for loading
     *
     * @param frame
     */
    void onUIRefreshPrepare(RefreshLayout frame);

    /**
     * prepare for loading is done
     *
     * @param frame
     */
    void onUIRefreshPrepared(RefreshLayout frame);

    /**
     * perform refreshing UI
     */
    void onUIRefreshBegin(RefreshLayout frame);

    /**
     * perform UI after refresh
     */
    void onUIRefreshComplete(RefreshLayout frame);

    void onUIPositionChange(RefreshLayout frame, boolean isUnderTouch, byte status, RefreshLayoutIndicator ptrIndicator);
}
