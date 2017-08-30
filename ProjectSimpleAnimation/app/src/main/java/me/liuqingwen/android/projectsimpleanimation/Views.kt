package me.liuqingwen.android.projectsimpleanimation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import org.jetbrains.anko.AnkoLogger

/**
 * Created by Qingwen on 2017-8-29.
 */

class SunView: View, AnkoLogger
{
    constructor(context: Context):super(context)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int, defStyleRes: Int):super(context, attributeSet, defStyleAttr, defStyleRes)
    {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.SunView, defStyleAttr, defStyleRes)
        this.sunColor = typedArray.getColor(R.styleable.SunView_sunColor, Color.RED)
        typedArray.recycle()
    }
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int):this(context, attributeSet, defStyleAttr, 0)
    constructor(context: Context, attributeSet: AttributeSet):this(context, attributeSet, 0, 0)
    
    private var sunColor = Color.RED
    private val paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = this@SunView.sunColor
            this.isAntiAlias = true
        }
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
            this.setMeasuredDimension(200, 200)
        }else if (widthMeasureMode == MeasureSpec.AT_MOST)
        {
            this.setMeasuredDimension(200, heightMeasureSize)
        }else if (heightMeasureMode == MeasureSpec.AT_MOST)
        {
            this.setMeasuredDimension(widthMeasureSize, 200)
        }
    }
    
    override fun onDraw(canvas: Canvas?)
    {
        super.onDraw(canvas)
        val w = this.width - this.paddingLeft - this.paddingRight
        val h = this.height - this.paddingTop - this.paddingBottom
        val radius = Math.min(w, h) * 0.5f
        canvas?.drawCircle(this.paddingLeft + w * 0.5f, this.paddingTop + h * 0.5f, radius, this.paint)
    }
}

class SkyView: View, AnkoLogger
{
    constructor(context: Context):super(context)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int, defStyleRes: Int):super(context, attributeSet, defStyleAttr, defStyleRes)
    {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.SkyView, defStyleAttr, defStyleRes)
        this.starColor = typedArray.getColor(R.styleable.SkyView_starColor, Color.WHITE)
        this.skyColor = typedArray.getColor(R.styleable.SkyView_skyColor, Color.BLACK)
        this.minStarSize = typedArray.getInt(R.styleable.SkyView_minStarSize, 5)
        this.maxStarSize = typedArray.getInt(R.styleable.SkyView_maxStarSize, 25)
        this.minStarCount = typedArray.getInt(R.styleable.SkyView_minStarCount, 20)
        this.maxStarCount = typedArray.getInt(R.styleable.SkyView_maxStarCount, 30)
        typedArray.recycle()
    }
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int):this(context, attributeSet, defStyleAttr, 0)
    constructor(context: Context, attributeSet: AttributeSet):this(context, attributeSet, 0, 0)
    
    private var maxStarCount = 30
    private var minStarCount = 20
    private var maxStarSize = 25
    private var minStarSize = 5
    
    private val currentCount by lazy(LazyThreadSafetyMode.NONE) { this.getRandomBetween(this.minStarCount, this.maxStarCount) }
    private val currentXLocations by lazy(LazyThreadSafetyMode.NONE) { FloatArray(this.currentCount) { this.getRandomBetween(0, this.width).toFloat() } }
    private val currentYLocations by lazy(LazyThreadSafetyMode.NONE) { FloatArray(this.currentCount) { this.getRandomBetween(0, this.height).toFloat() } }
    private val currentRadiusSizes by lazy(LazyThreadSafetyMode.NONE) { FloatArray(this.currentCount) { this.getRandomBetween(minStarSize, maxStarSize).toFloat() } }
    private val currentRotations by lazy(LazyThreadSafetyMode.NONE) { FloatArray(this.currentCount) { this.getRandomBetween(0, 360).toFloat() } }
    
    private var starColor = Color.WHITE
    private var skyColor = Color.BLACK
    private val path = Path()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.isAntiAlias = true }
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.isAntiAlias = true }
    
    private fun getRandomBetween(min:Int, max:Int) = (Math.random() * (max - min) + min).toInt()
    
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    
        val widthMeasureMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthMeasureSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMeasureSize = MeasureSpec.getSize(heightMeasureSpec)
    
        if (widthMeasureMode == MeasureSpec.AT_MOST && heightMeasureMode == MeasureSpec.AT_MOST)
        {
            this.setMeasuredDimension(200, 200)
        }else if (widthMeasureMode == MeasureSpec.AT_MOST)
        {
            this.setMeasuredDimension(200, heightMeasureSize)
        }else if (heightMeasureMode == MeasureSpec.AT_MOST)
        {
            this.setMeasuredDimension(widthMeasureSize, 200)
        }
    }
    
    override fun onDraw(canvas: Canvas?)
    {
        super.onDraw(canvas)
        this.backgroundPaint.color = this.skyColor
        canvas?.drawRect(this.left.toFloat(), this.top.toFloat(), this.right.toFloat(), this.bottom.toFloat(), this.backgroundPaint)
        
        this.paint.color = this.starColor
        
        for (i in 0 until this.currentCount)
        {
            canvas?.save()
            
            val pointX = this.currentXLocations[i]
            val pointY = this.currentYLocations[i]
            val rotation = this.currentRotations[i] + this.getRandomBetween(0, 30)
            val radius = this.currentRadiusSizes[i] + this.getRandomBetween(0, 4)
            canvas?.translate(pointX.toFloat(), pointY.toFloat())
            canvas?.rotate(rotation.toFloat())
            this.path.moveTo(radius.toFloat(), 0.0f)
            this.path.lineTo(- radius * 0.5f, 1.7320508075689f * radius * 0.5f)
            this.path.lineTo(- radius * 0.5f, -1.7320508075689f * radius * 0.5f)
            this.path.lineTo(radius.toFloat(), 0.0f)
            canvas?.drawPath(this.path, this.paint)
            
            this.path.reset()
            
            canvas?.restore()
        }
        
        this.postInvalidateDelayed(200)
    }
}