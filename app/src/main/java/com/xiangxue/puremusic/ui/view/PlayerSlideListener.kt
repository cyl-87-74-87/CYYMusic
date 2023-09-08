package com.xiangxue.puremusic.ui.view

import android.animation.ArgbEvaluator
import android.animation.FloatEvaluator
import android.animation.IntEvaluator
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Transformation
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState
import com.xiangxue.architecture.utils.DisplayUtils
import com.xiangxue.architecture.utils.ScreenUtils
import com.xiangxue.puremusic.R
import com.xiangxue.puremusic.databinding.FragmentPlayerBinding

/**
 * SlidingUpPanelLayout：可以上下滑动的菜单布
 * 可以认为是对：SlidingUpPanelLayout 进行了封装
 * 使用的详情可以看：https://www.jianshu.com/p/b6fb08a5b604
 */
class PlayerSlideListener(
    private val mBinding: FragmentPlayerBinding,
    private val mSlidingUpPanelLayout: SlidingUpPanelLayout
) : SlidingUpPanelLayout.PanelSlideListener {
    private val screenWidth: Int
    private val screenHeight: Int
    private val intEvaluator = IntEvaluator()
    private val floatEvaluator = FloatEvaluator()
    private val colorEvaluator = ArgbEvaluator()
    private val nowPlayingCardColor: Int
    private val playPauseDrawableColor: Int
    private var albumImageDrawable: Drawable? = null
    private var titleEndTranslationX = 0
    private var artistEndTranslationX = 0
    private var artistNormalEndTranslationY = 0
    private var artistFullEndTranslationY = 0
    private var contentNormalEndTranslationY = 0
    private var contentFullEndTranslationY = 0
    private var lyricLineHeight = 0
    private var lyricFullHeight = 0
    private var lyricLineStartTranslationY = 0
    private var lyricLineEndTranslationY = 0
    private var lyricFullTranslationY = 0
    private var markStartX = 0
    private var previousStartX = 0
    private var playPauseStartX = 0
    private var nextStartX = 0
    private var playqueueStartX = 0
    private var playPauseEndX = 0
    private var previousEndX = 0
    private var markEndX = 0
    private var nextEndX = 0
    private var playqueueEndX = 0
    private var iconContainerStartY = 0
    private var iconContainerEndY = 0
    private var mStatus = Status.COLLAPSED

    override fun onPanelSlide(panel: View, slideOffset: Float) {
        val params = mBinding.albumArt.layoutParams as CoordinatorLayout.LayoutParams

        //animate albumImage
        val tempImgSize = intEvaluator.evaluate(slideOffset, DisplayUtils.dp2px(55f), screenWidth)
        params.width = tempImgSize
        params.height = tempImgSize
        mBinding.albumArt.layoutParams = params

        //animate title and artist
        mBinding.title.translationX = floatEvaluator.evaluate(slideOffset, 0, titleEndTranslationX)
        mBinding.artist.translationX =
            floatEvaluator.evaluate(slideOffset, 0, artistEndTranslationX)
        mBinding.artist.translationY =
            floatEvaluator.evaluate(slideOffset, 0, artistNormalEndTranslationY)
        mBinding.summary.translationY =
            floatEvaluator.evaluate(slideOffset, 0, contentNormalEndTranslationY)

        //aniamte icons
        mBinding.playPause.x =
            intEvaluator.evaluate(slideOffset, playPauseStartX, playPauseEndX).toFloat()
        mBinding.playPause.setCircleAlpah(intEvaluator.evaluate(slideOffset, 0, 255))
        mBinding.playPause.drawableColor =
            colorEvaluator.evaluate(slideOffset, playPauseDrawableColor, nowPlayingCardColor) as Int
        mBinding.previous.x =
            intEvaluator.evaluate(slideOffset, previousStartX, previousEndX).toFloat()
        mBinding.mark.x = intEvaluator.evaluate(slideOffset, markStartX, markEndX).toFloat()
        mBinding.next.x = intEvaluator.evaluate(slideOffset, nextStartX, nextEndX).toFloat()
        mBinding.icPlayList.x =
            intEvaluator.evaluate(slideOffset, playqueueStartX, playqueueEndX).toFloat()
        mBinding.mark.alpha = floatEvaluator.evaluate(slideOffset, 0, 1)
        //
        mBinding.albumArt.alpha =floatEvaluator.evaluate(slideOffset,0,1)
        mBinding.previous.alpha = floatEvaluator.evaluate(slideOffset, 0, 1)
        mBinding.iconContainer.y =
            intEvaluator.evaluate(slideOffset, iconContainerStartY, iconContainerEndY).toFloat()
        val params1 = mBinding.summary.layoutParams as CoordinatorLayout.LayoutParams
        params1.height =
            intEvaluator.evaluate(slideOffset, DisplayUtils.dp2px(55f), DisplayUtils.dp2px(60f))
        mBinding.summary.layoutParams = params1

        //animate lyric view
        mBinding.lyricView.translationY =
            lyricLineStartTranslationY - (lyricLineStartTranslationY - lyricLineEndTranslationY) * slideOffset
    }

    override fun onPanelStateChanged(
        panel: View, previousState: PanelState,
        newState: PanelState
    ) {
        //.................................

        if (previousState == PanelState.COLLAPSED) {
            if (mBinding.songProgressNormal.visibility != View.INVISIBLE) {
                mBinding.songProgressNormal.visibility = View.INVISIBLE
            }
            if (mBinding.mark.visibility != View.VISIBLE) {
                mBinding.mark.visibility = View.VISIBLE
            }
            //...........
            if (mBinding.albumArt.visibility != View.VISIBLE) {
                mBinding.albumArt.visibility = View.VISIBLE
            }
            if (mBinding.albumArt1.visibility != View.INVISIBLE) {
                mBinding.albumArt1.visibility = View.INVISIBLE
            }

            if (mBinding.previous.visibility != View.VISIBLE) {
                mBinding.previous.visibility = View.VISIBLE
            }

        } else if (previousState == PanelState.EXPANDED) {
            if (mStatus == Status.FULLSCREEN) {
                animateToNormal()
            }

        }
        if (newState == PanelState.EXPANDED) {
            mStatus = Status.EXPANDED
            toolbarSlideIn(panel.context)
            mBinding.mark.isClickable = true
            mBinding.previous.isClickable = true
            //new
            /*if (mBinding.albumArt.visibility != View.VISIBLE) {
                mBinding.albumArt.visibility = View.VISIBLE
            }*/
            mBinding.topContainer.setOnClickListener { v: View? ->
                if (mStatus == Status.EXPANDED) {
                    animateToFullscreen()
                } else if (mStatus == Status.FULLSCREEN) {
                    animateToNormal()
                } else {
                    mSlidingUpPanelLayout.panelState = PanelState.COLLAPSED
                }
            }
        } else if (newState == PanelState.COLLAPSED) {
            mStatus = Status.COLLAPSED
            if (mBinding.songProgressNormal.visibility != View.VISIBLE) {
                mBinding.songProgressNormal.visibility = View.VISIBLE
            }
            if (mBinding.mark.visibility != View.GONE) {
                mBinding.mark.visibility = View.GONE
            }
            //new
            if (mBinding.albumArt.visibility != View.INVISIBLE) {
                mBinding.albumArt.visibility = View.INVISIBLE
            }
            //new
            if (mBinding.albumArt1.visibility != View.VISIBLE) {
                mBinding.albumArt1.visibility = View.VISIBLE
            }

            if (mBinding.previous.visibility != View.GONE) {
                mBinding.previous.visibility = View.GONE
            }
            mBinding.topContainer.setOnClickListener { v: View? ->
                if (mSlidingUpPanelLayout.isTouchEnabled) {
                    mSlidingUpPanelLayout.panelState = PanelState.EXPANDED
                }
            }
        } else if (newState == PanelState.DRAGGING) {
            if (mBinding.customToolbar.visibility != View.INVISIBLE) {
                mBinding.customToolbar.visibility = View.INVISIBLE
            }
            if (mBinding.lyricView.visibility != View.VISIBLE) {
                mBinding.lyricView.visibility = View.VISIBLE
            }
           /* //new
            if (mBinding.albumArt.visibility != View.INVISIBLE) {
                mBinding.albumArt.visibility = View.INVISIBLE
            }*/
        }
    }
//计算标题和艺术家文本的位置和动画效果
    private fun caculateTitleAndArtist() {
        val titleBounds = Rect()
        mBinding.title.paint.getTextBounds(
            mBinding.title.text.toString(), 0,
            mBinding.title.text.length, titleBounds
        )
        val titleWidth = titleBounds.width()
        val artistBounds = Rect()
        mBinding.artist.paint.getTextBounds(
            mBinding.artist.text.toString(), 0,
            mBinding.artist.text.length, artistBounds
        )
        val artistWidth = artistBounds.width()
        titleEndTranslationX = screenWidth / 2 - titleWidth / 2 - DisplayUtils.dp2px(67f)
        artistEndTranslationX = screenWidth / 2 - artistWidth / 2 - DisplayUtils.dp2px(67f)
        artistNormalEndTranslationY = DisplayUtils.dp2px(12f)
        artistFullEndTranslationY = 0
        contentNormalEndTranslationY = screenWidth + DisplayUtils.dp2px(32f)
        contentFullEndTranslationY = DisplayUtils.dp2px(32f)
        if (mStatus == Status.COLLAPSED) {
            mBinding.title.translationX = 0f
            mBinding.artist.translationX = 0f
        } else {
            mBinding.title.translationX = titleEndTranslationX.toFloat()
            mBinding.artist.translationX = artistEndTranslationX.toFloat()
        }
    }
//计算各个图标的位置和动画效果
    private fun caculateIcons() {
        markStartX = mBinding.mark.left
        previousStartX = mBinding.previous.left
        playPauseStartX = mBinding.playPause.left
        nextStartX = mBinding.next.left
        playqueueStartX = mBinding.icPlayList.left
        val size = DisplayUtils.dp2px(36f)
        val gap = (screenWidth - 5 * size) / 6
        playPauseEndX = screenWidth / 2 - size / 2
        previousEndX = playPauseEndX - gap - size
        markEndX = previousEndX - gap - size
        nextEndX = playPauseEndX + gap + size
        playqueueEndX = nextEndX + gap + size
        iconContainerStartY = mBinding.iconContainer.top
        iconContainerEndY =
            screenHeight - 3 * mBinding.iconContainer.height - mBinding.seekBottom.height
    }
//计算歌词视图的位置和动画效果
    private fun caculateLyricView() {
        val lyricFullMarginTop = (mBinding.customToolbar.top
                + mBinding.customToolbar.height + DisplayUtils.dp2px(32f))
        val lyricFullMarginBottom = (mBinding.iconContainer.bottom
                + mBinding.iconContainer.height + DisplayUtils.dp2px(32f))
        lyricLineHeight = DisplayUtils.dp2px(32f)
        lyricFullHeight = screenHeight - lyricFullMarginTop - lyricFullMarginBottom
        lyricLineStartTranslationY = screenHeight
        val gapBetweenArtistAndLyric =
            iconContainerEndY - contentNormalEndTranslationY - mBinding.summary.height
        lyricLineEndTranslationY =
            iconContainerEndY - gapBetweenArtistAndLyric / 2 - lyricLineHeight / 2
        lyricFullTranslationY = (mBinding.customToolbar.top
                + mBinding.customToolbar.height + DisplayUtils.dp2px(32f))
    }
//用于播放工具栏的滑入动画,滑动面板从折叠状态切换到展开状态时播放自定义工具栏的滑入动画。
    private fun toolbarSlideIn(context: Context) {
        mBinding.customToolbar.post {
            //创建一个滑入动画
            val animation = AnimationUtils.loadAnimation(context, R.anim.toolbar_slide_in)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    mBinding.customToolbar.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animation: Animation) {}
                override fun onAnimationRepeat(animation: Animation) {}
            })
            mBinding.customToolbar.startAnimation(animation)
        }
    }
//执行从展开状态切换到全屏状态的动画效果
    private fun animateToFullscreen() {
        //album art fullscreen
        //albumImageDrawable = mBinding.albumArt.drawab

        //animate title and artist
        val contentAnimation: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                mBinding.summary.translationY =
                    contentNormalEndTranslationY - (contentNormalEndTranslationY - contentFullEndTranslationY) * interpolatedTime
                mBinding.artist.translationY =
                    artistNormalEndTranslationY - (artistNormalEndTranslationY - artistFullEndTranslationY) * interpolatedTime
            }
        }
        contentAnimation.duration = 150
        mBinding.artist.startAnimation(contentAnimation)

        //animate lyric
        val lyricAnimation: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                val lyricLayout = mBinding.lyricView.layoutParams as CoordinatorLayout.LayoutParams
                lyricLayout.height =
                    (lyricLineHeight + (lyricFullHeight - lyricLineHeight) * interpolatedTime).toInt()
                mBinding.lyricView.layoutParams = lyricLayout
                mBinding.lyricView.translationY =
                    lyricLineEndTranslationY - (lyricLineEndTranslationY - lyricFullTranslationY) * interpolatedTime
            }
        }
        lyricAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                mBinding.lyricView.setHighLightTextColor(Color.BLACK)
                mBinding.lyricView.setPlayable(true)
                mBinding.lyricView.setTouchable(true)
                mBinding.lyricView.setOnClickListener { v: View? -> animateToNormal() }
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        lyricAnimation.duration = 150
        mBinding.lyricView.startAnimation(lyricAnimation)
        mStatus = Status.FULLSCREEN
    }
//执行从全屏状态切换回展开状态的动画效果
    private fun animateToNormal() {
        //album art
        val imageLayout = mBinding.albumArt.layoutParams as CoordinatorLayout.LayoutParams
        imageLayout.height = screenWidth
        imageLayout.width = screenWidth
        mBinding.albumArt.layoutParams = imageLayout

        //animate title and artist
        val contentAnimation: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                mBinding.summary.translationY =
                    contentFullEndTranslationY + (contentNormalEndTranslationY - contentFullEndTranslationY) * interpolatedTime
                mBinding.artist.translationY =
                    artistFullEndTranslationY + (artistNormalEndTranslationY - artistFullEndTranslationY) * interpolatedTime
            }
        }
        contentAnimation.duration = 300
        mBinding.artist.startAnimation(contentAnimation)

        //adjust lyricview
        val lyricAnimation: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                val layoutParams = mBinding.lyricView.layoutParams as CoordinatorLayout.LayoutParams
                layoutParams.height =
                    (lyricFullHeight - (lyricFullHeight - lyricLineHeight) * interpolatedTime).toInt()
                mBinding.lyricView.layoutParams = layoutParams
                mBinding.lyricView.translationY =
                    lyricFullTranslationY + (lyricLineEndTranslationY - lyricFullTranslationY) * interpolatedTime
            }
        }
        lyricAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                mBinding.lyricView.setPlayable(false)
                mBinding.lyricView.setHighLightTextColor(mBinding.lyricView.getDefaultColor())
                mBinding.lyricView.setTouchable(false)
                mBinding.lyricView.isClickable = false
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        lyricAnimation.duration = 300
        mBinding.lyricView.setPlayable(false)
        mBinding.lyricView.startAnimation(lyricAnimation)
        mStatus = Status.EXPANDED
    }

    enum class Status {
        EXPANDED, COLLAPSED, FULLSCREEN
    }

    init {
        screenWidth = ScreenUtils.screenWidth
        screenHeight = ScreenUtils.screenHeight
        playPauseDrawableColor = Color.BLACK
        nowPlayingCardColor = Color.WHITE
        caculateTitleAndArtist()
        caculateIcons()
        caculateLyricView()
        mBinding.playPause.drawableColor = playPauseDrawableColor
        mBinding.mark.isClickable = false
        mBinding.previous.isClickable = false
        //new
        /*mBinding.root.setOnClickListener { v ->
            // 在点击时切换 mBinding.albumArt 的可见性
            if (mBinding.albumArt.visibility == View.VISIBLE) {
                mBinding.albumArt.visibility = View.INVISIBLE
            } else {
                mBinding.albumArt.visibility = View.VISIBLE
            }
        }*/
    }
}