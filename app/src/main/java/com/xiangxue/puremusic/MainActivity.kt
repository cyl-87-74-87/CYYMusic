package com.xiangxue.puremusic

import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.xiangxue.puremusic.bridge.data.config.Configs
import com.xiangxue.puremusic.bridge.state.MainActivityViewModel
import com.xiangxue.puremusic.databinding.ActivityMainBinding
import com.xiangxue.puremusic.ui.base.BaseActivity

// 主页  管理者 总控中心
class MainActivity : BaseActivity() {

    var mainBinding: ActivityMainBinding? = null // 当前MainActivity的布局
    var mainActivityViewModel: MainActivityViewModel? = null  // ViewModel
    private var isListened = false // 被倾听 为了扩展，目前还用不上

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 绑定DataBinding 与 VIewModel 结合
        mainActivityViewModel = getActivityViewModelProvider(this).get(MainActivityViewModel::class.java)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainBinding ?.lifecycleOwner = this
        mainBinding ?.setVm(mainActivityViewModel)

        // 共享ViewModel

        // 眼睛 1 监听
        // 共享 （观察） 活动关闭的一些记录（播放条 缩小一条 与 扩大展开）
        mSharedViewModel.activityCanBeClosedDirectly.observe(this) {
            // 先不写，作用不大，以后扩展用的
            Log.d(
                Configs.TAG,
                if (it) "中控中心 我知道了，原来播放条被收缩了" else "中控中心 我知道了，原来播放条被展开了"
            )
            // ... 业务逻辑的
        }

        // 眼睛 2 监听（发送 打开菜单的指令 1）
        mSharedViewModel.openOrCloseDrawer.observe(this) { aBoolean ->

            // 做很多很多的 过滤 检查 工作
            // ....

            mainActivityViewModel!!.openDrawer.value = aBoolean // 触发，就会改变 --> 观察（打开菜单）
        }

        // 眼睛 3 监听 （发送 打开菜单的指令 1）
        mSharedViewModel.enableSwipeDrawer.observe(this) { aBoolean ->

            // 做很多很多的 过滤 检查 工作
            // ....

            mainActivityViewModel!!.allowDrawerOpen.value = aBoolean // 触发抽屉控件关联的值
        }

    }

    /**
     * 详情看：https://www.cnblogs.com/lijunamneg/archive/2013/01/19/2867532.html
     * 这个onWindowFocusChanged指的是这个Activity得到或者失去焦点的时候 就会call。。
     * 也就是说 如果你想要做一个Activity一加载完毕，就触发什么的话 完全可以用这个！！！
     *  entry: onStart---->onResume---->onAttachedToWindow----------->onWindowVisibilityChanged--visibility=0---------->onWindowFocusChanged(true)------->
     * @param hasFocus
     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!isListened) {
            // 此字段只要发生了改变，就会 添加监听（可以弹上来的监听）
            mSharedViewModel.timeToAddSlideListener.value = true // 触发改变
            isListened = true // 被倾听 修改成true
        }
    }

    /**
     * https://www.jianshu.com/p/d54cd7a724bc
     * Android中在按下back键时会调用到onBackPressed()方法，
     * onBackPressed相对于finish方法，还做了一些其他操作，
     * 而这些操作涉及到Activity的状态，所以调用还是需要谨慎对待。
     */
    override fun onBackPressed() {
        // super.onBackPressed();
        // 如果把下面的代码注释掉，back键时，不会把播放详情给掉下来
        mSharedViewModel.closeSlidePanelIfExpanded.value = true // 触发改变 true 如果此时是 播放详情，会被掉下来
        mSharedViewModel.backListMusic.value = true// nav().navigateUp() // back键的时候，返回上一个界面
        mSharedViewModel.backSearchFragment.value = true
    }
}