package com.xiangxue.puremusic.bridge.callback

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xiangxue.architecture.bridge.callback.UnPeekLiveData
import com.xiangxue.puremusic.bridge.data.login_register.Session

/**
 * SharedViewModel的职责是用于 页面通信的
 */
class SharedViewModel : ViewModel() {

    // 所有的字段，必须是 LiveData来管理，一般大部分情况下，要剔除黏性数据的功能
    // 所有ViewModel里面的字段，都是为了 驱动人家 干活

    // 添加监听（可以弹上来的监听）
    // 是为了 sliding.addPanelSlideListener(new PlayerSlideListener(mBinding, sliding));
    val timeToAddSlideListener = UnPeekLiveData<Boolean>()

    // 可以控制 播放详情 点击/back 掉下来
    // 播放详情中 左手边滑动图标(点击的时候)，与 MainActivity back 是 会set(true)
    //  ----> 如果是扩大，也就是 详情页面展示了出来，就会被掉下来
    val closeSlidePanelIfExpanded = UnPeekLiveData<Boolean>()
    //back键的时候，返回上一个界面 nav().navigateUp()
    val backListMusic = UnPeekLiveData<Boolean>()
    val backSearchFragment = UnPeekLiveData<Boolean>()
    // 活动关闭的一些记录（播放条 缩小一条 与 扩大展开） 通知给 控制者的（扩展的）
    val activityCanBeClosedDirectly = UnPeekLiveData<Boolean>()

    // openMenu打开菜单的时候会 set触发---> 改变 openDrawer.setValue(aBoolean); 的值
    val openOrCloseDrawer = UnPeekLiveData<Boolean>()

    // 开启和关闭 卡片相关的状态，如果发生改变 会和 allowDrawerOpen 挂钩 (后续的扩展 用的字段)
    val enableSwipeDrawer = UnPeekLiveData<Boolean>()

    // 保存登录信息的临时会话 需要贯穿整个项目，所以是共享的   UnPeekLiveData<Session>()
    val session = MutableLiveData<Session>()

    // 。。。。 扩展
}