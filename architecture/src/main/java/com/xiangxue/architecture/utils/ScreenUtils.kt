package com.xiangxue.architecture.utils

import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.util.DisplayMetrics
import android.view.Surface
import android.view.WindowManager
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity

/**
 * <pre>
 * author:
 * blog  :
 * time  :
 * desc  : utils derry
</pre> *
 */
class ScreenUtils private constructor() {

    companion object {
        /**
         * Return the width of screen, in pixel.
         *
         * @return the width of screen, in pixel
         */
        val screenWidth: Int
            get() {
                val wm = Utils.getApp().getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val point = Point()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    wm.defaultDisplay.getRealSize(point)
                } else {
                    wm.defaultDisplay.getSize(point)
                }
                return point.x
            }

        /**
         * Return the height of screen, in pixel.
         *
         * @return the height of screen, in pixel
         */
        val screenHeight: Int
            get() {
                val wm = Utils.getApp().getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val point = Point()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    wm.defaultDisplay.getRealSize(point)
                } else {
                    wm.defaultDisplay.getSize(point)
                }
                return point.y
            }

        /**
         * Return the density of screen.
         *
         * @return the density of screen
         */
        val screenDensity: Float
            get() = Resources.getSystem().displayMetrics.density

        /**
         * Return the screen density expressed as dots-per-inch.
         *
         * @return the screen density expressed as dots-per-inch
         */
        val screenDensityDpi: Int
            get() = Resources.getSystem().displayMetrics.densityDpi

        /**
         * Set full screen.
         *
         * @param activity The activity.
         */
        fun setFullScreen(activity: AppCompatActivity) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        /**
         * Set non full screen.
         *
         * @param activity The activity.
         */
        fun setNonFullScreen(activity: AppCompatActivity) {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        /**
         * Toggle full screen.
         *
         * @param activity The activity.
         */
        fun toggleFullScreen(activity: AppCompatActivity) {
            val fullScreenFlag = WindowManager.LayoutParams.FLAG_FULLSCREEN
            val window = activity.window
            if (window.attributes.flags and fullScreenFlag == fullScreenFlag) {
                window.clearFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                            or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )
            } else {
                window.addFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                            or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )
            }
        }

        /**
         * Return whether screen is full.
         *
         * @param activity The activity.
         * @return `true`: yes<br></br>`false`: no
         */
        fun isFullScreen(activity: AppCompatActivity): Boolean {
            val fullScreenFlag = WindowManager.LayoutParams.FLAG_FULLSCREEN
            return activity.window.attributes.flags and fullScreenFlag == fullScreenFlag
        }

        /**
         * Return whether screen is landscape.
         *
         * @return `true`: yes<br></br>`false`: no
         */
        val isLandscape: Boolean
            get() = (Utils.getApp().resources.configuration.orientation
                    == Configuration.ORIENTATION_LANDSCAPE)

        /**
         * Set the screen to landscape.
         *
         * @param activity The activity.
         */
        fun setLandscape(activity: AppCompatActivity) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        /**
         * Return whether screen is portrait.
         *
         * @return `true`: yes<br></br>`false`: no
         */
        val isPortrait: Boolean
            get() = (Utils.getApp().resources.configuration.orientation
                    == Configuration.ORIENTATION_PORTRAIT)

        /**
         * Set the screen to portrait.
         *
         * @param activity The activity.
         */
        fun setPortrait(activity: AppCompatActivity) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        /**
         * Return the rotation of screen.
         *
         * @param activity The activity.
         * @return the rotation of screen
         */
        @SuppressLint("SwitchIntDef")
        fun getScreenRotation(activity: AppCompatActivity): Int {
            return when (activity.windowManager.defaultDisplay.rotation) {
                Surface.ROTATION_90 -> 90
                Surface.ROTATION_180 -> 180
                Surface.ROTATION_270 -> 270
                else -> 0
            }
        }
        /**
         * Return the bitmap of screen.
         *
         * @param activity          The activity.
         * @param isDeleteStatusBar True to delete status bar, false otherwise.
         * @return the bitmap of screen
         */
        /**
         * Return the bitmap of screen.
         *
         * @param activity The activity.
         * @return the bitmap of screen
         */
        @JvmOverloads
        fun screenShot(activity: AppCompatActivity, isDeleteStatusBar: Boolean = false): Bitmap? {
            val decorView = activity.window.decorView
            decorView.isDrawingCacheEnabled = true
            decorView.setWillNotCacheDrawing(false)
            val bmp = decorView.drawingCache ?: return null
            val dm = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(dm)
            val ret: Bitmap
            ret = if (isDeleteStatusBar) {
                val resources = activity.resources
                val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
                val statusBarHeight = resources.getDimensionPixelSize(resourceId)
                Bitmap.createBitmap(
                    bmp,
                    0,
                    statusBarHeight,
                    dm.widthPixels,
                    dm.heightPixels - statusBarHeight
                )
            } else {
                Bitmap.createBitmap(bmp, 0, 0, dm.widthPixels, dm.heightPixels)
            }
            decorView.destroyDrawingCache()
            return ret
        }

        /**
         * Return whether screen is locked.
         *
         * @return `true`: yes<br></br>`false`: no
         */
        val isScreenLock: Boolean
            get() {
                val km =
                    Utils.getApp().getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                return km.inKeyguardRestrictedInputMode()
            }
        /**
         * Return the duration of sleep.
         *
         * @return the duration of sleep.
         */
        /**
         * Set the duration of sleep.
         *
         * Must hold `<uses-permission android:name="android.permission.WRITE_SETTINGS" />`
         *
         * @param duration The duration.
         */
        @set:RequiresPermission(permission.WRITE_SETTINGS)
        var sleepDuration: Int
            get() = try {
                Settings.System.getInt(
                    Utils.getApp().contentResolver,
                    Settings.System.SCREEN_OFF_TIMEOUT
                )
            } catch (e: SettingNotFoundException) {
                e.printStackTrace()
                -123
            }
            set(duration) {
                Settings.System.putInt(
                    Utils.getApp().contentResolver,
                    Settings.System.SCREEN_OFF_TIMEOUT,
                    duration
                )
            }

        /**
         * Return whether device is tablet.
         *
         * @return `true`: yes<br></br>`false`: no
         */
        val isTablet: Boolean
            get() = ((Utils.getApp().resources.configuration.screenLayout
                    and Configuration.SCREENLAYOUT_SIZE_MASK)
                    >= Configuration.SCREENLAYOUT_SIZE_LARGE)
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}