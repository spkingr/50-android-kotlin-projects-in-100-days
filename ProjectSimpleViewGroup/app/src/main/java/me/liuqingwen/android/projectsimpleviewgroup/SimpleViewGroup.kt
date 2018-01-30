package me.liuqingwen.android.projectsimpleviewgroup

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.*
import android.widget.Scroller
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.jetbrains.anko.info
import org.jetbrains.anko.px2dip
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

/**
 * Created by Qingwen on 2018-1-25, project: ProjectSimpleViewGroup.
 *
 * @Author: Qingwen
 * @DateTime: 2018-1-25
 * @Package: me.liuqingwen.android.projectsimpleviewgroup in project: ProjectSimpleViewGroup
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */
fun View.rect2dipString(rect: Rect) = "Rect[left:${px2dip(rect.left)}, top:${px2dip(rect.top)}, right:${px2dip(rect.right)}, bottom:${px2dip(rect.bottom)} ]"

class SimpleViewGroup:ViewGroup, AnkoLogger
{
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
    
    private var pointerX = 0.0f
    private var pageCount = 0
    private var pageSize = 0
    private var pageHeight = 0 //For constrains in Y, if not set the y of the children(big size) will not be correctly positioned
    private val childRect by lazy(LazyThreadSafetyMode.NONE) { Rect() }
    private val containerRect by lazy(LazyThreadSafetyMode.NONE) { Rect() }
    private val scroller by lazy(LazyThreadSafetyMode.NONE) { Scroller(this.context)}
    
    private fun setUp(attrs:AttributeSet, defStyleAttr:Int, defStyleRes:Int)
    {
        /*val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SimpleViewGroup, defStyleAttr, defStyleRes)
        typedArray.recycle()*/
    }
    
    override fun performClick(): Boolean
    {
        super.performClick()
        return true
    }
    
    override fun performLongClick(): Boolean
    {
        super.performLongClick()
        return true
    }
    
    override fun computeScroll()
    {
        super.computeScroll()
        if (this.scroller.computeScrollOffset())
        {
            this.scrollTo(this.scroller.currX, 0)
            postInvalidate()
        }
    }
    
    private var interceptEventY = 0.0f
    private var interceptEventX = 0.0f
    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean
    {
        if (event?.action == MotionEvent.ACTION_DOWN && event.edgeFlags != 0)
        {
            return false
        }
    
        //------------------------------------------------------------------------------------------
        info("""--------------------------------------->onInterceptTouchEvent
            |Event: (x:${event?.x}, y:${event?.y})
            |Type:  (${event?.action})
        """.trimMargin())
        
        var isIntercepted = false
        when(event?.action)
        {
            MotionEvent.ACTION_DOWN -> {
                this.interceptEventX = event.x
                this.interceptEventY = event.y
                isIntercepted = ! this.scroller.isFinished
            }
            MotionEvent.ACTION_MOVE ->
            {
                if ((event.x - this.interceptEventX).absoluteValue >= ViewConfiguration.get(this.context).scaledTouchSlop.toFloat())
                {
                    isIntercepted = true
                    //Important! Event loops: [InterceptEventDown->InterceptEventMove->TouchEventMove->TouchEventDown!!!]
                    //So must update the x and y for touch event!
                    //If here no updates, then you will jump when try to drag a button or other clickable ones to scroll!
                    this.pointerX = event.x
                }
            }
            /*MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                error("----------------this only happens when event is intercepted--------------->onInterceptTouchEvent(Cancel? = ${event.action == MotionEvent.ACTION_CANCEL})")
                this.checkPerformClicks(event)
            } //event is intercepted!*/
            else -> this.error("Not correctly handled yet!")
        }
        return isIntercepted
    }
    
    private fun checkPerformClicks(event: MotionEvent)
    {
        val touchSlop = ViewConfiguration.get(this.context).scaledTouchSlop.toFloat()
        val clickable = this.isClickable or this.isLongClickable
        if (event.action == MotionEvent.ACTION_UP && clickable && (event.x - this.interceptEventX).absoluteValue < touchSlop && (event.y - this.interceptEventY).absoluteValue < touchSlop)
        {
            if (this.isFocusable && this.isFocusableInTouchMode && ! this.isFocused)
            {
                this.requestFocus()
            }
            
            val longTouchSlop = ViewConfiguration.getLongPressTimeout()
            if (event.eventTime - event.downTime >= longTouchSlop && this.isLongClickable)
            {
                this.performLongClick()
            }
            else
            {
                this.performClick()
            }
        }
    }
    
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean
    {
        //------------------------------------------------------------------------------------------
        info("""--------------------------------------->onTouchEvent
            |Event: (x:${event?.x}, y:${event?.y})
            |Type:  (${event?.action})
        """.trimMargin())
        
        when(event?.action)
        {
            MotionEvent.ACTION_DOWN -> {
    
                error("-----------------this only happens when no children consume event------------->onTouchEvent")
                
                this.pointerX = event.x
                if (! this.scroller.isFinished)
                {
                    this.scroller.abortAnimation()
                }
                return true //True for accepting touch events
            }
            
            MotionEvent.ACTION_MOVE -> {
                this.scrollBy((this.pointerX - event.x).roundToInt(), 0)
                this.pointerX = event.x
            }
            
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                val delta = this.pointerX - event.x + this.scrollX
                when
                {
                    delta < 0                                    -> this.scroller.startScroll(this.scrollX, this.scrollY, - this.scrollX, 0)
                    delta > (this.pageCount - 1) * this.pageSize -> this.scroller.startScroll(this.scrollX, this.scrollY, (this.pageCount - 1) * this.pageSize - this.scrollX, 0)
                    else                                         -> {
                        val isNext = delta.roundToInt() % this.pageSize >= this.pageSize / 2
                        val pageNumber = delta.roundToInt() / this.pageSize + if (isNext) 1 else 0
                        this.scroller.startScroll(this.scrollX, this.scrollY, pageNumber * this.pageSize - this.scrollX, 0)
                    }
                }
                this.invalidate()
                
                //------------------------------------------------------------------------------------------
                info("""--------------------------------------->
                    |Group: [Padding: ($paddingLeft, $paddingTop, $paddingRight, $paddingBottom)]
                    |Page:[PageCount: $pageCount, PageSize: $pageSize]
                    |Scroll:[ScrollXY: ($scrollX, $scrollY)]
                """.trimMargin())
                
                this.checkPerformClicks(event)
            }
            else -> { this.error("Not correctly handled yet!") }
        }
        return super.onTouchEvent(event)
    }
    
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        //Max width and height is used only for AT_MOST(WRAP_CONTENT) layout parameters
        var maxWidth = 0
        var maxHeight = 0
        for(index in 0 until this.childCount)
        {
            val child = this.getChildAt(index)
            if (child.visibility == View.GONE)
            {
                continue
            }
            super.measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
            
            //Must add the margins to the child space rect
            val layoutParams = child.layoutParams as MarginLayoutParams
            val childWidth = child.measuredWidth + layoutParams.leftMargin + layoutParams.rightMargin
            val childHeight = child.measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin
            if (childHeight > maxHeight)
            {
                maxHeight = childHeight
            }
            if (childWidth > maxWidth)
            {
                maxWidth = childWidth
            }
    
            //------------------------------------------------------------------------------------------
            info("""--------------------------------------->onMeasure
                |Child[$index]: [Margin: (${px2dip(layoutParams.leftMargin)}, ${px2dip(layoutParams.topMargin)}, ${px2dip(layoutParams.rightMargin)}, ${px2dip(layoutParams.bottomMargin)})]
                |Child[$index]: [Padding: (${px2dip(child.paddingLeft)}, ${px2dip(child.paddingTop)}, ${px2dip(child.paddingRight)}, ${px2dip(child.paddingBottom)})]
                |Child[$index]: [Width&Height: (${px2dip(child.width)} = ${px2dip(child.measuredWidth)}, ${px2dip(child.height)} = ${px2dip(child.measuredHeight)})]
                |Child[$index]: [Position: (${rect2dipString(childRect)})]
            """.trimMargin())
        }
        maxWidth += this.paddingLeft + this.paddingRight
        maxHeight += this.paddingTop + this.paddingBottom
        
        //Check the mode of the ViewGroup size measurement
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(widthMeasureSpec)
        var widthMeasured = MeasureSpec.getSize(widthMeasureSpec)
        var heightMeasured = MeasureSpec.getSize(widthMeasureSpec)
    
        //------------------------------------------------------------------------------------------
        info("""--------------------------------------->onMeasure Result->
            |MaxWidth: ${px2dip(maxWidth)}, MaxHeight: ${px2dip(maxHeight)}
            |MeasuredWidth: ${px2dip(widthMeasured)}, MeasuredHeight: ${px2dip(heightMeasured)}
            |WidthMode=$widthMode, HeightMode=$heightMode
        """.trimMargin())
    
        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST)
        {
            widthMeasured = maxOf(maxWidth, widthMeasured, super.getSuggestedMinimumWidth())
            heightMeasured = maxOf(maxHeight, heightMeasured, super.getSuggestedMinimumHeight())
        }
        else if (widthMode == MeasureSpec.AT_MOST)
        {
            widthMeasured = maxOf(maxWidth, widthMeasured, super.getSuggestedMinimumWidth())
        }
        else if (heightMode == MeasureSpec.AT_MOST)
        {
            heightMeasured = maxOf(maxHeight, heightMeasured, super.getSuggestedMinimumHeight())
        }
        
        //Here the widthMeasured is the max one and is the page size(width)
        this.pageSize = widthMeasured
        this.pageHeight = heightMeasured
        
        widthMeasured = View.resolveSizeAndState(widthMeasured, widthMeasureSpec, 0)
        heightMeasured = View.resolveSizeAndState(heightMeasured, heightMeasureSpec, 0)
        super.setMeasuredDimension(widthMeasured, heightMeasured)
    }
    
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int)
    {
        with(this.containerRect) {
            this.left = this@SimpleViewGroup.paddingLeft
            this.top = this@SimpleViewGroup.paddingTop
            this.right = right - left - this@SimpleViewGroup.paddingRight
            this.bottom = bottom - top - this@SimpleViewGroup.paddingBottom
    
            //------------------------------------------------------------------------------------------
            info("--------------------------------------->Height:${px2dip(this.bottom)}-${px2dip(this.top)}=${px2dip(this.bottom - this.top)},Padding[$paddingLeft,$paddingTop,$paddingRight,$paddingBottom]")
        }
        
        this.pageCount = 0
        for (index in 0 until this.childCount)
        {
            val child = this.getChildAt(index)
            if (child.visibility == View.GONE)
            {
                continue
            }
            
            val layoutParams = child.layoutParams as LayoutParams
            val gravity = layoutParams.gravity
            val childWidth = minOf(child.measuredWidth + layoutParams.leftMargin + layoutParams.rightMargin, this.pageSize) //Cannot overflow the page width!
            val childHeight = minOf(child.measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin, this.pageHeight)
            Gravity.apply(gravity, childWidth, childHeight, this.containerRect, this.childRect)
            val l = this.childRect.left + layoutParams.leftMargin + this.pageCount * this.pageSize
            val t = this.childRect.top + layoutParams.topMargin
            val r = this.childRect.right - layoutParams.rightMargin + this.pageCount * this.pageSize
            val b = this.childRect.bottom - layoutParams.bottomMargin
            child.layout(l, t, r, b)
    
            //------------------------------------------------------------------------------------------
            info("""--------------------------------------->OnLayout
                |Child[$index]: [Gravity: $gravity = ${gravity.toString(16)}]
                |Child[$index]: [Margin: (${px2dip(layoutParams.leftMargin)}, ${px2dip(layoutParams.topMargin)}, ${px2dip(layoutParams.rightMargin)}, ${px2dip(layoutParams.bottomMargin)})]
                |Child[$index]: [Padding: (${px2dip(child.paddingLeft)}, ${px2dip(child.paddingTop)}, ${px2dip(child.paddingRight)}, ${px2dip(child.paddingBottom)})]
                |Child[$index]: [Width: (${px2dip(child.width)} = ${px2dip(child.measuredWidth)}, ${px2dip(child.height)} = ${px2dip(child.measuredHeight)})]
                |Child[$index]: [Position: (${rect2dipString(childRect)})]
            """.trimMargin())
            
            this.pageCount ++
        }
    }
    
    //Add custom layout parameters for all children
    override fun generateDefaultLayoutParams() = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    override fun generateLayoutParams(attrs: AttributeSet?) = LayoutParams(this.context, attrs)
    override fun generateLayoutParams(p: ViewGroup.LayoutParams?) = LayoutParams(p)
    override fun checkLayoutParams(p: ViewGroup.LayoutParams?) = p is LayoutParams
    
    //The custom class name can be the same with ViewGroup.LayoutParams
    inner class LayoutParams:MarginLayoutParams
    {
        var gravity = -1
        
        constructor(context: Context, attrs: AttributeSet?):super(context, attrs)
        {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SimpleViewGroup_Layout)
            this.gravity = typedArray.getInt(R.styleable.SimpleViewGroup_Layout_android_layout_gravity, -1)
            typedArray.recycle()
        }
        constructor(width: Int, height: Int):super(width, height)
        constructor(source: MarginLayoutParams):super(source)
        constructor(source: ViewGroup.LayoutParams?):super(source)
        constructor(source: LayoutParams):super(source)
        {
            this.gravity = source.gravity
        }
        constructor(width: Int, height: Int, gravity: Int):super(width, height)
        {
            this.gravity = gravity
        }
    }
}