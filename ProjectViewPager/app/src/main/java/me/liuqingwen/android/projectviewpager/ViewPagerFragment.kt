package me.liuqingwen.android.projectviewpager

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.layout_pager_item.view.*

class ViewPagerFragment : Fragment()
{
    companion object
    {
        private val ARG_TITLE = "title"
        private val ARG_IMAGE_URL = "image_url"
        private val ARG_CONTENT = "content"
        
        fun newInstance(page: Page)  = ViewPagerFragment().apply {
            val args = Bundle().apply {
                this.putString(ARG_TITLE, page.title)
                this.putInt(ARG_IMAGE_URL, page.image)
                this.putString(ARG_CONTENT, page.content)
            }
            this.arguments = args
        }
    }
    
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?):View?  = inflater!!.inflate(R.layout.layout_pager_item, container, false)
            .apply {
                this.textView.text = this@ViewPagerFragment.arguments.getString(ViewPagerFragment.ARG_CONTENT)
                this.imageView.setImageResource(this@ViewPagerFragment.arguments.getInt(ViewPagerFragment.ARG_IMAGE_URL))
            }
    
}
