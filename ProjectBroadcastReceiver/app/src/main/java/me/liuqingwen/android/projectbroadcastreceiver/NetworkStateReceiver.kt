package me.liuqingwen.android.projectbroadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.greenrobot.eventbus.EventBus

class NetworkStateReceiver : BroadcastReceiver()
{
    override fun onReceive(context: Context, intent: Intent)
    {
        if (intent.action == "android.net.wifi.WIFI_STATE_CHANGED" || intent.action == "android.net.wifi.STATE_CHANGED")
        {
            EventBus.getDefault().post(NetworkStatusEvent())
        }
    }
}
