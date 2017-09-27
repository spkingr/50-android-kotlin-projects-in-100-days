package me.liuqingwen.android.projectbroadcastreceiver

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.merge_logout.*

/**
 * Created by Qingwen on 2017-2017-9-23, project: ProjectBroadcastReceiver.
 *
 * @Author: Qingwen
 * @DateTime: 2017-9-23
 * @Package: me.liuqingwen.android.projectbroadcastreceiver in project: ProjectBroadcastReceiver
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

var IS_LOG_IN = false

fun AppCompatActivity.setupLogoutButton()
{
    this.buttonLogout?.setOnClickListener {
        val intent = Intent("me.liuqingwen.broadcast.LOGOUT_BROADCAST")
        this.sendBroadcast(intent)
    }
}

fun AppCompatActivity.logout()
{
    IS_LOG_IN = false
    
    val intent = LoginActivity.getIntent(this)
    this.startActivity(intent)
    
    this.finish()
}