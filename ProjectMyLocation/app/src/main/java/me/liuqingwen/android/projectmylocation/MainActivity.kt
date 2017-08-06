package me.liuqingwen.android.projectmylocation

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.layout_activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), AnkoLogger
{
    private val locationManager by lazy {
        (this.getSystemService(android.content.Context.LOCATION_SERVICE) as LocationManager).apply {
            this.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 0f, object:LocationListener {
                override fun onLocationChanged(l: Location?)
                {
                    if(l?.latitude != this@MainActivity.lastLocation?.latitude || l?.longitude != this@MainActivity.lastLocation?.longitude)
                    {
                        this@MainActivity.displayLocation(l)
                    }
                }
                override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
                override fun onProviderEnabled(p0: String?) {}
                override fun onProviderDisabled(p0: String?) {}
            })
        }
    }
    private var lastLocation:Location? = null
    private var isFetching = false
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)
        
        this.init()
    }
    
    private fun init()
    {
        this.buttonGetLocation.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
               ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 101)
            }
            else
            {
                this.getLocation()
            }
        }
    }
    
    private fun getLocation()
    {
        this.labelLocation.text = getString(R.string.waiting)
        try
        {
            val providers = this.locationManager.allProviders
            if(providers == null || providers.size == 0)
            {
                this.toast("No GPS location providers available, please check!")
                this.labelLocation.text = getString(R.string.nogps)
                return
            }
            
            this.lastLocation = if(providers.contains(LocationManager.GPS_PROVIDER)) this.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) else this.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            this.displayLocation(this.lastLocation, true)
        }
        catch(e: SecurityException)
        {
            this.toast("Error while get location: ${e.message}")
            this.labelLocation.text = getString(R.string.errorinfo)
        }
    }
    
    private fun displayLocation(location: Location?, requestAddress:Boolean = false)
    {
        val locationString = StringBuilder()
        location?.let {
            locationString.append("Location: (")
            locationString.append(location.latitude).append(" , ")
            locationString.append(location.longitude).append(")")
        }
        this.labelLocation.text = locationString.toString()
        
        if(requestAddress)
        {
            this.requestAddress()
        }
    }
    
    private fun requestAddress()
    {
        if(this.isFetching)
        {
            return
        }
        this.labelAddress.text = ""
        
        this.lastLocation?.let {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET), 102)
            }
            else
            {
                this.fetchAddress(this.lastLocation!!)
            }
        }
    }
    
    private fun fetchAddress(location: Location)
    {
        this.isFetching = true
        thread {
            val url = URL("http://maps.googleapis.com/maps/api/geocode/json?latlng=${location.latitude},${location.longitude}&sensor=false")
            try
            {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 15000
                connection.addRequestProperty("Accept-Language","zh-CN")
                if (connection.responseCode == 200)
                {
                    val stream = connection.inputStream
                    val address = stream.bufferedReader().lineSequence().asSequence().firstOrNull { it.contains("formatted_address") }
                    runOnUiThread {
                        this.labelAddress.text = address?.substring(32, address.lastIndexOf('"')) ?: "" //         "formatted_address" : "中国湖南省长沙市天心区书香路",
                    }
                }
                else
                {
                    this.labelAddress.text = getString(R.string.noconn)
                }
            }catch (e:IOException)
            {
                e.printStackTrace()
            }catch (e:SocketTimeoutException)
            {
                e.printStackTrace()
            }catch (e:Exception)
            {
                e.printStackTrace()
            }
            
            this.isFetching = false
        }
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        if (requestCode == 101 && grantResults.all { it == PackageManager.PERMISSION_GRANTED })
        {
            this.getLocation()
        }else if(requestCode == 102 && grantResults.all { it == PackageManager.PERMISSION_GRANTED })
        {
            this.fetchAddress(this.lastLocation!!)
        }
    }
}
