package me.liuqingwen.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.physics.box2d.Box2D
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

/**
 * Created by Qingwen on 2018-2018-7-15, project: ProjectGDXGame.
 *
 * @Author: Qingwen
 * @DateTime: 2018-7-15
 * @Package: me.liuqingwen.game in project: ProjectGDXGame
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

class MainGame: Game()
{
    override fun create()
    {
        ScreenManager.initialize(this)
        ScreenManager.showScreen(ScreenType.ScreenMainMenu)
    }
}
