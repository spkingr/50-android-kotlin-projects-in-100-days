package me.liuqingwen.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import me.liuqingwen.game.AbstractScreen
import me.liuqingwen.game.ScreenManager
import me.liuqingwen.game.ScreenType
import kotlin.math.atan

/**
 * Created by Qingwen on 2018-2018-7-15, project: ProjectGDXGame.
 *
 * @Author: Qingwen
 * @DateTime: 2018-7-15
 * @Package: me.liuqingwen.game in project: ProjectGDXGame
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

class MainMenuScreen: AbstractScreen()
{
    override fun buildStage()
    {
        val atlas = TextureAtlas(Gdx.files.internal("data/uiskin.atlas"), Gdx.files.internal("data"), false)
        val skin = Skin(Gdx.files.internal("data/uiskin.json"), atlas)
        
        val buttonStart = TextButton("Start", skin, "default").apply {
            this.label.setFontScale(2.0f)
            this.pad(10.0f, 50.0f, 10.0f, 50.0f)
        }
        val buttonScores = TextButton("Scores", skin, "default").apply {
            this.label.setFontScale(2.0f)
            this.pad(10.0f, 50.0f, 10.0f, 50.0f)
        }
        val buttonQuit = TextButton("Quit", skin, "default").apply {
            this.label.setFontScale(2.0f)
            this.pad(10.0f, 50.0f, 10.0f, 50.0f)
        }
        buttonStart.addListener(object: ClickListener(){
            override fun clicked(event: InputEvent?, x: Float, y: Float) = ScreenManager.showScreen(ScreenType.ScreenGame)
        })
        buttonScores.addListener(object: ClickListener(){
            override fun clicked(event: InputEvent?, x: Float, y: Float) = ScreenManager.showScreen(ScreenType.ScreenScoresList)
        })
        buttonQuit.addListener(object: ClickListener(){
            override fun clicked(event: InputEvent?, x: Float, y: Float) = Gdx.app.exit()
        })
        
        val group = VerticalGroup().space(20.0f).pad(50.0f).fill().apply {
            this.setBounds(0.0f, 0.0f, this@MainMenuScreen.width, this@MainMenuScreen.height)
        }
        group.add(buttonStart).add(buttonScores).add(buttonQuit)
        this.addActor(group)
    }
}

fun VerticalGroup.add(actor: Actor) = this.addActor(actor).let { this }
