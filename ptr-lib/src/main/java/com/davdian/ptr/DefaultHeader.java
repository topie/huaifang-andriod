package com.davdian.ptr;

import android.content.Context;
import android.util.AttributeSet;

import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * @author arman
 *         下拉刷新的头部
 */
public class DefaultHeader extends MaterialHeader {

    public DefaultHeader(Context context) {
        this(context, null);
    }

    public DefaultHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}