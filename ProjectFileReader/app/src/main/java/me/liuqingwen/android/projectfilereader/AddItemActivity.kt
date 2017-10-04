package me.liuqingwen.android.projectfilereader

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.layout_activity_add_item.*
import org.jetbrains.anko.toast

class AddItemActivity : AppCompatActivity()
{
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_add_item)
        
        this.init()
    }
    
    private fun init()
    {
        this.supportActionBar?.setHomeButtonEnabled(true)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        this.buttonAdd.setOnClickListener {
            if (this.textName.text.isBlank())
            {
                toast("Invalid name, please try again!")
            }
            else
            {
                val intent = Intent()
                intent.putExtra(MainActivity.DATA_NAME, this.textName.text.toString())
                this.setResult(Activity.RESULT_OK, intent)
                this.finish()
            }
        }
    }
    
    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        when(item?.itemId)
        {
            android.R.id.home -> {
                this.onBackPressed()
            }
            else -> {}
        }
        return true
    }
    
    override fun onBackPressed()
    {
        if (this.textName.text.isBlank())
        {
            this.finish()
            super.onBackPressed()
        }
        else
        {
            AlertDialog.Builder(this).setTitle("Are you sure?").setMessage("Quit without saving?").setNegativeButton("Stay", null).setPositiveButton("Quit", {_, _ ->
                this.finish()
                super.onBackPressed()
            }).show()
        }
    }
}
