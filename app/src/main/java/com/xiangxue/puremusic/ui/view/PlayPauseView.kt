package com.xiangxue.puremusic.ui.view

import android.animation.AnimatorSet
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.Property
import android.view.View
import android.view.ViewOutlineProvider
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import com.xiangxue.puremusic.R

/**
 * 此类被 IconBindingAdapter 使用了，因为IconBindingAdapter需要去 isPlaying（pauseView.play(); / pauseView.pause();）
 * 此类被 fragment_player.XML使用了，因为播放条需要 播放状态 与 暂停状态
 */
class PlayPauseView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    val isDrawCircle: Boolean
    private val mDrawable: PlayPauseDrawable
    private val mPaint = Paint()
    var circleAlpha: Int
    private var mDrawableColor: Int
    private var mAnimatorSet: AnimatorSet? = null
    private var mBackgroundColor = 0
    private var mWidth = 0
    private var mHeight = 0
    var isPlay = false
        private set

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // final int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
        // setMeasuredDimension(size, size);
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mDrawable.setBounds(0, 0, w, h)
        mWidth = w
        mHeight = h
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            outlineProvider = object : ViewOutlineProvider() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                override fun getOutline(view: View, outline: Outline) {
                    outline.setOval(0, 0, view.width, view.height)
                }
            }
            clipToOutline = true
        }
    }

    fun setCircleAlpah(alpah: Int) {
        circleAlpha = alpah
        invalidate()
    }

    private var circleColor: Int
        private get() = mBackgroundColor
        set(color) {
            mBackgroundColor = color
            invalidate()
        }
    var drawableColor: Int
        get() = mDrawableColor
        set(color) {
            mDrawableColor = color
            mDrawable.setDrawableColor(color)
            invalidate()
        }

    override fun verifyDrawable(who: Drawable): Boolean {
        return who === mDrawable || super.verifyDrawable(who)
    }

    override fun hasOverlappingRendering(): Boolean {
        return false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPaint.color = mBackgroundColor
        val radius = Math.min(mWidth, mHeight) / 2f
        if (isDrawCircle) {
            mPaint.color = mBackgroundColor
            mPaint.alpha = circleAlpha
            canvas.drawCircle(mWidth / 2f, mHeight / 2f, radius, mPaint)
        }
        mDrawable.draw(canvas)
    }

    /**
     * 此时为待暂停标识
     */
    fun play() {
        if (mAnimatorSet != null) {
            mAnimatorSet!!.cancel()
        }
        mAnimatorSet = AnimatorSet()
        isPlay = true
        mDrawable.setmIsPlay(isPlay)
        val pausePlayAnim = mDrawable.pausePlayAnimator
        mAnimatorSet!!.interpolator = DecelerateInterpolator()
        mAnimatorSet!!.duration = PLAY_PAUSE_ANIMATION_DURATION
        pausePlayAnim.start()
    }

    /**
     * 此时为为待播放标识
     */
    fun pause() {
        if (mAnimatorSet != null) {
            mAnimatorSet!!.cancel()
        }
        mAnimatorSet = AnimatorSet()
        isPlay = false
        mDrawable.setmIsPlay(isPlay)
        val pausePlayAnim = mDrawable.pausePlayAnimator
        mAnimatorSet!!.interpolator = DecelerateInterpolator()
        mAnimatorSet!!.duration = PLAY_PAUSE_ANIMATION_DURATION
        pausePlayAnim.start()
    }

    companion object {
        private val COLOR: Property<PlayPauseView, Int> = object : Property<PlayPauseView, Int>(
            Int::class.java, "color"
        ) {
            override fun get(v: PlayPauseView): Int {
                return v.circleColor
            }

            override fun set(v: PlayPauseView, value: Int) {
                v.circleColor = value
            }
        }
        private const val PLAY_PAUSE_ANIMATION_DURATION: Long = 200 // 设置动画时长
    }

    init {
        setWillNotDraw(false)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PlayPause)
        isDrawCircle = typedArray.getBoolean(R.styleable.PlayPause_isCircleDraw, true)
        circleAlpha = typedArray.getInt(R.styleable.PlayPause_circleAlpha, 255)
        //        mBackgroundColor = ATEUtil.getThemeAccentColor(context);
        mDrawableColor = typedArray.getInt(R.styleable.PlayPause_drawableColor, Color.WHITE)
        typedArray.recycle()
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
        mPaint.alpha = circleAlpha
        mPaint.color = mBackgroundColor
        mDrawable = PlayPauseDrawable(context, mDrawableColor)
        mDrawable.callback = this
    }
}