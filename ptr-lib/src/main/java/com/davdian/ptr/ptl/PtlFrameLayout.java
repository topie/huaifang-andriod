package com.davdian.ptr.ptl;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.TextView;

import com.davdian.ptr.R;

import in.srain.cube.views.ptr.util.PtrCLog;

/**
 * This layout view for "Pull to Refresh(Ptr)" support all of the view, you can contain everything you want.
 * support: pull to refresh / release to refresh / auto refresh / keep header view while refreshing / hide header view while refreshing
 * It defines {@link PtlUIHandler}, which allows you customize the UI easily.
 */
public class PtlFrameLayout extends ViewGroup {

    private static final String LOG_TAG = "PtlFrameLayout";

    public final static byte PTL_STATUS_INIT = 1;
    public final static byte PTL_STATUS_PREPARE = 2;
    public final static byte PTL_STATUS_LOADING = 3;
    public final static byte PTL_STATUS_COMPLETE = 4;
    private byte mStatus = PTL_STATUS_INIT;
    private MotionEvent mLastMoveEvent;

    private View mFooterView;
    private View mContentView;
    private int mContentHeight;
    private int mPagingTouchSlop;

    private PtlIndicator mPtlIndicator = new PtlIndicator();
    private ScrollChecker mScrollChecker = new ScrollChecker();
    private PtlHandler mPtlHandler;
    private PtlUIHandlerHolder mPtlUIHandlerHolder = PtlUIHandlerHolder.create();
    /*config*/
    private boolean mKeepFooterWhenLoadMore = true;
    private boolean mPullToLoadMore = false;
    private boolean isPinContent = false;
    private int mDurationToClose = 500;
    private int mDurationToCloseFooter = 1000;
    private long mLoadingMinTime = 500;
    private long mLoadingMaxTime = 10000;
    private int mFooterId = 0;
    private int mContentId = 0;

    private boolean mPreventForHorizontal = false;
    private boolean mHasSendCancelEvent;
    private long mLoadingStartTime;

    private Runnable mPerformLoadMoreCompleteDelay = new Runnable() {
        @Override
        public void run() {
            performLoadMoreComplete();
        }
    };

    public PtlFrameLayout(Context context) {
        this(context, null);
    }

    public PtlFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PtlFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final ViewConfiguration conf = ViewConfiguration.get(getContext());
        mPagingTouchSlop = conf.getScaledTouchSlop() * 2;
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.PtlFrameLayout, 0, 0);
        if (arr != null) {
            mContentId = arr.getResourceId(R.styleable.PtlFrameLayout_ptl_content, mContentId);
            mFooterId = arr.getResourceId(R.styleable.PtlFrameLayout_ptl_footer, mFooterId);
            mKeepFooterWhenLoadMore = arr.getBoolean(R.styleable.PtlFrameLayout_ptl_keep_footer_when_load_more, mKeepFooterWhenLoadMore);
            mPullToLoadMore = arr.getBoolean(R.styleable.PtlFrameLayout_ptl_pull_to_load_more, mPullToLoadMore);
            mDurationToClose = arr.getInt(R.styleable.PtlFrameLayout_ptl_duration_to_close, mDurationToClose);
            mDurationToCloseFooter = arr.getInt(R.styleable.PtlFrameLayout_ptl_duration_to_close_footer, mDurationToCloseFooter);
            arr.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        final int childCount = getChildCount();
        if (childCount > 2) {
            throw new IllegalStateException("ptlFrameLayout can only contains 2 children");
        } else if (childCount == 2) {
            if (mFooterId != 0 && mFooterView == null) {
                mFooterView = findViewById(mFooterId);
            }
            if (mContentId != 0 && mContentView == null) {
                mContentView = findViewById(mContentId);
            }

            // not specify header or content
            if (mContentView == null || mFooterView == null) {

                View child1 = getChildAt(0);
                View child2 = getChildAt(1);
                if (child1 instanceof PtlUIHandler) {
                    mFooterView = child1;
                    mContentView = child2;
                } else if (child2 instanceof PtlUIHandler) {
                    mFooterView = child2;
                    mContentView = child1;
                } else {
                    // both are not specified
                    if (mContentView == null && mFooterView == null) {
                        mContentView = child1;
                        mFooterView = child2;
                    }
                    // only one is specified
                    else {
                        if (mFooterView == null) {
                            mFooterView = mContentView == child1 ? child2 : child1;
                        } else {
                            mContentView = mFooterView == child1 ? child2 : child1;
                        }
                    }
                }
            }
        } else if (childCount == 1) {
            mContentView = getChildAt(0);
        } else {
            TextView errorView = new TextView(getContext());
            errorView.setClickable(true);
            errorView.setTextColor(0xffff6600);
            errorView.setGravity(Gravity.CENTER);
            errorView.setTextSize(20);
            errorView.setText("The content view in PtlFrameLayout is empty. Do you forget to specify its id in xml layout file?");
            mContentView = errorView;
            addView(mContentView);
        }
        if (mFooterView != null) {
            mFooterView.bringToFront();
        }
        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mContentView != null) {
            measureContentView(mContentView, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) mFooterView.getLayoutParams();
            mContentHeight = mContentView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
        }
        if (mFooterView != null) {
            measureChildWithMargins(mFooterView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = (MarginLayoutParams) mFooterView.getLayoutParams();
            int footerHeight = mFooterView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            mPtlIndicator.setFooterHeight(footerHeight);
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

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int offset = mPtlIndicator.getCurrentPosY();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        if (mFooterView != null) {
            MarginLayoutParams lp = (MarginLayoutParams) mFooterView.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            // enhance readability(header is layout above screen when first init)
            final int top = mContentHeight + paddingTop + lp.topMargin + offset;
            final int right = left + mFooterView.getMeasuredWidth();
            final int bottom = top + mFooterView.getMeasuredHeight();
            mFooterView.layout(left, top, right, bottom);
            if (isDebug()) {
                PtrCLog.i(LOG_TAG, "onLayout header: %s %s %s %s", left, top, right, bottom);
            }
        }
        if (mContentView != null) {
            if (isPinContent()) {
                offset = 0;
            }
            MarginLayoutParams lp = (MarginLayoutParams) mContentView.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = paddingTop + lp.topMargin + offset;
            final int right = left + mContentView.getMeasuredWidth();
            final int bottom = top + mContentView.getMeasuredHeight();
            if (isDebug()) {
                PtrCLog.i(LOG_TAG, "onLayout content: %s %s %s %s", left, top, right, bottom);
            }
            mContentView.layout(left, top, right, bottom);
        }
    }

    public void setPtlHandler(PtlHandler pPtlHandler) {
        mPtlHandler = pPtlHandler;
    }

    public void addUiHandler(PtlUIHandler pPtlUIHandler) {
        PtlUIHandlerHolder.addHandler(mPtlUIHandlerHolder, pPtlUIHandler);
    }

    public void removeUiHandler(PtlUIHandler pPtlUIHandler) {
        PtlUIHandlerHolder.removeHandler(mPtlUIHandlerHolder, pPtlUIHandler);
    }

    private boolean isDebug() {
        return true;
    }

    private boolean isPinContent() {
        return isPinContent;
    }

    public void setPinContent(boolean pPinContent) {
        isPinContent = pPinContent;
    }

    public void setKeepFooterWhenLoadMore(boolean pKeepFooterWhenLoadMore) {
        mKeepFooterWhenLoadMore = pKeepFooterWhenLoadMore;
    }

    public void setPullToLoadMore(boolean pPullToLoadMore) {
        mPullToLoadMore = pPullToLoadMore;
    }

    public void setDurationToClose(int pDurationToClose) {
        mDurationToClose = pDurationToClose;
    }

    public void setDurationToCloseFooter(int pDurationToCloseHeader) {
        mDurationToCloseFooter = pDurationToCloseHeader;
    }

    public void setLoadingMinTime(long pLoadingMinTime) {
        mLoadingMinTime = pLoadingMinTime;
    }

    public byte getStatus() {
        return mStatus;
    }

    public void setFooterView(View pFooterView) {
        if (mFooterView != null && pFooterView != null && mFooterView != pFooterView) {
            removeView(mFooterView);
        }
        ViewGroup.LayoutParams lp = pFooterView.getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            pFooterView.setLayoutParams(lp);
        }
        mFooterView = pFooterView;
        addView(pFooterView);
    }

    public View getFooterView() {
        return mFooterView;
    }

    public View getContentView() {
        return mContentView;
    }

    public boolean dispatchTouchEventSupper(MotionEvent e) {
        Log.i(LOG_TAG, "dispatchTouchEventSupper: ");
        return super.dispatchTouchEvent(e);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        if (!isEnabled() || mContentView == null || mFooterView == null) {
            return dispatchTouchEventSupper(e);
        }
        int action = e.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mPtlIndicator.onRelease();
                if (mPtlIndicator.hasLeftStartPosition()) {
                    if (isDebug()) {
                        PtrCLog.d(LOG_TAG, "call onRelease when user release");
                    }
                    onRelease(false);
                    if (mPtlIndicator.hasMovedAfterPressedDown()) {
                        sendCancelEvent();
                        return true;
                    }
                    return dispatchTouchEventSupper(e);
                } else {
                    return dispatchTouchEventSupper(e);
                }
            case MotionEvent.ACTION_DOWN:
                mLastMoveEvent = e;
                mPreventForHorizontal = false;
                mHasSendCancelEvent = false;
                mScrollChecker.abortIfWorking();
                mPtlIndicator.onPressDown(e.getX(), e.getY());
                dispatchTouchEventSupper(e);
                return true;
            case MotionEvent.ACTION_MOVE:
                mPtlIndicator.onMove(e.getX(), e.getY());
                float offsetX = mPtlIndicator.getOffsetX();
                float offsetY = mPtlIndicator.getOffsetY();
                if (!mPreventForHorizontal && (Math.abs(offsetX) > mPagingTouchSlop && Math.abs(offsetX) > Math.abs(offsetY))) {
                    if (mPtlIndicator.isInStartPosition()) {
                        mPreventForHorizontal = true;
                    }
                }
                if (mPreventForHorizontal) {
                    return dispatchTouchEventSupper(e);
                }
                boolean moveUp = offsetY < 0;
                boolean moveDown = !moveUp;
                boolean canMoveDown = mPtlIndicator.hasLeftStartPosition();
                if (moveUp && mPtlHandler != null && !mPtlHandler.checkCanDoLoad(this, mContentView, mFooterView)) {
                    return dispatchTouchEventSupper(e);
                }
                if ((moveDown && canMoveDown) || moveUp) {
                    movePos(offsetY);
                    return true;
                }
        }
        return dispatchTouchEventSupper(e);
    }

    private void onRelease(boolean pStayForLoading) {
        tryToPerformLoadMore();
        if (mStatus == PTL_STATUS_LOADING) {
            // keep header for fresh
            if (mKeepFooterWhenLoadMore) {
                // scroll header back
                if (mPtlIndicator.isOverOffsetToKeepFooterWhileLoading() && !pStayForLoading) {
                    mScrollChecker.tryToScrollTo(-mPtlIndicator.getOffsetToKeepFooterWhileLoading(), mDurationToClose);
                } else {
                    // do nothing
                }
            } else {
                tryScrollBackToBottom();
            }
        } else {
            if (mStatus == PTL_STATUS_COMPLETE) {
                notifyUIRefreshComplete();
            } else {
                tryScrollBackToBottom();
            }
        }
    }

    /**
     * Scroll back to to if is not under touch
     */
    private void tryScrollBackToBottom() {
        if (!mPtlIndicator.isUnderTouch()) {
            mScrollChecker.tryToScrollTo(PtlIndicator.POS_START, mDurationToCloseFooter);
        }
    }

    private void notifyUIRefreshComplete() {
        if (mPtlUIHandlerHolder.hasHandler()) {
            if (isDebug()) {
                PtrCLog.i(LOG_TAG, "PtrUIHandler: onUIRefreshComplete");
            }
            mPtlUIHandlerHolder.onUIRefreshComplete(this);
        }
        tryScrollBackToBottom();
        tryToNotifyReset();
    }

    /**
     * if deltaY > 0, move the content down
     */
    private void movePos(float deltaY) {

        if ((deltaY > 0 && mPtlIndicator.isInStartPosition())) {
            if (isDebug()) {
                PtrCLog.e(LOG_TAG, String.format("has reached the top"));
            }
            return;
        }

        if (mPtlIndicator.hasOutOfLargeDistance() && deltaY < 0) {
            deltaY /= 2.0f;
        }
        int to = (int) (mPtlIndicator.getCurrentPosY() + deltaY);
        if (mPtlIndicator.willOverBottom(to)) {
            to = mPtlIndicator.getPosStart();
        }
        mPtlIndicator.setCurrentPos(to);
        int change = mPtlIndicator.getCurrentPosY() - mPtlIndicator.getLastPosY();
        updatePos(change);
    }

    private void updatePos(int pChange) {
        if (pChange == 0) {
            return;
        }
        boolean isUnderTouch = mPtlIndicator.isUnderTouch();

        // once moved, cancel event will be sent to child
        if (isUnderTouch && !mHasSendCancelEvent && mPtlIndicator.hasMovedAfterPressedDown()) {
            mHasSendCancelEvent = true;
            sendCancelEvent();
        }

        // leave initiated position or just refresh complete
        if ((mPtlIndicator.hasJustLeftStartPosition() && mStatus == PTL_STATUS_INIT)) {
            mStatus = PTL_STATUS_PREPARE;
            mPtlUIHandlerHolder.onUILoadMorePrepare(this);
        }

        // back to initiated position
        if (mPtlIndicator.hasJustBackToStartPosition()) {
            tryToNotifyReset();
            // recover event to children
            if (isUnderTouch) {
                sendDownEvent();
            }
        }

        // Pull to Refresh
        if (mStatus == PTL_STATUS_PREPARE) {
            // reach fresh height while moving from top to bottom
            if (isUnderTouch && mPullToLoadMore && mPtlIndicator.crossLoadMoreLineFromBottomToTop()) {
                PtrCLog.i(LOG_TAG, "lastPos=%s,curPos=%s,offsetLoadMore=%s", mPtlIndicator.getLastPosY(), mPtlIndicator.getCurrentPosY(), mPtlIndicator.getOffsetToLoadMore());
                tryToPerformLoadMore();
            }
        }

        if (isDebug()) {
            PtrCLog.v(LOG_TAG, "updatePos: change: %s, current: %s last: %s, top: %s, mContentHeight: %s",
                    pChange, mPtlIndicator.getCurrentPosY(), mPtlIndicator.getLastPosY(), mContentView.getTop(), mContentHeight);
        }
        mFooterView.offsetTopAndBottom(pChange);
        if (!isPinContent()) {
            mContentView.offsetTopAndBottom(pChange);
        }
        invalidate();
        if (mPtlUIHandlerHolder.hasHandler()) {
            mPtlUIHandlerHolder.onUIPositionChange(this, isUnderTouch, mStatus, mPtlIndicator);
        }
    }

    public void autoLoadMore(boolean atOnce) {
        autoLoadMore(atOnce, mDurationToCloseFooter);
    }

    public void autoLoadMore(boolean atOnce, int duration) {

        if (mStatus != PTL_STATUS_INIT) {
            return;
        }

        mStatus = PTL_STATUS_PREPARE;
        if (mPtlUIHandlerHolder.hasHandler()) {
            mPtlUIHandlerHolder.onUILoadMorePrepare(this);
        }
        if (atOnce) {
            mStatus = PTL_STATUS_LOADING;
            performLoadMore();
        } else {
            mScrollChecker.tryToScrollTo(-mPtlIndicator.getOffsetToLoadMore(), duration);
        }
    }

    /**
     * Call this when data is loaded.
     * The UI will perform complete at once or after a delay, depends on the time elapsed is greater then {@link #mLoadingMinTime} or not.
     */
    final public void loadMoreComplete() {
        if (isDebug()) {
            PtrCLog.i(LOG_TAG, "loadMoreComplete");
        }

        int delay = (int) (mLoadingMinTime - (System.currentTimeMillis() - mLoadingStartTime));
        if (delay <= 0 || delay > mLoadingMaxTime) {
            if (isDebug()) {
                PtrCLog.i(LOG_TAG, "performLoadMoreComplete at once");
            }
            performLoadMoreComplete();
        } else {
            postDelayed(mPerformLoadMoreCompleteDelay, delay);
            if (isDebug()) {
                PtrCLog.i(LOG_TAG, "performLoadMoreComplete after delay: %s", delay);
            }
        }
    }

    private void performLoadMoreComplete() {
        mStatus = PTL_STATUS_COMPLETE;
        notifyUIRefreshComplete();
    }

    private boolean tryToPerformLoadMore() {
        if (mStatus != PTL_STATUS_PREPARE) {
            return false;
        }

        //
        if (mPtlIndicator.isOverOffsetToLoadMore()) {
            mStatus = PTL_STATUS_LOADING;
            performLoadMore();
        }
        return false;
    }

    private void performLoadMore() {
        mLoadingStartTime = System.currentTimeMillis();
        if (mPtlUIHandlerHolder.hasHandler()) {
            mPtlUIHandlerHolder.onUIRefreshBegin(this);
            if (isDebug()) {
                PtrCLog.i(LOG_TAG, "PtlUIHandler: onUIRefreshBegin");
            }
        }
        if (mPtlHandler != null) {
            mPtlHandler.onLoadMoreBegin(this);
        }
    }

    private boolean tryToNotifyReset() {
        if ((mStatus == PTL_STATUS_COMPLETE || mStatus == PTL_STATUS_PREPARE) && mPtlIndicator.isInStartPosition()) {
            if (mPtlUIHandlerHolder.hasHandler()) {
                mPtlUIHandlerHolder.onUIReset(this);
                if (isDebug()) {
                    PtrCLog.i(LOG_TAG, "PtlUIHandler: onUIReset");
                }
            }
            mStatus = PTL_STATUS_INIT;
            return true;
        }
        return false;
    }

    private void sendDownEvent() {
        if (mLastMoveEvent == null) {
            return;
        }
        Log.i(LOG_TAG, "sendDownEvent: ");
        final MotionEvent last = mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime(), MotionEvent.ACTION_DOWN, last.getX(), last.getY(), last.getMetaState());
        dispatchTouchEventSupper(e);
    }

    private void sendCancelEvent() {
        if (mLastMoveEvent == null) {
            return;
        }
        Log.i(LOG_TAG, "sendCancelEvent: ");
        MotionEvent last = mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime() + ViewConfiguration.getLongPressTimeout(), MotionEvent.ACTION_CANCEL, last.getX(), last.getY(), last.getMetaState());
        dispatchTouchEventSupper(e);
    }

    protected void onPtlScrollAbort() {
        if (mPtlIndicator.hasLeftStartPosition()) {
            if (isDebug()) {
                PtrCLog.d(LOG_TAG, "call onRelease after scroll abort");
            }
            onRelease(true);
        }
    }

    protected void onPtlScrollFinish() {
        if (mPtlIndicator.hasLeftStartPosition()) {
            if (isDebug()) {
                PtrCLog.d(LOG_TAG, "call onRelease after scroll finish");
            }
            onRelease(true);
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p != null && p instanceof PtlFrameLayout.LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new PtlFrameLayout.LayoutParams(PtlFrameLayout.LayoutParams.MATCH_PARENT, PtlFrameLayout.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new PtlFrameLayout.LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new PtlFrameLayout.LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        @SuppressWarnings({"unused"})
        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mScrollChecker != null) {
            mScrollChecker.destroy();
        }
        if (mPerformLoadMoreCompleteDelay != null) {
            removeCallbacks(mPerformLoadMoreCompleteDelay);
        }
    }

    private class ScrollChecker implements Runnable {

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
            if (isDebug()) {
                if (deltaY != 0) {
                    PtrCLog.v(LOG_TAG,
                            "scroll: %s, start: %s, to: %s, currentPos: %s, current :%s, last: %s, delta: %s",
                            finish, mStart, mTo, mPtlIndicator.getCurrentPosY(), curY, mLastFlingY, deltaY);
                }
            }
            if (!finish) {
                mLastFlingY = curY;
                movePos(deltaY);
                post(this);
            } else {
                finish();
            }
        }

        private void finish() {
            if (isDebug()) {
                PtrCLog.v(LOG_TAG, "finish, currentPos:%s", mPtlIndicator.getCurrentPosY());
            }
            reset();
            onPtlScrollFinish();
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
        }

        public void abortIfWorking() {
            if (mIsRunning) {
                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
                }
                onPtlScrollAbort();
                reset();
            }
        }

        public void tryToScrollTo(int to, int duration) {
            if (mPtlIndicator.isAlreadyHere(to)) {
                return;
            }
            mStart = mPtlIndicator.getCurrentPosY();
            mTo = to;
            int distance = to - mStart;
            if (isDebug()) {
                PtrCLog.i(LOG_TAG, "tryToScrollTo: start: %s, distance:%s, to:%s", mStart, distance, to);
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
