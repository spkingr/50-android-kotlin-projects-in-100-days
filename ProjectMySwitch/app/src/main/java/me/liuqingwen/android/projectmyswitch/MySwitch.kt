package me.liuqingwen.android.projectmyswitch

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import kotlin.math.roundToInt

/**
 * Created by Qingwen on 2018-2018-1-16, project: ProjectMySwitch.
 *
 * @Author: Qingwen
 * @DateTime: 2018-1-16
 * @Package: me.liuqingwen.android.projectmyswitch in project: ProjectMySwitch
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

class MySwitch:View, AnkoLogger
{
    companion object
    {
        private const val DEFAULT_WIDTH = 160
        private const val DEFAULT_HEIGHT = 80
        private const val ANIMATION_DISTANCE_TOLERANCE = 0.1
    }
    constructor(context: Context):super(context)
    constructor(context: Context, attrs:AttributeSet):this(context, attrs, 0)
    constructor(context: Context, attrs:AttributeSet, defStyleAttr:Int):super(context, attrs, defStyleAttr)
    {
        this.setUp(attrs, defStyleAttr, 0)
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs:AttributeSet, defStyleAttr:Int, defStyleRes:Int):super(context, attrs, defStyleAttr, defStyleRes)
    {
        this.setUp(attrs, defStyleAttr, defStyleRes)
    }
    
    private fun setUp(attrs:AttributeSet, defStyleAttr:Int, defStyleRes:Int)
    {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MySwitch, defStyleAttr, defStyleRes)
        this.isOn = typedArray.getBoolean(R.styleable.MySwitch_on, false)
        this.maxAnimationDuration = typedArray.getInt(R.styleable.MySwitch_maxAnimationDuration, 100).toLong()
        this.contentOnBackgroundColor = typedArray.getColor(R.styleable.MySwitch_backgroundColor_switch_enabled_on, Color.GREEN)
        this.contentOffBackgroundColor = typedArray.getColor(R.styleable.MySwitch_backgroundColor_switch_enabled_off, Color.GRAY)
        this.contentDisabledBackgroundColor = typedArray.getColor(R.styleable.MySwitch_backgroundColor_switch_disabled, Color.GRAY)
        this.contentStrokeColor = typedArray.getColor(R.styleable.MySwitch_strokeColor_switch_enabled, Color.BLACK)
        this.contentDisabledStrokeColor = typedArray.getColor(R.styleable.MySwitch_strokeColor_switch_disabled, Color.GRAY)
        this.buttonColor = typedArray.getColor(R.styleable.MySwitch_buttonColor_switch_enabled, Color.RED)
        this.buttonDisabledColor = typedArray.getColor(R.styleable.MySwitch_buttonColor_switch_disabled, Color.BLACK)
        this.contentStrokeWidth = typedArray.getFloat(R.styleable.MySwitch_contentStrokeWidth_In_Pixel, 4.0f)
        this.buttonRadiusSizeDelta = typedArray.getFloat(R.styleable.MySwitch_buttonSpaceSize_In_Pixel, 4.0f)
        typedArray.recycle()
    }
    
    var maxAnimationDuration = 100L
    private var onSwitchValueChangedHandler:((MySwitch, Boolean) -> Unit)? = null
    
    private var isWillOn = false
    var isOn = false
        set(value)
        {
            field = value
            if (this.animator.isRunning)
            {
                this.animator.cancel()
            }
            this.buttonLocationX = if (this.isOn) this.rightEndPosition else this.leftStartPosition
            this.invalidate()
        }
    var contentOnBackgroundColor = Color.GREEN
        set(value)
        {
            field = value
            if (this.isEnabled && this.isOn)
            {
                this.invalidate()
            }
        }
    var contentOffBackgroundColor = Color.GRAY
        set(value)
        {
            field = value
            if (this.isEnabled && ! this.isOn)
            {
                this.invalidate()
            }
        }
    var contentDisabledBackgroundColor = Color.GRAY
        set(value)
        {
            field = value
            if (! this.isEnabled)
            {
                this.invalidate()
            }
        }
    var contentStrokeColor = Color.BLACK
        set(value)
        {
            field = value
            if (this.isEnabled)
            {
                this.invalidate()
            }
        }
    var contentDisabledStrokeColor = Color.GRAY
        set(value)
        {
            field = value
            if (! this.isEnabled)
            {
                this.invalidate()
            }
        }
    var buttonColor = Color.RED
        set(value)
        {
            field = value
            if (this.isEnabled)
            {
                this.invalidate()
            }
        }
    var buttonDisabledColor = Color.BLACK
        set(value)
        {
            field = value
            if (! this.isEnabled)
            {
                this.invalidate()
            }
        }
    var contentStrokeWidth = 4.0f
        set(value)
        {
            field = value
            this.invalidate()
        }
    private var buttonRadiusSizeDelta = 4.0f
        set(value)
        {
            field = value
            this.invalidate()
        }
    
    private var buttonRadius = 0.0f
    private var drawingRectWidth = 0.0f
    
    private var drawingRectHeight = 0.0f
    private var isClickNotDrag = false
    private var buttonLocationX = 0.0f
    
    private var leftStartPosition = 0.0f
    private var rightEndPosition = 0.0f
    private var centerPosition = 0.0f
    
    private val rect = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val animator by lazy(LazyThreadSafetyMode.NONE) { ValueAnimator().also {
        it.interpolator = DecelerateInterpolator()
        it.addUpdateListener {
            this.buttonLocationX = it.animatedValue as Float
            this.invalidate()
        }
        it.addListener(object:AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?)
            {
                super.onAnimationEnd(animation)
                if (this@MySwitch.buttonLocationX <= this@MySwitch.leftStartPosition || this@MySwitch.buttonLocationX >= this@MySwitch.rightEndPosition)
                {
                    this@MySwitch.isOn = this@MySwitch.isWillOn
                    this@MySwitch.onSwitchValueChangedHandler?.run { this(this@MySwitch, this@MySwitch.isOn) }
                }
            }
        })
    } }
    
    fun setOnSwitchValueChangedListener(listener:((MySwitch, Boolean) -> Unit)? = null)
    {
        this.onSwitchValueChangedHandler = listener
    }
    
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        
        val widthMeasureMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthMeasureSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMeasureSize = MeasureSpec.getSize(heightMeasureSpec)
        
        if (widthMeasureMode == MeasureSpec.AT_MOST && heightMeasureMode == MeasureSpec.AT_MOST)
        {
            this.setMeasuredDimension(MySwitch.DEFAULT_WIDTH, MySwitch.DEFAULT_HEIGHT)
        }else if (widthMeasureMode == MeasureSpec.AT_MOST)
        {
            this.setMeasuredDimension((heightMeasureSize * 3.0f / 2.0f).roundToInt(), heightMeasureSize)
        }else if (heightMeasureMode == MeasureSpec.AT_MOST)
        {
            this.setMeasuredDimension(widthMeasureSize, (widthMeasureSize * 2.0f / 3.0f).roundToInt())
        }
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int)
    {
        super.onSizeChanged(w, h, oldW, oldH)
        
        this.drawingRectWidth = w - this.paddingLeft - this.paddingRight - this.contentStrokeWidth * 2
        this.drawingRectHeight = h - this.paddingTop - this.paddingBottom - this.contentStrokeWidth * 2
        this.recalculateSizeAndRadius()
        
        info("----------------------->Get the drawing canvas rect, width: $drawingRectWidth, height: $drawingRectHeight")
    }
    
    private fun recalculateSizeAndRadius()
    {
        if (this.drawingRectWidth < this.drawingRectHeight)
        {
            this.drawingRectHeight = this.drawingRectWidth / 3 * 2
        }
    
        this.leftStartPosition = this.paddingLeft + this.contentStrokeWidth + this.drawingRectHeight / 2
        this.rightEndPosition = this.paddingLeft + this.contentStrokeWidth + this.drawingRectWidth - this.drawingRectHeight / 2
        this.centerPosition = this.paddingLeft + this.contentStrokeWidth + this.drawingRectWidth / 2
        
        this.buttonRadius = this.drawingRectHeight / 2 - this.buttonRadiusSizeDelta
        
        this.buttonLocationX = if (this.isOn) this.rightEndPosition else this.leftStartPosition
    }
    
    private fun startButtonAnimation()
    {
        val endPointX = when
        {
            this.isClickNotDrag && this.isWillOn        -> this.rightEndPosition
            this.isClickNotDrag && ! this.isWillOn      -> this.leftStartPosition
            this.buttonLocationX >= this.centerPosition -> {this.isWillOn = true; this.rightEndPosition
            }
            else                                        -> {this.isWillOn = false; this.leftStartPosition
            }
        }
        
        this.animator.setFloatValues(this.buttonLocationX, endPointX)
        val delta = if (endPointX > this.buttonLocationX) endPointX - this.buttonLocationX else this.buttonLocationX - endPointX
        this.animator.duration = if (delta <= MySwitch.ANIMATION_DISTANCE_TOLERANCE) 0L else (delta * this.maxAnimationDuration / (this.rightEndPosition - this.leftStartPosition)).toLong()
        this.animator.start()
    }
    
    private fun getMovementButtonX(x:Float) = when
    {
        x <= this.leftStartPosition -> this.leftStartPosition
        x >= this.rightEndPosition  -> this.rightEndPosition
        else                        -> x
    }
    
    override fun performClick(): Boolean
    {
        super.performClick()
        return true
    }
    
    override fun onTouchEvent(event: MotionEvent?): Boolean
    {
        if (! this.isEnabled)
        {
            return false
        }
        
        when(event?.action)
        {
            MotionEvent.ACTION_DOWN -> {
                if (this.animator.isRunning) { this.animator.cancel() }
                this.isClickNotDrag = ! this.isClickOnButton(this.buttonLocationX, event.x, event.y)
                if (this.isClickNotDrag)
                {
                    this.isWillOn = event.x >= this.buttonLocationX
                }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (! this.isClickNotDrag)
                {
                    this.buttonLocationX = this.getMovementButtonX(event.x)
                    this.invalidate()
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (event.action == MotionEvent.ACTION_UP && this.isClickNotDrag)
                {
                    this.performClick()
                }
                this.startButtonAnimation()
            }
            else -> { info("Not handled touch event: ${event.toString()}") }
        }
        return super.onTouchEvent(event)
    }
    
    private fun isClickOnButton(buttonX:Float, x: Float, y: Float) = (x - buttonX) * (x - buttonX) +
                                                      (y - this.paddingTop - this.contentStrokeWidth - this.drawingRectHeight / 2) * (y - this.paddingTop - this.contentStrokeWidth - this.drawingRectHeight / 2) <
                                                      this.buttonRadius * this.buttonRadius
    
    override fun onDraw(canvas: Canvas?)
    {
        super.onDraw(canvas)
        
        canvas?.save()
        canvas?.translate(this.paddingLeft + this.contentStrokeWidth, this.paddingTop + this.contentStrokeWidth)
        this.drawBackground(canvas)
        this.drawButton(canvas)
        canvas?.restore()
    }
    
    private fun drawBackground(canvas: Canvas?)
    {
        this.drawBackgroundStroke(canvas)
        this.drawBackgroundContent(canvas)
    }
    
    private fun drawButton(canvas: Canvas?)
    {
        this.paint.color = if (this.isEnabled) this.buttonColor else this.buttonDisabledColor
        this.paint.style = Paint.Style.FILL
        canvas?.drawCircle(this.buttonLocationX - this.paddingLeft - this.contentStrokeWidth, this.drawingRectHeight / 2, this.buttonRadius, this.paint)
    }
    
    private fun drawBackgroundStroke(canvas: Canvas?)
    {
        this.paint.color = if (this.isEnabled) this.contentStrokeColor else this.contentDisabledStrokeColor
        this.paint.style = Paint.Style.STROKE
        this.paint.strokeWidth = this.contentStrokeWidth * 2 //important!
        
        this.rect.top = 0.0f
        this.rect.bottom = this.drawingRectHeight
        this.rect.left = 0.0f
        this.rect.right = this.drawingRectWidth
        canvas?.drawRoundRect(this.rect, this.drawingRectHeight / 2, this.drawingRectHeight / 2, this.paint)
    }
    
    private fun drawBackgroundContent(canvas: Canvas?)
    {
        this.paint.color = when{
            this.isOn && this.isEnabled -> this.contentOnBackgroundColor
            ! this.isOn && this.isEnabled -> this.contentOffBackgroundColor
            else -> this.contentDisabledBackgroundColor
        }
        this.paint.style = Paint.Style.FILL
        
        canvas?.drawRoundRect(this.rect, this.drawingRectHeight / 2, this.drawingRectHeight / 2, this.paint)
    }
}