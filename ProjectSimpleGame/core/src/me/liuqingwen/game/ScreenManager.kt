package me.liuqingwen.game

import com.badlogic.gdx.Game
import me.liuqingwen.game.screens.MainGameScreen
import me.liuqingwen.game.screens.MainMenuScreen
import me.liuqingwen.game.screens.ScoresListScreen

/**
 * Created by Qingwen on 2018-2018-7-20, project: ProjectSimpleGame.
 *
 * @Author: Qingwen
 * @DateTime: 2018-7-20
 * @Package: me.liuqingwen.game in project: ProjectSimpleGame
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

enum class ScreenType()
{
    ScreenMainMenu, ScreenScoresList, ScreenGame;
    
    fun getScreen() = when(this)
    {
        ScreenMainMenu -> MainMenuScreen()
        ScreenScoresList -> ScoresListScreen()
        ScreenGame -> MainGameScreen()
    }
}

object ScreenManager
{
    private lateinit var game: Game
    
    fun initialize(game: Game)
    {
        this.game = game
    }
    
    fun showScreen(screenType: ScreenType)
    {
        val currentScreen = this.game.screen
        this.game.screen = screenType.getScreen().apply { this.buildStage() }
        currentScreen?.dispose()
    }
}