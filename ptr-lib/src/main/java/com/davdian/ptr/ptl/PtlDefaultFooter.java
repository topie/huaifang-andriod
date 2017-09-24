package com.davdian.ptr.ptl;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.davdian.ptr.R;

public class PtlDefaultFooter extends FrameLayout implements PtlUIHandler {

    public static final byte STATUS_REST = 0;
    public static final byte STATUS_LOADING = 1;
    public static final byte STATUS_COMPLETE = 2;

    private ImageView mIvLoadMore;
    private AnimationDrawable mAnimationDrawable;
    private TextView mTvLoadMoreStatus;
    private byte mStatus;

    public PtlDefaultFooter(Context context) {
        super(context);
        initViews(context);
    }

    public PtlDefaultFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public PtlDefaultFooter(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews(context);
    }

    private void initViews(Context pContext) {
        LayoutInflater.from(pContext).inflate(R.layout.ptr_default_footer, this, true);
        mIvLoadMore = (ImageView) findViewById(R.id.iv_lm_ptr_load_more);
        mAnimationDrawable = (AnimationDrawable) mIvLoadMore.getDrawable();
        mTvLoadMoreStatus = (TextView) findViewById(R.id.tv_lm_ptr_load_more);
        changeStatus(STATUS_REST);
    }

    public void changeStatus(byte status) {
        mStatus = status;
        switch (status) {
            case STATUS_LOADING:
                mTvLoadMoreStatus.setText(R.string.cube_ptr_loading);
                mIvLoadMore.setVisibility(VISIBLE);
                mAnimationDrawable.start();
                break;
            case STATUS_COMPLETE:
                mTvLoadMoreStatus.setText(R.string.cube_ptr_load_complete);
                mIvLoadMore.setVisibility(GONE);
                mAnimationDrawable.stop();
                break;
            case STATUS_REST:
            default:
                mTvLoadMoreStatus.setText(R.string.cube_ptr_pull_up_to_load);
                mIvLoadMore.setVisibility(GONE);
                mAnimationDrawable.stop();
                break;
        }
    }

    public byte getStatus() {
        return mStatus;
    }

    @Override
    public void onUIReset(PtlFrameLayout frame) {
        changeStatus(STATUS_REST);
    }

    @Override
    public void onUILoadMorePrepare(PtlFrameLayout frame) {
        changeStatus(STATUS_REST);
    }

    @Override
    public void onUIRefreshBegin(PtlFrameLayout frame) {
        changeStatus(STATUS_LOADING);
    }

    @Override
    public void onUIRefreshComplete(PtlFrameLayout frame) {
        changeStatus(STATUS_COMPLETE);
    }

    @Override
    public void onUIPositionChange(PtlFrameLayout frame, boolean isUnderTouch, byte status, PtlIndicator pPtlIndicator) {

    }

}
