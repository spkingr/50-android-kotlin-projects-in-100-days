package me.liuqingwen.android.projectbetterpracticefragment

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.layout_activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast


class MainActivity : AppCompatActivity()
{
    private lateinit var fragment:MainFragment
    private val alertView by lazy(LazyThreadSafetyMode.NONE) {
        val parent:ViewGroup? = null;
        val view = LayoutInflater.from(this).inflate(R.layout.layout_fragment_detail, parent)
        view
    }
    private val alertDialog by lazy(LazyThreadSafetyMode.NONE) { alert {
        this.title = "Add New Contact"
        this.customView = this@MainActivity.alertView
        this.positiveButton("OK"){ this@MainActivity.toast("Not implemented yet.") }
        this.negativeButton("Cancel"){ this@MainActivity.toast("Not implemented yet.") }
    } }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)
        
        this.init()
    }
    
    private fun init()
    {
        this.setSupportActionBar(this.toolbar)
        this.supportActionBar?.title = "Contact List"
        
        this.buttonAdd.setOnClickListener {
            this.alertDialog.show()
        }
        
        this.fragment = this.supportFragmentManager.findFragmentById(R.id.layoutMainContainer) as? MainFragment ?: MainFragment.newInstance().apply {
            this@MainActivity.supportFragmentManager.beginTransaction().add(R.id.layoutMainContainer, this).commit()
        }
    }
}
