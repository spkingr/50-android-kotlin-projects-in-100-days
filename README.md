# 50-android-kotlin-projects-in-100-days
My everyday Android practice demos with Kotlin in 100 days.


***
## 29. A Map Demo

*Date: 2018-1-23*

![ProjectAMap.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectAMap/ProjectAMap.gif)

What I learned from this demo:

* Show map, display marker, animate camera, search, deal with the latitude and longitude with A Map API
* Fullscreen control, show and hide the soft keyboard
* Work with the `AutoCompleteTextView` widget

**Resource: [A MAP](http://lbs.amap.com/)**

***
## 28. Custom View The Switch

*Date: 2018-1-21*

![ProjectMySwitch.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectMySwitch/ProjectMySwitch.gif)

What I learned from this demo:

* Custom view with basic workflow: measure, layout, draw and touch event handle
* Add custom properties or attributes in XML of view
* The basics of drawing api in Android with: path, paint, canvas

***
## 27. Play With Audio Assets

*Date: 2018-1-11*

![ProjectAudioAssets.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectAudioAssets/ProjectAudioAssets.gif)

What I learned from this demo:

* No XML layouts, the UI is completely replaced with Anko layout codes
* Work with assets in Android, and use `AssetManager` to do asset management
* Use `SoundPool` to play tiny `wav` audio files
* The basic knowledge of Anko layouts, pure code to make views

***
## 26. Better Practice Fragment (Part.1)

*Date: 2018-1-8*

![ProjectBetterPracticeFragment.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectBetterPracticeFragment/ProjectBetterPracticeFragment.gif)

What I learned from this demo:

* Use RecylcerView list item animations with XML resource
* Understand the `layout_behavior` in `CoordinatorLayout` and `layout_anchor` property
* Try to use Fragment instead the Activity for better practise, it makes ViewPager very esay to build
* Work with the alert dilog with custom view

**The refactor is not so easy for me, a lot of functionalities is not finished, and I will try to get that done later.**

***
## 25. Image Uploader

*Date: 2017-12-29*

![ProjectImageUploader.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectImageUploader/ProjectImageUploader.gif)

What I learned from this demo:

* Work with OKHttp to post data: upload files, get and send cookies, retrive the result
* Use XML Shape to create nice controls such as buttons
* Crop image and get the bitmap data through `FileProvider` in API greater than 24
* Use view animations, Gson to analysis json string, `indeterminateProgressDialog` and `alert` in anko

**Realy thanks to the guy in Weixin group: @高尚的乞丐王子 for solving my code problem, while reuse the animation XML will cause wried things!**

***
## 24. Photo Wall

*Date: 2017-12-26*

![ProjectPhotoWall.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectPhotoWall/ProjectPhotoWall.gif)

What I learned from this demo:

* Get images throught network, decode stream and display
* Use `LruCache` to cache the bitmap data for quick loading and recylcling data usage
* The same Activity is setted as both the **Detail** activity and **Add New** activity
* Load data from database in the asynchronized way

**If too much photos loaded, will the app get the changce to crash for OOM? I should try to figure it out!**

***
## 23. Parcelable Data Objects

*Date: 2017-12-23*

![ProjectParcelableData.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectParcelableData/ProjectParcelableData.gif)

What I learned from this demo:

* Use `Parcelable` objects for data passing between activities
* Try Android unit test with: **Instrumented Test** and **Unit Test**
* Data class used both as Parcelable data object and Room database entity
* Solve two problems through StackOverFlow.com: [Gradle error, failed to create directory](https://stackoverflow.com/questions/46512990/gradle-error-failed-to-create-directory) and [Empty test suite](https://stackoverflow.com/questions/14381694/why-is-the-android-test-runner-reporting-empty-test-suite)

**A lot of functionalities are not implemeted yet in this app, and I will finish that in my next days, hold on please! :)**

***
## 22. Simple View Pager

*Date: 2017-12-16*

![ProjectViewPager.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectViewPager/ProjectViewPager.gif)

What I learned from this demo:

* Work with `ViewPager` and `TabLayout` together to display walk through pages
* Use `FragmentStatePagerAdapter` as the `ViewPager` adapter and, the fragments
* Finish the activity so that make sure it is removed from the stack

**Resource: [ViewPager Tutorial: Getting Started in Kotlin](https://www.raywenderlich.com/169774/viewpager-tutorial-android-getting-started-kotlin)**

***
## 21. The Floating Window

*Date: 2017-12-16*

![ProjectFloatingWindow.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectFloatingWindow/ProjectFloatingWindow.gif)

What I learned from this demo:

* No layout XML files, but replace with the anko layout library to create views
* Use `WindowManager` to display floating views on the window
* Deal with the back button pressed to hide the activity, prevent from finishing the app

**After 2 months at last! Still 2 problems or questions:**
**1. If back pressed and app is killed, then the `android.view.WindowLeaked` exception throws, how to solve?**
**2. In the emulator, if the drag and drop event happens outside, then the position of the window will be some wried.**

***
## 20. Simple Search Web View

*Date: 2017-10-18*

![ProjectSimpleWebview.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectSimpleWebview/ProjectSimpleWebview.gif)

What I learned from this demo:

* Work with WebView, set the client as `WebViewClient` and `WebChromeClient`
* Use the ToolBar instead of ActionBar, try `SearchView` in the tool bar
* Save and retrieve list items (`StringSet`) in shared preferences
* Add or remove item in `RecyclerView`, handle long click events on list item
* Deal with the WebView in NestedScrollView through the library: `NestedScrollWebView` in Github

Resource: [NestedScrollWebView](https://gist.github.com/alexmiragall/0c4c7163f7a17938518ce9794c4a5236)

***
## 19. Downloader With Notifications

*Date: 2017-10-14*

![ProjectDownloaderWithNotification.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectDownloaderWithNotification/ProjectDownloaderWithNotification.gif)

What I learned from this demo:

* Use the basic notifications in Android
* The asynchronized task with `doAsync` and `uiThread` in Kotlin
* Basic file and stream operations in Android 

***
## 18. Basic File Operation

*Date: 2017-10-4*

![ProjectFileReader.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectFileReader/ProjectFileReader.gif)

What I learned from this demo:

* Use SharedPreferences to store and retrieve simple data
* Basic file operations on Android system, with `openFileInput` and `openFileOutput`
* Set data in the activity result and return

***
## 17. Simple Broadcast Receiver

*Date: 2017-09-27*

![ProjectBroadcastReceiver.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectBroadcastReceiver/ProjectBroadcastReceiver.gif)

What I learned from this demo:

* Use one of the most four important components in Android: Broadcast Receiver
* Extension functions in Kotlin with `AppCompatActivity`
* Try the open-source library: EventBus to post and handle events, and also the CircleImageView
* Reduce the redundant of layout by using `<inlucde>` tags

**I am still not very clear with Android `BroadcastReceiver`, I found that it sometimes(especially for the single app development) can be replaced with `EventBus` through publish/subscribe pattern, is that right?**

***
## 16. Self Adaption

*Date: 2017-09-12*

![ProjectSelfAdaption.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectSelfAdaption/ProjectSelfAdaption.gif)

What I learned from this demo:

* Use the Fragments in the Activities
* Specify different layouts for different size or screen orientation (layout-land and layout-large)
* Use the empty view element as spacer or divider (be careful of the tag, it is `View` not `view`!)
* Dynamically set the visibility of view

**Till now I have no idea of using the `savedInstanceState` variable to make the data consistent while rotate the screen orientation, I should try it out later.**

***
## 15. Database with Room

*Date: 2017-09-04*

![ProjectDatabaseRoom.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectDatabaseRoom/ProjectDatabaseRoom.gif)
![ProjectDatabaseRoom_adb_shell_sqlite3.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectDatabaseRoom/ProjectDatabaseRoom_adb_shell_sqlite3.gif)

What I learned from this demo:

* Use the Room Persistence Library for the basic database operations: INSERT/DELETE/UPDATE/QUERY
* Work with data class in Kotlin and the `let` lamda, and the annotations
* Convert date types to string types, and vice versa by using `SimpleDateFormat` or `DateFormat.getDateInstance()`
* Use `adb shell` and `sqlite3` command to query the data in emulator local system files:

```bash
$ adb quit

# for more than one device found here
$ adb devices
$ adb -s <emulator name> shell
$ cd data/data/me.liuqingwen.android.projectdatabaseroom/databases

$ sqlite3
$ .open <dbname.db>
$ .tables
$ SELECT * FROM <table name>
```

***
## 14. Activity Animations

*Date: 2017-09-01*

![ProjectActivityAnimations.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectActivityAnimations/ProjectActivityAnimations.gif)

What I learned from this demo:

* Transitions between activities use `overridePendingTransition`
* Work with animation xml resource file, the difference of "50%" and "50%p" (relative to parent view)
* Override `onBackPressed` method to finish the activity

***
## 13. Simple Activity with Intent

*Date: 2017-08-31*

![ProjectActivityIntent.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectActivityIntent/ProjectActivityIntent.gif)

What I learned from this demo:

* Dealing with the custom item click handler of RecyclerView
* Start an intent to show another Activity by `startActivityForResult`
* Get the result from another activity through method of `onActivityResult`

***
## 12. Simple Animations with Custom View

*Date: 2017-08-30*

![ProjectSimpleAnimation.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectSimpleAnimation/ProjectSimpleAnimation.gif)

What I learned from this demo:

* Custom view with custom attributes (declared in the xml value file)
* Use `onMeasure` to set the correct size of view
* Use `onDraw` to display the paint on the canvas
* Work with the basic object animator and animator-set
* The importance of lazy properties in Kotlin, think about the code:

```kotlin
//the [sunColor] initialized here will be changed later in the constructor through xml user attributes.
private var sunColor = Color.RED
/*
//the paint directly initialized will not be the expected one, as the [sunColor] will change later for xml attributes!
private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    this.color = this@SunView.sunColor
    this.isAntiAlias = true
}
*/
//by using the lazy properties, the paint will be all right with the [sunColor] property!
private val paint by lazy {
    Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = this@SunView.sunColor
        this.isAntiAlias = true
    }
}
```

***
## 11. Simple Scalable ImageView

*Date: 2017-08-28*

![ProjectScalableImageView.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectScalableImageView/ProjectScalableImageView.gif)

What I learned from this demo:

* Multiple constructors from base class inheritance in Kotlin
* The basic knowledge of custom view: onLayout (left, right, top, bottom)/setFrame/onTouchEvent (Here I should always return `true` for receiving other touch events)
* Multi-touch handle on views: use `event.actionMasked` instead of `event.action`

**This is a bad-experienced project(view), I think I have to improve that in the next days while learning.**

***
## 11. Simple ASynchronized Works

*Date: 2017-08-17*

![ProjectAsyncWorks.gif](https://github.com/spkingr/50-android-kotlin-projects-in-100-days/raw/master/ProjectAsyncWorks/ProjectAsyncWorks.gif)

What I learned from this demo:

* Try to figure out the differences of usage between Handler and AsyncTask in Android
* Download file from server via OkHttp and read bytes from InputStream
* RandomAccessFile for writing file content from specified positions

**Till now I can't figure out a solution to pause/resume the downloading tasks, and I will try it later.**

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

