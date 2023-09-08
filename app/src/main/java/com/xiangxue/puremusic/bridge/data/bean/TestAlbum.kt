package com.xiangxue.puremusic.data.bean

import com.kunminx.player.bean.base.BaseAlbumItem
import com.kunminx.player.bean.base.BaseArtistItem
import com.kunminx.player.bean.base.BaseMusicItem
import com.xiangxue.puremusic.data.bean.TestAlbum.TestArtist
import com.xiangxue.puremusic.data.bean.TestAlbum.TestMusic

/**
 * 歌曲 专辑 歌手  本身的实体Bean 对象
 * 被 PlayerManager 使用
 * 被 PlayerService 使用
 * 被 IRemoteRequest接口 使用了
 * 被 IRemoteRequest接口 使用了
 */
class TestAlbum : BaseAlbumItem<TestMusic?, TestArtist?>() {

    // 专辑 Mid
    var albumMid: String? = null

    // 歌曲 Mid
    class TestMusic : BaseMusicItem<TestArtist?>() {
        var songMid: String? = null
    }

    // 歌手相关
    class TestArtist : BaseArtistItem() {
        var birthday: String? = null
    }
}