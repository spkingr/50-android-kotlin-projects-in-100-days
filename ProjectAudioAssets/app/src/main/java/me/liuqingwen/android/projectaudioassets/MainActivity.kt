package me.liuqingwen.android.projectaudioassets

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView

class MainActivity : AppCompatActivity()
{
    companion object
    {
        const val COLUMN_COUNT = 3
        const val MAX_SOUND_POOL = 5
        private const val ASSETS_FOLDER = "carton"
    }
    private val dataList by lazy(LazyThreadSafetyMode.NONE) { arrayListOf<Sound>() }
    private val adapter by lazy(LazyThreadSafetyMode.NONE) { CustomAdapter(this.dataList, this::clickAction) }
    private val layoutManager by lazy(LazyThreadSafetyMode.NONE) { GridLayoutManager(this, MainActivity.COLUMN_COUNT) }
    private lateinit var recyclerView:RecyclerView
    private val soundPool by lazy(LazyThreadSafetyMode.NONE) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            SoundPool.Builder()
                    .setMaxStreams(MainActivity.MAX_SOUND_POOL)
                    .setAudioAttributes(AudioAttributes.Builder()
                                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                                .setUsage(AudioAttributes.USAGE_GAME)
                                                .build())
                    .build()
        else
            SoundPool(MainActivity.MAX_SOUND_POOL, AudioManager.STREAM_MUSIC, 0)
    }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        frameLayout {
            lparams(width = matchParent, height = matchParent)
            padding = dip(4)
            
            this@MainActivity.recyclerView = recyclerView{
                adapter = this@MainActivity.adapter
                layoutManager = this@MainActivity.layoutManager
            }
        }
        
        this.init()
    }
    
    private fun init()
    {
        this.loadData()
    }
    
    private fun clickAction(sound: Sound)
    {
        sound.id?.let { this.soundPool.play(it, 1.0f, 1.0f, 1, 0, 1.0f) }
    }
    
    private fun loadData()
    {
        val list = this.assets.list(MainActivity.ASSETS_FOLDER)
        list.forEach {
            val path = "${MainActivity.ASSETS_FOLDER}/$it"
            val name = it.removeSuffix(".wav")
            val id = try { this.assets.openFd(path).run { this@MainActivity.soundPool.load(this, 1) } } catch (e:Exception) { null }
            this.dataList.add(Sound(id, name, path))
        }
        //this.adapter.notifyDataSetChanged()
    }
    
    override fun onDestroy()
    {
        this.soundPool.release()
        super.onDestroy()
    }
}
