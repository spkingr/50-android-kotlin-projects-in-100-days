package me.liuqingwen.android.projectbetterpracticefragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.layout_fragment_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainFragment : Fragment()
{
    companion object
    {
        fun newInstance(): MainFragment = MainFragment()
    }
    private val dataList by lazy(LazyThreadSafetyMode.NONE) { arrayListOf<Contact>() }
    private val adapter by lazy(LazyThreadSafetyMode.NONE) { MyAdapter(this.activityContext!!, this.dataList, {
        val index = this.recyclerView.getChildAdapterPosition(it)
        this.activityContext?.let { this.startActivity(DetailActivity.getDetailIntent(it, this.dataList, index)) }
    }) }
    private val layoutAnimation by lazy(LazyThreadSafetyMode.NONE) { AnimationUtils.loadLayoutAnimation(this.activityContext, R.anim.anim_item_layout) }
    private var activityContext : Context? = null
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.layout_fragment_main, container, false)
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
    
        this.labelNoData.visibility = View.INVISIBLE
        this.layoutSwipRefresh.setOnRefreshListener {
            this.loadData()
        }
        
        this.recyclerView.adapter = this.adapter
        this.recyclerView.layoutManager = LinearLayoutManager(this.activityContext, LinearLayoutManager.VERTICAL, false)
        this.recyclerView.addItemDecoration(DividerItemDecoration(this.activityContext, DividerItemDecoration.VERTICAL))
        this@MainFragment.recyclerView.layoutAnimation = this.layoutAnimation
        
        this.layoutSwipRefresh.isRefreshing = true
        this.loadData()
    }
    
    private fun loadData()
    {
        this.labelNoData.visibility = View.INVISIBLE
        doAsync {
            val data = this@MainFragment.activityContext?.let { DatabaseHelper.getInstance(it).getAllContacts() }
            this@MainFragment.dataList.clear()
            data?.let { this@MainFragment.dataList.addAll(it) }
            uiThread {
                this@MainFragment.layoutSwipRefresh.isRefreshing = false
                if (this@MainFragment.dataList.size <= 0)
                {
                    this@MainFragment.labelNoData.visibility = View.VISIBLE
                }
                this@MainFragment.recyclerView.scheduleLayoutAnimation()
                this@MainFragment.adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onAttach(context: Context?)
    {
        this.activityContext = context!!
        super.onAttach(context)
    }

    override fun onDetach()
    {
        this.activityContext = null
        super.onDetach()
    }

}
