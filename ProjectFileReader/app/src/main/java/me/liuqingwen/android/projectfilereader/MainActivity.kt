package me.liuqingwen.android.projectfilereader

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.layout_activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import java.io.IOException

class MainActivity : AppCompatActivity(), AnkoLogger
{
    companion object
    {
        const val REQUEST_CODE = 1001
        const val DATA_NAME = "name"
        const val FILE_NAME = "project_file_reader"
    }
    
    private val dataList by lazy(LazyThreadSafetyMode.NONE) { ArrayList<MyContact>() }
    private val adapter by lazy(LazyThreadSafetyMode.NONE) { MyAdapter(this, this.dataList, null) }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)
    
        this.readData()
        this.init()
    }
    
    private fun readData()
    {
        try
        {
            val stream = this.openFileInput(MainActivity.FILE_NAME)
            val reader = stream.bufferedReader()
            
            var id = this.dataList.size + 1
            reader.readLines().forEach { this.dataList.add(MyContact(id ++, it)) }
            this.adapter.notifyDataSetChanged()
        }
        catch (e: IOException)
        {
            e.printStackTrace()
        }
    }
    
    private fun saveData(contact: MyContact)
    {
        try
        {
            val stream = this.openFileOutput(MainActivity.FILE_NAME, android.content.Context.MODE_APPEND)
            val writer = stream.bufferedWriter()
            writer.write(contact.name)
            writer.write("\n")
            writer.flush()
        }
        catch (e: IOException)
        {
            e.printStackTrace()
        }
    }
    
    private fun init()
    {
        this.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        this.recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        this.recyclerView.adapter = this.adapter
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        this.menuInflater.inflate(R.menu.menu_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        when(item?.itemId)
        {
            R.id.menuAdd -> {
                val intent = Intent(this, AddItemActivity::class.java)
                this.startActivityForResult(intent, MainActivity.REQUEST_CODE)
            }
            else -> { toast("Not implement yet!") }
        }
        return true
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == MainActivity.REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            val name = data?.getStringExtra(MainActivity.DATA_NAME)
            name?.let {
                val contact = MyContact(this.dataList.size + 1, it)
                this.dataList.add(contact)
                this.adapter.notifyDataSetChanged()
                
                this.saveData(contact)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
