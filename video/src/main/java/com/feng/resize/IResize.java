package com.feng.resize;

/**
 * 对根据比例和百分比设置view大小
 */
public interface IResize {

    /**
     * 等比缩放留黑边
     */
    int VIEW_CUT_MODE_NONE = 0;
    /**
     * 等比放大铺满全屏，多余部分裁剪
     */
    int VIEW_CUT_MODE_FULL = 1;

    /**
     * 拉伸铺满全屏，不裁剪
     */
    int VIEW_CUT_MODE_FITXY = 2;

    /**
     * 智能平铺
     */
    int VIEW_CUT_MODE_SMART = 3;

    /**
     * 设置平铺模式
     *
     * @param mode
     */
    void setVideoCutMode(int mode);

    /**
     * 添加画面宽高变化回调
     *
     * @param listener
     */
    void addOnResizeListener(OnResizeListener listener);
}