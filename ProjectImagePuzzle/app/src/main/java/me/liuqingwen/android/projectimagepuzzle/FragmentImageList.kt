package me.liuqingwen.android.projectimagepuzzle

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseSectionQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.SectionEntity
import kotlinx.android.synthetic.main.item_image_view.view.*
import kotlinx.android.synthetic.main.item_section_header.view.*
import kotlinx.android.synthetic.main.layout_fragment_list.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.info
import org.jetbrains.anko.uiThread

/**
 * Created by Qingwen on 2018-8-1, project: ProjectImagePuzzle.
 *
 * @Author: Qingwen
 * @DateTime: 2018-8-1
 * @Package: me.liuqingwen.android.projectimagepuzzle in project: ProjectImagePuzzle
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

class FragmentImageList: Fragment(), AnkoLogger
{
    interface IFragmentInteractionListener: IFragmentActivity
    {
        fun onSelectImagePath(path: String)
    }
    
    companion object
    {
        fun newInstance() = FragmentImageList()
        private const val SPAN_COUNT = 3
    }
    
    private var listener: IFragmentInteractionListener? = null
    private val imageList by lazy { arrayListOf<ImageTypeSection>() }
    private val adapter by lazy { ImageListAdapter(this.imageList, R.layout.item_image_view, R.layout.item_section_header, this.listener?.assetManager) }
    
    override fun onAttach(context: Context?)
    {
        super.onAttach(context)
        this.listener = if (context is IFragmentInteractionListener) context else throw RuntimeException(context.toString() + " must implement IFragmentInteractionListener")
    }
    
    override fun onStart()
    {
        super.onStart()
        this.adapter.setOnItemClickListener { _, _, position ->
            val section = this.imageList[position]
            if (! section.isHeader)
            {
                this.listener?.onSelectImagePath(section.t)
            }
        }
        
        this.recyclerView.layoutManager = GridLayoutManager(this.context, FragmentImageList.SPAN_COUNT)
        this.adapter.setSpanSizeLookup{ _, position -> if (this.imageList[position].isHeader) FragmentImageList.SPAN_COUNT else 1 }
        this.recyclerView.adapter = this.adapter
        this.recyclerView.addItemDecoration(MarginItemDecoration(8, FragmentImageList.SPAN_COUNT))
    
        this.swipeRefreshLayout.setOnRefreshListener { this.loadAssets() }
        this.swipeRefreshLayout.isRefreshing = true
        this.loadAssets()
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.layout_fragment_list, container, false)
    
    private fun loadAssets()
    {
        doAsync {
            val list = this@FragmentImageList.imageList.apply {
                this.clear()
                this.add(ImageTypeSection(true, this@FragmentImageList.context?.getString(R.string.build_in_assets) ?: "Assets"))
            }
            this@FragmentImageList.listener?.assetManager?.list(MainActivity.ASSET_DIRECTORY_NAME)?.forEach {
                list.add(ImageTypeSection(it))
            }
            uiThread {
                it.swipeRefreshLayout.isRefreshing = false
                it.adapter.notifyDataSetChanged()
            }
        }
    }
}

class MarginItemDecoration(private val margin: Int, private val columns: Int): RecyclerView.ItemDecoration()
{
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) = with(outRect){
        val headerCount = 1
        this.top = if (parent.getChildAdapterPosition(view) < headerCount) margin else 0
        this.left = if (parent.getChildAdapterPosition(view) % columns == headerCount) margin else 0
        this.right = margin
        this.bottom = margin
        Unit
    }
}

class ImageItemHolder(itemView: View): BaseViewHolder(itemView)
{
    fun bindItem(assetManager: AssetManager?, path: String)
    {
        this.itemView.progressItem.isGone = true
        if (assetManager == null)
        {
            this.itemView.imageItem.setImageResource(R.drawable.image_load_error)
            this.itemView.labelName.text = this.itemView.context?.getString(R.string.error).orEmpty()
            return
        }
        
        this.itemView.imageItem.post {
            val stream = assetManager.open("${MainActivity.ASSET_DIRECTORY_NAME}/$path")
            val bitmap = sampleBitmapData(this.itemView.imageItem.width, this.itemView.imageItem.height, stream)
            this.itemView.imageItem.setImageBitmap(bitmap)
        }
        
        val name = path.substringBefore(".")
        this.itemView.labelName.text = name
    }
    
    fun setupHeader(title: String)
    {
        this.itemView.labelHeader.text = title
    }
}

class ImageTypeSection:SectionEntity<String>
{
    constructor(isHeader: Boolean, header: String) : super(isHeader, header)
    constructor(path: String):super(path)
}

class ImageListAdapter(dataList: List<ImageTypeSection>, @LayoutRes layoutItemId: Int, @LayoutRes layoutHeaderId: Int, private val assetManager: AssetManager?)
    : BaseSectionQuickAdapter<ImageTypeSection, ImageItemHolder>(layoutItemId, layoutHeaderId, dataList)
{
    override fun convert(helper: ImageItemHolder?, item: ImageTypeSection?)
    {
        item?.let{ helper?.bindItem(this.assetManager, it.t) }
    }
    
    override fun convertHead(helper: ImageItemHolder?, item: ImageTypeSection?)
    {
        helper?.setupHeader(item?.header ?: "Untitled")
    }
}
