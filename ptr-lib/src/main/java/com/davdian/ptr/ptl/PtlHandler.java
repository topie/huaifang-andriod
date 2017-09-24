package com.davdian.ptr.ptl;

import android.view.View;

import in.srain.cube.views.ptr.PtrDefaultHandler;

public interface PtlHandler {

    /**
     * Check can do refresh or not. For example the content is empty or the first child is in view.
     * <p/>
     * {@link PtrDefaultHandler#checkContentCanBePulledDown}
     */
    boolean checkCanDoLoad(final PtlFrameLayout frame, final View content, final View footer);

    /**
     * When refresh begin
     *
     * @param frame
     */
    void onLoadMoreBegin(final PtlFrameLayout frame);
}