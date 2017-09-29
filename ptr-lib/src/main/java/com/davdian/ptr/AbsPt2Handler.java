package com.davdian.ptr;

import android.view.View;

import com.davdian.ptr.ptl.PtlFrameLayout;

import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by arman on 2017/9/28.
 * 默认的下拉刷新逻辑
 */

abstract public class AbsPt2Handler implements Pt2Handler {

    @Override
    public boolean checkCanDoLoad(PtlFrameLayout frame, View content, View footer) {
        return Pt2Util.checkCanDoLoad(content);
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return Pt2Util.checkCanDoRefresh(content);
    }

    public static class Pt2Util {

        public static boolean checkCanDoRefresh(View content) {
            return content != null && !content.canScrollVertically(-1);
        }

        public static boolean checkCanDoLoad(View content) {
            return content != null && !content.canScrollVertically(1);
        }
    }
}
