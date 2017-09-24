package com.davdian.ptr.ptl;

/**
 * A single linked list to wrap PtrUIHandler
 */
class PtlUIHandlerHolder implements PtlUIHandler {

    private PtlUIHandler mHandler;
    private PtlUIHandlerHolder mNext;

    private boolean contains(PtlUIHandler handler) {
        return mHandler != null && mHandler == handler;
    }

    private PtlUIHandlerHolder() {

    }

    public boolean hasHandler() {
        return mHandler != null;
    }

    private PtlUIHandler getHandler() {
        return mHandler;
    }

    public static void addHandler(PtlUIHandlerHolder head, PtlUIHandler handler) {

        if (null == handler) {
            return;
        }
        if (head == null) {
            return;
        }
        if (null == head.mHandler) {
            head.mHandler = handler;
            return;
        }

        PtlUIHandlerHolder current = head;
        for (; ; current = current.mNext) {

            // duplicated
            if (current.contains(handler)) {
                return;
            }
            if (current.mNext == null) {
                break;
            }
        }

        PtlUIHandlerHolder newHolder = new PtlUIHandlerHolder();
        newHolder.mHandler = handler;
        current.mNext = newHolder;
    }

    public static PtlUIHandlerHolder create() {
        return new PtlUIHandlerHolder();
    }

    public static PtlUIHandlerHolder removeHandler(PtlUIHandlerHolder head, PtlUIHandler handler) {
        if (head == null || handler == null || null == head.mHandler) {
            return head;
        }

        PtlUIHandlerHolder current = head;
        PtlUIHandlerHolder pre = null;
        do {

            // delete current: link pre to next, unlink next from current;
            // pre will no change, current move to next element;
            if (current.contains(handler)) {

                // current is head
                if (pre == null) {

                    head = current.mNext;
                    current.mNext = null;

                    current = head;
                } else {

                    pre.mNext = current.mNext;
                    current.mNext = null;
                    current = pre.mNext;
                }
            } else {
                pre = current;
                current = current.mNext;
            }

        } while (current != null);

        if (head == null) {
            head = new PtlUIHandlerHolder();
        }
        return head;
    }

    @Override
    public void onUIReset(PtlFrameLayout frame) {
        PtlUIHandlerHolder current = this;
        do {
            final PtlUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUIReset(frame);
            }
        } while ((current = current.mNext) != null);
    }

    @Override
    public void onUILoadMorePrepare(PtlFrameLayout frame) {
        if (!hasHandler()) {
            return;
        }
        PtlUIHandlerHolder current = this;
        do {
            final PtlUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUILoadMorePrepare(frame);
            }
        } while ((current = current.mNext) != null);
    }

    @Override
    public void onUIRefreshBegin(PtlFrameLayout frame) {
        PtlUIHandlerHolder current = this;
        do {
            final PtlUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUIRefreshBegin(frame);
            }
        } while ((current = current.mNext) != null);
    }

    @Override
    public void onUIRefreshComplete(PtlFrameLayout frame) {
        PtlUIHandlerHolder current = this;
        do {
            final PtlUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUIRefreshComplete(frame);
            }
        } while ((current = current.mNext) != null);
    }

    @Override
    public void onUIPositionChange(PtlFrameLayout frame, boolean isUnderTouch, byte status, PtlIndicator ptrIndicator) {
        PtlUIHandlerHolder current = this;
        do {
            final PtlUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUIPositionChange(frame, isUnderTouch, status, ptrIndicator);
            }
        } while ((current = current.mNext) != null);
    }
}
