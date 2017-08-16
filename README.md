# 50-android-kotlin-projects-in-100-days
My everyday Android practice demos with Kotlin in 100 days.

***
## 10. Swipe Refresh RecylcerView

*Date: 2017-08-16*

![ProjectRefreshRecyclerView.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectRefreshRecyclerView/ProjectRefreshRecyclerView.gif)

What I learned from this demo:

* Work with SwipeRefreshLayout and RecyclerView
* Basic AlertDialog and Snackbar usage
* Custom header and footer item in RecyclerView (It looks a little weird!)

***
## 9. Simple Video Player

*Date: 2017-08-14*

![ProjectVideoPlayer.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectVideoPlayer/ProjectVideoPlayer.gif)

What I learned from this demo:

* The first time explore the MediaPlayer with SurfaceView
* Got idea of LayoutParams and its simple properties (ConstraintLayout.LayoutParams)

***
## 8. Basic Material Design

*Date: 2017-08-12*

![ProjectBasicMaterialDesign.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectBasicMaterialDesign/ProjectBasicMaterialDesign.gif)

What I learned from this demo:

* Basic material design elements: DrawerLayout, NavigationView, CoordinatorLayout, AppBarLayout with Toolbar and FloatingActionButton
* Try to use the open source Android libraries of RxAndroid, OkHttp, Gson and Glide, ect.
* DrawerLayout must work with a child with `layout_gravity` property specified.
* Gson tokens with types: `object : TypeToken<List<Turns>>() {}.type` is the right way. 

Resource: [RxAndroid](https://github.com/ReactiveX/RxAndroid), [OkHttp](http://square.github.io/okhttp/), [Gson](https://github.com/google/gson), [Glide](https://github.com/bumptech/glide)

***
## 7. Recycle Image Loader

*Date: 2017-08-11*

![ProjectRecycleImageLoader.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectRecycleImageLoader/ProjectRecycleImageLoader.gif)

What I learned from this demo:

* CardView is very cute, isn't is? (*I hate the gap between image nad text!*)
* Load json data through HttpURLConnection and display view with ViewStub
* Use `doAsync{}` and `uiThread{}` to do asynchronized task in Kotlin
* Read InputStream in Kotlin and convert string data to JSONObject

***
## 6. Basic RecycleView

*Date: 2017-08-09*

![ProjectRecycleView.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectRecycleView/ProjectRecycleView.gif)

What I learned from this demo:

* The very basics of RecycleView
* RecycleView with custom ViewHolder and Adapter, and layout managers
* Using `lazy` delegates , and `Pair<out A, out B>` in Kotlin

**In fact it shows the unexpected results, but I will give more effort to the RecycleView app next time.**

***
## 5. Pick Image

*Date: 2017-08-08*

![ProjectPickImage.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectPickImage/ProjectPickImage.gif)

What I learned from this demo:

* Start a intent and get the result from that
* Work with local images and camera basics
* Display bitmap data on an ImageView

***
## 4. My Location

*Date: 2017-08-06*

![ProjectMyLocation.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectMyLocation/ProjectMyLocation.gif)

What I learned from this demo:

* Work with LocationManager, get GPS providers and locations
* Got to know how to request specified Permissions at RunTime
* Use HttpUrlConnection to fetch data with url and read the input streams
* The LAMDA of `thread` and `runOnUiThread`

**My network is not stable, and I really do a lot of hard-code, I think I can fix that later.**

***
## 3. Tip Calculator

*Date: 2017-08-05*

![ProjectTipCalculator.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectTipCalculator/ProjectTipCalculator.gif)

What I learned from this demo:

* Work with EditText and SeekBar
* The editor of keyboard event handler with EditText
* Strings to formatted floats and strings remove specified prefix in kotlin

**And what I cannot resolve is the focus changes of EditText and auto-hided of keyboard, I hope I can work it later.**

***
## 2. Tap or Hold Counter

*Date: 2017-08-04*

![ProjectTapHoldCounter.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectTapHoldCounter/ProjectTapHoldCounter.gif)

What I learned from this demo:

* Delegates of property in Kotlin
* Basic usage of Timer and TimerTask
* Button long click and touch event listener
* AnkoLogger for test (I have deleted the test code, but the activity has implemented the interface)

***
## 1. Tap Counte

*Date: 2017-08-03*

![ProjectTapCounter.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectTapCounter/ProjectTapCounter.gif)

**What I learned from this demo:**

* Menu resource file creation and option menus add to title bar
* Use anko library to reach UI elements easily in layout
* Button click handler with lambda in kotlin
* Change the button and text view appearance in editor
* Basic usage of Git commands and Github repository with AS 3.0

Resource: [anko](https://github.com/Kotlin/anko)

