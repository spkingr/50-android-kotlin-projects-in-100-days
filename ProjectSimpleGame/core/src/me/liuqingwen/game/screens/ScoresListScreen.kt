package me.liuqingwen.game.screens

import com.badlogic.gdx.Application
import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import me.liuqingwen.game.*

/**
 * Created by Qingwen on 2018-2018-7-20, project: ProjectSimpleGame.
 *
 * @Author: Qingwen
 * @DateTime: 2018-7-20
 * @Package: me.liuqingwen.game.screens in project: ProjectSimpleGame
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

class ScoresListScreen: AbstractScreen()
{
    private lateinit var batch: SpriteBatch
    private lateinit var font: BitmapFont
    
    override fun buildStage()
    {
        this.batch = SpriteBatch()
        this.font = BitmapFont().apply { this.data.scale(1.0f) }
    
        val atlas = TextureAtlas(Gdx.files.internal("data/uiskin.atlas"), Gdx.files.internal("data"), false)
        val skin = Skin(Gdx.files.internal("data/uiskin.json"), atlas)
    
        val buttonStart = TextButton("Start", skin, "default").apply {
            this.label.setFontScale(2.0f)
            this.pad(10.0f, 50.0f, 10.0f, 50.0f)
        }
        val buttonBack = TextButton("Back", skin, "default").apply {
            this.label.setFontScale(2.0f)
            this.pad(10.0f, 50.0f, 10.0f, 50.0f)
        }
        buttonStart.addListener(object: ClickListener(){
            override fun clicked(event: InputEvent?, x: Float, y: Float) = ScreenManager.showScreen(ScreenType.ScreenGame)
        })
        buttonBack.addListener(object: ClickListener(){
            override fun clicked(event: InputEvent?, x: Float, y: Float) = ScreenManager.showScreen(ScreenType.ScreenMainMenu)
        })
    
        val group = VerticalGroup().space(20.0f).pad(50.0f).fill().apply {
            this.setBounds(0.0f, 0.0f, this@ScoresListScreen.width, this@ScoresListScreen.height)
        }
        group.add(buttonStart).add(buttonBack)
        
        this.buildScores(group, skin)
        
        this.addActor(group)
    }
    
    private fun buildScores(group: VerticalGroup, skin: Skin)
    {
        val preferences = Gdx.app.getPreferences(PREFERENCE_NAME)
        val key = "score"
        if (preferences.contains(key))
        {
            val result = preferences.getString(key)
            var index = 'A'
            result.split(PREFERENCE_SEPARATOR).forEach {
                val label = Label("Score $index -> $it", skin).apply { this.setFontScale(2.0f) }
                group.add(label)
                index ++
            }
        }
        else
        {
            val label = Label("No scores data.", skin)
            group.add(label)
        }
    }
}