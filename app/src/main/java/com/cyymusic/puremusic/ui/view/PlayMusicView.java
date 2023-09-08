package com.cyymusic.puremusic.ui.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import com.cyymusic.puremusic.R;
import com.cyymusic.puremusic.player.PlayerManager;

/**
 * 功能：播放中的打碟界面 的 自定义View
 */
public class PlayMusicView extends FrameLayout{
    // private MediaPlayerHelp mMediaPlayerHelp;
    private Context mContext; // 上下文环境
    private View mView; // 当前 自定义View 关联的 布局文件
    private boolean isPlaying; // 是否播放中，是否绑定服务中

    private ImageView mIvIcon, mIvNeedle, mIvPlay; // 中间圆形图片，指针杆，播放/暂停图标
    private FrameLayout rotationView; // 整个光盘

    //private TestAlbum.TestMusic mMusicModel; // 音乐本身实体
    private Intent myServiceIntent; // MusicService的Intent
    private Animation mMusicPlay, mStartNeedle, mStopNeedle; // 播放时旋转动画，播放时指针杆动画，停止时指针杆动画
//new
    public boolean isPlaying() {
        return isPlaying;
    }
    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }
    public void initView(Context context) {
        mContext = context;
        // 当前 自定义View 关联的 布局文件
        mView = LayoutInflater.from(mContext).inflate(R.layout.play_music, this, false);
        rotationView = mView.findViewById(R.id.Fl_playMusic); // 整个光盘
        mIvIcon = mView.findViewById(R.id.iv_icon); // 中间圆形图片
        mIvNeedle = mView.findViewById(R.id.iv_needle); // 指针杆
        mIvPlay = mView.findViewById(R.id.iv_play); // 播放/暂停图标
        //mMusicPlay = AnimationUtils.loadAnimation(mContext, R.anim.play_music_animation); // 播放时旋转动画
        mStartNeedle = AnimationUtils.loadAnimation(mContext, R.anim.play_needle_anim); // 播放时指针杆动画
        mStopNeedle = AnimationUtils.loadAnimation(mContext, R.anim.stop_needle_anim); // 停止时指针杆动画
        // 启动动画
        //点击事件  暂停音乐/播放音乐
        rotationView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // PlayerManager.Companion.getInstance().togglePlay();
                trigger(); // 点击后  暂停音乐/播放音乐
            }
        });
        addView(mView); // 把 “当前 自定义View 关联的 布局文件” 加入到 ViewGroup中去
    }

/*使用XML文件，定义了一个旋转动画效果。SlidingUpPanelLayout包裹fragment，fragment再包裹这个旋转视图时，这个视图的旋转中心点改变了
*原因可能是：布局嵌套的方式和每个布局容器的属性影响子视图的布局和旋转中心点。
*所以使用ViewPropertyAnimator来执行动态动画，该动画会自动考虑视图的布局和中心点
*
* */
    private void initRotationAnimator() {

            // 如果动画没有在播放，则启动动画
            // 设置初始角度
            float fromDegrees = 0;

            // 设置每次旋转的角度
            float toDegrees = 360;

            // 设置动画的持续时间（毫秒）
            int duration = getResources().getInteger(R.integer.play_music_disc_anim_duration);

            // 设置动画执行方式
           rotationView.animate()
                    .rotation(toDegrees)
                    .setDuration(duration)
                    .setInterpolator(new LinearInterpolator()) // 可以根据需要更改插值器
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            // 动画结束后执行的操作（如果需要）
                            // 在这里可以添加循环播放的逻辑
                            //!!动画结束时一定要角度重置为初始角度，并且立即开始下一个动画来实现连续旋转。
                            // 这种连续旋转方式可能导致一次动画结束时，下一个动画的开始立即被触发，从而导致看起来只旋转了两圈。
                                rotationView.setRotation(fromDegrees); // 重置角度
                                rotationView.animate()
                                        .rotationBy(toDegrees) // 继续旋转
                                        .setDuration(duration)
                                        .setInterpolator(new LinearInterpolator())
                                        .withEndAction(this); // 递归调用，实现无限循环

                        }
                    })
                    .start();
    }

    private void pauseRotationAnimator() {
           rotationView.animate().cancel(); // 清除动画
        //当调用pauseRotationAnimator()后，虽然使用rotationView.clearAnimation()来清除动画，但这并不会重置rotationView的旋转角度。
        //因此，当你再次调用initRotationAnimator()时，动画似乎没有启动的原因是因为rotationView的角度仍然处于上一次动画结束的状态，导致新的动画看起来没有任何变化。
           rotationView.setRotation(0); // 手动重置旋转角度为初始状态
    }
    private void restartRotationAnimator() {
        pauseRotationAnimator(); // 先停止之前的动画
        initRotationAnimator(); // 再启动新的动画
    }

    /**
     * 切换音乐状态
     */
    public void trigger() {

        if (isPlaying) {
            stopMusic(); // 停止音乐
        } else {
            playMusic(null); // 播放音乐
        }
    }

    /**
     * 播放音乐
     *
     * 被哪里调用： 本类上面的trigger函数调用，  外类PlayMusicActivity的initView函数调用
     *
     * @param toastContent 提示的内容
     */
    public void playMusic(String toastContent) {
        isPlaying = true; // 修改为，正在播放中的标记
        mIvPlay.setVisibility(View.GONE); // 播放/暂停 图标隐藏
        //mFrameLayout.startAnimation(mMusicPlay); // 给 整个光盘 启动 播放时旋转动画
        restartRotationAnimator();
        mIvNeedle.startAnimation(mStartNeedle); // 给 指针杆 启动 播放时指针杆动画
       // startMusicService(); // 启动音乐服务
        PlayerManager.Companion.getInstance().playAudio();
        if (null != toastContent) // 从外界进入播放中的打碟界面的 一个播放的 提示
            Toast.makeText(mContext, toastContent, Toast.LENGTH_SHORT).show();

        /*
        //        1.判断当前音乐是否是已经在播放的音乐
        //        2.如果是，那么执行start方法
        //        3.如果当前音乐不是，那么调用setPath方法
        if(mMediaPlayerHelp.getPath()!=null&&mMediaPlayerHelp.getPath().equals(path)){
            mMediaPlayerHelp.start();
        }
        else{
            mMediaPlayerHelp.setPath(path);
            mMediaPlayerHelp.setOnMeidaPlayerHelperListener(new MediaPlayerHelp.OnMeidaPlayerHelperListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayerHelp.start();
                }
            });
        }*/
    }


    /**
     * 停止音乐
     *
     * 被哪里调用：本类上面的trigger函数调用
     */
    public void stopMusic() {
        isPlaying = false; // 修改成停止播放标记
        mIvPlay.setVisibility(View.VISIBLE); // 暂停的图标 显示出来
        //mFrameLayout.clearAnimation(); // 整个光盘 所启动的动画，全部清除
        pauseRotationAnimator();
        mIvNeedle.startAnimation(mStopNeedle); // 指针杆 启动 停止时指针杆动画
       /* if (mMusicBind != null) {
            mMusicBind.stopMusic(); // 停止音乐
        }*/
        // mMediaPlayerHelp.pause();
        PlayerManager.Companion.getInstance().pauseAudio();

    }

    /**
     * 启动音乐服务
     */
    /*private void startMusicService() {
      *//*  // 启动Service
        if (myServiceIntent == null) {
            myServiceIntent = new Intent(mContext, MusicService.class);
            mContext.startService(myServiceIntent); // 第一次：一定是startService
        } else {
            mMusicBind.playMusic(); // 第二次：就会手动 playMusic 播放音乐
        }
        // 绑定Service:如果当前service未绑定的话，我们就给他绑定
        if (!isBindService) {
            isBindService = true; // 第一次会尽量，bind服务，注意：一旦 bindService 也会调用 playMusic
            mContext.bindService(myServiceIntent, conn, Context.BIND_AUTO_CREATE);
        }*//*

    }*/

    /**
     * 解除绑定
     */
   /* public void unbindService() {
        // 如果已经绑定了服务，解除绑定
        if (isBindService) {
            isBindService = false;
            mContext.unbindService(conn);
        }
    }*/

    /**
     * 设置 音乐本身实体Bean 进来
     *
     * 哪里调用的：类PlayMusicActivity的initView函数调用
     *
     * @param music
     */
    public void setMusic(String music) {
       /* PlayerManager.instance.changeMusicLiveData.observe(viewLifecycleOwner) { changeMusic ->

                // 例如 ：理解 切歌的时候， 音乐的标题，作者，封面 状态等 改变
                playerViewModel!!.title.set(changeMusic.title)
            playerViewModel!!.artist.set(changeMusic.summary)
            playerViewModel!!.coverImg.set(changeMusic.img)
        }*/
        // 设置光盘中显示的音乐封面图片
        //PlayerManager.Companion.getInstance().getChangeMusicLiveData().observe();
        Glide.with(mContext)
                .load(music)
                .into(mIvIcon);
    }

    // 连接MusicService的连接
    /*ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMusicBind = (MusicService.MusicBind) service;
            mMusicBind.setMusic(mMusicModel); // 把 音乐本身实体Bean 传递给 MusicService
            mMusicBind.playMusic(); // 在绑定MusicService的时候，就需要调用 playMusic 播放音乐
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {}
    };
*/

    // TODO ================================== 下面是自定义的 构造函数系列区域

    public PlayMusicView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public PlayMusicView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PlayMusicView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public PlayMusicView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }
}
