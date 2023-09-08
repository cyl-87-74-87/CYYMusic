package com.xiangxue.puremusic.ui.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import java.io.*
import java.text.DecimalFormat
import java.util.*

/**
 * 歌词相关的自定义View
 */
class LyricView(context: Context, attrs: AttributeSet ?, defStyleAttr : Int) : View(context, attrs, defStyleAttr) {

    // 按钮颜色
    private val mBtnColor = Color.parseColor("#EFEFEF")

    // 指示器颜色
    private val mIndicatorColor = Color.parseColor("#EFEFEF")

    // 当前拖动位置的颜色
    private val mCurrentShowColor = Color.parseColor("#AAAAAA")

    // 最低滑行启动速度
    private val mMinStartUpSpeed = 1600
    private val mDefaultMargin = 12
    private val mDefaultTime = "00:00"
    private val MSG_PLAYER_SLIDE = 0x158
    private val MSG_PLAYER_HIDE = 0x157

    /**
     * 计算阻尼效果的大小
     */
    private val mMaxDampingDistance = 360

    // 提示语颜色
    private var mHintColor = Color.parseColor("#666666")

    // 默认字体颜色
    private var mDefaultColor = Color.parseColor("#000000")

    // 当前播放位置的颜色
    private var mHighLightColor = Color.parseColor("#4FC5C7")

    // 行数
    private var mLineCount = 0

    // 行高
    private var mLineHeight = 0f

    // 纵轴偏移量
    private var mScrollY = 0f

    // 纵轴上的滑动速度
    private var mVelocity = 0f

    // 行间距（包含在行高中）
    private var mLineSpace = 0f

    // 渐变过渡的距离
    private var mShaderWidth = 0f

    // 当前拖动位置对应的行数
    private var mCurrentShowLine = 0

    // 当前播放位置对应的行数
    private var mCurrentPlayLine = 0

    // 判断当前用户是否触摸
    private var mUserTouch = false

    // 判断当前滑动指示器是否显示
    private var mIndicatorShow = false

    val postman: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_PLAYER_HIDE -> {
                    sendEmptyMessageDelayed(MSG_PLAYER_SLIDE, 1200)
                    mIndicatorShow = false
                    invalidateView()
                }
                MSG_PLAYER_SLIDE -> {
                    smoothScrollTo(measureCurrentScrollY(mCurrentPlayLine))
                    invalidateView()
                }
                else -> {
                }
            }
        }
    }

    // 判断用户触摸时是否发生move事件
    private var mIsMoved = false

    // 判断当前用户是否点击指示器
    private var mPlayerClick = false

    // Btn 按钮的宽度
    private var mBtnWidth = 0
    private var mBtnBound: Rect ? = null
    private var mTimerBound : Rect ? = null
    private var mVelocityTracker: VelocityTracker? = null
    private var mLyricInfo: LyricInfo? = null
    private var mDefaultHint = "暂无歌词"
    private var mTextPaint: Paint? = null
    private  var mBtnPaint:android.graphics.Paint? = null
    private  var mIndicatorPaint:android.graphics.Paint? = null
    private var mClickListener: OnPlayerClickListener? = null
    private var mFlingAnimator: ValueAnimator? = null
    private var mPlayable = false
    private var mSliding = false

    // 最大纵向滑动速度
    private var maximumFlingVelocity = 0
    private var mTouchable = true

    // 记录手指按下时的坐标和当前的滑动偏移量
    private var mDownX: Float = 0f
    private  var mDownY:kotlin.Float = 0f
    private  var mLastScrollY:kotlin.Float = 0f

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet ?) : this(context, attrs, 0)

    init {
        initMyView(context)
    }

    /**
     * 将解析得到的表示时间的字符转化为Long型
     */
    private fun measureStartTimeMillis(timeString: String): Long {
        //因为给如的字符串的时间格式为XX:XX.XX,返回的long要求是以毫秒为单位
        //将字符串 XX:XX.XX 转换为 XX:XX:XX
        var timeString = timeString
        timeString = timeString.replace('.', ':')
        //将字符串 XX:XX:XX 拆分
        val times = timeString.split(":".toRegex()).toTypedArray()
        // mm:ss:SS
        return (Integer.valueOf(times[0]) * 60 * 1000 + //分
                Integer.valueOf(times[1]) * 1000 +  //秒
                Integer.valueOf(times[2])).toLong() //毫秒
    }

    private fun initMyView(context: Context) {
        maximumFlingVelocity = ViewConfiguration.get(context).scaledMaximumFlingVelocity
        initAllPaints()
        initAllBounds()
    }

    /**
     * 初始化需要的尺寸
     */
    private fun initAllBounds() {
        setTextSize(15f)
        setLineSpace(12f)
        mBtnWidth = getRawSize(TypedValue.COMPLEX_UNIT_SP, 24f).toInt()
        mTimerBound = Rect()
        mIndicatorPaint!!.getTextBounds(mDefaultTime, 0, mDefaultTime.length, mTimerBound)
        measureLineHeight()
    }

    /**
     * 初始化画笔
     */
    private fun initAllPaints() {
        mTextPaint = Paint()
        mTextPaint ?.setDither(true)
        mTextPaint ?.setAntiAlias(true)
        mTextPaint ?.setTextAlign(Paint.Align.CENTER)
        mIndicatorPaint = Paint()
        mIndicatorPaint!!.isDither = true
        mIndicatorPaint!!.isAntiAlias = true
        mIndicatorPaint!!.textSize = getRawSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        mIndicatorPaint!!.textAlign = Paint.Align.CENTER
        mBtnPaint = Paint()
        mBtnPaint!!.isDither = true
        mBtnPaint!!.isAntiAlias = true
        mBtnPaint!!.color = mBtnColor
        mBtnPaint!!.strokeWidth = 3.0f
        mBtnPaint!!.style = Paint.Style.STROKE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mShaderWidth = measuredHeight * 0.3f
    }

    override fun onDraw(canvas: Canvas) {
        if (mLyricInfo != null && mLyricInfo ?.song_lines != null && mLyricInfo ?.song_lines!!.size > 0) {
            var i = 0
            val size = mLineCount
            while (i < size) {
                val x = measuredWidth * 0.5f
                val y =
                    measuredHeight * 0.5f + (i + 0.5f) * mLineHeight - 6 - mLineSpace * 0.5f - mScrollY
                if (y + mLineHeight * 0.5f < 0) {
                    i++
                    continue
                }
                if (y - mLineHeight * 0.5f > measuredHeight) {
                    break
                }
                if (i == mCurrentPlayLine - 1) {
                    mTextPaint!!.color = mHighLightColor
                } else {
                    if (mIndicatorShow && i == mCurrentShowLine - 1) {
                        mTextPaint!!.color = mCurrentShowColor
                    } else {
                        mTextPaint!!.color = mDefaultColor
                    }
                }
                if (y > measuredHeight - mShaderWidth || y < mShaderWidth) {
                    if (y < mShaderWidth) {
                        mTextPaint!!.alpha = 26 + (23000.0f * y / mShaderWidth * 0.01f).toInt()
                    } else {
                        mTextPaint!!.alpha =
                            26 + (23000.0f * (measuredHeight - y) / mShaderWidth * 0.01f).toInt()
                    }
                } else {
                    mTextPaint!!.alpha = 255
                }
                canvas.drawText(mLyricInfo ?.song_lines!![i].content!!, x, y, mTextPaint!!)
                i++
            }
        } else {
            mTextPaint!!.color = mHintColor
            canvas.drawText(
                mDefaultHint, measuredWidth * 0.5f, (measuredHeight + mLineHeight - 6) * 0.5f,
                mTextPaint!!
            )
        }
        // 滑动提示部分内容绘制
        if (mIndicatorShow && scrollable()) {
            if (mPlayable) {
                drawPlayer(canvas)
                drawIndicator(canvas)
            }
        }
    }

    /**
     * 绘制左侧的播放按钮
     *
     * @param canvas .
     */
    private fun drawPlayer(canvas: Canvas) {
        mBtnBound = Rect(
            mDefaultMargin,
            (measuredHeight * 0.5f - mBtnWidth * 0.5f).toInt(),
            mBtnWidth + mDefaultMargin,
            (measuredHeight * 0.5f + mBtnWidth * 0.5f).toInt()
        )
        val path = Path()
        val radio = mBtnBound !!.width() * 0.3f
        val value =
            Math.sqrt(Math.pow(radio.toDouble(), 2.0) - Math.pow((radio * 0.5f).toDouble(), 2.0))
                .toFloat()
        path.moveTo(mBtnBound !!.centerX() - radio * 0.5f, mBtnBound !!.centerY() - value)
        path.lineTo(mBtnBound !!.centerX() - radio * 0.5f, mBtnBound !!.centerY() + value)
        path.lineTo(mBtnBound !!.centerX() + radio, mBtnBound !!.centerY().toFloat())
        path.lineTo(mBtnBound !!.centerX() - radio * 0.5f, mBtnBound !!.centerY() - value)
        mBtnPaint!!.alpha = 128
        // 绘制播放按钮的三角形
        canvas.drawPath(path, mBtnPaint!!)
        // 绘制圆环
        canvas.drawCircle(
            mBtnBound !!.centerX().toFloat(), mBtnBound !!.centerY().toFloat(), mBtnBound !!.width() * 0.48f,
            mBtnPaint!!
        )
    }

    /**
     * 绘制指示器
     *
     * @param canvas .
     */
    private fun drawIndicator(canvas: Canvas) {
        mIndicatorPaint!!.color = mIndicatorColor
        mIndicatorPaint!!.alpha = 128
        mIndicatorPaint!!.style = Paint.Style.FILL
        canvas.drawText(
            measureCurrentTime()!!,
            (measuredWidth - mTimerBound!!.width()).toFloat(),
            (measuredHeight + mTimerBound!!.height() - 6) * 0.5f,
            mIndicatorPaint!!
        )
        val path = Path()
        mIndicatorPaint!!.strokeWidth = 2.0f
        mIndicatorPaint!!.style = Paint.Style.STROKE
        mIndicatorPaint!!.pathEffect = DashPathEffect(floatArrayOf(20f, 10f), 0f)
        path.moveTo((if (mPlayable) mBtnBound!!.right + 24 else 24.toFloat()) as Float, measuredHeight * 0.5f)
        path.lineTo(
            (measuredWidth - mTimerBound!!.width() - mTimerBound!!.width() - 36).toFloat(),
            measuredHeight * 0.5f
        )
        canvas.drawPath(path, mIndicatorPaint!!)
    }

    /**
     * 计算行高度
     */
    private fun measureLineHeight() {
        val lineBound = Rect()
        mTextPaint!!.getTextBounds(mDefaultHint, 0, mDefaultHint.length, lineBound)
        mLineHeight = lineBound.height() + mLineSpace
    }

    /**
     * 获取当前滑动到的位置的当前时间
     */
    private fun measureCurrentTime(): String? {
        val format = DecimalFormat("00")
        if (mLyricInfo != null && mLineCount > 0 && mCurrentShowLine - 1 < mLineCount && mCurrentShowLine > 0) {
            return format.format(mLyricInfo ?.song_lines!![mCurrentShowLine - 1].start / 1000 / 60) + ":" + format.format(
                mLyricInfo ?.song_lines!![mCurrentShowLine - 1].start / 1000 % 60
            )
        }
        if (mLyricInfo != null && mLineCount > 0 && mCurrentShowLine - 1 >= mLineCount) {
            return format.format(mLyricInfo ?.song_lines!![mLineCount - 1].start / 1000 / 60) + ":" + format.format(
                mLyricInfo ?.song_lines!![mLineCount - 1].start / 1000 % 60
            )
        }
        return if (mLyricInfo != null && mLineCount > 0 && mCurrentShowLine - 1 <= 0) {
            format.format(mLyricInfo ?.song_lines!![0].start / 1000 / 60) + ":" + format.format(
                mLyricInfo ?.song_lines!![0].start / 1000 % 60
            )
        } else mDefaultTime
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> parent.requestDisallowInterceptTouchEvent(
                true
            )
            else -> {
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!mTouchable) {
            return super.onTouchEvent(event)
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(event)
        when (event.action) {
            MotionEvent.ACTION_CANCEL -> actionCancel(event)
            MotionEvent.ACTION_DOWN -> actionDown(event)
            MotionEvent.ACTION_MOVE -> actionMove(event)
            MotionEvent.ACTION_UP -> actionUp(event)
            else -> {
            }
        }
        invalidateView()
        return if (mIsMoved || mPlayerClick) {
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    /**
     * 手势取消执行事件
     *
     * @param event .
     */
    private fun actionCancel(event: MotionEvent) {
        releaseVelocityTracker()
    }

    /**
     * 手势按下执行事件
     *
     * @param event .
     */
    private fun actionDown(event: MotionEvent) {
        postman.removeMessages(MSG_PLAYER_SLIDE)
        postman.removeMessages(MSG_PLAYER_HIDE)
        mLastScrollY = mScrollY
        mDownX = event.x
        mDownY = event.y
        if (mFlingAnimator != null) {
            mFlingAnimator ?.cancel()
            mFlingAnimator = null
        }
        setUserTouch(true)
        mIsMoved = false
        mPlayerClick = false
    }

    /**
     * 手势移动执行事件
     *
     * @param event .
     */
    private fun actionMove(event: MotionEvent) {
        if (scrollable()) {
            val tracker = mVelocityTracker!!
            tracker.computeCurrentVelocity(1000, maximumFlingVelocity.toFloat())
            // 102  -2  58  42
            val scrollY = mLastScrollY + mDownY - event.y
            // 52  -52  8  -8
            val value01 = scrollY - mLineCount * mLineHeight * 0.5f
            // 2  2  -42  -42
            val value02 = Math.abs(value01) - mLineCount * mLineHeight * 0.5f
            mScrollY =
                if (value02 > 0) scrollY - measureDampingDistance(value02) * value01 / Math.abs(
                    value01
                ) else scrollY //   value01 / Math.abs(value01)  控制滑动方向
            mVelocity = tracker.yVelocity
            measureCurrentLine()
            if (Math.abs(mVelocity) > 1) {
                mIsMoved = true
            }
        }
    }

    private fun measureDampingDistance(value02: Float): Float {
        return if (value02 > mMaxDampingDistance) mMaxDampingDistance * 0.6f + (value02 - mMaxDampingDistance) * 0.72f else value02 * 0.6f
    }

    /**
     * 手势抬起执行事件
     */
    private fun actionUp(event: MotionEvent) {
        releaseVelocityTracker()
        // 2.4s 后发送一个指示器隐藏的请求
        postman.sendEmptyMessageDelayed(MSG_PLAYER_HIDE, 2400)
        if (scrollable()) {
            // 用户手指离开屏幕，取消触摸标记
            setUserTouch(false)
            if (overScrolled() && mScrollY < 0) {
                smoothScrollTo(0f)
                return
            }
            if (overScrolled() && mScrollY > mLineHeight * (mLineCount - 1)) {
                smoothScrollTo(mLineHeight * (mLineCount - 1))
                return
            }
            if (Math.abs(mVelocity) > mMinStartUpSpeed) {
                doFlingAnimator(mVelocity)
                return
            }
            if (mIndicatorShow && clickPlayer(event)) {
                if (mCurrentShowLine != mCurrentPlayLine) {
                    mIndicatorShow = false
                    mPlayerClick = true
                    mClickListener?.onPlayerClicked(
                        mLyricInfo!!.song_lines!![mCurrentShowLine - 1].start,
                        mLyricInfo!!.song_lines!![mCurrentShowLine - 1].content
                    )
                }
            }
        } else {
            performClick()
        }
    }

    /**
     * 刷新View
     */
    private fun invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            //  当前线程是主UI线程，直接刷新。
            invalidate()
        } else {
            //  当前线程是非UI线程，post刷新。
            postInvalidate()
        }
    }

    /**
     * 设置用户是否触摸的标记
     *
     * @param isUserTouch 标记用户是否触摸屏幕
     */
    private fun setUserTouch(isUserTouch: Boolean) {
        if (mUserTouch == isUserTouch) {
            return
        }
        mUserTouch = isUserTouch
        if (isUserTouch) {
            mIndicatorShow = isUserTouch
        }
    }

    /**
     * 释放速度追踪器
     */
    private fun releaseVelocityTracker() {
        if (null != mVelocityTracker) {
            mVelocityTracker ?.clear()
            mVelocityTracker ?.recycle()
            mVelocityTracker = null
        }
    }

    /**
     * 滑行动画
     *
     * @param velocity 滑动速度
     */
    private fun doFlingAnimator(velocity: Float) {
        //注：Math.abs(velocity)  < =  16000
        // 计算就已当前的滑动速度理论上的滑行距离是多少
        val distance = velocity / Math.abs(velocity) * Math.min(Math.abs(velocity) * 0.050f, 640f)
        // 综合考虑边界问题后得出的实际滑行距离
        val to = Math.min(Math.max(0f, mScrollY - distance), (mLineCount - 1) * mLineHeight)
        mFlingAnimator = ValueAnimator.ofFloat(mScrollY, to)
        mFlingAnimator ?.addUpdateListener(AnimatorUpdateListener { animation: ValueAnimator ->
            mScrollY = animation.animatedValue as Float
            measureCurrentLine()
            invalidateView()
        })
        mFlingAnimator ?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                mVelocity = (mMinStartUpSpeed - 1).toFloat()
                mSliding = true
            }

            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                mSliding = false
            }

            override fun onAnimationCancel(animation: Animator) {
                super.onAnimationCancel(animation)
            }
        })
        mFlingAnimator ?.setDuration(420)
        mFlingAnimator ?.setInterpolator(DecelerateInterpolator())
        mFlingAnimator ?.start()
    }

    /**
     * To measure current showing line number based on the view's scroll Y
     */
    private fun measureCurrentLine() {
        val baseScrollY = mScrollY + mLineHeight * 0.5f
        mCurrentShowLine = (baseScrollY / mLineHeight + 1).toInt()
    }

    /**
     * Input current showing line to measure the view's current scroll Y
     *
     * @param line 当前指定行号
     */
    private fun measureCurrentScrollY(line: Int): Float {
        return (line - 1) * mLineHeight
    }

    /**
     * 判断当前点击事件是否落在播放按钮触摸区域范围内
     *
     * @param event 触摸事件
     */
    private fun clickPlayer(event: MotionEvent): Boolean {
        if (mBtnBound != null && mDownX > mBtnBound !!.left - mDefaultMargin && mDownX < mBtnBound !!.right + mDefaultMargin && mDownY > mBtnBound !!.top - mDefaultMargin && mDownY < mBtnBound !!.bottom + mDefaultMargin) {
            val upX = event.x
            val upY = event.y
            return upX > mBtnBound !!.left - mDefaultMargin && upX < mBtnBound !!.right + mDefaultMargin && upY > mBtnBound !!.top - mDefaultMargin && upY < mBtnBound !!.bottom + mDefaultMargin
        }
        return false
    }

    /**
     * 从当前位置滑动到指定位置上
     *
     * @param toY 指定纵坐标位置
     */
    private fun smoothScrollTo(toY: Float) {
        val animator = ValueAnimator.ofFloat(mScrollY, toY)
        animator.addUpdateListener { animation: ValueAnimator ->
            if (mUserTouch) {
                animator.cancel()
                return@addUpdateListener
            }
            mScrollY = animation.animatedValue as Float
            invalidateView()
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                mSliding = true
            }

            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                mSliding = false
                measureCurrentLine()
                invalidateView()
            }
        })
        animator.duration = 640
        animator.interpolator = OvershootInterpolator(0.5f)
        animator.start()
    }

    /**
     * 判断是否可以进行滑动
     */
    private fun scrollable(): Boolean {
        return mLyricInfo != null && mLyricInfo !!.song_lines != null && mLyricInfo ?.song_lines!!.size > 0
    }

    /**
     * 判断当前View是否已经滑动到歌词的内容区域之外
     */
    private fun overScrolled(): Boolean {
        return scrollable() && (mScrollY > mLineHeight * (mLineCount - 1) || mScrollY < 0)
    }

    /**
     * 根据当前给定的时间戳滑动到指定位置
     *
     * @param time 时间戳
     */
    private fun scrollToCurrentTimeMillis(time: Long) {
        var position = 0
        if (scrollable()) {
            var i = 0
            val size = mLineCount
            while (i < size) {
                val lineInfo = mLyricInfo!!.song_lines!![i]
                if (lineInfo != null && lineInfo.start > time) {
                    position = i
                    break
                }
                if (i == mLineCount - 1) {
                    position = mLineCount
                }
                i++
            }
        }
        if (mCurrentPlayLine != position && !mUserTouch && !mSliding && !mIndicatorShow) {
            mCurrentPlayLine = position
            smoothScrollTo(measureCurrentScrollY(position))
        } else {
            if (!mSliding && !mIndicatorShow) {
                mCurrentShowLine = position
                mCurrentPlayLine = mCurrentShowLine
            }
        }
    }

    /**
     * 初始化歌词信息
     *
     * @param inputStream 歌词文件的流信息
     */
    private fun setupLyricResource(inputStream: InputStream?, charsetName: String) {
        if (inputStream != null) {
            try {
                val lyricInfo = LyricInfo()
                lyricInfo.song_lines = ArrayList()
                val inputStreamReader = InputStreamReader(inputStream, charsetName)
                val reader = BufferedReader(inputStreamReader)
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    analyzeLyric(lyricInfo, line)
                }
                //歌词排序
                Collections.sort(lyricInfo.song_lines, sort())
                reader.close()
                inputStream.close()
                inputStreamReader.close()
                mLyricInfo = lyricInfo
                mLineCount = mLyricInfo !!.song_lines!!.size
                invalidateView()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            mDefaultHint = "暂无歌词"
            invalidateView()
        }
    }

    /**
     * 逐行解析歌词内容
     */
    private fun analyzeLyric(lyricInfo: LyricInfo, line: String?) {
        val index = line!!.indexOf("]")
        if (line != null && line.startsWith("[offset:")) {
            // 时间偏移量
            val string = line.substring(8, index).trim { it <= ' ' }
            lyricInfo.song_offset = string.toLong()
            return
        }
        if (line != null && line.startsWith("[ti:")) {
            // title 标题
            lyricInfo.song_title = line.substring(4, index).trim { it <= ' ' }
            return
        }
        if (line != null && line.startsWith("[ar:")) {
            // artist 作者
            lyricInfo.song_artist = line.substring(4, index).trim { it <= ' ' }
            return
        }
        if (line != null && line.startsWith("[al:")) {
            // album 所属专辑
            lyricInfo.song_album = line.substring(4, index).trim { it <= ' ' }
            return
        }
        if (line != null && line.startsWith("[by:")) {
            return
        }
        if (line != null && index == 9 && line.trim { it <= ' ' }.length > 10) {
            // 歌词内容,需要考虑一行歌词有多个时间戳的情况
            val lastIndexOfRightBracket = line.lastIndexOf("]")
            val content = line.substring(lastIndexOfRightBracket + 1)
            val times =
                line.substring(0, lastIndexOfRightBracket + 1).replace("[", "-").replace("]", "-")
            val arrTimes = times.split("-".toRegex()).toTypedArray()
            for (temp in arrTimes) {
                if (temp.trim { it <= ' ' }.length == 0) {
                    continue
                }
                /* [02:34.14][01:07.00]当你我不小心又想起她
                 *
                 上面的歌词的就可以拆分为下面两句歌词了
                 [02:34.14]当你我不小心又想起她
                 [01:07.00]当你我不小心又想起她
                 */
                val lineInfo = LineInfo()
                lineInfo.content = content
                lineInfo.start = measureStartTimeMillis(temp)
                lyricInfo.song_lines!!.add(lineInfo)
            }
        }
    }

    /**
     * 重置歌词内容
     */
    private fun resetLyricInfo() {
        if (mLyricInfo != null) {
            if (mLyricInfo !!.song_lines != null) {
                mLyricInfo !!.song_lines!!.clear()
                mLyricInfo !!.song_lines = null
            }
            mLyricInfo = null
        }
    }

    /**
     * 初始化控件
     */
    private fun resetView() {
        mCurrentShowLine = 0
        mCurrentPlayLine = mCurrentShowLine
        resetLyricInfo()
        invalidateView()
        mLineCount = 0
        mScrollY = 0f
    }

    private fun setRawTextSize(size: Float) {
        if (size != mTextPaint!!.textSize) {
            mTextPaint!!.textSize = size
            measureLineHeight()
            mScrollY = measureCurrentScrollY(mCurrentPlayLine)
            invalidateView()
        }
    }

    private fun getRawSize(unit: Int, size: Float): Float {
        val context = context
        val resources: Resources
        resources = if (context == null) {
            Resources.getSystem()
        } else {
            context.resources
        }
        return TypedValue.applyDimension(unit, size, resources.displayMetrics)
    }

    /**
     * 设置当前时间显示位置
     *
     * @param current 时间戳
     */
    fun setCurrentTimeMillis(current: Long) {
        scrollToCurrentTimeMillis(current)
    }

    /**
     * 设置歌词文件
     *
     * @param file        歌词文件
     * @param charsetName 解析字符集
     */
    fun setLyricFile(file: File?, charsetName: String) {
        if (file != null && file.exists()) {
            try {
                setupLyricResource(FileInputStream(file), charsetName)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        } else {
            mDefaultHint = "暂无歌词"
            invalidateView()
        }
    }

    /**
     * 设置播放按钮点击监听事件
     *
     * @param mClickListener 监听器
     */
    fun setOnPlayerClickListener(mClickListener: OnPlayerClickListener?) {
        this.mClickListener = mClickListener
    }

    /**
     * 重置、设置歌词内容被重置后的提示内容
     *
     * @param message 提示内容
     */
    fun reset(message: String) {
        mDefaultHint = message
        resetView()
    }


    /*
     * ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ *
     *                                                                                             对外API                                                                                        *
     * ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ *
     * */

    /*
     * ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ *
     *                                                                                             对外API                                                                                        *
     * ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ *
     * */
    /**
     * 设置高亮显示文本的字体颜色
     *
     * @param color 颜色值
     */
    fun setHighLightTextColor(color: Int) {
        if (mHighLightColor != color) {
            mHighLightColor = color
            invalidateView()
        }
    }

    fun getDefaultColor(): Int {
        return mDefaultColor
    }

    fun setDefaultColor(color: Int) {
        if (mDefaultColor != color) {
            mDefaultColor = color
            invalidateView()
        }
    }

    /**
     * 设置歌词内容行间距
     *
     * @param lineSpace 行间距大小
     */
    fun setLineSpace(lineSpace: Float) {
        if (mLineSpace != lineSpace) {
            mLineSpace = getRawSize(TypedValue.COMPLEX_UNIT_SP, lineSpace)
            measureLineHeight()
            mScrollY = measureCurrentScrollY(mCurrentPlayLine)
            invalidateView()
        }
    }

    /**
     * 设置歌词文本内容字体大小
     *
     * @param unit .
     * @param size .
     */
    fun setTextSize(unit: Int, size: Float) {
        setRawTextSize(getRawSize(unit, size))
    }

    /**
     * 设置歌词文本内容字体大小
     *
     * @param size .
     */
    fun setTextSize(size: Float) {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
    }

    fun setPlayable(playable: Boolean) {
        mPlayable = playable
    }

    fun setTouchable(touchable: Boolean) {
        mTouchable = touchable
    }

    fun setHintColor(color: Int) {
        if (mHintColor != color) {
            mHintColor = color
            invalidate()
        }
    }

    interface OnPlayerClickListener {
        fun onPlayerClicked(progress: Long, content: String?)
    }

    internal class LyricInfo {
        var song_lines: MutableList<LineInfo>? = null
        var song_artist // 歌手
                : String? = null
        var song_title // 标题
                : String? = null
        var song_album // 专辑
                : String? = null
        var song_offset // 偏移量
                : Long = 0
    }

    internal class LineInfo {
        var content // 歌词内容
                : String? = null
        var start // 开始时间
                : Long = 0
    }

    internal class sort : Comparator<LineInfo?> {
        override fun compare(lrc: LineInfo?, lrc2: LineInfo?): Int {
            return java.lang.Long.compare(lrc!!.start, lrc2!!.start)
        }
    }
}