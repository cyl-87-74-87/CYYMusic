package com.xiangxue.puremusic.ui.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Property
import androidx.annotation.ColorInt

/**
 * 此自定义Drawable是专门为 PlayPauseView服务的
 */
class PlayPauseDrawable : Drawable {
    private val mLeftPauseBar = Path()
    private val mRightPauseBar = Path()
    private val mPaint = Paint()
    private val mBounds = RectF()
    private var mPauseBarWidth = 0f
    private var mPauseBarHeight = 0f
    private var mPauseBarDistance = 0f
    private var mWidth = 0f
    private var mHeight = 0f
    private var mProgress = 0f
    var isPlay = false
        private set

    constructor(context: Context) {
        val res = context.resources
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
        mPaint.color = Color.BLACK
    }

    constructor(context: Context, @ColorInt color: Int) {
        val res = context.resources
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
        mPaint.color = color
    }

    fun setmIsPlay(mIsPlay: Boolean) {
        isPlay = mIsPlay
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        mBounds.set(bounds)
        mWidth = mBounds.width()
        mHeight = mBounds.height()
        mPauseBarWidth = mWidth / 8
        mPauseBarHeight = mHeight * 0.40f
        mPauseBarDistance = mPauseBarWidth
    }

    override fun draw(canvas: Canvas) {
        mLeftPauseBar.rewind()
        mRightPauseBar.rewind()

        // The current distance between the two pause bars.
        val barDist = lerp(mPauseBarDistance, 0f, mProgress)
        // The current width of each pause bar.
        val barWidth = lerp(mPauseBarWidth, mPauseBarHeight / 2f, mProgress)
        // The current position of the left pause bar's top left coordinate.
        val firstBarTopLeft = lerp(0f, barWidth, mProgress)
        // The current position of the right pause bar's top right coordinate.
        val secondBarTopRight = lerp(2 * barWidth + barDist, barWidth + barDist, mProgress)

        // Draw the left pause bar. The left pause bar transforms into the
        // top half of the play button triangle by animating the position of the
        // rectangle's top left coordinate and expanding its bottom width.
        mLeftPauseBar.moveTo(0f, 0f)
        mLeftPauseBar.lineTo(firstBarTopLeft, -mPauseBarHeight)
        mLeftPauseBar.lineTo(barWidth, -mPauseBarHeight)
        mLeftPauseBar.lineTo(barWidth, 0f)
        mLeftPauseBar.close()

        // Draw the right pause bar. The right pause bar transforms into the
        // bottom half of the play button triangle by animating the position of
        // the
        // rectangle's top right coordinate and expanding its bottom width.
        mRightPauseBar.moveTo(barWidth + barDist, 0f)
        mRightPauseBar.lineTo(barWidth + barDist, -mPauseBarHeight)
        mRightPauseBar.lineTo(secondBarTopRight, -mPauseBarHeight)
        mRightPauseBar.lineTo(2 * barWidth + barDist, 0f)
        mRightPauseBar.close()
        canvas.save()

        // Translate the play button a tiny bit to the right so it looks more
        // centered.
        canvas.translate(lerp(0f, mPauseBarHeight / 8f, mProgress), 0f)

        // (1) Pause --> Play: rotate 0 to 90 degrees clockwise.
        // (2) Play --> Pause: rotate 90 to 180 degrees clockwise.
        val rotationProgress = if (isPlay) 1 - mProgress else mProgress
        val startingRotation: Float = if (isPlay) 90f else 0.toFloat()
        canvas.rotate(
            lerp(startingRotation, startingRotation + 90, rotationProgress),
            mWidth / 2f,
            mHeight / 2f
        )

        // Position the pause/play button in the center of the drawable's
        // bounds.
        canvas.translate(
            mWidth / 2f - (2 * barWidth + barDist) / 2f,
            mHeight / 2f + mPauseBarHeight / 2f
        )

        // Draw the two bars that form the animated pause/play button.
        canvas.drawPath(mLeftPauseBar, mPaint)
        canvas.drawPath(mRightPauseBar, mPaint)
        canvas.restore()
    }

    val pausePlayAnimator: Animator
        get() {
            val anim: Animator = ObjectAnimator.ofFloat(
                this,
                PROGRESS,
                if (isPlay) 1f else 0.toFloat(),
                if (isPlay) 0f else 1.toFloat()
            )
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    isPlay = !isPlay
                }
            })
            return anim
        }

    private var progress: Float
        private get() = mProgress
        private set(progress) {
            mProgress = progress
            invalidateSelf()
        }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
        invalidateSelf()
    }

    fun setDrawableColor(@ColorInt color: Int) {
        mPaint.color = color
        invalidateSelf()
    }

    override fun setColorFilter(cf: ColorFilter?) {
        mPaint.colorFilter = cf
        invalidateSelf()
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    companion object {
        private val PROGRESS: Property<PlayPauseDrawable, Float> =
            object : Property<PlayPauseDrawable, Float>(
                Float::class.java, "progress"
            ) {
                override fun get(d: PlayPauseDrawable): Float {
                    return d.progress
                }

                override fun set(d: PlayPauseDrawable, value: Float) {
                    d.progress = value
                }
            }

        /**
         * Linear interpolate between a and b with parameter t.
         */
        private fun lerp(a: Float, b: Float, t: Float): Float {
            return a + (b - a) * t
        }
    }
}