package com.xiangxue.puremusic.player.notification

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.xiangxue.architecture.utils.ImageUtils
import com.xiangxue.puremusic.MainActivity
import com.xiangxue.puremusic.R
import com.xiangxue.puremusic.data.bean.TestAlbum.TestMusic
import com.xiangxue.puremusic.bridge.data.config.Configs;
import com.xiangxue.puremusic.player.PlayerManager
import com.xiangxue.puremusic.player.helper.PlayerCallHelper
import com.xiangxue.puremusic.player.helper.PlayerCallHelper.PlayerCallHelperListener
import java.io.File

/**
 * 音乐播放的服务
 * 后台音乐播放的服务
 */
@Suppress("DEPRECATION")
class PlayerService : Service() {
    private var mPlayerCallHelper: PlayerCallHelper? = null

    // private DownloadUseCase mDownloadUseCase;
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        // 在播放的时候来电，怎么办， 所有需要做出处理
        if (mPlayerCallHelper == null) {
            mPlayerCallHelper = PlayerCallHelper(object : PlayerCallHelperListener {
                override fun playAudio() {
                    PlayerManager.instance.playAudio()
                }

                override val isPlaying: Boolean
                    get() = PlayerManager.instance.isPlaying
                override val isPaused: Boolean
                    get() = PlayerManager.instance.isPaused

                override fun pauseAudio() {
                    PlayerManager.instance.pauseAudio()
                }
            })
        }
        val results = PlayerManager.instance.currentPlayingMusic
        if (results == null) {
            stopSelf()
            return START_NOT_STICKY
        }
        mPlayerCallHelper!!.bindCallListener(applicationContext)
        createNotification(results)
        return START_NOT_STICKY
    }

    // 创建通知
    private fun createNotification(testMusic: TestMusic) {
        try {
            val title = testMusic.title
            val album = PlayerManager.instance.album
            val summary = album !!.summary
            val simpleContentView = RemoteViews(
                applicationContext.packageName, R.layout.notify_player_small
            )
            val expandedView: RemoteViews
            expandedView = RemoteViews(
                applicationContext.packageName, R.layout.notify_player_big
            )
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.action = "showPlayer"
            val contentIntent = PendingIntent.getActivity(
                this, 0, intent, 0
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                val playGroup = NotificationChannelGroup(GROUP_ID, getString(R.string.play))
                notificationManager.createNotificationChannelGroup(playGroup)
                val playChannel = NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.notify_of_play), NotificationManager.IMPORTANCE_DEFAULT
                )
                playChannel.group = GROUP_ID
                notificationManager.createNotificationChannel(playChannel)
            }
            val notification = NotificationCompat.Builder(
                applicationContext, CHANNEL_ID
            )
                .setSmallIcon(R.drawable.ic_player)
                .setContentIntent(contentIntent)
                .setOnlyAlertOnce(true)
                .setContentTitle(title).build()
            notification.contentView = simpleContentView
            notification.bigContentView = expandedView
            setListeners(simpleContentView)
            setListeners(expandedView)
            notification.contentView.setViewVisibility(R.id.player_progress_bar, View.GONE)
            notification.contentView.setViewVisibility(R.id.player_next, View.VISIBLE)
            notification.contentView.setViewVisibility(R.id.player_previous, View.VISIBLE)
            notification.bigContentView.setViewVisibility(R.id.player_next, View.VISIBLE)
            notification.bigContentView.setViewVisibility(R.id.player_previous, View.VISIBLE)
            notification.bigContentView.setViewVisibility(R.id.player_progress_bar, View.GONE)
            val isPaused = PlayerManager.instance.isPaused
            notification.contentView.setViewVisibility(
                R.id.player_pause,
                if (isPaused) View.GONE else View.VISIBLE
            )
            notification.contentView.setViewVisibility(
                R.id.player_play,
                if (isPaused) View.VISIBLE else View.GONE
            )
            notification.bigContentView.setViewVisibility(
                R.id.player_pause,
                if (isPaused) View.GONE else View.VISIBLE
            )
            notification.bigContentView.setViewVisibility(
                R.id.player_play,
                if (isPaused) View.VISIBLE else View.GONE
            )
            notification.contentView.setTextViewText(R.id.player_song_name, title)
            notification.contentView.setTextViewText(R.id.player_author_name, summary)
            notification.bigContentView.setTextViewText(R.id.player_song_name, title)
            notification.bigContentView.setTextViewText(R.id.player_author_name, summary)
            notification.flags = notification.flags or Notification.FLAG_ONGOING_EVENT
            val coverPath = Configs.COVER_PATH + File.separator + testMusic.musicId + ".jpg"
            val bitmap = ImageUtils.getBitmap(coverPath)
            if (bitmap != null) {
                notification.contentView.setImageViewBitmap(R.id.player_album_art, bitmap)
                notification.bigContentView.setImageViewBitmap(R.id.player_album_art, bitmap)
            } else {
                requestAlbumCover(testMusic.coverImg, testMusic.musicId)
                notification.contentView.setImageViewResource(
                    R.id.player_album_art,
                    R.drawable.bg_album_default
                )
                notification.bigContentView.setImageViewResource(
                    R.id.player_album_art,
                    R.drawable.bg_album_default
                )
            }
            startForeground(5, notification)
            mPlayerCallHelper!!.bindRemoteController(applicationContext)
            mPlayerCallHelper!!.requestAudioFocus(title, summary)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 设置监听器
    fun setListeners(view: RemoteViews) {
        try {
            var pendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                0, Intent(NOTIFY_PREVIOUS).setPackage(packageName),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            view.setOnClickPendingIntent(R.id.player_previous, pendingIntent)
            pendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                0, Intent(NOTIFY_CLOSE).setPackage(packageName),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            view.setOnClickPendingIntent(R.id.player_close, pendingIntent)
            pendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                0, Intent(NOTIFY_PAUSE).setPackage(packageName),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            view.setOnClickPendingIntent(R.id.player_pause, pendingIntent)
            pendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                0, Intent(NOTIFY_NEXT).setPackage(packageName),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            view.setOnClickPendingIntent(R.id.player_next, pendingIntent)
            pendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                0, Intent(NOTIFY_PLAY).setPackage(packageName),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            view.setOnClickPendingIntent(R.id.player_play, pendingIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 请求专辑封面
    private fun requestAlbumCover(coverUrl: String, musicId: String) {
        /*if (mDownloadUseCase == null) {
            mDownloadUseCase = new DownloadUseCase();
        }

        UseCaseHandler.getInstance().execute(mDownloadUseCase,
                new DownloadUseCase.RequestValues(coverUrl, musicId + ".jpg"),
                new UseCase.UseCaseCallback<DownloadUseCase.ResponseValue>() {
                    @Override
                    public void onSuccess(DownloadUseCase.ResponseValue response) {
                        startService(new Intent(getApplicationContext(), PlayerService.class));
                    }

                    @Override
                    public void onError() {

                    }
                });*/
    }

    // 解绑监听等操作
    override fun onDestroy() {
        super.onDestroy()
        mPlayerCallHelper!!.unbindCallListener(applicationContext)
        mPlayerCallHelper!!.unbindRemoteController()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    // 此标记 与 广播接受者 AndroidManifest.xml 里面的标记保持一致
    companion object {
        const val NOTIFY_PREVIOUS = "pure_music.xiangxue_derry.previous"
        const val NOTIFY_CLOSE = "pure_music.xiangxue_derry.close"
        const val NOTIFY_PAUSE = "pure_music.xiangxue_derry.pause"
        const val NOTIFY_PLAY = "pure_music.xiangxue_derry.play"
        const val NOTIFY_NEXT = "pure_music.xiangxue_derry.next"
        private const val GROUP_ID = "group_001"
        private const val CHANNEL_ID = "channel_001"
    }
}