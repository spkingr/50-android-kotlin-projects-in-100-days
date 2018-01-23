package me.liuqingwen.android.projectamap

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.LatLngBounds
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.maps.model.MyLocationStyle
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.help.Inputtips
import com.amap.api.services.help.InputtipsQuery
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import kotlinx.android.synthetic.main.layout_activity_main.*
import org.jetbrains.anko.toast

//Log:73-135, Lat:17-53
val LatLonPoint.latLon:LatLng
    get()
    {
        return LatLng(this.latitude, this.longitude)
    }
const val CITY = "长沙"

class MainActivity : AppCompatActivity()
{
    private var isFullScreen = false
    private var isSearching = false
    private lateinit var map:AMap
    private val searchItems by lazy(LazyThreadSafetyMode.NONE) { arrayListOf<String>() }
    private val searchAdapter by lazy(LazyThreadSafetyMode.NONE) { ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, this.searchItems) }
    private var poiQuery:PoiSearch.Query? = null
    private val poiSearch by lazy(LazyThreadSafetyMode.NONE) { PoiSearch(this, this.poiQuery).apply { this.setOnPoiSearchListener(this@MainActivity.searchListener) } }
    private val searchListener = object : PoiSearch.OnPoiSearchListener {
        override fun onPoiItemSearched(item: PoiItem?, code: Int)
        {
            this@MainActivity.isSearching = false
        }
        override fun onPoiSearched(result: PoiResult?, code: Int)
        {
            if (code == 1000 && result?.pois != null)
            {
                this@MainActivity.showResult(result.pois)
            }
            else
            {
                this@MainActivity.toast("Search failed!")
            }
            this@MainActivity.isSearching = false
        }
    }
    
    private fun showResult(poiItems:List<PoiItem>)
    {
        this.map.clear()
        
        if (poiItems.isNotEmpty())
        {
            poiItems.forEach {
                val info = "Lat:${it.latLonPoint.latitude}, Log:${it.latLonPoint.longitude}"
                val marker = this.map.addMarker(MarkerOptions().infoWindowEnable(true).snippet(info).position(it.latLonPoint.latLon).title(it.title))
                marker.hideInfoWindow()
            }
            
            val longitude1 = poiItems.maxBy { it.latLonPoint.longitude }!!.latLonPoint.longitude
            val longitude2 = poiItems.minBy { it.latLonPoint.longitude }!!.latLonPoint.longitude
            val latitude1 = poiItems.maxBy { it.latLonPoint.latitude }!!.latLonPoint.latitude
            val latitude2 = poiItems.minBy { it.latLonPoint.latitude }!!.latLonPoint.latitude
            
            val bounds = LatLngBounds.builder().include(LatLng(latitude1, longitude1)).include(LatLng(latitude2, longitude2)).build()
            this.map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)
    
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        
        this.mapView.onCreate(savedInstanceState)
        this.init()
    }
    
    private fun init()
    {
        this.isFullScreen = true
        
        this.supportActionBar?.hide()
        this.window.decorView.setOnSystemUiVisibilityChangeListener {visibility ->
            this.isFullScreen = visibility and View.SYSTEM_UI_FLAG_FULLSCREEN != 0
        }
        
        this.switchMapType.setOnClickListener {
            this.map.mapType = if (this.switchMapType.isChecked) AMap.MAP_TYPE_NORMAL else AMap.MAP_TYPE_SATELLITE
        }
        
        this.buttonSearch.setOnClickListener {
            this.hideKeyboard()
            val text = this.textSearch.text.toString()
            this.searchMap(text)
        }
        
        this.textSearch.setAdapter(this.searchAdapter)
        this.textSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
            {
                if (s?.isNotBlank() == true && s.length >= 2)
                {
                    this@MainActivity.queryInputTips(s.toString())
                }
            }
        })
        this.textSearch.setOnItemClickListener { _, _, _, _ ->
            this.hideKeyboard()
            this.searchMap(this.textSearch.text.toString())
        }
        this.textSearch.setOnClickListener {
            this.textSearch.showDropDown()
        }
        
        this.config()
    }
    
    private fun queryInputTips(text:String)
    {
        val inputTipsQuery = InputtipsQuery(text, CITY)
        inputTipsQuery.cityLimit = true
        val inputTips = Inputtips(this, inputTipsQuery)
        inputTips.setInputtipsListener { list, code ->
            if (code == 1000 && list?.isNotEmpty() == true)
            {
                this.searchAdapter.clear()
                list.map { it.name }.let { this.searchAdapter.addAll(it) }
                this.searchAdapter.notifyDataSetChanged()
            }
        }
        inputTips.requestInputtipsAsyn()
    }
    
    private fun hideKeyboard()
    {
        this.currentFocus?.let {
            val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
    
    private fun searchMap(searchText:String)
    {
        if (this.isSearching || searchText.isBlank())
        {
            return
        }
        this.isSearching = true
        
        this.poiQuery = PoiSearch.Query(searchText, "", CITY)
        this.poiQuery?.pageSize = 10
        this.poiQuery?.pageNum = 1
        
        this.poiSearch.searchPOIAsyn()
    }
    
    private fun config()
    {
        this.map = this.mapView.map
        this.map.setOnMapLoadedListener {
        }
        this.map.setOnMapClickListener {
            if (! this.isFullScreen)
            {
                this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            }
        }
    
        val myLocationStyle = MyLocationStyle().apply {
            this.interval(5000)
            this.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER)
            this.showMyLocation(true)
        }
        this.map.myLocationStyle = myLocationStyle
        
        this.map.isMyLocationEnabled = true
        this.map.setOnMyLocationChangeListener {
            if (it.latitude in 17.0..53.0 && it.longitude in 73.0..135.0)
            {
                this.map.moveCamera(CameraUpdateFactory.changeLatLng(LatLng(it.latitude, it.longitude)))
            }
        }
        
        this.map.uiSettings.isCompassEnabled = true
        this.map.uiSettings.isZoomControlsEnabled = true
        this.map.uiSettings.isMyLocationButtonEnabled = true
        /*this.map.setLocationSource(object : LocationSource{
            override fun deactivate()
            {
                info("------------------>setLocationSource: deactivate")
            }
            override fun activate(p0: LocationSource.OnLocationChangedListener?)
            {
                info("------------------>setLocationSource: activate")
            }
        })*/
        
        this.map.setOnMarkerClickListener {
            this.map.animateCamera(CameraUpdateFactory.newLatLngZoom(it.position, 17.0f))
            it.showInfoWindow()
            false
        }
    }
    
    override fun onDestroy()
    {
        super.onDestroy()
        this.mapView.onDestroy()
    }
    
    override fun onResume()
    {
        super.onResume()
        this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        this.hideKeyboard()
        this.mapView.onResume()
    }
    
    override fun onPause()
    {
        super.onPause()
        this.mapView.onPause()
    }
    
    override fun onSaveInstanceState(outState: Bundle?)
    {
        super.onSaveInstanceState(outState)
        this.mapView.onSaveInstanceState(outState)
    }
}
