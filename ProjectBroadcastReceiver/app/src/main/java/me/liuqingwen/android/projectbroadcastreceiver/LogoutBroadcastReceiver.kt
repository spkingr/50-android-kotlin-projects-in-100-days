package me.liuqingwen.android.projectbroadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.greenrobot.eventbus.EventBus

class LogoutBroadcastReceiver : BroadcastReceiver()
{
    override fun onReceive(context: Context, intent: Intent)
    {
        EventBus.getDefault().post(LogoutEvent())
    }
}
