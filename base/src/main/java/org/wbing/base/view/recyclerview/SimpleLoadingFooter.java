package org.wbing.base.view.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.wbing.base.R;

/**
 * Created by WBing on 17/10/19.
 */

public class SimpleLoadingFooter extends RelativeLayout implements LoadMoreUIHandler{
    /**
     * 加载中的view
     */
    private View mLoadingView;
    /**
     * 结束时的view
     */
    private View mTheEndView;
    private ProgressBar mLoadingProgress;
    private TextView mLoadingText;
    protected TextView mEndText;


    public SimpleLoadingFooter(Context context) {
        this(context, null);
    }

    public SimpleLoadingFooter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleLoadingFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(getContext(), R.layout.common_list_footer, this);
    }

    @Override
    public View getLoadView() {
        return this;
    }

    @Override
    public void onLoading(ILoadMore container) {
        if (mTheEndView != null) {
            mTheEndView.setVisibility(GONE);
        }

        if (mLoadingView == null) {
            ViewStub viewStub = (ViewStub) findViewById(R.id.loading_viewstub);
            mLoadingView = viewStub.inflate();

            mLoadingProgress = (ProgressBar) mLoadingView.findViewById(R.id.loading_progress);
            mLoadingText = (TextView) mLoadingView.findViewById(R.id.loading_text);
        } else {
            mLoadingView.setVisibility(VISIBLE);
        }

        mLoadingView.setVisibility(VISIBLE);

        mLoadingProgress.setVisibility(View.VISIBLE);
        mLoadingText.setText(R.string.list_footer_loading);
    }

    @Override
    public void onLoadFinish(ILoadMore container, boolean hasMore, boolean isDisplay) {
        setOnClickListener(null);
        if (hasMore) {
            if (mLoadingView != null) {
                mLoadingView.setVisibility(GONE);
            }

            if (mTheEndView != null) {
                mTheEndView.setVisibility(GONE);
            }
        } else if (isDisplay) {
            if (mLoadingView != null) {
                mLoadingView.setVisibility(GONE);
            }

            if (mTheEndView == null) {
                ViewStub viewStub = (ViewStub) findViewById(R.id.end_viewstub);
                mTheEndView = viewStub.inflate();
                mEndText = (TextView) mTheEndView.findViewById(R.id.loading_end_text);
            }

            mTheEndView.setVisibility(VISIBLE);
            setEndText();
        } else {
            if (mLoadingView != null) {
                mLoadingView.setVisibility(GONE);
            }

            if (mTheEndView != null) {
                mTheEndView.setVisibility(GONE);
            }

            container.setEnableLoadMore(false);
        }
    }

    @Override
    public void onWaitToLoadMore(ILoadMore container) {

    }

    @Override
    public void onLoadError(ILoadMore container, int errorCode, String errorMessage) {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(GONE);
        }
        if (mTheEndView == null) {
            ViewStub viewStub = (ViewStub) findViewById(R.id.end_viewstub);
            mTheEndView = viewStub.inflate();
            mEndText = (TextView) mTheEndView.findViewById(R.id.loading_end_text);
        }
        mTheEndView.setVisibility(VISIBLE);
        mEndText.setText(R.string.list_footer_error);
    }

    /**
     * 设置底部加载无更多内容文案
     */
    protected void setEndText() {
        mEndText.setText(R.string.list_footer_end);
    }

}
