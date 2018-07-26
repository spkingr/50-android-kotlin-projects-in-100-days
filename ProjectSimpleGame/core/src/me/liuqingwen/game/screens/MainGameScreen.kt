package me.liuqingwen.game.screens

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.TimeUtils
import com.sun.org.apache.xpath.internal.operations.Bool
import me.liuqingwen.game.*
import kotlin.math.absoluteValue

/**
 * Created by Qingwen on 2018-2018-7-15, project: ProjectGDXGame.
 *
 * @Author: Qingwen
 * @DateTime: 2018-7-15
 * @Package: me.liuqingwen.game in project: ProjectGDXGame
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

data class BallData(var isRemoved: Boolean = false, var lastHitTime: Long? = null)

class MainGameScreen: AbstractScreen(), ContactListener, InputProcessor
{
    companion object
    {
        private const val DATA_HAT = "Hat"
        private const val DATA_GROUND = "Ground"
    }
    
    private var isPaused = false
    private var isTouchDown = false
    
    private lateinit var batch: SpriteBatch
    private lateinit var batchUI: SpriteBatch
    private lateinit var font: BitmapFont
    private val world = World(Vector2(0.0f, -4.0f), true)
    private val camera = OrthographicCamera()
    
    private lateinit var effectPool: ParticleEffectPool
    private val effectList = Array<ParticleEffectPool.PooledEffect>()
    
    private var startTime = 0L
    private var score = 0
    private var isScoreSaved = false
    
    private val musicBackground = Gdx.audio.newMusic(Gdx.files.internal("audio/background.mp3"))
    private val soundBird = Gdx.audio.newSound(Gdx.files.internal("audio/bird.wav"))
    private val soundMagical = Gdx.audio.newSound(Gdx.files.internal("audio/magical.ogg"))
    
    private val imageHatBack = Texture(Gdx.files.internal("images/HatBackSprite.png"))
    private val imageHatFront = Texture(Gdx.files.internal("images/HatFrontSprite.png"))
    private val imageBall = Texture(Gdx.files.internal("images/BowlingBallSprite.png"))
    private val imageGrass = Texture(Gdx.files.internal("images/GrassSprite.png"))
    private val imageSky = Texture(Gdx.files.internal("images/SkySprite.png"))
    private val imageSwan = Texture(Gdx.files.internal("images/SwanSheet.png"))
    
    private val grassHeight = 4.0f
    private val hatSize = 1.0f
    private val hatPositionY = 2.0f
    private val hatSpeed = 2.0f
    private val ballRadius = 0.25f
    private var targetPosition: Vector3? = null
    
    private val hatBody: Body
    private val groundBody: Body
    private val ballBodies = arrayListOf<Body>()
    private val debugRender = Box2DDebugRenderer()
    
    private var timeBetweenSpawn = 2000L //milliseconds
    private var lastSpawnTime = 0L
    //private var minSpawnCount = 1
    //private var maxSpawnCount = 3
    private val timeLastInGround = 5000L
    
    private val swanAnimation: Animation<TextureRegion>
    private var stateTime = 0.0f
    private var swanPositionX = 0.0f
    private var swanPositionY = this.height / PPM * 2 / 3
    private var swanSpeed = 2.0f
    private var swanDirection = -1
    private val swanSize = 1.0f
    
    init
    {
        this.musicBackground.isLooping = true
        
        val columns = 8
        val rows = 1
        val swanFrames = Array<TextureRegion>(columns * rows)
        TextureRegion.split(this.imageSwan, this.imageSwan.width / columns, this.imageSwan.height / rows).forEach {
            it.forEach {
                swanFrames.add(it)
            }
        }
        this.swanAnimation = Animation(1f / 15, swanFrames)
        
        this.imageGrass.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge)
        
        Box2D.init()
        this.hatBody = this.createHatBody(this.width / PPM / 2, this.hatPositionY, this.hatSize, this.hatSize)
        this.groundBody = this.createGroundBody(0.0f, 0.0f, 10_000.0f, this.grassHeight / 4)
    }
    
    override fun beginContact(contact: Contact?)
    {
        if (contact == null)
        {
            return
        }
        
        fun tagRemove(ball: Body)
        {
            val data = ball.userData as BallData
            data.isRemoved = true
            this.score ++
            
            val effect = this.effectPool.obtain()
            val position = this.camera.project(Vector3(ball.position.x, ball.position.y, 0.0f))
            effect.setPosition(position.x, position.y)
            this.effectList.add(effect)
        }
        
        val bodyA = contact.fixtureA.body
        val bodyB = contact.fixtureB.body
        when
        {
            bodyA.userData == MainGameScreen.DATA_HAT    -> tagRemove(bodyB)
            bodyB.userData == MainGameScreen.DATA_HAT    -> tagRemove(bodyA)
            bodyA.userData == MainGameScreen.DATA_GROUND -> (bodyB.userData as BallData).lastHitTime = TimeUtils.millis()
            bodyB.userData == MainGameScreen.DATA_GROUND -> (bodyA.userData as BallData).lastHitTime = TimeUtils.millis()
        }
    }
    
    override fun endContact(contact: Contact?) = Unit
    
    override fun buildStage()
    {
        this.batch = SpriteBatch()
        this.batchUI = SpriteBatch()
        this.font = BitmapFont().apply { this.data.scale(1.0f) }
    
        val effect = ParticleEffect().apply {
            this.load(Gdx.files.internal("images/ParticleEffects.p"), Gdx.files.internal("images/"))
            this.setEmittersCleanUpBlendFunction(true)
        }
        this.effectPool = ParticleEffectPool(effect, 1, 4)
        this.effectPool.obtain().free() //the first effect!
        
        this.world.setContactListener(this)
        this.camera.setToOrtho(false, this.width / PPM, this.height / PPM)
    
        this.startTime = TimeUtils.millis()
    
        this.viewport.update(Gdx.graphics.width, Gdx.graphics.height, true)
    }
    
    override fun render(delta: Float)
    {
        super.render(delta)
        
        if (TimeUtils.millis() - this.startTime >= GAME_TIME * 1000)
        {
            this.batchUI.begin()
            this.drawGameOver()
            this.batchUI.end()
            
            if (! this.isScoreSaved)
            {
                this.saveScore()
                this.isScoreSaved = true
            }
            
            return
        }
        
        if (! this.isPaused)
        {
            this.camera.update()
            this.batch.projectionMatrix = this.camera.combined
    
            this.batch.begin()
            this.batchDraw(delta)
            this.batch.end()
        }
        
        this.batchUI.begin()
        this.drawScore()
        this.drawEffect(delta)
        this.batchUI.end()
        
        if (! this.isPaused)
        {
            this.renderBox2D(delta)
        }
    }
    
    private fun saveScore()
    {
        val preferences = Gdx.app.getPreferences(PREFERENCE_NAME)
        val key = "score"
        if (preferences.contains(key))
        {
            var result = preferences.getString(key)
            val scores = arrayListOf<Int>().apply {
                this.addAll(result.split("|").map { it.toInt() })
            }
            scores.add(element = this.score)
            scores.sortDescending()
            result = scores.take(5).joinToString(separator = PREFERENCE_SEPARATOR) {
                it.toString()
            }
            preferences.putString(key, result)
        }
        else
        {
            preferences.putString(key, this.score.toString())
        }
        preferences.flush()
    }
    
    private fun drawEffect(delta: Float)
    {
        val removedValues = Array<ParticleEffectPool.PooledEffect>(4)
        for (i in 0 until this.effectList.size)
        {
            val effect = this.effectList[i]
            effect.draw(this.batchUI, delta)
            if (effect.isComplete)
            {
                effect.free()
                removedValues.add(effect)
            }
        }
        removedValues.forEach {
            this.effectList.removeValue(it, true)
        }
    }
    
    private fun renderBox2D(delta: Float)
    {
        this.world.step(delta, 6, 2)
        //this.debugRender.render(this.world, this.camera.combined)
        
        if (this.targetPosition != null)
        {
            val preStep = this.hatBody.position.x - this.targetPosition!!.x
            val nextStep = this.hatBody.position.x + this.hatBody.linearVelocity.x * Gdx.graphics.deltaTime - this.targetPosition!!.x
            if(nextStep.absoluteValue >= preStep.absoluteValue)
            {
                this.targetPosition = null
                this.hatBody.linearVelocity = Vector2.Zero
            }
        }
        
        val time = TimeUtils.millis()
        val timeBetween = this.timeBetweenSpawn - (time - this.startTime) / 1000 / 60 * 100
        if(time - this.lastSpawnTime >= timeBetween)
        {
            //val count = MathUtils.random(this.minSpawnCount, this.maxSpawnCount)
            this.spawnBalls()
            this.lastSpawnTime = time
        }
        
        this.removeBalls()
    }
    
    private fun removeBalls()
    {
        this.ballBodies.filter { (it.userData as BallData).isRemoved || (it.userData as BallData).lastHitTime != null }.forEach {
            val data = it.userData as BallData
            if(data.isRemoved || TimeUtils.millis() - data.lastHitTime!! >= this.timeLastInGround)
            {
                this.ballBodies.remove(it)
                it.userData = null
                this.world.destroyBody(it)
                
                if (data.isRemoved)
                {
                    this.soundMagical.play()
                }
            }
        }
    }
    
    private fun spawnBalls(count: Int = 1) = repeat(count){
        val x = MathUtils.random(this.ballRadius, this.width / PPM - this.ballRadius)
        val body = this.createBallBody(x, (this.height + MathUtils.random(this.height / 3)) / PPM, this.ballRadius)
        this.ballBodies.add(body)
    }
    
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean
    {
        if (this.isBackButtonClicked(screenX, screenY) || this.isQuitButtonClicked(screenX, screenY))
        {
            ScreenManager.showScreen(if (this.isScoreSaved) ScreenType.ScreenScoresList else ScreenType.ScreenMainMenu)
            return true
        }
        
        this.isTouchDown = true
        val position = Vector3(screenX.toFloat(), 0.0f, 0.0f)
        this.camera.unproject(position)
        if (position.x < this.hatSize / 2)
        {
            position.x = this.hatSize / 2
        }
        else if (position.x > this.width / PPM - this.hatSize / 2)
        {
            position.x = this.width / PPM - this.hatSize / 2
        }
        this.targetPosition = position
        
        when
        {
            position.x > this.hatBody.position.x -> this.hatBody.linearVelocity = Vector2(this.hatSpeed, 0.0f)
            position.x < this.hatBody.position.x -> this.hatBody.linearVelocity = Vector2(- this.hatSpeed, 0.0f)
            else                                 -> this.hatBody.linearVelocity = Vector2.Zero
        }
        return true
    }
    
    private fun isQuitButtonClicked(screenX: Int, screenY: Int): Boolean
    {
        val layout = GlyphLayout(this.font, "Quit Game")
        val xMin = this.width / 2 - layout.width / 2
        val xMax = this.width / 2 + layout.width / 2
        val yMin = 10.0f
        val yMax = layout.height + 10.0f
        return (screenX in xMin..xMax) && (screenY in yMin..yMax)
    }
    
    private fun isBackButtonClicked(screenX: Int, screenY: Int): Boolean
    {
        val xMin = this.width / 2 - 100
        val xMax = this.width / 2 + 100
        val yMin = this.height / 2 - 100
        val yMax = this.height / 2 + 100
        return this.isScoreSaved && (screenX in xMin..xMax) && (screenY in yMin..yMax)
    }
    
    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean
    {
        val position = Vector3(screenX.toFloat(), 0.0f, 0.0f)
        this.camera.unproject(position)
        if (position.x < this.hatSize / 2)
        {
            position.x = this.hatSize / 2
        }
        else if (position.x > this.width / PPM - this.hatSize / 2)
        {
            position.x = this.width / PPM - this.hatSize / 2
        }
        this.targetPosition = position
        
        when
        {
            position.x > this.hatBody.position.x -> this.hatBody.linearVelocity = Vector2(this.hatSpeed, 0.0f)
            position.x < this.hatBody.position.x -> this.hatBody.linearVelocity = Vector2(- this.hatSpeed, 0.0f)
            else                                 -> this.hatBody.linearVelocity = Vector2.Zero
        }
        return true
    }
    
    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean
    {
        this.isTouchDown = false
        this.hatBody.linearVelocity = Vector2.Zero
        return true
    }
    
    private fun batchDraw(delta: Float)
    {
        this.batch.draw(this.imageSky, 0.0f, 0.0f, this.width / PPM, this.height / PPM)
        this.batch.draw(this.imageGrass, 0.0f, 0.0f, this.width / PPM, this.grassHeight)
        this.batch.draw(this.imageHatBack, this.hatBody.position.x - this.hatSize / 2, this.hatBody.position.y - this.hatSize / 2, this.hatSize, this.hatSize)
        this.ballBodies.forEach {
            this.batch.draw(this.imageBall, it.position.x - this.ballRadius, it.position.y - this.ballRadius, this.ballRadius * 2, this.ballRadius * 2)
        }
        this.batch.draw(this.imageHatFront, this.hatBody.position.x - this.hatSize / 2, this.hatBody.position.y - this.hatSize / 2, this.hatSize, this.hatSize)
    
        this.batch.draw(this.getFrame(delta), this.swanPositionX, this.swanPositionY, this.swanSize, this.swanSize)
    }
    
    private fun getFrame(delta: Float):TextureRegion
    {
        this.stateTime += delta
        this.swanPositionX += this.swanDirection * this.swanSpeed * delta
        if (this.swanPositionX < - this.swanSize * 3 && this.swanDirection == -1)
        {
            this.swanDirection = 1
            this.swanPositionY = this.height / PPM * 2 / 3 + MathUtils.random(this.height / PPM / 3) - this.swanSize
            this.swanAnimation.keyFrames.forEach {
                it.flip(true, false)
            }
            
            this.soundBird.play()
        }
        else if(this.swanPositionX > this.width / PPM + this.swanSize * 3 && this.swanDirection == 1)
        {
            this.swanDirection = -1
            this.swanPositionY = this.height / PPM * 2 / 3 + MathUtils.random(this.height / PPM / 3) - this.swanSize
            this.swanAnimation.keyFrames.forEach {
                it.flip(true, false)
            }
            
            this.soundBird.play()
        }
        return swanAnimation.getKeyFrame(stateTime, true)
    }
    
    private fun drawGameOver()
    {
        var layout = GlyphLayout(this.font, "Game Over!")
        val x = this.width / 2 - layout.width / 2
        var y = this.height / 2 + layout.height * 1.5f
        this.font.draw(this.batchUI, layout, x, y)
        
        layout = GlyphLayout(this.font, "Your score: ${this.score}")
        y -= layout.height * 1.5f
        this.font.draw(this.batchUI, layout, x, y)
        
        layout = GlyphLayout(this.font, "Click to Back")
        y -= layout.height * 1.5f
        this.font.draw(this.batchUI, layout, x, y)
    }
    
    private fun drawScore()
    {
        val time = (TimeUtils.millis() - this.startTime) / 1000
        val h = time / 60
        val m = time % 60
        var layout = GlyphLayout(this.font, "Elapsed Time: ${if(h < 10) "0" else ""}$h:${if(m < 10) "0" else ""}$m")
        var x = 10.0f
        val y = this.height - 10.0f
        this.font.draw(this.batchUI, layout, x, y)
        
        layout = GlyphLayout(this.font, "Score: ${this.score}")
        x = this.width - layout.width - 10.0f
        this.font.draw(this.batchUI, layout, x, y)
        
        layout = GlyphLayout(this.font, "Quit Game")
        x = this.width / 2 - layout.width / 2
        this.font.draw(this.batchUI, layout, x, y)
    }
    
    private fun createBallBody(x: Float, y: Float, radius: Float, isStatic: Boolean = false): Body
    {
        val bodyDef = BodyDef().apply {
            this.type = if (isStatic) BodyDef.BodyType.StaticBody else BodyDef.BodyType.DynamicBody
            this.position.set(x, y)
            this.fixedRotation = false
        }
        
        val shape = CircleShape().apply {
            this.radius = radius
        }
        
        val body = this.world.createBody(bodyDef).apply {
            this.createFixture(shape, 1.0f)
            this.userData = BallData()
        }
        
        shape.dispose()
        
        return body
    }
    
    private fun createHatBody(x: Float, y:Float, width: Float, height: Float): Body
    {
        val bodyDef = BodyDef().apply {
            this.type = BodyDef.BodyType.KinematicBody
            this.position.set(x, y)
            this.fixedRotation = true
        }
        
        val shape = PolygonShape().apply {
            //this.setAsBox(width / 2, height / 2)
            this.setAsBox(width / 4, height / 4, Vector2(0.0f, - height / 4), 0.0f)
        }
        
        val body = this.world.createBody(bodyDef).apply {
            this.createFixture(shape, 1.0f)
            this.userData = MainGameScreen.DATA_HAT
        }
        
        shape.dispose()
        
        return body
    }
    
    private fun createGroundBody(x: Float, y:Float, width: Float, height: Float): Body
    {
        val bodyDef = BodyDef().apply {
            this.type = BodyDef.BodyType.StaticBody
            this.position.set(x, y)
            this.fixedRotation = true
        }
        
        val shape = PolygonShape().apply {
            this.setAsBox(width / 2, height / 2)
        }
        
        val body = this.world.createBody(bodyDef).apply {
            this.createFixture(shape, 1.0f)
            this.userData = MainGameScreen.DATA_GROUND
        }
        
        shape.dispose()
        
        return body
    }
    
    override fun preSolve(contact: Contact?, oldManifold: Manifold?) = Unit
    
    override fun postSolve(contact: Contact?, impulse: ContactImpulse?) = Unit
    
    override fun show()
    {
        super.show()
        
        this.musicBackground.play()
    }
    
    override fun hide()
    {
        super.hide()
        
        this.musicBackground.pause()
    }
    
    override fun dispose()
    {
        super.dispose()
        this.batch.dispose()
        this.batchUI.dispose()
        this.font.dispose()
        this.imageBall.dispose()
        this.imageHatFront.dispose()
        this.imageHatBack.dispose()
        this.imageGrass.dispose()
        this.imageSky.dispose()
        this.imageSwan.dispose()
        this.soundBird.dispose()
        this.musicBackground.dispose()
        this.soundMagical.dispose()
        this.effectPool.clear()
        this.effectList.forEach {
            it.free()
            it.dispose()
        }
        this.world.dispose()
        
        this.debugRender.dispose()
    }
}
