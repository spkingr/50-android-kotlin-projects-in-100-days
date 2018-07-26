package me.liuqingwen.game.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import me.liuqingwen.game.MainGame

/**
 * Created by Qingwen on 2018-2018-7-16, project: ProjectGDXGame.
 *
 * @Author: Qingwen
 * @DateTime: 2018-7-16
 * @Package: me.liuqingwen.desktop in project: ProjectGDXGame
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

fun main(vararg args: String)
{
    val config = LwjglApplicationConfiguration().apply {
        this.title = "Drop Game"
        this.width = 1200
        this.height = 800
    }
    LwjglApplication(MainGame(), config)
}