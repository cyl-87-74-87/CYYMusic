package com.xiangxue.architecture.utils

/**
 * <pre>
 * author:
 * blog  :
 * time  :
 * desc  : utils derry
</pre> *
 */
object DisplayUtils {
    /**
     * convert px to its equivalent dp
     *
     *
     * 将px转换为与之相等的dp
     */
    fun px2dp(pxValue: Float): Int {
        val scale = Utils.getApp().resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * convert dp to its equivalent px
     *
     *
     * 将dp转换为与之相等的px
     */
    fun dp2px(dipValue: Float): Int {
        val scale = Utils.getApp().resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    /**
     * convert px to its equivalent sp
     *
     *
     * 将px转换为sp
     */
    fun px2sp(pxValue: Float): Int {
        val fontScale = Utils.getApp().resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    /**
     * convert sp to its equivalent px
     *
     *
     * 将sp转换为px
     */
    fun sp2px(spValue: Float): Int {
        val fontScale = Utils.getApp().resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }
}