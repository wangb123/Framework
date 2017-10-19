package org.wbing.base.view.refreshlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Scroller;

import org.wbing.base.view.refreshlayout.headers.PtrClassicDefaultHeader;
import org.wbing.base.view.refreshlayout.utils.RefreshLayoutIndicator;
import org.wbing.base.view.refreshlayout.utils.RefreshLayoutLog;
import org.wbing.base.view.refreshlayout.utils.RefreshLayoutUIHandlerHook;


/**
 * 基础下拉刷新组件,支持各种View和ViewGroup的下拉刷新
 * <p>
 * +--------------------+
 * ＊|  +--------------+  |
 * ＊|  | Header View  |  |
 * ＊|  +--------------+  |
 * ＊|  +--------------+  |  <----- RefreshLayout
 * ＊|  |              |  |
 * ＊|  |              |  |
 * ＊|  |              |  |
 * ＊|  | Content View |  |
 * ＊|  |              |  |
 * ＊|  |              |  |
 * ＊|  |              |  |
 * ＊|  |              |  |
 * ＊|  |              |  |
 * ＊|  +--------------+  |
 * ＊+--------------------+
 *
 * @author suitian on 16/9/11.
 * @see RefreshLayoutIndicator
 * @see RefreshLayoutUIHandlerHook
 */
public abstract class RefreshLayout extends ViewGroup {

    // Debug开关
    public static boolean DEBUG = false;
    private static final String LOG_TAG = "PullRefreshLayout";
    // 刷新的头部高度
    public static final int HEAD_HEIGHT = 190;

    // 下拉刷新控件所处状态
    private byte mStatus;
    // status enum
    // 初始化状态
    public final static byte STATUS_INIT = 1;
    // 准备刷新状态
    public final static byte STATUS_PREPARE = 2;
    // 加载刷新状态
    public final static byte STATUS_LOADING = 3;
    // 刷新完成状态
    public final static byte STATUS_COMPLETE = 4;

    // 刷新控件content部分
    protected View mRefreshView;
    // 刷新控件头部部分
    protected View mRefreshHeaderView;

    // 刷新控件的指示器,可以理解为刷新位置等实时数据的容器
    private RefreshLayoutIndicator mRefreshLayoutIndicator;
    // 弹性滑动的Scroller
    private ScrollChecker mScrollChecker;
    // 下拉刷新控件的一个钩子方法，帮助开发者在加载完成以后插入做一些额外的业务
    private RefreshLayoutUIHandlerHook mRefreshCompleteHook;

    // 是否允许下拉刷新
    private boolean mLoadingEnable = true;
    // 加载刷新的最短时间
    private int mLoadingMinTime = 500;
    // 弹性滑动恢复到加载状态位置的时间
    private int mDurationToCloseHeader = 700;
    // 弹性滑动恢复到不显示头部的时间
    private int mDurationToClose = 200;
    //自动下拉刷新情况缩短时间，防止用户在未到达刷新状态时中断刷新，导致不进行网络请求
    private int mAutoRefreshDuration = 1;
    private int mPagingTouchSlop;
    private float mTouchSlop;

    // 头部View的高度
    private int mHeaderViewHeight = 0;
    private long mLoadingStartTime = 0;
    // 是否处于自动刷新状态
    private boolean isAutoRefresh;
    // 是否支持横滑
    private boolean mPreventForHorizontal = false;
    private MotionEvent mLastMoveEvent;
    private boolean mHasSendCancelEvent = false;

    // 下拉刷新的业务接口
    protected OnRefreshListener mRefreshListener;
    protected Runnable mPerformRefreshCompleteDelay = new Runnable() {
        @Override
        public void run() {
            performRefreshComplete();
        }
    };


    public final boolean isAutoRefresh() {
        return isAutoRefresh;
    }

    public interface OnRefreshListener {

        /**
         * on pull down status
         *
         * @param y
         */
        void onPullDown(float y);

        /**
         * on refreshing status
         */
        void onRefresh();

        /**
         * on refresh over on normal status
         * <p/>
         * i suggest in this callback refresh the view data;
         */
        void onRefreshOver(Object obj);
    }

    /**
     * 设置下拉刷新控件的业务逻辑接口
     *
     * @param listener
     */
    public final void setOnRefreshListener(OnRefreshListener listener) {
        mRefreshListener = listener;
    }

    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRefreshLayoutIndicator = new RefreshLayoutIndicator();
        mScrollChecker = new ScrollChecker();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mRefreshHeaderView = (View) createHeaderView();
        addView(mRefreshHeaderView);
        mRefreshView = createRefreshView();
        addView(mRefreshView);
        mStatus = STATUS_INIT;
        mRefreshView.setFadingEdgeLength(0);
        mRefreshView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mPagingTouchSlop = (int) (mTouchSlop * 0.2f);
    }

    /**
     * 获取下拉刷新的头部View，必须实现ILoadingLayout接口
     *
     * @return
     */
    protected ILoadingLayout createHeaderView() {
        return new PtrClassicDefaultHeader(getContext());
    }

    /**
     * 获取下拉刷新控件的content View
     *
     * @return
     */
    protected abstract View createRefreshView();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mRefreshHeaderView != null) {
            measureChildWithMargins(mRefreshHeaderView, widthMeasureSpec, 0, MeasureSpec.makeMeasureSpec(HEAD_HEIGHT, MeasureSpec.EXACTLY), 0);
            MarginLayoutParams lp = (MarginLayoutParams) mRefreshHeaderView.getLayoutParams();
            mHeaderViewHeight = mRefreshHeaderView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            mRefreshLayoutIndicator.setHeaderHeight(mHeaderViewHeight);
        }

        if (mRefreshView != null) {
            measureContentView(mRefreshView, widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void measureContentView(View child,
                                    int parentWidthMeasureSpec,
                                    int parentHeightMeasureSpec) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin, lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                getPaddingTop() + getPaddingBottom() + lp.topMargin, lp.height);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int offset = mRefreshLayoutIndicator.getCurrentPosY();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        if (mRefreshHeaderView != null) {
            MarginLayoutParams lp = (MarginLayoutParams) mRefreshHeaderView.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            // enhance readability(header is layout above screen when first init)
            final int top = -(mHeaderViewHeight - paddingTop - lp.topMargin - offset);
            final int right = left + mRefreshHeaderView.getMeasuredWidth();
            final int bottom = top + mRefreshHeaderView.getMeasuredHeight();
            mRefreshHeaderView.layout(left, top, right, bottom);
        }
        if (mRefreshView != null) {
            MarginLayoutParams lp = (MarginLayoutParams) mRefreshView.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = paddingTop + lp.topMargin + offset;
            final int right = left + mRefreshView.getMeasuredWidth();
            final int bottom = top + mRefreshView.getMeasuredHeight();
            mRefreshView.layout(left, top, right, bottom);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        if (!isEnabled() || !mLoadingEnable || mRefreshView == null || mRefreshHeaderView == null) {
            return super.dispatchTouchEvent(e);
        }
        int action = e.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mRefreshLayoutIndicator.onRelease();
                if (mRefreshLayoutIndicator.hasLeftStartPosition()) {
                    if (DEBUG) {
                        RefreshLayoutLog.d(LOG_TAG, "call onRelease when user release");
                    }
                    onRelease(false);
                    if (mRefreshLayoutIndicator.hasMovedAfterPressedDown()) {
                        sendCancelEvent();
                        return true;
                    }
                    return super.dispatchTouchEvent(e);
                } else {
                    return super.dispatchTouchEvent(e);
                }

            case MotionEvent.ACTION_DOWN:
                mHasSendCancelEvent = false;
                mRefreshLayoutIndicator.onPressDown(e.getX(), e.getY());

                mScrollChecker.abortIfWorking();

                mPreventForHorizontal = false;
                // The cancel event will be sent once the position is moved.
                // So let the event pass to children.
                // fix #93, #102
                super.dispatchTouchEvent(e);
                return true;

            case MotionEvent.ACTION_MOVE:
                mLastMoveEvent = e;
                mRefreshLayoutIndicator.onMove(e.getX(), e.getY());
                float offsetX = mRefreshLayoutIndicator.getOffsetX();
                float offsetY = mRefreshLayoutIndicator.getOffsetY();

                if (!mPreventForHorizontal && (Math.abs(offsetX) > mPagingTouchSlop && Math.abs(offsetX) > Math.abs(offsetY))) {
                    if (mRefreshLayoutIndicator.isInStartPosition()) {
                        mPreventForHorizontal = true;
                    }
                }
                if (mPreventForHorizontal) {
                    return super.dispatchTouchEvent(e);
                }

                boolean moveDown = offsetY > 0;
                boolean moveUp = !moveDown;
                boolean canMoveUp = mRefreshLayoutIndicator.hasLeftStartPosition();

                if (DEBUG) {
                    boolean canMoveDown = childIsOnTop();
                    RefreshLayoutLog.v(LOG_TAG, "ACTION_MOVE: offsetY:%s, currentPos: %s, moveUp: %s, canMoveUp: %s, moveDown: %s: canMoveDown: %s", offsetY, mRefreshLayoutIndicator.getCurrentPosY(), moveUp, canMoveUp, moveDown, canMoveDown);
                }

                // disable move when header not reach top
                if (moveDown && !childIsOnTop()) {
                    return super.dispatchTouchEvent(e);
                }

                if ((moveUp && canMoveUp) || moveDown) {
                    movePos(offsetY);
                    return true;
                }
        }
        return super.dispatchTouchEvent(e);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mScrollChecker != null) {
            mScrollChecker.destroy();
        }

        if (mPerformRefreshCompleteDelay != null) {
            removeCallbacks(mPerformRefreshCompleteDelay);
            mPerformRefreshCompleteDelay.run();
        }
    }

    /**
     * 自动刷新
     */
    public final void setToRefreshing() {
        post(new Runnable() {
            @Override
            public void run() {
                setToRefreshingRunnable();
            }
        });
    }

    private void setToRefreshingRunnable() {
        if (mStatus != STATUS_INIT) {
            return;
        }

        isAutoRefresh = true;

        mStatus = STATUS_PREPARE;
        if (mRefreshHeaderView != null && mRefreshHeaderView instanceof ILoadingLayout) {
            ((ILoadingLayout) mRefreshHeaderView).onUIRefreshPrepare(this);
            if (DEBUG) {
                RefreshLayoutLog.i(LOG_TAG, "PtrUIHandler: onUIRefreshPrepare, isAutoRefresh %s", isAutoRefresh);
            }
        }
        mScrollChecker.tryToScrollTo(mRefreshLayoutIndicator.getOffsetToRefresh(), mAutoRefreshDuration);
    }

    /**
     * 刷新完成后务必调用这个方法
     *
     * @param obj
     */
    public final void refreshOver(final Object obj) {
        if (DEBUG) {
            RefreshLayoutLog.i(LOG_TAG, "refreshComplete");
        }

        if (mRefreshCompleteHook != null) {
            mRefreshCompleteHook.reset();
        }

        long delay = mLoadingMinTime - (System.currentTimeMillis() - mLoadingStartTime);
        if (delay <= 0) {
            if (DEBUG) {
                RefreshLayoutLog.d(LOG_TAG, "performRefreshComplete at once");
            }
            performRefreshComplete();
        } else {
            postDelayed(mPerformRefreshCompleteDelay, delay);
            if (DEBUG) {
                RefreshLayoutLog.d(LOG_TAG, "performRefreshComplete after delay: %s", delay);
            }
        }

        if (mRefreshListener != null) {
            mRefreshListener.onRefreshOver(obj);
        }
    }

    /**
     * 设置是否允许下拉刷新
     *
     * @param isAbled
     */
    public void setLoadingHeaderEnable(boolean isAbled) {
        mLoadingEnable = isAbled;
    }

    /**
     * 设置刷新完成之前的钩子方法,一般在自定义头部view时才可能被调用
     *
     * @param hook
     */
    public final void setRefreshCompleteHook(RefreshLayoutUIHandlerHook hook) {
        mRefreshCompleteHook = hook;
        mRefreshCompleteHook.setResumeAction(new Runnable() {
            @Override
            public void run() {
                if (DEBUG) {
                    RefreshLayoutLog.d(LOG_TAG, "mRefreshCompleteHook resume.");
                }
                notifyUIRefreshComplete(true);
            }
        });
    }

    public final void configureLoadingHeaderView() {
        removeView(mRefreshHeaderView);
        mRefreshHeaderView = (View) createHeaderView();
        addView(mRefreshHeaderView);
    }

    public final RefreshLayoutIndicator getRefreshIndicator() {
        return mRefreshLayoutIndicator;
    }

    protected View getRefreshView() {
        return mRefreshView;
    }

    protected View getRefreshHeaderView() {
        return mRefreshHeaderView;
    }

    protected void setRefreshIndicator(RefreshLayoutIndicator refreshLayoutIndicator) {
        if (mRefreshLayoutIndicator != null && mRefreshLayoutIndicator != refreshLayoutIndicator) {
            refreshLayoutIndicator.convertFrom(mRefreshLayoutIndicator);
        }
        this.mRefreshLayoutIndicator = refreshLayoutIndicator;
    }

    protected boolean childIsOnTop() {
        boolean result = false;
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mRefreshView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mRefreshView;
                result = absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                result = mRefreshView.getScrollY() > 0;
            }
        } else {
            result = mRefreshView.canScrollVertically(-1);
        }

        return !result;
    }

    protected void onPtrScrollAbort() {
        if (mRefreshLayoutIndicator.hasLeftStartPosition() && isAutoRefresh()) {
            if (DEBUG) {
                RefreshLayoutLog.d(LOG_TAG, "call onRelease after scroll abort");
            }
            onRelease(true);
        }
    }

    protected void onPtrScrollFinish() {
        if (mRefreshLayoutIndicator.hasLeftStartPosition() && isAutoRefresh()) {
            if (DEBUG) {
                RefreshLayoutLog.d(LOG_TAG, "call onRelease after scroll finish");
            }
            onRelease(true);
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends MarginLayoutParams {
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    private void onRelease(boolean stayForLoading) {

        tryToPerformRefresh();

        if (mStatus == STATUS_LOADING) {
            // keep header for fresh
            if (mRefreshLayoutIndicator.isOverOffsetToKeepHeaderWhileLoading() && !stayForLoading) {
                mScrollChecker.tryToScrollTo(mRefreshLayoutIndicator.getOffsetToKeepHeaderWhileLoading(), mDurationToClose);
            }
        } else {
            if (mStatus == STATUS_COMPLETE) {
                notifyUIRefreshComplete(false);
            } else {
                tryScrollBackToTopAbortRefresh();
            }
        }
    }

    /**
     * Do real refresh work. If there is a hook, execute the hook first.
     *
     * @param ignoreHook
     */
    private void notifyUIRefreshComplete(boolean ignoreHook) {
        /**
         * After hook operation is done, {@link #notifyUIRefreshComplete} will be call in resume action to ignore hook.
         */
        if (mRefreshLayoutIndicator.hasLeftStartPosition() && !ignoreHook && mRefreshCompleteHook != null) {
            if (DEBUG) {
                RefreshLayoutLog.d(LOG_TAG, "notifyUIRefreshComplete mRefreshCompleteHook run.");
            }

            mRefreshCompleteHook.takeOver();
            return;
        }
        if (mRefreshHeaderView != null && mRefreshHeaderView instanceof ILoadingLayout) {
            if (DEBUG) {
                RefreshLayoutLog.i(LOG_TAG, "PtrUIHandler: onUIRefreshComplete");
            }
            ((ILoadingLayout) mRefreshHeaderView).onUIRefreshComplete(this);
        }
        mRefreshLayoutIndicator.onUIRefreshComplete();
        tryScrollBackToTopAfterComplete();
        tryToNotifyReset();
    }

    /**
     * just make easier to understand
     */
    private void tryScrollBackToTopAfterComplete() {
        tryScrollBackToTop();
    }

    /**
     * just make easier to understand
     */
    private void tryScrollBackToTopAbortRefresh() {
        tryScrollBackToTop();
    }

    /**
     * Scroll back to to if is not under touch
     */
    private void tryScrollBackToTop() {
        if (!mRefreshLayoutIndicator.isUnderTouch()) {
            mScrollChecker.tryToScrollTo(mRefreshLayoutIndicator.POS_START, mDurationToCloseHeader);
        }
    }

    /**
     * If at the top and not in loading, reset
     */
    private boolean tryToNotifyReset() {
        if ((mStatus == STATUS_COMPLETE || mStatus == STATUS_PREPARE) && mRefreshLayoutIndicator.isInStartPosition()) {
            if (mRefreshHeaderView != null && mRefreshHeaderView instanceof ILoadingLayout) {
                ((ILoadingLayout) mRefreshHeaderView).onUIReset(this);
                if (DEBUG) {
                    RefreshLayoutLog.i(LOG_TAG, "PtrUIHandler: onUIReset");
                }
            }
            mStatus = STATUS_INIT;
            isAutoRefresh = false;
            return true;
        }
        return false;
    }

    private boolean tryToPerformRefresh() {
        if (mStatus != STATUS_PREPARE) {
            return false;
        }

        //
        if (mRefreshLayoutIndicator.isOverOffsetToRefresh()) {
            mStatus = STATUS_LOADING;
            performRefresh();
        }
        return false;
    }

    private void performRefresh() {
        mLoadingStartTime = System.currentTimeMillis();
        if (mRefreshHeaderView != null && mRefreshHeaderView instanceof ILoadingLayout) {
            ((ILoadingLayout) mRefreshHeaderView).onUIRefreshBegin(this);
        }

        if (DEBUG) {
            RefreshLayoutLog.i(LOG_TAG, "PtrUIHandler: onUIRefreshBegin");
        }
        if (mRefreshListener != null) {
            mRefreshListener.onRefresh();
        }
    }

    /**
     * if deltaY > 0, move the content down
     *
     * @param deltaY
     */
    private void movePos(float deltaY) {
        // has reached the top
        if ((deltaY < 0 && mRefreshLayoutIndicator.isInStartPosition())) {
            if (DEBUG) {
                RefreshLayoutLog.e(LOG_TAG, String.format("has reached the top"));
            }
            return;
        }

        int to = mRefreshLayoutIndicator.getCurrentPosY() + (int) deltaY;

        // over top
        if (mRefreshLayoutIndicator.willOverTop(to)) {
            if (DEBUG) {
                RefreshLayoutLog.e(LOG_TAG, String.format("over top"));
            }
            to = RefreshLayoutIndicator.POS_START;
        }

        mRefreshLayoutIndicator.setCurrentPos(to);
        int change = to - mRefreshLayoutIndicator.getLastPosY();
        updatePos(change);
    }

    private void updatePos(int change) {
        if (change == 0) {
            return;
        }

        boolean isUnderTouch = mRefreshLayoutIndicator.isUnderTouch();

        // once moved, cancel event will be sent to child
        if (isUnderTouch && !mHasSendCancelEvent && mRefreshLayoutIndicator.hasMovedAfterPressedDown()) {
            mHasSendCancelEvent = true;
            sendCancelEvent();
        }

        // leave initiated position or just refresh complete
        if ((mRefreshLayoutIndicator.hasJustLeftStartPosition() && mStatus == STATUS_INIT) ||
                (mRefreshLayoutIndicator.goDownCrossFinishPosition() && mStatus == STATUS_COMPLETE)) {

            mStatus = STATUS_PREPARE;
            if (mRefreshHeaderView != null && mRefreshHeaderView instanceof ILoadingLayout) {
                ((ILoadingLayout) mRefreshHeaderView).onUIRefreshPrepare(this);
            }
        }

        // back to initiated position
        if (mRefreshLayoutIndicator.hasJustBackToStartPosition()) {
            tryToNotifyReset();

            // recover event to children
            if (isUnderTouch) {
                sendDownEvent();
            }
        }

        // Pull to Refresh
        if (mStatus == STATUS_PREPARE) {
            // reach fresh height while moving from top to bottom
            if (isUnderTouch && !isAutoRefresh()
                    && mRefreshLayoutIndicator.crossRefreshLineFromTopToBottom()) {
                notifyUIRefreshPrepared();
            }
            // reach header height while auto refresh
            if (isAutoRefresh() && mRefreshLayoutIndicator.hasJustReachedHeaderHeightFromTopToBottom()) {
                tryToPerformRefresh();
            }
        }

        if (DEBUG) {
            RefreshLayoutLog.v(LOG_TAG, "updatePos: change: %s, current: %s last: %s, top: %s, headerHeight: %s",
                    change, mRefreshLayoutIndicator.getCurrentPosY(), mRefreshLayoutIndicator.getLastPosY(), mRefreshView.getTop(), mHeaderViewHeight);
        }

        mRefreshHeaderView.offsetTopAndBottom(change);
        mRefreshView.offsetTopAndBottom(change);
        invalidate();

        if (mRefreshHeaderView != null && mRefreshHeaderView instanceof ILoadingLayout) {
            ((ILoadingLayout) mRefreshHeaderView).onUIPositionChange(this, isUnderTouch, mStatus, mRefreshLayoutIndicator);
        }

        if (mRefreshListener != null) {
            mRefreshListener.onPullDown(mRefreshLayoutIndicator.getPtLastMoveY());
        }
    }

    private void notifyUIRefreshPrepared() {
        if (mRefreshHeaderView != null && mRefreshHeaderView instanceof ILoadingLayout) {
            ((ILoadingLayout) mRefreshHeaderView).onUIRefreshPrepared(this);
        }
    }

    private void sendCancelEvent() {
        if (DEBUG) {
            RefreshLayoutLog.d(LOG_TAG, "send cancel event");
        }
        // The ScrollChecker will update position and lead to send cancel event when mLastMoveEvent is null.
        // fix #104, #80, #92
        if (mLastMoveEvent == null) {
            return;
        }
        MotionEvent last = mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime() + ViewConfiguration.getLongPressTimeout(), MotionEvent.ACTION_CANCEL, last.getX(), last.getY(), last.getMetaState());
        super.dispatchTouchEvent(e);
    }

    private void sendDownEvent() {
        if (DEBUG) {
            RefreshLayoutLog.d(LOG_TAG, "send down event");
        }
        final MotionEvent last = mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime(), MotionEvent.ACTION_DOWN, last.getX(), last.getY(), last.getMetaState());
        super.dispatchTouchEvent(e);
    }

    /**
     * Do refresh complete work when time elapsed is greater than {@link #mLoadingMinTime}
     */
    private void performRefreshComplete() {
        mStatus = STATUS_COMPLETE;

        // if is auto refresh do nothing, wait scroller stop
        if (mScrollChecker.mIsRunning && isAutoRefresh()) {
            // do nothing
            if (DEBUG) {
                RefreshLayoutLog.d(LOG_TAG, "performRefreshComplete do nothing, scrolling: %s, auto refresh: %s",
                        mScrollChecker.mIsRunning, isAutoRefresh);
            }
            return;
        }

        notifyUIRefreshComplete(false);
    }

    class ScrollChecker implements Runnable {

        private int mLastFlingY;
        private Scroller mScroller;
        private boolean mIsRunning = false;
        private int mStart;
        private int mTo;

        public ScrollChecker() {
            mScroller = new Scroller(getContext());
        }

        public void run() {
            boolean finish = !mScroller.computeScrollOffset() || mScroller.isFinished();
            int curY = mScroller.getCurrY();
            int deltaY = curY - mLastFlingY;
            if (DEBUG) {
                if (deltaY != 0) {
                    RefreshLayoutLog.v(LOG_TAG,
                            "scroll: %s, start: %s, to: %s, currentPos: %s, current :%s, last: %s, delta: %s",
                            finish, mStart, mTo, mRefreshLayoutIndicator.getCurrentPosY(), curY, mLastFlingY, deltaY);
                }
            }
            mLastFlingY = curY;
            movePos(deltaY);
            if (finish) {
                finish();
            } else {
                post(this);
            }
        }

        private void finish() {
            if (DEBUG) {
                RefreshLayoutLog.v(LOG_TAG, "finish, currentPos:%s", mRefreshLayoutIndicator.getCurrentPosY());
            }
            reset();
            onPtrScrollFinish();
        }

        private void reset() {
            mIsRunning = false;
            mLastFlingY = 0;
            removeCallbacks(this);
        }

        private void destroy() {
            reset();
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
            //防止切换Fragment的时候,没有收回;
            movePos(mTo - mRefreshLayoutIndicator.getCurrentPosY());
        }

        public void abortIfWorking() {
            if (mIsRunning) {
                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
                }
                onPtrScrollAbort();
                reset();
            }
        }

        public void tryToScrollTo(int to, int duration) {
            if (mRefreshLayoutIndicator.isAlreadyHere(to)) {
                return;
            }
            mStart = mRefreshLayoutIndicator.getCurrentPosY();
            mTo = to;
            int distance = to - mStart;
            if (DEBUG) {
                RefreshLayoutLog.d(LOG_TAG, "tryToScrollTo: start: %s, distance:%s, to:%s", mStart, distance, to);
            }
            removeCallbacks(this);

            mLastFlingY = 0;

            // fix #47: Scroller should be reused, https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh/issues/47
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
            mScroller.startScroll(0, 0, 0, distance, duration);
            post(this);
            mIsRunning = true;
        }
    }
}
