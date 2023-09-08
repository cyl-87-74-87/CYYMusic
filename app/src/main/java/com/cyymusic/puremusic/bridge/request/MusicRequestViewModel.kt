package com.cyymusic.puremusic.bridge.request

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cyymusic.puremusic.bridge.data.bean.AlbumModel
import com.cyymusic.puremusic.bridge.data.repository.HttpRequestManager
import com.cyymusic.puremusic.data.bean.TestAlbum

/**
 * 音乐资源 请求 相关的 ViewModel（只负责 MainFragment）
 */
class MusicRequestViewModel : ViewModel() {

    // by lazy 我想手写出 这个效果
    // val age by lazy { 88 }

    var freeMusicesLiveData : MutableLiveData<TestAlbum> ? = null
        get() {
            if (field == null) {
                field = MutableLiveData()
            }
            return field
        }
        private set
    var AlbumMusicesLiveData : MutableLiveData<List<AlbumModel>> ? = null
        get() {
            if (field == null) {
                field = MutableLiveData()
            }
            return field
        }
        private set
    fun requestFreeMusics() {
        //调用这个方法后freeMusicesliveData就设置了数据了
        HttpRequestManager.instance.getFreeMusic(freeMusicesLiveData)
    }
    fun requestAlbumMusics() {
        //调用这个方法后AlbumMusicesliveData就设置了数据了
        HttpRequestManager.instance.getAlumMusic(AlbumMusicesLiveData)
    }

}