package org.wbing.base.view.recyclerview;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by suitian on 16/9/7.
 */

/**
 * 有上拉加载功能RecyclerView的Adapter的包装类
 *
 * @author suitian on 16/9/12.
 * @see RecyclerView.Adapter
 * @see ILoadMore
 */
public abstract class RecyclerViewAdapterWithLoadingFooter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected static final int ITEM_TYPE_FOOT = Integer.MIN_VALUE;
    /**
     * 标记位，指示在layoutManager是GridLayoutManager时是否已经设置过SpanSizeLookup
     */
    private boolean mAlreadySetLookup = false;
    /**
     * 是否显示底部view
     */
    private boolean mEnableFooter = true;
    /**
     * 内部真正的adapter
     */
    private RecyclerView.Adapter mInnerAdapter;

    protected RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {

        @Override
        public void onChanged() {
            super.onChanged();
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            notifyItemRangeChanged(fromPosition, toPosition + itemCount);
        }
    };

    public RecyclerViewAdapterWithLoadingFooter(RecyclerView.Adapter adapter) {
        super();
        this.mInnerAdapter = adapter;
        mInnerAdapter.registerAdapterDataObserver(mDataObserver);
        mAlreadySetLookup = false;
    }

    /**
     * 设置底部View是否可见
     *
     * @param enableFooter
     */
    public void setEnableFooter(boolean enableFooter) {
        if (mEnableFooter != enableFooter) {
            mEnableFooter = enableFooter;
            mAlreadySetLookup = false;
            notifyDataSetChanged();
        }
    }

    /**
     * 获取实际的item的数量
     *
     * @return
     */
    public int getInnerItemCount() {
        return mInnerAdapter.getItemCount();
    }

    /**
     * 获取内部真正的adapter
     *
     * @return
     */
    public RecyclerView.Adapter getInnerAdapter() {
        return mInnerAdapter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_FOOT) {
            return onCreateViewHolderForFooter(parent);
        } else {
            return mInnerAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!(holder instanceof LoadingFooterViewHolder)) {
            mInnerAdapter.onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        if (mEnableFooter) {
            return mInnerAdapter.getItemCount() + 1;
        }
        return mInnerAdapter.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (!mEnableFooter || position < getItemCount() - 1) {
            return mInnerAdapter.getItemViewType(position);
        }

        return ITEM_TYPE_FOOT;
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        if (holder instanceof LoadingFooterViewHolder) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();

            if (lp != null
                    && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (!mAlreadySetLookup && manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            final GridLayoutManager.SpanSizeLookup lookup = gridManager.getSpanSizeLookup();
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) == ITEM_TYPE_FOOT
                            ? gridManager.getSpanCount() : (lookup != null ? lookup.getSpanSize(position) : 1);
                }
            });
            mAlreadySetLookup = true;
        }
    }

    private RecyclerView.ViewHolder onCreateViewHolderForFooter(ViewGroup parent) {
        View footer = generateLoadingFooter();
        if (footer == null || !(footer instanceof LoadMoreUIHandler)) {
            footer = new SimpleLoadingFooter(parent.getContext());
        }
        return new LoadingFooterViewHolder(footer);
    }

    protected abstract View generateLoadingFooter();

    static class LoadingFooterViewHolder extends RecyclerView.ViewHolder {

        public LoadingFooterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
