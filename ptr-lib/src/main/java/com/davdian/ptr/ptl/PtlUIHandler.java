package com.davdian.ptr.ptl;

/**
 *
 */
public interface PtlUIHandler {

    /**
     * When the content view has reached top and refresh has been completed, view will be reset.
     *
     * @param frame
     */
    public void onUIReset(PtlFrameLayout frame);

    /**
     * prepare for loading
     *
     * @param frame
     */
    public void onUILoadMorePrepare(PtlFrameLayout frame);

    /**
     * perform refreshing UI
     */
    public void onUIRefreshBegin(PtlFrameLayout frame);

    /**
     * perform UI after refresh
     */
    public void onUIRefreshComplete(PtlFrameLayout frame);

    public void onUIPositionChange(PtlFrameLayout frame, boolean isUnderTouch, byte status, PtlIndicator pPtlIndicator);
}
