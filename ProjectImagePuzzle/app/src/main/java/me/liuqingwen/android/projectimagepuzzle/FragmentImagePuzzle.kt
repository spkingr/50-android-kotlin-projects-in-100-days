package me.liuqingwen.android.projectimagepuzzle

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.values
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.layout_fragment_puzzle.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.info
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Created by Qingwen on 2018-8-1, project: ProjectImagePuzzle.
 *
 * @Author: Qingwen
 * @DateTime: 2018-8-1
 * @Package: me.liuqingwen.android.projectimagepuzzle in project: ProjectImagePuzzle
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

class FragmentImagePuzzle: Fragment(), AnkoLogger
{
    interface IFragmentInteractionListener: IFragmentActivity
    
    companion object
    {
        private const val PARAM_PATH = "path"
        private const val PARAM_URI = "uri"
        fun newInstance(path: String) = FragmentImagePuzzle().apply {
            this.arguments = bundleOf(PARAM_PATH to path)
        }
        fun newInstance(uri: Uri) = FragmentImagePuzzle().apply {
            this.arguments = bundleOf(PARAM_URI to uri)
        }
    }
    
    private var listener: IFragmentInteractionListener? = null
    private var imagePath: String? = null
    private var imageUri: Uri? = null
    
    private var rows = 4
    private var columns = 3
    private var cellWidth = 0
    private var cellHeight = 0
    private var minMarginLeft = 0
    private var maxMarginLeft = 0
    private var minMarginTop = 0
    private var maxMarginTop = 0
    
    private lateinit var pieces: Array<ImagePuzzleCell?>
    
    override fun onAttach(context: Context?)
    {
        super.onAttach(context)
        this.listener = if (context is FragmentImagePuzzle.IFragmentInteractionListener) context else throw RuntimeException(context.toString() + " must implement IFragmentInteractionListener")
    }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        this.arguments?.let {
            this.imagePath = it.getString(FragmentImagePuzzle.PARAM_PATH, null)
            this.imageUri = it.getParcelable(FragmentImagePuzzle.PARAM_URI)
        }
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.layout_fragment_puzzle, container, false)
    
    override fun onStart()
    {
        super.onStart()
        
        this.buttonReset.setOnClickListener { this.resetPuzzle() }
        this.buttonResult.setOnClickListener { this.showResult() }
        this.imagePuzzle.post {
            fun processImage()
            {
                this.splitImage(this.context, this.imagePuzzle, rows = this.rows, columns = this.columns)
                this.pieces.forEach { this.addImageView(it!!) }
            }
    
            val assetManager = this.listener?.assetManager
            when
            {
                this.imagePath != null && assetManager != null -> {
                    val stream = assetManager.open("${MainActivity.ASSET_DIRECTORY_NAME}/${this.imagePath}")
                    val bitmap = sampleBitmapData(this.imagePuzzle.width, this.imagePuzzle.height, stream)
                    this.imagePuzzle.setImageBitmap(bitmap)
                    processImage()
                }
                this.imageUri != null -> {
                    this.imagePuzzle.setImageURI(this.imageUri!!)
                    processImage()
                }
                else -> {
                    this.disableButtons()
                    this.imagePuzzle.setImageResource(R.drawable.image_load_error)
                    Toast.makeText(this.context, "Incorrect image data!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    private fun disableButtons(disable: Boolean = true)
    {
        this.buttonReset.isEnabled = ! disable
        this.buttonResult.isEnabled = ! disable
    }
    
    private fun showResult()
    {
        if (this.pieces.isEmpty() || this.pieces.filterNotNull().isEmpty())
        {
            return
        }
        
        this.disableButtons()
    
        val duration = 500L
        var count = this.pieces.size * 2
        val listener = object: Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) = Unit
            override fun onAnimationCancel(animation: Animator?) = Unit
            override fun onAnimationStart(animation: Animator?) = Unit
            override fun onAnimationEnd(animation: Animator?)
            {
                count --
                if(count <= 0)
                {
                    this@FragmentImagePuzzle.disableButtons(false)
                }
            }
        }
        this.pieces.forEach {image ->
            image!!.isTouchable = false
            
            val layoutParams = image.layoutParams as RelativeLayout.LayoutParams
            val left = image.column * this.cellWidth - image.offsetX
            val top = image.row * this.cellHeight - image.offsetY
            
            ValueAnimator.ofInt(layoutParams.leftMargin, left).setDuration(duration).let {
                it.addUpdateListener {
                    image.layoutParams = layoutParams.apply { this.leftMargin = it.animatedValue as Int }
                }
                it.interpolator = DecelerateInterpolator()
                it.addListener(listener)
                it.start()
            }
            ValueAnimator.ofInt(layoutParams.topMargin, top).setDuration(duration).let {
                it.addUpdateListener {
                    image.layoutParams = layoutParams.apply { this.topMargin = it.animatedValue as Int }
                }
                it.interpolator = DecelerateInterpolator()
                it.addListener(listener)
                it.start()
            }
        }
    }
    
    private fun resetPuzzle() = this.pieces.forEach {imageView->
        imageView!!.isTouchable = true
        this.setRandomPosition(imageView, imageView.layoutParams as RelativeLayout.LayoutParams)
    }
    
    private fun setRandomPosition(imageView: ImagePuzzleCell, layoutParams: RelativeLayout.LayoutParams)
    {
        val random = Random()
        imageView.layoutParams = layoutParams.also {
            it.leftMargin = random.nextInt(this.maxMarginLeft - this.minMarginLeft) + this.minMarginLeft
            it.topMargin = random.nextInt(this.maxMarginTop - this.minMarginTop) + this.minMarginTop
        }
        if (random.nextInt() % 2 == 0)
        {
            imageView.parent.bringChildToFront(imageView)
        }
    }
    
    @SuppressLint("ClickableViewAccessibility")
    private fun addImageView(imageView: ImagePuzzleCell)
    {
        this.layoutContainer.addView(imageView)
        val layoutParams = imageView.layoutParams as RelativeLayout.LayoutParams
        this.setRandomPosition(imageView, layoutParams)
        
        this.testInGridLocation(imageView, layoutParams.leftMargin, layoutParams.topMargin)
        
        var deltaX = 0.0f
        var deltaY = 0.0f
        imageView.setOnTouchListener { view, motionEvent ->
            if (! (view as ImagePuzzleCell).isTouchable)
            {
                return@setOnTouchListener true
            }
            
            val x = motionEvent.rawX
            val y = motionEvent.rawY
            when(motionEvent.action and MotionEvent.ACTION_MASK)
            {
                MotionEvent.ACTION_DOWN -> {
                    deltaX = x - layoutParams.leftMargin
                    deltaY = y - layoutParams.topMargin
                    view.parent.bringChildToFront(view)
                }
                MotionEvent.ACTION_MOVE -> {
                    layoutParams.leftMargin = min(max((x - deltaX).roundToInt(), this.minMarginLeft), this.maxMarginLeft - view.offsetX)
                    layoutParams.topMargin = min(max((y - deltaY).roundToInt(), this.minMarginTop), this.maxMarginTop - view.offsetY)
                    view.layoutParams = layoutParams
                }
                MotionEvent.ACTION_UP -> {
                    val leftUp = min(max((x - deltaX).roundToInt(), this.minMarginLeft), this.maxMarginLeft - view.offsetX)
                    val topUp = min(max((y - deltaY).roundToInt(), this.minMarginTop), this.maxMarginTop - view.offsetY)
                    val (hasValue, left, top, row, column) = this.locatePrecision(leftUp, topUp, view.offsetX, view.offsetY)
                    if (hasValue)
                    {
                        view.locationRow = row
                        view.locationColumn = column
                        view.layoutParams = layoutParams.apply {
                            this.leftMargin = left
                            this.topMargin = top
                        }
                        
                        this.triggerGameResult()
                    }
                }
                MotionEvent.ACTION_CANCEL -> {}
                else -> {}
            }
            return@setOnTouchListener true
        }
    }
    
    private fun testInGridLocation(imageView: ImagePuzzleCell, leftMargin: Int, topMargin: Int)
    {
        val (hasValue, _, _, row, column) = this.locatePrecision(leftMargin, topMargin, 0, 0, precision = 0)
        if (hasValue)
        {
            imageView.locationRow = row
            imageView.locationColumn = column
            
            info("-----------------------------------Piece in right position: [$row, $column]")
        }
    }
    
    private fun triggerGameResult()
    {
        val win = this.pieces.all { it?.isInRightLocation == true }
        if (win)
        {
            Toast.makeText(this.context, "Winner, you are great!", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun locatePrecision(leftUp: Int, topUp: Int, offsetX: Int, offsetY: Int, precision: Int = 8): Tuple5
    {
        val slop = if(precision <= 0) 0 else (this.cellWidth * this.cellWidth + this.cellHeight * this.cellHeight) / precision / precision
        
        val leftColumn = (((leftUp + offsetX - this.minMarginLeft)).toFloat() / this.cellWidth).roundToInt()
        val leftRemainder = leftUp + offsetX - this.minMarginLeft - leftColumn * this.cellWidth
        
        val topRow = (((topUp + offsetY - this.minMarginTop)).toFloat() / this.cellHeight).roundToInt()
        val topRemainder = topUp + offsetY - this.minMarginTop - topRow * this.cellHeight
        
        return if (leftRemainder * leftRemainder <= slop && topRemainder * topRemainder <= slop)
            Tuple5(true, leftColumn * this.cellWidth + this.minMarginLeft - offsetX, topRow * this.cellHeight + this.minMarginTop - offsetY, topRow, leftColumn) else
            Tuple5()
    }
    
    private fun splitImage(context: Context?, imageView: ImageView, rows: Int = 1, columns: Int = 1)
    {
        val matrix = imageView.imageMatrix.values()
        val scaleX = matrix[Matrix.MSCALE_X]
        val scaleY = matrix[Matrix.MSCALE_Y]
    
        val imageDrawable = imageView.drawable!!
        var imageBitmap = imageDrawable.toBitmap()
        
        val originalWidth = imageDrawable.intrinsicWidth
        val originalHeight = imageDrawable.intrinsicHeight
        val actualWidth = (originalWidth * scaleX).roundToInt()
        val actualHeight = (originalHeight * scaleY).roundToInt()
        val displayWidth = imageView.width
        val displayHeight = imageView.height
        
        val paddingHorizontal = (actualWidth - displayWidth).absoluteValue / 2
        val paddingVertical = (actualHeight - displayHeight).absoluteValue / 2
        
        imageBitmap = Bitmap.createScaledBitmap(imageBitmap, actualWidth, actualHeight, true)
        imageBitmap = Bitmap.createBitmap(imageBitmap, paddingHorizontal, paddingVertical, displayWidth, displayHeight)
        
        val cellWidth = displayWidth / columns
        val cellHeight = displayHeight / rows
        val bumpSize = min(cellWidth, cellHeight) / 4
        
        //this.setMarginBorder(paddingHorizontal, paddingVertical, cellWidth, cellHeight, rows, columns)
        this.setMarginBorder(0, 0, cellWidth, cellHeight, rows, columns)
        
        this.pieces = arrayOfNulls<ImagePuzzleCell>(rows * columns)
        val path = Path()
        val paint = Paint()
        for (i in 0 until rows)
        {
            for (j in 0 until columns)
            {
                val cellX = j * cellWidth
                val cellY = i * cellHeight
                val bumpLeft = if (j == 0) 0 else bumpSize
                val bumpTop = if (i == 0) 0 else bumpSize
                
                val sizedBitmap = Bitmap.createBitmap(imageBitmap, cellX - bumpLeft, cellY - bumpTop, cellWidth + bumpLeft, cellHeight + bumpTop)
                val resultBitmap = Bitmap.createBitmap(sizedBitmap.width, sizedBitmap.height, Bitmap.Config.ARGB_8888)
                val imageCell = ImagePuzzleCell(context, row = i, column = j).apply {
                    this.offsetX = bumpLeft
                    this.offsetY = bumpTop
                    this.imageWidth = cellWidth + bumpLeft
                    this.imageHeight = cellHeight + bumpTop
                    this.locationRow = -1
                    this.locationColumn = -1
                }
                val canvas = Canvas(resultBitmap)
                with(path) {
                    val w = resultBitmap.width.toFloat()
                    val h = resultBitmap.height.toFloat()
                    val t = bumpTop.toFloat()
                    val l = bumpLeft.toFloat()
                    
                    this.reset()
                    this.moveTo(w, t)
                    if (i != 0)
                    {
                        this.lineTo(w - (w - l) / 3, t)
                        this.cubicTo(w - (w - l) / 6, t - bumpSize, w - (w - l) * 5 / 6, t - bumpSize, w - (w - l) * 2 / 3, t)
                    }
                    this.lineTo(l, t)
                    if (j != 0)
                    {
                        this.lineTo(l, h - (h - t) * 2 / 3)
                        this.cubicTo(l - bumpSize, h - (h - t) * 5 / 6, l - bumpSize, h - (h - t) / 6, l, h - (h - t) / 3)
                    }
                    this.lineTo(l, h)
                    if (i != rows - 1)
                    {
                        this.lineTo(w - (w - l) * 2 / 3, h)
                        this.cubicTo(w - (w - l) * 5 / 6, h - bumpSize, w - (w - l) / 6, h - bumpSize, w - (w - l) / 3, h)
                    }
                    this.lineTo(w, h)
                    if (j != columns - 1)
                    {
                        this.lineTo(w, h - (h - t) / 3)
                        this.cubicTo(w - bumpSize, h - (h - t) / 6, w - bumpSize, h - (h - t) * 5 / 6, w, h - (h - t) * 2 / 3)
                    }
                    this.close()
                }
                paint.xfermode = null
                paint.style = Paint.Style.FILL
                paint.color = 0xFF000000.toInt()
                canvas.drawPath(path, paint)
                
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                canvas.drawBitmap(sizedBitmap, 0.0f, 0.0f, paint.apply {  })
                
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 8.0f
                paint.color = 0x80FFFFFF.toInt()
                canvas.drawPath(path, paint)
    
                paint.strokeWidth = 3.0f
                paint.color = 0x80000000.toInt()
                canvas.drawPath(path, paint)
                
                imageCell.setImageBitmap(resultBitmap)
                this.pieces[i * columns + j] = imageCell
            }
        }
    }
    
    private fun setMarginBorder(leftStart: Int, topStart: Int, cellWidth: Int, cellHeight: Int, rows: Int, columns: Int)
    {
        this.minMarginLeft = leftStart
        this.minMarginTop = topStart
        this.maxMarginLeft = leftStart + (columns - 1) * cellWidth
        this.maxMarginTop = topStart + (rows - 1) * cellHeight
        
        this.cellWidth = cellWidth
        this.cellHeight = cellHeight
    }
}

data class Tuple5(val hasValue: Boolean = false, val v1: Int = 0, val v2: Int = 0, val v3: Int = 0, val v4: Int = 0)

class ImagePuzzleCell(context: Context?, val row: Int, val column: Int, val rotation: Int = 0): AppCompatImageView(context)
{
    constructor(context: Context?):this(context, -1, -1)
    
    var isInRightLocation: Boolean = false
        get() = this.row == this.locationRow && this.column == this.locationColumn
    
    var offsetX = 0
    var offsetY = 0
    
    var imageWidth = 0
    var imageHeight = 0
    
    var locationRow = -1
    var locationColumn = -1
    
    var currentRotation = 0
    
    var isTouchable = true
}