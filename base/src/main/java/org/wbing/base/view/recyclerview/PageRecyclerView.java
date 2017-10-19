package org.wbing.base.view.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.wbing.base.utils.LogUtils;

/**
 * Created by WBing on 17/10/19.
 */

public class PageRecyclerView extends RecyclerView implements ILoadMore {
    /**
     * 是否下拉自动加载更多
     */
    private boolean autoLoadMore = true;
    /**
     * 是否允许显示底部view
     */
    private boolean enableLoadMore = true;
    /**
     * 是否还有更多
     */
    private boolean hasMore;
    /**
     * 是否正在加载中
     */
    private boolean isLoading;
    LoadMoreUIHandler uiHandler;
    OnLoadMoreListener onLoadMoreListener;


    public PageRecyclerView(Context context) {
        this(context, null);
    }

    public PageRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLoadFooter();
    }

    private void initLoadFooter() {

        LogUtils.e("qwer");
        setLoadMoreHandler(generateDefaultLoadingFooter());

        addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void reachBottom() {
                onReachBottom();
            }
        });
    }


    protected LoadMoreUIHandler generateDefaultLoadingFooter() {
        return new SimpleLoadingFooter(getContext());
    }

    private void onReachBottom() {
        if (mFootAdapter == null
                || mFootAdapter.getInnerAdapter() == null
                || mFootAdapter.getInnerAdapter().getItemCount() == 0) {
            isLoading = true;
        } else {
            isLoading = false;
        }

        if (autoLoadMore) {
            tryToPerformLoadMore();
        } else {
            if (hasMore && uiHandler != null) {
                uiHandler.onWaitToLoadMore(this);
            }
        }
    }

    private void tryToPerformLoadMore() {
        if (isLoading) {
            return;
        }

        // no more content and also not load for first page
        if (!hasMore) {
            return;
        }

        isLoading = true;

        if (uiHandler != null) {
            uiHandler.onLoading(this);
        }
        if (null != onLoadMoreListener) {
            onLoadMoreListener.onLoadNMore(this);
        }
    }


    @Override
    public void setAutoLoadMore(boolean autoLoadMore) {
        this.autoLoadMore = autoLoadMore;
    }

    @Override
    public void setLoadMoreHandler(LoadMoreUIHandler view) {
        this.uiHandler = view;
    }

    @Override
    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.onLoadMoreListener = listener;
    }


    @Override
    public void loadMoreFinish(boolean hasMore, boolean isDisplay) {
        this.hasMore = hasMore;
        this.isLoading = false;
        if (uiHandler != null)
            uiHandler.onLoadFinish(this, hasMore, isDisplay);
    }

    @Override
    public void loadMoreError(int errorCode, String errorMessage) {
        this.isLoading = false;
        if (uiHandler != null)
            uiHandler.onLoadError(this, errorCode, errorMessage);
    }

    @Override
    public void setEnableLoadMore(boolean enableLoadMore) {
        if (mFootAdapter != null) {
            mFootAdapter.setEnableFooter(enableLoadMore);
        }
        this.enableLoadMore = enableLoadMore;
        if (!enableLoadMore) {
            LogUtils.e("qwer");
            setLoadMoreHandler(null);
        }
    }

    @Override
    public void showLoadMoreView(boolean show) {
        if (uiHandler != null)
            uiHandler.getLoadView().setVisibility(show ? VISIBLE : INVISIBLE);
    }

    RecyclerViewAdapterWithLoadingFooter mFootAdapter;

    @Override
    public void setAdapter(Adapter adapter) {

        LogUtils.e(String.valueOf(uiHandler==null));
        mFootAdapter = new RecyclerViewAdapterWithLoadingFooter(adapter) {
            @Override
            protected View generateLoadingFooter() {
                return uiHandler.getLoadView();
            }
        };
        mFootAdapter.setEnableFooter(enableLoadMore);
        super.setAdapter(mFootAdapter);
    }


    public static abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

        /**
         * 当前RecyclerView类型
         */
        protected EndlessRecyclerOnScrollListener.LayoutManagerType layoutManagerType;

        /**
         * 最后一个的位置
         */
        private int[] lastPositions;

        /**
         * 最后一个可见的item的位置
         */
        private int lastVisibleItemPosition;

        /**
         * 当前滑动的状态
         */
        private int currentScrollState = 0;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

            if (layoutManagerType == null) {
                if (layoutManager instanceof LinearLayoutManager) {
                    layoutManagerType = EndlessRecyclerOnScrollListener.LayoutManagerType.LinearLayout;
                } else if (layoutManager instanceof GridLayoutManager) {
                    layoutManagerType = EndlessRecyclerOnScrollListener.LayoutManagerType.GridLayout;
                } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                    layoutManagerType = EndlessRecyclerOnScrollListener.LayoutManagerType.StaggeredGridLayout;
                } else {
                    throw new RuntimeException(
                            "Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
                }
            }

            switch (layoutManagerType) {
                case LinearLayout:
                    lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                    break;
                case GridLayout:
                    lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                    break;
                case StaggeredGridLayout:
                    StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                    if (lastPositions == null) {
                        lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                    }
                    staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                    lastVisibleItemPosition = findMax(lastPositions);
                    break;
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            currentScrollState = newState;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            if ((visibleItemCount > 0 && currentScrollState == RecyclerView.SCROLL_STATE_IDLE && (lastVisibleItemPosition) >= totalItemCount - 1)) {
                reachBottom();
            }

            //listview图片滑动锁
//            final Picasso picasso = Picasso.with(recyclerView.getContext());
//            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                picasso.resumeTag(recyclerView.getContext());
//            } else {
//                picasso.pauseTag(recyclerView.getContext());
//            }
        }

        /**
         * 取数组中最大值
         *
         * @param lastPositions
         * @return
         */
        private int findMax(int[] lastPositions) {
            int max = lastPositions[0];
            for (int value : lastPositions) {
                if (value > max) {
                    max = value;
                }
            }

            return max;
        }

        public enum LayoutManagerType {
            LinearLayout,
            StaggeredGridLayout,
            GridLayout
        }

        public abstract void reachBottom();
    }

}
