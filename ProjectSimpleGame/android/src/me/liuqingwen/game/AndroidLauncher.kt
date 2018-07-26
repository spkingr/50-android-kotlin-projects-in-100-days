package me.liuqingwen.game

import android.os.Bundle

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration

class AndroidLauncher : AndroidApplication()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        val config = AndroidApplicationConfiguration().apply {
            this.useAccelerometer = false
            this.useCompass = false
        }
        super.initialize(MainGame(), config)
    }
}
