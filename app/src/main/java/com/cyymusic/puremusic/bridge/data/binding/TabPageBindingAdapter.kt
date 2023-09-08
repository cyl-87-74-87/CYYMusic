package com.cyymusic.puremusic.bridge.data.binding

import androidx.databinding.BindingAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.cyymusic.architecture.ui.adapter.CommonViewPagerAdapter
import com.cyymusic.puremusic.R

/**
 * TODO 一直编译不通过，各种错误，真正的原因是在这里哦，这里和布局建立绑定的呢
 * 注意：这个类的使用，居然是和fragment_player.xml里面的initTabAndPage挂钩
 */
object TabPageBindingAdapter {

    // MainFragment初始化页面的标记，初始化选项卡和页面
    @JvmStatic
    @BindingAdapter(value = ["initTabAndPage"], requireAll = false)
    fun initTabAndPage(tabLayout: TabLayout, initTabAndPage: Boolean) {
        if (initTabAndPage) {
            val count = tabLayout.tabCount
            val title = arrayOfNulls<String>(count)
            for (i in 0 until count) {
                val tab = tabLayout.getTabAt(i)
                if (tab != null && tab.text != null) {
                    title[i] = tab.text.toString()
                }
            }
            val viewPager: ViewPager = tabLayout.rootView.findViewById(R.id.view_pager)
            if (viewPager != null) {
                viewPager.adapter = CommonViewPagerAdapter(count, false, title)
                tabLayout.setupWithViewPager(viewPager)
            }
        }
    }
    // 在选项卡上添加选定的侦听器（备用的功能）
    @BindingAdapter(value = ["tabSelectedListener"], requireAll = false)
    fun tabSelectedListener(tabLayout: TabLayout, listener: OnTabSelectedListener?) {
        tabLayout.addOnTabSelectedListener(listener)
    }
}