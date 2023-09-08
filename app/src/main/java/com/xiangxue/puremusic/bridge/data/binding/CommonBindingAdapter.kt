package com.xiangxue.puremusic.bridge.data.binding

import android.R
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.xiangxue.architecture.utils.ClickUtils
import com.xiangxue.puremusic.ui.view.PlayMusicView

/**
 * TODO 一直编译不通过，各种错误，真正的原因是在这里哦，这里和布局建立绑定的呢
 * 注意：这个类的使用，居然是和 fragment_main.xml里面的onClickWithDebouncing 挂钩的
 */
@SuppressWarnings("unused")
object CommonBindingAdapter {

    // To use data binding annotations in Kotlin, apply the 'kotlin-kapt' plugin in your module's build.gradle
    // 要在 Kotlin 中使用数据绑定注释，请在模块的 build.gradle 中应用“kotlin-kapt”插件

    @JvmStatic
    @BindingAdapter(value = ["imageUrl", "placeHolder"], requireAll = false)
    fun loadUrl(view: ImageView, url: String?, placeHolder: Drawable?) {
        Glide.with(view.context).load(url).placeholder(placeHolder).into(view)
    }

    @BindingAdapter(value = ["visible"], requireAll = false)
    fun visible(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    @BindingAdapter(value = ["showDrawable", "drawableShowed"], requireAll = false)
    fun showDrawable(view: ImageView, showDrawable: Boolean, drawableShowed: Int) {
        view.setImageResource(if (showDrawable) drawableShowed else R.color.transparent)
    }

    @BindingAdapter(value = ["textColor"], requireAll = false)
    fun setTextColor(textView: TextView, textColorRes: Int) {
        textView.setTextColor(textView.resources.getColor(textColorRes))
    }

    @BindingAdapter(value = ["imageRes"], requireAll = false)
    fun setImageRes(imageView: ImageView, imageRes: Int) {
        imageView.setImageResource(imageRes)
    }

    @BindingAdapter(value = ["selected"], requireAll = false)
    fun selected(view: View, select: Boolean) {
        view.isSelected = select
    }

    @JvmStatic
    @BindingAdapter(value = ["onClickWithDebouncing"], requireAll = false)
    fun onClickWithDebouncing(view: View?, clickListener: View.OnClickListener?) {
        ClickUtils.applySingleDebouncing(view, clickListener)
    }
}

/*private fun <TranscodeType> RequestBuilder<TranscodeType>.into(view: FrameLayout) {
    TODO("Not yet implemented")
}*/

/*
private fun Any.into(view: FrameLayout) {

}
*/
