package com.xiangxue.architecture.data.manager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.Toast
import com.xiangxue.architecture.R
import com.xiangxue.architecture.utils.NetworkUtils

/**
 * 监听网络状态的广播接收者
 * 例如：可以收到 “网络不给力”
 */
class NetworkStateReceive : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            if (!NetworkUtils.isConnected) {
                Toast.makeText(context, context.getString(R.string.network_not_good), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 这里可以写的很复杂 ..

    // 这里可以扩展很多功能 ...

    // WorkManager（不成熟的）     所以需要自己去扩展
}