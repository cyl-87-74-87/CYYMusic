package com.cyymusic.puremusic.ui.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import com.kunminx.player.PlayingInfoManager
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.cyymusic.puremusic.R
import com.cyymusic.puremusic.bridge.state.PlayerViewModel
import com.cyymusic.puremusic.databinding.FragmentPlayerBinding
import com.cyymusic.puremusic.player.PlayerManager
import com.cyymusic.puremusic.ui.base.BaseFragment
import com.cyymusic.puremusic.ui.view.PlayerSlideListener
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder

// 播放画面的Fragment 真正显示
class PlayerFragment  : BaseFragment(){

    private var binding: FragmentPlayerBinding? = null
    private var playerViewModel: PlayerViewModel? = null // todo Status ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化 VM
        playerViewModel = getFragmentViewModelProvider(this).get<PlayerViewModel>(PlayerViewModel::class.java)
    }

    // DataBinding + ViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        // 加载界面
        val view: View = inflater.inflate(R.layout.fragment_player, container, false)

        // 绑定 Binding
        binding = FragmentPlayerBinding.bind(view)
        binding ?.click = ClickProxy() // 布局控制 点击事件的
        binding ?.event = EventHandler() // 布局控制 拖动条的
        binding ?.vm = playerViewModel // ViewModel与布局关联
        return view
    }

    // 观察变化 很多双眼睛
    // 观察到数据的变化，我就变化
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 观察：此字段只要发生了改变，就会 添加监听（可以弹上来的监听）
        sharedViewModel.timeToAddSlideListener.observe(viewLifecycleOwner) {

            if (view.parent.parent is SlidingUpPanelLayout) {

                val sliding = view.parent.parent as SlidingUpPanelLayout

                // 添加监听（可以弹上来的监听）
                sliding.addPanelSlideListener(PlayerSlideListener(binding!!, sliding))
                //binding!!.albumArt.visibility=View.VISIBLE
                //binding!!.albumArt1.visibility=View.GONE
            }
        }

        // 我是播放条，我要去变化，我成为观察者 ---> 播放相关的类 PlayerManager
        PlayerManager.instance.changeMusicLiveData.observe(viewLifecycleOwner) { changeMusic ->

            // 例如 ：理解 切歌的时候， 音乐的标题，作者，封面 状态等 改变
            playerViewModel!!.title.set(changeMusic.title)
            playerViewModel!!.artist.set(changeMusic.summary)
            playerViewModel!!.coverImg.set(changeMusic.img)
            //设置打碟中间的图片
            binding!!.albumArt.setMusic(changeMusic.img)
          //  binding!!.albumArt.setForegroundResource(resources.getDrawable(R.drawable.bg_album_default))
        }

        // 我是播放条，我要去变化，我成为观察者 -----> 播放相关的类PlayerManager
        PlayerManager.instance.playingMusicLiveData.observe(viewLifecycleOwner) { playingMusic ->

            // 例如 ：理解 切歌的时候，  播放进度的改变  按钮图标的改变
            playerViewModel!!.maxSeekDuration.set(playingMusic.duration) // 总时长
            playerViewModel!!.currentSeekPosition.set(playingMusic.playerPosition) // 拖动条
        }

        // 播放/暂停是一个控件  图标的true和false
        PlayerManager.instance.pauseLiveData.observe(viewLifecycleOwner) { aBoolean ->
            playerViewModel!!.isPlaying.set(!aBoolean!!) // 播放时显示暂停，暂停时显示播放
        }

        // 列表循环，单曲循环，随机播放 模式
        PlayerManager.instance.playModeLiveData.observe(viewLifecycleOwner) { anEnum ->
            val resultID: Int
            resultID = if (anEnum === PlayingInfoManager.RepeatMode.LIST_LOOP) { // 列表循环
                playerViewModel!!.playModeIcon.set(MaterialDrawableBuilder.IconValue.REPEAT)
                R.string.play_repeat // 列表循环
            } else if (anEnum === PlayingInfoManager.RepeatMode.ONE_LOOP) { // 单曲循环
                playerViewModel!!.playModeIcon.set(MaterialDrawableBuilder.IconValue.REPEAT_ONCE)
                R.string.play_repeat_once // 单曲循环
            } else { // 随机循环
                playerViewModel!!.playModeIcon.set(MaterialDrawableBuilder.IconValue.SHUFFLE)
                R.string.play_shuffle // 随机循环
            }

            // resultID // 成果

            // 提示改变
            if (view.parent.parent is SlidingUpPanelLayout) {
                val sliding = view.parent.parent as SlidingUpPanelLayout

                if (sliding.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) { // 张开状态
                    // 这里一定会弹出：“列表循环” or “单曲循环” or “随机播放”
                    showShortToast(resultID)
                }
            }
        }

        // 可以控制 播放详情 点击/back 掉下来
        // 例如：场景  back  要不要做什么事情
        sharedViewModel.closeSlidePanelIfExpanded.observe(viewLifecycleOwner) {
            if (view.parent.parent is SlidingUpPanelLayout) {
                val sliding = view.parent.parent as SlidingUpPanelLayout

                // 如果是扩大，也就是，详情页面展示出来的
                if (sliding.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    sliding.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED // 缩小了（掉下来了）
                    sharedViewModel.activityCanBeClosedDirectly.setValue(true) // 活动关闭的一些记录（播放条 缩小一条 与 扩大展开）
                } else {
                    sharedViewModel.activityCanBeClosedDirectly.setValue(false) // 活动关闭的一些记录（播放条 缩小一条 与 扩大展开）
                }
            } else {
                sharedViewModel.activityCanBeClosedDirectly.setValue(false) // 活动关闭的一些记录（播放条 缩小一条 与 扩大展开）
            }
        }
    }

    // 内部类的好处，方便获取 当前Fragment的 环境
    /*// 点击事件
    {
        sharedViewModel.closeSlidePanelIfExpanded = true
    }*/
    /**
     * 当我们点击的时候，我们要触发
     */
    inner class ClickProxy {

        /*public void playerMode() {
            PlayerManager.getInstance().changeMode();
        }*/

        fun previous() = PlayerManager.instance.playPrevious() // 上一首

        operator fun next() = PlayerManager.instance.playNext() // 下一首

        // 左手边的滑落，点击缩小的，可以控制 播放详情 点击/back 掉下来
        fun slideDown() = sharedViewModel.closeSlidePanelIfExpanded.setValue(true)

        //　更多的
        fun more() = Toast.makeText(mActivity, "你能不能不要乱点", Toast.LENGTH_SHORT).show()

        fun togglePlay() {
            PlayerManager.instance.togglePlay()
            //binding!!.albumArt.setIsPlaying(playerViewModel!!.isPlaying.get())
            binding!!.albumArt.trigger()
        }

        fun playMode() = PlayerManager.instance.changeMode() // 播放

        fun showPlayList() = showShortToast("最近播放的细节，我没有搞...")
    }

    // 内部类的好处，方便获取 当前Fragment的 环境
    /**
     * 专门更新 拖动条进度相关的
     */
    inner class EventHandler : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
        override fun onStartTrackingTouch(seekBar: SeekBar) {}

        // 一拖动 松开手  把当前进度值 告诉PlayerManager
        override fun onStopTrackingTouch(seekBar: SeekBar) = PlayerManager.instance.setSeek(seekBar.progress)
    }
}