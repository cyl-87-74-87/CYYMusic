package com.cyymusic.puremusic.ui.page

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cyymusic.puremusic.R
import com.cyymusic.puremusic.bridge.request.MusicRequestViewModel
import com.cyymusic.puremusic.data.bean.TestAlbum
import com.cyymusic.puremusic.databinding.AdapterPlayItemBinding
import com.cyymusic.puremusic.databinding.FragmentListBinding
import com.cyymusic.puremusic.player.PlayerManager
import com.cyymusic.puremusic.ui.adapter.SimpleBaseBindingAdapter
import com.cyymusic.puremusic.ui.base.BaseFragment

class ListFragment : BaseFragment() {
    private var mainBinding:FragmentListBinding ? = null
    private var adapter: SimpleBaseBindingAdapter<TestAlbum.TestMusic?, AdapterPlayItemBinding?>? = null
    private var musicRequestViewModel: MusicRequestViewModel? = null // 音乐资源相关的VM  todo Request ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        musicRequestViewModel = getFragmentViewModelProvider(this).get(MusicRequestViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view:View = inflater.inflate(R.layout.fragment_list, container, false)
        mainBinding = FragmentListBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = object : SimpleBaseBindingAdapter<TestAlbum.TestMusic?, AdapterPlayItemBinding?>(context, R.layout.adapter_play_item) {
            override fun onSimpleBindItem(
                binding: AdapterPlayItemBinding?,
                item: TestAlbum.TestMusic?,
                holder: RecyclerView.ViewHolder?) {

                binding ?.tvTitle ?.text = item ?.title // 标题
                binding ?.tvArtist ?.text = item ?.artist ?.name // 歌手 就是 艺术家
                Glide.with(binding ?.ivCover!!.context).load(item ?.coverImg).into(binding.ivCover) // 左右边的图片

                // 歌曲下标记录
                val currentIndex = PlayerManager.instance.albumIndex // 歌曲下标记录

                // 播放的标记
                binding.ivPlayStatus.setColor(
                    if (currentIndex == holder ?.adapterPosition) resources.getColor(R.color.colorAccent) else Color.TRANSPARENT
                ) // 播放的时候，右变状态图标就是红色， 如果对不上的时候，就是没有

                // 点击Item
                binding.root.setOnClickListener {
                    Toast.makeText(mContext, "播放音乐", Toast.LENGTH_SHORT).show()
                    PlayerManager.instance.playAudio(holder !!.adapterPosition)
                }
            }
        }
        mainBinding !!.rv.adapter = adapter
        mainBinding !!.rv.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        PlayerManager.instance.changeMusicLiveData.observe(viewLifecycleOwner) {
            adapter?.notifyDataSetChanged() // 刷新适配器
        }

        // 请求数据
        // 保证我们列表没有数据（music list）

        musicRequestViewModel !!.requestFreeMusics()

        musicRequestViewModel !!.freeMusicesLiveData !!.observe(viewLifecycleOwner) { musicAlbum: TestAlbum? ->
            if (musicAlbum != null && musicAlbum.musics != null) {
                // 这里特殊：直接更新UI，越快越好
                adapter?.list = musicAlbum.musics // 数据加入适配器
                adapter?.notifyDataSetChanged()

                // 播放相关的业务需要这个数据
                if (PlayerManager.instance.album == null ||
                    PlayerManager.instance.album!!.albumId != musicAlbum.albumId
                ) {
                    //给PlayerManager设置数据
                    PlayerManager.instance.loadAlbum(musicAlbum)
                }
            }
        }
        sharedViewModel.backListMusic.observe(viewLifecycleOwner){
            nav().navigateUp() // back键的时候，返回上一个界面
        }
    }
}