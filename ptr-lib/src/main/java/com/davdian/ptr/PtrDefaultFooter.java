package com.davdian.ptr;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class PtrDefaultFooter extends FrameLayout {

    public static final byte STATUS_REST = 0;
    public static final byte STATUS_LOADING = 1;
    public static final byte STATUS_COMPLETE = 2;
    public static final byte STATUS_ERROR = 3;

    private ImageView mIvLoadMore;
    private AnimationDrawable mAnimationDrawable;
    private TextView mTvLoadMoreStatus;

    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean mEnableLoadMore = true;
    private byte mStatus;


    public PtrDefaultFooter(Context context) {
        super(context);
        initViews(context);
    }

    public PtrDefaultFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public PtrDefaultFooter(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews(context);
    }

    private void initViews(Context pContext) {
        LayoutInflater.from(pContext).inflate(R.layout.ptr_default_footer, this, true);
        mIvLoadMore = findViewById(R.id.iv_lm_ptr_load_more);
        mAnimationDrawable = (AnimationDrawable) mIvLoadMore.getDrawable();
        mTvLoadMoreStatus = findViewById(R.id.tv_lm_ptr_load_more);
        changeStatus(STATUS_REST);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener pOnLoadMoreListener) {
        mOnLoadMoreListener = pOnLoadMoreListener;
    }

    public void tryToLoadMore() {
        if (mEnableLoadMore && mStatus != STATUS_LOADING && mOnLoadMoreListener != null) {
            mOnLoadMoreListener.onLoadMore();
            changeStatus(STATUS_LOADING);
        }
    }

    public void changeStatus(byte status) {
        mStatus = status;
        switch (status) {
            case STATUS_REST:
                mTvLoadMoreStatus.setText(R.string.cube_ptr_pull_up);
                mIvLoadMore.setVisibility(GONE);
                mAnimationDrawable.stop();
                break;
            case STATUS_LOADING:
                mTvLoadMoreStatus.setText(R.string.cube_ptr_loading);
                mIvLoadMore.setVisibility(VISIBLE);
                mAnimationDrawable.start();
                break;
            case STATUS_COMPLETE:
                mTvLoadMoreStatus.setText(R.string.cube_ptr_not_more);
                mIvLoadMore.setVisibility(GONE);
                mAnimationDrawable.stop();
                break;
            case STATUS_ERROR:
            default:
                mTvLoadMoreStatus.setText(R.string.cube_ptr_error);
                mIvLoadMore.setVisibility(GONE);
                mAnimationDrawable.stop();
                break;
        }
    }

    public void disableLoadMore() {
        mEnableLoadMore = false;
    }

    public void enableLoadMore() {
        mEnableLoadMore = true;
    }

    public byte getStatus() {
        return mStatus;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
