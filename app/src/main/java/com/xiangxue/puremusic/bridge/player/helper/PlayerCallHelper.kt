package com.xiangxue.puremusic.player.helper

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.media.MediaMetadataRetriever
import android.media.RemoteControlClient
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import com.xiangxue.puremusic.player.notification.PlayerReceiver

/**
 * 在来电时自动协调和暂停音乐播放
 * 只为 PlayerService 服务的
 */
@Suppress("DEPRECATION")
class PlayerCallHelper(private val mPlayerCallHelperListener: PlayerCallHelperListener?) : OnAudioFocusChangeListener {

    private var phoneStateListener: PhoneStateListener? = null  // 电话状态监听器
    private var remoteControlClient: RemoteControlClient? = null // 远程控制客户端
    private var mAudioManager: AudioManager? = null // 音频管理器
    private var ignoreAudioFocus = false // 忽略音频焦点
    private var mIsTempPauseByPhone = false // 是通过电话临时暂停
    private var tempPause = false // 暂停标记

    // 同学们，这里是绑定调用监听器
    fun bindCallListener(context: Context) {
        phoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, incomingNumber: String) {
                if (state == TelephonyManager.CALL_STATE_IDLE) { // 呼叫状态空闲
                    if (mIsTempPauseByPhone) {
                        mPlayerCallHelperListener?.playAudio() // 播放音频
                        mIsTempPauseByPhone = false
                    }
                } else if (state == TelephonyManager.CALL_STATE_RINGING) { // 呼叫状态振铃
                    if (mPlayerCallHelperListener != null) {
                        if (mPlayerCallHelperListener.isPlaying &&
                            !mPlayerCallHelperListener.isPaused
                        ) {
                            mPlayerCallHelperListener.pauseAudio()
                            mIsTempPauseByPhone = true
                        }
                    }
                } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) { // 呼叫状态摘机
                }
                super.onCallStateChanged(state, incomingNumber)
            }
        }
        // 下面是获取电话管理器，来建立设置好监听
        val manager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        manager?.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE) // 对电话监听，建立绑定关系
    }

    // 绑定电话相关的控制器
    fun bindRemoteController(context: Context) {
        mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val remoteComponentName = ComponentName(context, PlayerReceiver::class.java.name)
        try {
            if (remoteControlClient == null) {
                mAudioManager!!.registerMediaButtonEventReceiver(remoteComponentName)
                val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
                mediaButtonIntent.component = remoteComponentName
                val mediaPendingIntent = PendingIntent.getBroadcast(
                    context, 0, mediaButtonIntent, 0
                )
                remoteControlClient = RemoteControlClient(mediaPendingIntent)
                mAudioManager!!.registerRemoteControlClient(remoteControlClient)
            }
            remoteControlClient!!.setTransportControlFlags(
                RemoteControlClient.FLAG_KEY_MEDIA_PLAY
                        or RemoteControlClient.FLAG_KEY_MEDIA_PAUSE
                        or RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE
                        or RemoteControlClient.FLAG_KEY_MEDIA_STOP
                        or RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS
                        or RemoteControlClient.FLAG_KEY_MEDIA_NEXT
            )
        } catch (e: Exception) {
            Log.e("tmessages", e.toString())
        }
    }

    // 同学们，这里是解除绑定调用监听器
    fun unbindCallListener(context: Context) {
        try {
            val mgr = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            mgr?.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
        } catch (e: Exception) {
            Log.e("tmessages", e.toString())
        }
    }

    // 绑定电话相关的控制器
    fun unbindRemoteController() {
        if (remoteControlClient != null) {
            val metadataEditor = remoteControlClient!!.editMetadata(true)
            metadataEditor.clear()
            metadataEditor.apply()
            mAudioManager!!.unregisterRemoteControlClient(remoteControlClient)
            mAudioManager!!.abandonAudioFocus(this)
        }
    }

    // 获取音频信息进行关联
    fun requestAudioFocus(title: String?, summary: String?) {
        if (remoteControlClient != null) {
            val metadataEditor = remoteControlClient!!.editMetadata(true)
            metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, summary)
            metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, title)
            metadataEditor.apply()
            mAudioManager!!.requestAudioFocus(
                this,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
    }

    // 设置忽略音频焦点
    fun setIgnoreAudioFocus() {
        ignoreAudioFocus = true
    }

    // 音频焦点变化的函数 可以修改音频焦点
    override fun onAudioFocusChange(focusChange: Int) {
        if (ignoreAudioFocus) {
            ignoreAudioFocus = false
            return
        }
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            if (mPlayerCallHelperListener != null) {
                if (mPlayerCallHelperListener.isPlaying &&
                    !mPlayerCallHelperListener.isPaused
                ) {
                    mPlayerCallHelperListener.pauseAudio()
                    tempPause = true
                }
            }
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            if (tempPause) {
                mPlayerCallHelperListener?.playAudio()
                tempPause = false
            }
        }
    }

    // 电话监听呼叫器
    interface PlayerCallHelperListener {
        fun playAudio() // 播放音频中
        val isPlaying: Boolean // 是否播放
        val isPaused: Boolean // 是否暂停
        fun pauseAudio() // 暂停音频中
    }
}