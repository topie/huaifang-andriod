package com.davdian.ptr;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.davdian.ptr.ptl.PtlDefaultFooter;
import com.davdian.ptr.ptl.PtlFrameLayout;
import com.davdian.ptr.ptl.PtlUIHandler;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * Created by bigniu on 2017/3/29.
 */
public class Pt2FrameLayout extends PtrFrameLayout {

    private PtlFrameLayout mPtlFrameLayout;
    private Pt2HandlerWrapper mPt2Handler;
    private PtrIndicator mPtrIndicator;

    public Pt2FrameLayout(Context context) {
        this(context, null);
    }

    public Pt2FrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Pt2FrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mPt2Handler = new Pt2HandlerWrapper();
        setPtrIndicator(new MyPtrIndicator());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mContent != null && mContent instanceof PtlFrameLayout) {
            mPtlFrameLayout = (PtlFrameLayout) mContent;
            if (mPtlFrameLayout.getFooterView() == null) {
                PtlDefaultFooter ptlDefaultFooter = new PtlDefaultFooter(getContext());
                mPtlFrameLayout.setFooterView(ptlDefaultFooter);
                mPtlFrameLayout.addUiHandler(ptlDefaultFooter);
                mPtlFrameLayout.setPtlHandler(mPt2Handler);
            }
        }
        View headerView = getHeaderView();
        if (headerView == null) {
            DefaultHeader header = new DefaultHeader(getContext());
            setDurationToClose(100);
            setDurationToCloseHeader(300);
            setKeepHeaderWhenRefresh(true);
            setPullToRefresh(false);
            setRatioOfHeaderHeightToRefresh(1.0f);
            setResistance(2.0f);
            setEnabledNextPtrAtOnce(true);
            setHeaderView(header);
            addPtrUIHandler(header);
            super.setPinContent(true);
        } else if (headerView instanceof PtrUIHandler) {
            addPtrUIHandler((PtrUIHandler) headerView);
        }
        setPtrHandlerSuper(mPt2Handler);
    }

    @Override
    public void setPtrIndicator(PtrIndicator slider) {
        super.setPtrIndicator(slider);
        mPtrIndicator = slider;
    }

    /**
     * @deprecated please use {@link #setPt2Handler(Pt2Handler)}
     */
    @Override
    @Deprecated
    public void setPtrHandler(PtrHandler ptrHandler) {
        throw new RuntimeException("please use setPt2Handler()");
    }

    @Override
    public void setPinContent(boolean pinContent) {
        super.setPinContent(pinContent);
        if (mPtlFrameLayout != null) {
            mPtlFrameLayout.setPinContent(pinContent);
        }
    }

    private void setPtrHandlerSuper(PtrHandler ptrHandler) {
        super.setPtrHandler(ptrHandler);
    }

    public PtlFrameLayout getPtlFrameLayout() {
        return mPtlFrameLayout;
    }

    public void complete2() {
        if (mPtlFrameLayout != null) {
            mPtlFrameLayout.loadMoreComplete();
        }
        refreshComplete();
    }

    public void setPt2Handler(Pt2Handler pPt2Handler) {
        mPt2Handler.setBase(pPt2Handler);
    }

    public void setFooterView(View mFooter) {
        if (mPtlFrameLayout != null) {
            mPtlFrameLayout.setFooterView(mFooter);
        }
    }

    public void addPtlUIHandler(PtlUIHandler pPtlUIHandler) {
        if (mPtlFrameLayout != null) {
            mPtlFrameLayout.addUiHandler(pPtlUIHandler);
        }
    }

    public void removePtlUIHandler(PtlUIHandler pPtlUIHandler) {
        if (mPtlFrameLayout != null) {
            mPtlFrameLayout.removeUiHandler(pPtlUIHandler);
        }
    }

    private class Pt2HandlerWrapper implements Pt2Handler {
        private Pt2Handler mBase;

        public void setBase(Pt2Handler pBase) {
            mBase = pBase;
        }

        @Override
        public boolean checkCanDoLoad(PtlFrameLayout frame, View content, View footer) {
            if (content instanceof PtlFrameLayout) {
                content = ((PtlFrameLayout) content).getContentView();
            }
            return mBase != null && mBase.checkCanDoLoad(frame, content, footer) && !isRefreshing();
        }

        @Override
        public void onLoadMoreBegin(PtlFrameLayout frame) {
            if (mBase != null) {
                mBase.onLoadMoreBegin(frame);
            }
        }

        @Override
        public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
            if (content instanceof PtlFrameLayout) {
                content = ((PtlFrameLayout) content).getContentView();
            }
            return Math.abs(mPtrIndicator.getOffsetY()) > Math.abs(mPtrIndicator.getOffsetX())
                    && mBase != null
                    && mBase.checkCanDoRefresh(frame, content, header)
                    && (mPtlFrameLayout == null || mPtlFrameLayout.getStatus() == PtlFrameLayout.PTL_STATUS_INIT);
        }

        @Override
        public void onRefreshBegin(PtrFrameLayout frame) {
            if (mBase != null) {
                mBase.onRefreshBegin(frame);
            }
        }
    }

    private static class MyPtrIndicator extends PtrIndicator {
        @Override
        protected void processOnMove(float currentX, float currentY, float offsetX, float offsetY) {
            setOffset(offsetX / getResistance(), offsetY / getResistance());
        }
    }

}
