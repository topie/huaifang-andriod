package com.davdian.ptr.ptl;

import android.graphics.PointF;

public class PtlIndicator {

    public final static int POS_START = 0;
    private PointF mPtLastMove = new PointF();
    private int mOffsetToLoadMore = 0;
    private float mOffsetX;
    private float mOffsetY;
    private int mPressedPos;
    private int mCurrentPos = 0;
    private int mLastPos = 0;
    private int mFooterHeight;
    private int mOffsetToKeepFooterWhileLoading = -1;
    private boolean mIsUnderTouch = false;

    public boolean isUnderTouch() {
        return mIsUnderTouch;
    }

    public void onRelease() {
        mIsUnderTouch = false;
    }

    private void processOnMove(float currentX, float currentY, float offsetX, float offsetY) {
        setOffset(offsetX, offsetY);
    }

    public void onPressDown(float x, float y) {
        mPressedPos = mCurrentPos;
        mIsUnderTouch = true;
        mPtLastMove.set(x, y);
    }

    public final void onMove(float x, float y) {
        float offsetX = x - mPtLastMove.x;
        float offsetY = y - mPtLastMove.y;
        processOnMove(x, y, offsetX, offsetY);
        mPtLastMove.set(x, y);
    }

    private void setOffset(float x, float y) {
        mOffsetX = x;
        mOffsetY = y;
    }

    public float getOffsetX() {
        return mOffsetX;
    }

    public float getOffsetY() {
        return mOffsetY;
    }

    public int getLastPosY() {
        return mLastPos;
    }

    public int getCurrentPosY() {
        return mCurrentPos;
    }

    /**
     * Update current position before update the UI
     */
    public final void setCurrentPos(int current) {
        mLastPos = mCurrentPos;
        mCurrentPos = current;
    }

    public int getFooterHeight() {
        return mFooterHeight;
    }

    public void setFooterHeight(int height) {
        mFooterHeight = height;
        updateHeight();
    }

    private void updateHeight() {
        mOffsetToLoadMore = mFooterHeight;
    }

    public void setOffsetToKeepFooterWhileLoading(int offset) {
        mOffsetToKeepFooterWhileLoading = offset;
    }

    public int getOffsetToKeepFooterWhileLoading() {
        return mOffsetToKeepFooterWhileLoading >= 0 ? mOffsetToKeepFooterWhileLoading : mFooterHeight;
    }

    public int getOffsetToLoadMore() {
        return mOffsetToLoadMore;
    }

    public void setOffsetToLoadMore(int pOffsetToLoadMore) {
        mOffsetToLoadMore = pOffsetToLoadMore;
    }

    public boolean isInStartPosition() {
        return mCurrentPos == POS_START;
    }

    public boolean willOverBottom(int to) {
        return to > POS_START;
    }

    public boolean hasJustLeftStartPosition() {
        return mLastPos >= POS_START && mCurrentPos < POS_START;
    }

    public boolean hasOutOfLargeDistance() {
        return mCurrentPos <= -getOffsetToLoadMore();
    }

    public boolean isAlreadyHere(int pTo) {
        return mCurrentPos == pTo;
    }

    public boolean hasLeftStartPosition() {
        return mCurrentPos != POS_START;
    }

    public boolean hasMovedAfterPressedDown() {
        return mCurrentPos != mPressedPos;
    }

    public boolean hasJustBackToStartPosition() {
        return mLastPos != POS_START && mCurrentPos == POS_START;
    }

    public boolean crossLoadMoreLineFromBottomToTop() {
        return mLastPos > -getOffsetToLoadMore() && mCurrentPos <= -getOffsetToLoadMore();
    }

    public boolean crossLoadMoreLineFromTopToBottom() {
        return mLastPos < -getOffsetToLoadMore() && mCurrentPos >= -getOffsetToLoadMore();
    }

    public boolean isOverOffsetToLoadMore() {
        return mCurrentPos <= -getOffsetToLoadMore();
    }

    public boolean isOverOffsetToKeepFooterWhileLoading() {
        return mCurrentPos <= -getOffsetToKeepFooterWhileLoading();
    }

    public int getPosStart() {
        return POS_START;
    }
}
