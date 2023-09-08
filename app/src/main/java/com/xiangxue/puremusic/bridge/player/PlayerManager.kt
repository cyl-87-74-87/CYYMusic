package com.xiangxue.puremusic.player

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.kunminx.player.PlayerController
import com.kunminx.player.bean.dto.ChangeMusic
import com.kunminx.player.bean.dto.PlayingMusic
import com.kunminx.player.contract.IPlayController
import com.kunminx.player.contract.IServiceNotifier
import com.xiangxue.puremusic.data.bean.TestAlbum
import com.xiangxue.puremusic.data.bean.TestAlbum.TestMusic
import com.xiangxue.puremusic.player.notification.PlayerService

/**
 * 播放的管理器，由此类去调用，播放库来播放 等操作
 *
 * PlayerManager  ---> 播放库 播放 暂停 等等
 */
class PlayerManager private constructor() : IPlayController<TestAlbum, TestMusic?> {

    private val mController: PlayerController<TestAlbum, TestMusic>

    private var mContext: Context? = null

    fun init(context: Context) {
        init(context, null)
    }

    override fun init(context: Context, iServiceNotifier: IServiceNotifier?) {
        mContext = context.applicationContext
        mController.init(mContext, null) { startOrStop: Boolean ->
            val intent = Intent(mContext, PlayerService::class.java)
            if (startOrStop) {
                mContext ?.startService(intent) // 后台播放
            } else {
                mContext ?.stopService(intent) // 停止后台播放
            }
        }
    }

    override fun loadAlbum(musicAlbum: TestAlbum) {
        mController.loadAlbum(mContext, musicAlbum)
    }

    override fun loadAlbum(musicAlbum: TestAlbum, playIndex: Int) {
        mController.loadAlbum(mContext, musicAlbum, playIndex)
    }

    override fun playAudio() {
        mController.playAudio(mContext)
    }

    // 下标的 播放
    override fun playAudio(albumIndex: Int) {
        // 在这里只需要知道，调用此 playAudio函数，就能够播放音乐了
        mController.playAudio(mContext, albumIndex)
    }

    // 下一首播放
    override fun playNext() {
        mController.playNext(mContext)
    }

    // 上一首播放
    override fun playPrevious() {
        mController.playPrevious(mContext)
    }

    override fun playAgain() {
        mController.playAgain(mContext)
    }

    // 暂停 播放
    override fun pauseAudio() {
        mController.pauseAudio()
    }

    override fun resumeAudio() {
        mController.resumeAudio()
    }

    // 清除歌曲下标 的 标记
    override fun clear() {
        mController.clear(mContext)
    }

    override fun changeMode() {
        mController.changeMode()
    }

    override fun isPlaying(): Boolean {
        return mController.isPlaying
    }

    override fun isPaused(): Boolean {
        return mController.isPaused
    }

    override fun isInited(): Boolean {
        return mController.isInited
    }

    override fun requestLastPlayingInfo() {
        mController.requestLastPlayingInfo()
    }

    override fun setSeek(progress: Int) {
        mController.setSeek(progress)
    }

    override fun getTrackTime(progress: Int): String {
        return mController.getTrackTime(progress)
    }

    override fun getAlbum(): TestAlbum? {
        return mController.album
    }

    override fun getAlbumMusics(): List<TestMusic?> {
        return mController.albumMusics
    }

    override fun setChangingPlayingMusic(changingPlayingMusic: Boolean) {
        mController.setChangingPlayingMusic(mContext, changingPlayingMusic)
    }

    override fun getAlbumIndex(): Int {
        return mController.albumIndex
    }

    // 返回 音乐播放的 ld，上一首，下一首， 你只要敢改变 音乐，就可以被人家观察
    override fun getChangeMusicLiveData(): MutableLiveData<ChangeMusic<*, *, *>> {
        return mController.changeMusicLiveData
    }

    // 音乐数据，也需要观察
    override fun getPlayingMusicLiveData(): MutableLiveData<PlayingMusic<*, *>> {
        return mController.playingMusicLiveData
    }

    override fun getPauseLiveData(): MutableLiveData<Boolean> {
        return mController.pauseLiveData
    }

    // 同学们注意：播放模式：列表循环，单曲循环，随机播放
    override fun getPlayModeLiveData(): MutableLiveData<Enum<*>> {
        return mController.playModeLiveData
    }

    override fun getRepeatMode(): Enum<*> ? {
        return mController.repeatMode
    }

    override fun togglePlay() {
        mController.togglePlay(mContext)
    }

    override fun getCurrentPlayingMusic(): TestMusic {
        return mController.currentPlayingMusic
    }

    companion object {
        // 单例模式
        @SuppressLint("StaticFieldLeak")
        val instance = PlayerManager() // 单例相关的
    }

    init {
        mController = PlayerController()
    }
}