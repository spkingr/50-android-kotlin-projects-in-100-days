package me.liuqingwen.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FillViewport
import com.badlogic.gdx.utils.viewport.StretchViewport

/**
 * Created by Qingwen on 2018-2018-7-20, project: ProjectSimpleGame.
 *
 * @Author: Qingwen
 * @DateTime: 2018-7-20
 * @Package: me.liuqingwen.game in project: ProjectSimpleGame
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

abstract class AbstractScreen: Stage(), Screen
{
    init
    {
        this.viewport = FillViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        this.viewport.apply(true)
    }
    
    abstract fun buildStage()
    
    override fun render(delta: Float)
    {
        Gdx.gl.glClearColor(0.1f, 0.30f, 0.65f, 1.0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        
        super.act(delta)
        super.draw()
    }
    
    override fun show()
    {
        Gdx.input.inputProcessor = this
    }
    
    override fun resize(width: Int, height: Int) = this.viewport.update(width, height, true)
    
    override fun hide() = Unit
    override fun pause() = Unit
    override fun resume() = Unit
}