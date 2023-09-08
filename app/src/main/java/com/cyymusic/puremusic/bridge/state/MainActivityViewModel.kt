package com.cyymusic.puremusic.bridge.state

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// MainActivity的ViewModel （布局）
class MainActivityViewModel : ViewModel() {

    /*
   @JvmField消除了变量的getter与setter方法
   @JvmField修饰的变量不能是private属性的
   @JvmStatic只能在object类或者伴生对象companion object中使用，而@JvmField没有这些限制
   @JvmStatic一般用于修饰方法，使方法变成真正的静态方法；如果修饰变量不会消除变量的getter与setter方法，但会使getter与setter方法和变量都变成静态
   */

    // 首页需要记录抽屉布局的情况 （响应的效果，都让 抽屉控件干了）
    @JvmField // @JvmField消除了变量的getter方法
    val openDrawer = MutableLiveData<Boolean>()  // isOpenDrawer="@{vm.openDrawer}" 打开抽屉 与 关闭抽屉

    // （响应的效果，都让 抽屉控件干了）
    @JvmField // @JvmField消除了变量的getter方法
    val allowDrawerOpen = MutableLiveData<Boolean>() // allowDrawerOpen="@{vm.allowDrawerOpen}" 允许抽屉打开 与 关闭

    // 构造代码块
    init {
        allowDrawerOpen.value = true // 给一个默认值而已
    }

}