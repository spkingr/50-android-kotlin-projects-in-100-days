package me.liuqingwen.android.projectscalableimageview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView
import org.jetbrains.anko.AnkoLogger

/**
 * Created by Qingwen on 2017-8-23.
 */

enum class OperationMode
{
    DRAG, ZOOM, NONE
}

class ScalableImageView:ImageView, AnkoLogger
{
    constructor(context: Context):super(context)
    constructor(context: Context, attributes: AttributeSet, defStyleAttr : Int, defStyleRes: Int):super(context, attributes, defStyleAttr, defStyleRes)
    constructor(context: Context, attributes: AttributeSet, defStyleAttr: Int):this(context, attributes, defStyleAttr, 0)
    constructor(context: Context, attributes: AttributeSet):this(context, attributes, 0)
    
    companion object
    {
        private const val MIN_ZOOM_POINTER_DISTANCE = 5.0
    }
    
    private var opMode = OperationMode.NONE
    
    override fun performClick(): Boolean
    {
        return super.performClick()
    }
    
    override fun onTouchEvent(event: MotionEvent?): Boolean
    {
        this.performClick()
    
        when(event?.actionMasked)
        {
            MotionEvent.ACTION_DOWN -> {
                this.opMode = OperationMode.DRAG
                this.touchDown(event)
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (this.opMode != OperationMode.ZOOM)
                {
                    this.opMode = OperationMode.ZOOM
                    this.touchZoomDown(event)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (this.opMode != OperationMode.NONE)
                {
                    this.touchMove(event)
                }
            }
            MotionEvent.ACTION_POINTER_UP -> {
                this.opMode = OperationMode.NONE
                this.resizeImage()
            }
            MotionEvent.ACTION_UP -> {
                this.opMode = OperationMode.NONE
                this.replaceImage()
            }
        }
        
        return true
    }
    
    private fun replaceImage()
    {
    }
    
    private var originalX = 0f
    private var originalY = 0f
    private var originalDistance = 0.0
    
    private fun touchDown(event: MotionEvent)
    {
        this.originalX = event.x
        this.originalY = event.y
    }
    
    private fun touchZoomDown(event: MotionEvent)
    {
        this.originalDistance = this.getDistanceOfPointers(event)
    }
    
    private fun touchMove(event: MotionEvent)
    {
        val deltaX = event.x - this.originalX
        val deltaY = event.y - this.originalY
        if (this.opMode == OperationMode.DRAG)
        {
            this.left += deltaX.toInt()
            this.right += deltaX.toInt()
            this.top += deltaY.toInt()
            this.bottom += deltaY.toInt()
            
            super.layout(this.left, this.top, this.right, this.bottom)
        }else if (this.opMode == OperationMode.ZOOM)
        {
            val distance = this.getDistanceOfPointers(event)
            val deltaDistance = Math.abs(distance - this.originalDistance)
            if (deltaDistance > ScalableImageView.MIN_ZOOM_POINTER_DISTANCE)
            {
                this.scaleImage(distance)
                this.originalDistance = distance //!importance
            }
            
            //this.originalDistance = distance
        }
    }
    
    private fun scaleImage(newDistance: Double)
    {
        val ratio = newDistance / this.originalDistance
        if (ratio > 1)
        {
            val sizeX = this.width * (ratio - 1) / 2
            val sizeY = this.height * (ratio - 1) / 2
        
            val left = this.left - sizeX
            val right = this.right + sizeX
            val top = this.top - sizeY
            val bottom = this.bottom + sizeY
        
            this.setFrame(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
        }else if (ratio < 1)
        {
            val sizeX = this.width * (1- ratio) / 2
            val sizeY = this.height * (1 - ratio) / 2
        
            val left = this.left + sizeX
            val right = this.right - sizeX
            val top = this.top + sizeY
            val bottom = this.bottom - sizeY
        
            this.setFrame(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
        }
    }
    
    private fun getDistanceOfPointers(event: MotionEvent): Double
    {
        val deltaX = event.getX(0) - event.getX(1)
        val deltaY = event.getY(0) - event.getY(1)
        return Math.sqrt((deltaX * deltaX + deltaY * deltaY).toDouble())
    }
    
    private fun resizeImage()
    {
    }
    
    /*override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int)
    {
        super.onLayout(changed, left, top, right, bottom)
    }*/
    
    /*override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }*/
    
    /*override fun onDraw(canvas: Canvas?)
    {
        super.onDraw(canvas)
    }*/
}