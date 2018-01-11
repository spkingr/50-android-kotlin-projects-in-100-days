package me.liuqingwen.android.projectaudioassets

import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import org.jetbrains.anko.*

/**
 * Created by Qingwen on 2018-2018-1-10, project: ProjectAudioAssets.
 *
 * @Author: Qingwen
 * @DateTime: 2018-1-10
 * @Package: me.liuqingwen.android.projectaudioassets in project: ProjectAudioAssets
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

data class Sound(var id:Int? = null, var name:String = "", var path:String = "")

class CustomItemViewUI:AnkoComponent<ViewGroup>
{
    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        val outPadding = 4
        val outSize = Point().apply { ui.ctx.windowManager.defaultDisplay.getSize(this) }
        frameLayout {
            padding = dip(outPadding)
            
            button {
                id = R.id.button_play
                height = outSize.x / MainActivity.COLUMN_COUNT - outPadding * 2
                backgroundResource = R.drawable.shape_round_corner_button
                transformationMethod = null
                textColor = Color.WHITE
                //textSize = sp(16).toFloat()
            }.lparams(width = matchParent)
    
            imageView {
                id = R.id.image_sound
                imageResource = R.drawable.ic_volume_up_black_48dp
                alpha = 0.0f
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) elevation = dip(4).toFloat()
                //translationZ = 4.0f
            }.lparams { gravity = Gravity.CENTER }
        }
    }
}

class CustomItemView(itemView:View) : RecyclerView.ViewHolder(itemView)
{
    private val buttonPlay = itemView.find<Button>(R.id.button_play)
    private val imageSound = itemView.find<ImageView>(R.id.image_sound)
    fun bind(sound: Sound, action: ((Sound)->Unit)? = null)
    {
        this.buttonPlay.text = sound.name
        this.buttonPlay.setOnClickListener {
            sound.id?.let { this.playSoundAnimation() }
            action?.invoke(sound)
        }
    }
    
    private fun playSoundAnimation()
    {
        this.imageSound.alpha = 0.75f
        this.imageSound.scaleX = 1.5f
        this.imageSound.scaleY = 1.5f
        this.imageSound.animate().alpha(0.0f).setStartDelay(500).setDuration(1600).start()
        this.imageSound.animate().scaleX(0.8f).setDuration(2000).start()
        this.imageSound.animate().scaleY(0.8f).setDuration(2000).start()
    }
}

class CustomAdapter(private val dataList:List<Sound>, private val action: ((Sound)->Unit)? = null): RecyclerView.Adapter<CustomItemView>()
{
    override fun getItemCount() = this.dataList.size
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = CustomItemView(CustomItemViewUI().createView(AnkoContext.create(parent!!.context, parent)))
    override fun onBindViewHolder(holder: CustomItemView?, position: Int) = holder!!.bind(this.dataList[position], action)
}