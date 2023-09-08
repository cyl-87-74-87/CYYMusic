package com.cyymusic.puremusic.bridge.state

import android.graphics.drawable.Drawable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import com.kunminx.player.PlayingInfoManager
import com.cyymusic.architecture.utils.Utils
import com.cyymusic.puremusic.R
import com.cyymusic.puremusic.player.PlayerManager
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder

/**
 * 播放Fragment 的 PlayerViewModel
 *
 * 底部播放条的 UI数据管理层
 */
class PlayerViewModel : ViewModel() {

    // 为什么要使用 ObservableField   为什么不用LiveData
    // 不需要 LiveData 可见才 能观察的 能力，反而会导致我有bug，因为 我灭屏 可见 都要播放

    // 歌曲名称
    @JvmField // 剔除 getxxx函数
    val title = ObservableField<String>()

    // 歌手
    @JvmField
    val artist = ObservableField<String>()

    // 歌曲图片的地址  htpp:xxxx/img0.jpg
    @JvmField
    val coverImg = ObservableField<String>()

    // 歌曲正方形图片
    @JvmField
    val placeHolder = ObservableField<Drawable>()

    // 歌曲的总时长，会显示在拖动条后面
    @JvmField
    val maxSeekDuration = ObservableInt()

    // 当前拖动条的进度值
    @JvmField
    val currentSeekPosition = ObservableInt()

    // 播放按钮，状态的改变(播放和暂停)
    @JvmField
    val isPlaying = ObservableBoolean()

    // 这个是播放图标的状态，也都是属于状态的改变
    @JvmField
    val playModeIcon = ObservableField<MaterialDrawableBuilder.IconValue>()

    // 构造代码块，默认初始化
    init {
        title.set(Utils.getApp().getString(R.string.app_name)) // 默认信息
        artist.set(Utils.getApp().getString(R.string.app_name)) // 默认信息
        placeHolder.set(Utils.getApp().resources.getDrawable(R.drawable.bg_album_default)) // 默认的播放图标

        if (PlayerManager.instance.repeatMode === PlayingInfoManager.RepeatMode.LIST_LOOP) { // 如果等于“列表循环”
            playModeIcon.set(MaterialDrawableBuilder.IconValue.REPEAT)
        } else if (PlayerManager.instance.repeatMode === PlayingInfoManager.RepeatMode.ONE_LOOP) { // 如果等于“单曲循环”
            playModeIcon.set(MaterialDrawableBuilder.IconValue.REPEAT_ONCE)
        } else {
            playModeIcon.set(MaterialDrawableBuilder.IconValue.SHUFFLE) // 随机播放
        }
    }

}