package com.davdian.ptr;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * Created by bigniu on 2017/3/31.
 */
public class DefaultHeader2 extends FrameLayout implements PtrUIHandler {

    private TextView mTextView;

    private String strDefault;
    private String strLoading;
    private String strComplete;
    private AnimationDrawable mAnimationDrawable;

    public DefaultHeader2(Context context) {
        this(context, null);
    }

    public DefaultHeader2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultHeader2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context pContext) {
        View inflate = LayoutInflater.from(pContext).inflate(R.layout.ptr_default_header_v2, null);
        mTextView = (TextView) inflate.findViewById(R.id.tv_ptr_default_header_v2);
        ImageView ivLoading = (ImageView) inflate.findViewById(R.id.iv_ptr_default_header_v2);
        mAnimationDrawable = (AnimationDrawable) ivLoading.getDrawable();
        addView(inflate);
        strDefault = getResources().getString(R.string.cube_ptr_pull_down);
        strLoading = getResources().getString(R.string.cube_ptr_refreshing);
        strComplete = getResources().getString(R.string.cube_ptr_refresh_complete);
    }

    public void setStrDefault(String pStrDefault) {
        strDefault = pStrDefault;
    }

    public void setStrLoading(String pStrLoading) {
        strLoading = pStrLoading;
    }

    public void setStrComplete(String pStrComplete) {
        strComplete = pStrComplete;
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {
        mTextView.setText(strDefault);
        mAnimationDrawable.stop();
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        mTextView.setText(strDefault);
        mAnimationDrawable.start();
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        mTextView.setText(strLoading);
        mAnimationDrawable.start();
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        mTextView.setText(strComplete);
        mAnimationDrawable.stop();
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

    }
}
