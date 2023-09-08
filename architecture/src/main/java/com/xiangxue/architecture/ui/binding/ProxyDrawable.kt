package com.xiangxue.architecture.ui.binding

import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable

// 同学们，此类只给 Drawables 服务哦
class ProxyDrawable : StateListDrawable() {
    var originDrawable: Drawable? = null
        private set

    override fun addState(stateSet: IntArray, drawable: Drawable) {
        if (stateSet != null && stateSet.size == 1 && stateSet[0] == 0) {
            originDrawable = drawable
        }
        super.addState(stateSet, drawable)
    }
}