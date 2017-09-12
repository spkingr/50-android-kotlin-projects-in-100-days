package me.liuqingwen.android.projectselfadaption

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.layout_list_fragment.view.*
import org.jetbrains.anko.AnkoLogger
import java.util.*

class ListFragment:Fragment(), AnkoLogger
{
    companion object
    {
        private val POST_TITLES = arrayOf("Helping indie developers get discovered on Google Play"
                                         , "Introducing Android Native Development Kit r16"
                                         , "Optimize your Android apps for Chromebooks"
                                         , "Enroll for app signing in the Google Play Console & secure your app using Google’s robust security infrastructure"
                                         , "Create stickers for Gboard on Google Play"
                                         , "Android Developer Story: Zalando increases installs and revenue by focusing on app quality"
                                         , "Updates to Google Play policy promote standalone Android Wear apps")
        private val POST_AUTHORS = arrayOf("Adriana Puchianu"
                                          ,"Dan Albert"
                                          , "Cheryl Lindo Jones"
                                          , "Kobi Glick"
                                          , "Alan Ni"
                                          , "Adriana Puchianu"
                                          , "Hoi Lam")
        private val POST_CONTENT = arrayOf("""There are increasing growth opportunities for indie game developers, but being one can still feel daunting in today's crowded gaming industry. We've been working hard to help indie developers find an audience and to recognize them for their creativity and innovation. We launched the Indie Corner as a destination for exciting new games along with longstanding indie masterpieces. Since launch, more than 380 games have been featured. Earlier this year, we launched Android Excellence which showcases apps and games that deliver incredible user experiences on Android, while providing another opportunity to be discovered on Google Play. ..."""
                                          ,"""The latest version of the Android Native Development Kit (NDK), Android NDK r16 Beta 1, is now available for download. It is also available in the SDK manager via Android Studio.
NDK r16 is a big milestone for us, because it's the first release that we're ready to recommend that people start migrating to libc++! More on this later.
We've also updated libc++ and its related projects, so this release has improved support for C++1z. Keep in mind that until C++1z becomes C++17, everything included is subject to change. ..."""
                                          , """As more Chromebooks are enabled with Google Play, now is a great time to optimize your Android app for Chromebooks to reach a larger audience. The changes made to optimize for large screens will benefit mobile devices that are able to project to desktop monitors, like the Samsung Galaxy S8. The current list of Chromebooks that can access the Play Store continues to grow. ..."""
                                          , """Every app on Android is signed with a key. This key is used to ensure the app's integrity by checking that updates are signed with the same signature. In the past, the burden of securely holding the signing key has always been with the developer. We're now offering an app signing service on Google Play that can help you if you lose or compromise your key. ..."""
                                          , """Messaging is getting more and more expressive -- today you can say I love you with an emoji, a gif, or a sticker. Millions of users share expressive content every day on Android devices using Gboard as their default keyboard. We want to push expression even further by allowing developers to create their own stickers for Gboard. Some of our early partners include Bitmoji, Disney, and even our own Allo team. Once published, your stickers could be seen and shared by millions of users around the world. ..."""
                                          , """Based in Berlin, Zalando is Europe's leading online fashion platform. With more than 70% of its traffic now coming from mobile, the company has invested a lot in improving the quality of its app to provide a good user experience. Investing in bridging the online and the offline worlds, as well as providing a seamless cross-platform experience, has had positive results on their user engagement and revenue. Using features like A/B testing, the pre-launch report and the new release dashboard from the Google Play Console, Zalando saw a 6% increase in installs and a 15% increase in the users' lifetime value. ..."""
                                          , """Android Wear 2.0 represents the the latest evolution of the Android Wear platform. It introduced the concept of standalone apps that can connect to the network directly and work independently of a smartphone. This is critical to providing apps not only to our Android users, but also iOS users - which is increasingly important as we continue to expand our diverse ecosystem of watches and users. In addition, Wear 2.0 brought multi-APK support to Wear apps, which reduces the APK size of your phone apps, and makes it possible for iOS users to experience your Wear apps. ..."""
                                          )
    }
    
    var onPostClickHandler:((Post, Int) -> Unit)? = null
    
    private val dataList by lazy { ArrayList<Post>() }
    private val myAdapter by lazy { CustomAdapter(this.context, this.dataList, null) }
    private lateinit var recyclerView:RecyclerView
    
    fun getPostAt(index:Int) = this.dataList[index]
    
    fun setLabelDateVisible(visible:Boolean)
    {
        this.myAdapter.dateLabelVisible = visible
    }
    
    override fun onAttach(context: Context?)
    {
        super.onAttach(context)
        
        this.myAdapter.itemClick = { view ->
            this.onPostClickHandler?.let {
                val index = this.recyclerView.getChildAdapterPosition(view)
                val post = this.dataList[index]
                it(post, index)
            }
        }
        
        this.loadData()
    }
    
    private fun loadData()
    {
        val count = ListFragment.POST_TITLES.size
        repeat(count) {
            val post = Post(ListFragment.POST_TITLES[it], ListFragment.POST_CONTENT[it], ListFragment.POST_AUTHORS[it], Date(), (Math.random() * 5.0 + 1).toFloat())
            this.dataList.add(post)
        }
        this.myAdapter.notifyDataSetChanged()
    }
    
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater!!.inflate(R.layout.layout_list_fragment, container, false)!!
        this.recyclerView = view.recyclerViewList
        this.recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        this.recyclerView.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        this.recyclerView.adapter = this.myAdapter
        return view
    }
}