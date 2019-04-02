package com.autolink.sayaradz.ui.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.get
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.autolink.sayaradz.R
import com.autolink.sayaradz.ui.adapter.announcement.AnnouncementsAdapter
import com.autolink.sayaradz.ui.adapter.brand.BrandsAdapter
import com.autolink.sayaradz.ui.adapter.model.ModelsAdapter
import com.autolink.sayaradz.ui.adapter.version.VersionsAdapter
import com.autolink.sayaradz.ui.fragment.newcar.BrandsFragment
import com.autolink.sayaradz.ui.fragment.newcar.VersionProfileFragment
import com.autolink.sayaradz.ui.fragment.newcar.VersionsFragment
import com.autolink.sayaradz.util.OnScrollStateChangedListener
import com.autolink.sayaradz.util.RepositoryKey
import com.autolink.sayaradz.util.getViewModel
import com.autolink.sayaradz.util.setupWithNavController
import com.autolink.sayaradz.viewmodel.AnnouncementsViewModel
import com.autolink.sayaradz.viewmodel.UserViewModel
import com.autolink.sayaradz.vo.Announcement
import com.autolink.sayaradz.vo.Brand
import com.autolink.sayaradz.vo.Model
import com.autolink.sayaradz.vo.Version
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity: AppCompatActivity(),
                    BrandsAdapter.OnBrandsClickListener,
                    ModelsAdapter.OnModelClickListener,
                    VersionsAdapter.OnVersionClickListener,
                    OnScrollStateChangedListener,
                    AnnouncementsAdapter.OnAnnouncementClickListener{

    companion object {
        private const val TAG  = "MainActivity"
    }

    private lateinit var mUserViewModel: UserViewModel

    private var currentNavController: LiveData<NavController>? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mUserViewModel = getViewModel(this, RepositoryKey.USER_REPOSITORY) as UserViewModel


        if(mUserViewModel.isUserSignIn()){

            mUserViewModel.initCarDriverProfile()



        }else{
            val intent = Intent(this,AuthActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP shl Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }


        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = ""
        createNotificationChannel()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar,menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val fragmentId = currentNavController?.value?.currentDestination?.id ?: return super.onPrepareOptionsMenu(menu)
        menu!![2].isVisible =  fragmentId == R.id.announcementsFragment
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.logout -> {
                mUserViewModel.signOutUser()
                val intent = Intent(this,AuthActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP shl Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                return true
            }
            else -> return  super.onOptionsItemSelected(item)
        }

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    override fun onBackPressed() {
        if (currentNavController?.value?.popBackStack() != true) {
            super.onBackPressed()
        }
    }

    private fun setupBottomNavigationBar() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigation)

        val navGraphIds = listOf(R.navigation.nav_graph_new_cars,
            R.navigation.nav_graph_old_cars,
            R.navigation.nav_graph_journal)

        val callback  = { _ :MenuItem->



        }
        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent,
            callback = callback
        )

        controller.observe(this, Observer { navController ->
            setupWithToolBar(navController)
            invalidateOptionsMenu()
            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.versionProfileFragment -> {
                        hideBottomNavigation()
                    }
                    R.id.announcementsFragment -> {
                        create_post_fab.visibility = View.VISIBLE
                        invalidateOptionsMenu()
                    }
                    R.id.newAnnouncementFragment ->{
                        invalidateOptionsMenu()
                    }
                    else -> {
                        create_post_fab.visibility = View.GONE
                        showBottomNavigation()
                    }
                }
            }
        })

        currentNavController = controller
    }

    private fun setupWithToolBar(navController:NavController){

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        findViewById<Toolbar>(R.id.toolbar)
            .setupWithNavController(navController, appBarConfiguration)
    }

    override fun onBrandClick(brand:Brand) {
        val bundle = Bundle()
        bundle.putParcelable(BrandsFragment.BRAND_OBJECT_ARG_KEY,brand)
        currentNavController?.value?.navigate(R.id.action_brandsFragment_to_modelsFragment,bundle)
    }

    override fun onModelClick(model: Model) {
        val bundle = Bundle()
        bundle.putParcelable(VersionsFragment.MODEL_OBJECT_ARG_KEY,model)
        currentNavController?.value?.navigate(R.id.action_modelsFragment_to_versionsFragment,bundle)
    }

    override fun onVersionClick(version: Version,imageView: ImageView) {
        val bundle = Bundle()

        val extras = FragmentNavigatorExtras(imageView to "header_image")
        bundle.putParcelable(VersionProfileFragment.VERSION_OBJECT_ARG_KEY,version)
        currentNavController?.value?.navigate(R.id.action_versionsFragment_to_versionProfileFragment,bundle,null,extras)
    }

    override fun onAnnouncementClick(announcement: Announcement) {

    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannelsTitles = resources.getStringArray(R.array.notification_channel_titles)
            val notificationChannelsDescription = resources.getStringArray(R.array.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH

            notificationChannelsTitles.forEachIndexed { id, name ->

                val channel = NotificationChannel(id.toString(), name, importance).apply {
                    description = notificationChannelsDescription[id]
                }
                // Register the channel with the system
                val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }

        }
    }

    private fun hideBottomNavigation() {
        // bottom_navigation is BottomNavigationView
        with(navigation) {
            if (visibility == View.VISIBLE && alpha == 1f) {
                animate()
                    .alpha(0f)
                    .withEndAction { visibility = View.GONE }
                    .duration = 200
            }
        }
    }

    private fun showBottomNavigation() {
        // bottom_navigation is BottomNavigationView
        with(navigation) {
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .duration = 200
        }
    }

    override fun onScrollStateChanged(scrolling: Boolean,up:Boolean) {
        if(scrolling){
            if(up) create_post_fab.show(true) else create_post_fab.hide(true)
            create_post_fab.shrink(true)
        }else {
            create_post_fab.extend(true)
        }
    }
}