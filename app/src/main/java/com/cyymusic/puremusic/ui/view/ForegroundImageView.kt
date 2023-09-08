package com.cyymusic.puremusic.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.cyymusic.puremusic.R

/**
 * 对主界面重要的自定义ImageView
 * <!-- 就是播放大图标 -->
 */
class ForegroundImageView(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {

    constructor(context: Context) : this(context, null)

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ForegroundImageView)
        val foreground = a.getDrawable(R.styleable.ForegroundImageView_android_foreground)
        foreground?.let { setForeground(it) }
        a.recycle()
    }

    /**
     * Supply a drawable resource that is to be rendered on top of all of the child
     * views in the frame layout.
     *
     * @param drawableResId The drawable resource to be drawn on top of the children.
     */
    fun setForegroundResource(drawableResId: Int) {
        foreground = context.resources.getDrawable(drawableResId)

    }

    /**
     * Supply a Drawable that is to be rendered on top of all of the child
     * views in the frame layout.
     *
     * @param drawable The Drawable to be drawn on top of the children.
     */
    override fun setForeground(drawable: Drawable) {
        if (foreground === drawable) {
            return
        }
        if (foreground != null) {
            foreground.callback = null
            unscheduleDrawable(foreground)
        }
        foreground = drawable
        if (drawable != null) {
            drawable.callback = this
            if (drawable.isStateful) {
                drawable.state = drawableState
            }
        }
        requestLayout()
        invalidate()
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return super.verifyDrawable(who) || who === foreground
    }

    override fun jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState()
        if (foreground != null) {
            foreground.jumpToCurrentState()
        }
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (foreground != null && foreground.isStateful) {
            foreground.state = drawableState
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (foreground != null) {
            foreground.setBounds(0, 0, measuredWidth, measuredHeight)
            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (foreground != null) {
            foreground.setBounds(0, 0, w, h)
            invalidate()
        }
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (foreground != null) {
            foreground.draw(canvas!!)
        }
    }

}