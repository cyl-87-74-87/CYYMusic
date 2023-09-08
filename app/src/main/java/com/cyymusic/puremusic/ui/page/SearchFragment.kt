package com.cyymusic.puremusic.ui.page

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cyymusic.puremusic.R
import com.cyymusic.puremusic.bridge.request.DownloadViewModel
import com.cyymusic.puremusic.bridge.state.SearchViewModel
import com.cyymusic.puremusic.databinding.FragmentSearchBinding
import com.cyymusic.puremusic.ui.base.BaseFragment

/**
 * 搜索界面 的 Fragment
 */
class SearchFragment  : BaseFragment(){

    private var mBinding: FragmentSearchBinding? = null
    private var mSearchViewModel: SearchViewModel? = null // 搜索界面 相关的 VM  // todo Status ViewModel
    private var mDownloadViewModel: DownloadViewModel? = null  // 下载相关的 VM // todo Request ViewModel 额外 1
    // private 点赞的ViewModle vm; // 额外 2
    // private 点赞的ViewModle vm; // 额外 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDownloadViewModel = getActivityViewModelProvider(mActivity!!).get(DownloadViewModel::class.java)
        mSearchViewModel = getFragmentViewModelProvider(this).get(SearchViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_search, container, false)
        mBinding = FragmentSearchBinding.bind(view)
        mBinding ?.click = ClickProxy() // 设置监听
        mBinding ?.vm = mSearchViewModel // 设置 自身VM // todo Status ViewModel
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 第一种方式 （不需要生命周期观察 变化）
        /*mDownloadViewModel!!.downloadFileLiveData ?.addOnPropertyChangedCallback(object:
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                TODO("Not yet implemented")
            }
        })*/

        // 第二种方式 （不需要生命周期观察 变化）
        // 就是不需要Lifecycle观察 宿主生命周期变化
        mDownloadViewModel!!.downloadFileLiveData ?.observeForever {

            // 二次转化
            // 做成果的校验
            // 逻辑1
            // 逻辑2
            // 逻辑3
            // ....

            // 修改了 StatusViewModel
            // 让我自身的VM 数据发送变化, 那么布局就会感应变化
            mSearchViewModel!!.progress.set(it.progress)
        }

        /*mDownloadViewModel!!.downloadFileLiveData ?.observe(viewLifecycleOwner, { downloadFile ->

            // 修改了 StatusViewModel
            // 让我自身的VM 数据发送变化, 那么布局就会感应变化
            mSearchViewModel!!.progress.set(downloadFile.progress)
        })*/
         sharedViewModel.backSearchFragment.observe(viewLifecycleOwner){
             nav().navigateUp()
         }
    }

    inner class ClickProxy {

        private val PATH2 = "http://www.xiangxueketang.cn/" // 网页

        // 跳转 加载一个网页
        fun testNav()  = startPATH()

        // 跳转 加载一个网页
        fun subscribe() = startPATH()

        // 返回
        fun back() {
            nav().navigateUp() // back键的时候，返回上一个界面
        }

        private fun startPATH() {
            val uri = Uri.parse(PATH2)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        // 测试下载，返回页面依然有效
        fun testLifecycleDownload() {
            mDownloadViewModel ?.requestDownloadFile()
        }

        // 测试下载，返回页面依然有效
        fun testDownload() {
            mDownloadViewModel ?.requestDownloadFile()
        }
    }
}