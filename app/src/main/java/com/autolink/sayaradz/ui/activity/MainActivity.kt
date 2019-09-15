package com.autolink.sayaradz.ui.activity

import android.Manifest
import android.app.Activity
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
import com.autolink.sayaradz.ui.fragment.oldcar.AnnouncementProfileFragment.Companion.ANNOUNCEMENT_OBJECT_ARG_KEY
import com.autolink.sayaradz.ui.fragment.oldcar.AnnouncementsFilterSheetFragment
import com.autolink.sayaradz.ui.fragment.oldcar.AnnouncementsFilterSheetFragment.Companion.BRANDS_KEY
import com.autolink.sayaradz.ui.fragment.oldcar.AnnouncementsFilterSheetFragment.Companion.MAX_DISTANCE_KEY
import com.autolink.sayaradz.ui.fragment.oldcar.AnnouncementsFilterSheetFragment.Companion.MAX_PRICE_KEY
import com.autolink.sayaradz.ui.fragment.oldcar.AnnouncementsFilterSheetFragment.Companion.MIN_DISTANCE_KEY
import com.autolink.sayaradz.ui.fragment.oldcar.AnnouncementsFilterSheetFragment.Companion.MIN_PRICE_KEY
import com.autolink.sayaradz.ui.fragment.oldcar.VehicleSelectionFragment
import com.autolink.sayaradz.ui.fragment.oldcar.NewAnnouncementFragment
import com.autolink.sayaradz.util.*
import com.autolink.sayaradz.viewmodel.*
import com.autolink.sayaradz.viewmodel.AnnouncementsViewModel.Companion.DISTANCE_PREFIX
import com.autolink.sayaradz.viewmodel.AnnouncementsViewModel.Companion.MAX_DISTANCE
import com.autolink.sayaradz.viewmodel.AnnouncementsViewModel.Companion.MAX_PRICE
import com.autolink.sayaradz.viewmodel.AnnouncementsViewModel.Companion.MIN_DISTANCE
import com.autolink.sayaradz.viewmodel.AnnouncementsViewModel.Companion.MIN_PRICE
import com.autolink.sayaradz.viewmodel.AnnouncementsViewModel.Companion.PRICE_PREFIX
import com.autolink.sayaradz.vo.Announcement
import com.autolink.sayaradz.vo.Brand
import com.autolink.sayaradz.vo.Model
import com.autolink.sayaradz.vo.Version
import com.erikagtierrez.multiple_media_picker.Gallery
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity: AppCompatActivity(),
                    BrandsAdapter.OnBrandsClickListener,
                    ModelsAdapter.OnModelClickListener,
                    VersionsAdapter.OnVersionClickListener,
                    OnScrollStateChangedListener,
                    AnnouncementsAdapter.OnAnnouncementClickListener,
                    AnnouncementsFilterSheetFragment.OnFilterSubmittedListener,
                    NewAnnouncementFragment.OnSelectVehicleClickListener,
                    VehicleSelectionFragment.OnVehicleSelectionCompletedListener,
                    NewAnnouncementFragment.OnSelectPhotoClickListener,
                    NewAnnouncementFragment.OnAnnouncementSubmittedListener{


    companion object {
        private const val TAG  = "MainActivity"
        private const val PICK_ANNOUNCEMENT_PHOTO = 1
        private const val PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2
    }

    private lateinit var mUserViewModel: UserViewModel
    private lateinit var mAnnouncementsViewModel: AnnouncementsViewModel
    private lateinit var mBrandsViewModel:BrandsViewModel

    private var mFilterAnnouncementsFragment = AnnouncementsFilterSheetFragment()
    private var mCarSelectionFragment = VehicleSelectionFragment()

    private var currentNavController: LiveData<NavController>? = null

    private val mImagePicker = {

        val intent = Intent(this, Gallery::class.java)
        intent.putExtra("title", "Choisire une photo")
        intent.putExtra("mode", 2)
        intent.putExtra("maxSelection", 1)

        startActivityForResult(intent, PICK_ANNOUNCEMENT_PHOTO)
    }
    private val mVisibleFragment = {
        val  navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container)!!
         navHostFragment.childFragmentManager.fragments[0]

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mUserViewModel = getViewModel(this, RepositoryKey.USER_REPOSITORY) as UserViewModel


        if(mUserViewModel.isUserSignIn()){

            mUserViewModel.initCarDriverProfile()

            mAnnouncementsViewModel = getViewModel(this,RepositoryKey.ANNOUNCEMENT_REPOSITORY) as AnnouncementsViewModel

            mBrandsViewModel = getViewModel(this,RepositoryKey.BRANDS_REPOSITORY) as BrandsViewModel



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

        create_post_fab.setOnClickListener {
            currentNavController?.value?.navigate(R.id.action_announcementsFragment_to_newAnnouncementFragment)
        }


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
            R.id.filter -> {

                if( mFilterAnnouncementsFragment.isVisible) return true

                val args = Bundle()
                val priceRange = mAnnouncementsViewModel.getPriceConstraints()
                val distanceRangeRange = mAnnouncementsViewModel.getDistanceConstraints()

                args.putFloat(MIN_PRICE_KEY,priceRange.first / PRICE_PREFIX)
                args.putFloat(MAX_PRICE_KEY, priceRange.second / PRICE_PREFIX)

                args.putFloat(MIN_DISTANCE_KEY,distanceRangeRange.first/  DISTANCE_PREFIX)
                args.putFloat(MAX_DISTANCE_KEY, distanceRangeRange.second / DISTANCE_PREFIX)

                args.putParcelableArrayList(BRANDS_KEY, ArrayList(mAnnouncementsViewModel.getBrandsConstraints()))

                mFilterAnnouncementsFragment.arguments = args
                mFilterAnnouncementsFragment.show(supportFragmentManager, "bottomSheet")
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
                        create_post_fab.visibility = View.GONE
                    }

                    R.id.announcementsFragment -> {
                        create_post_fab.visibility = View.VISIBLE
                        showBottomNavigation()
                        invalidateOptionsMenu()
                    }

                    R.id.announcementProfileFragment->{
                        hideBottomNavigation()
                        create_post_fab.visibility = View.GONE
                    }

                    R.id.newAnnouncementFragment ->{
                        invalidateOptionsMenu()
                        hideBottomNavigation()
                        create_post_fab.visibility = View.GONE
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

    override fun onAnnouncementClick(announcement: Announcement,sharedViews:Map<String,View>) {
        val bundle = Bundle()
        val extras = FragmentNavigatorExtras(
              sharedViews.getValue("announce_image_view") to "announce_image_view_${announcement.id}",
              sharedViews.getValue("brand_image_view") to "brand_image_view_${announcement.id}",
              sharedViews.getValue("vehicle_name_text_view") to "vehicle_name_text_view_${announcement.id}")



        bundle.putParcelable(ANNOUNCEMENT_OBJECT_ARG_KEY,announcement)
        currentNavController?.value?.navigate(R.id.action_announcementsFragment_to_announcementProfileFragment,bundle,null,extras)
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

    override fun onFilterSubmitted(brands: List<Brand>, priceRange: Pair<Float, Float>,distanceRange: Pair<Float, Float>) {

        val price  = Pair(priceRange.first*PRICE_PREFIX, priceRange.second * PRICE_PREFIX)
        val distance  = Pair(distanceRange.first* DISTANCE_PREFIX, distanceRange.second * DISTANCE_PREFIX)
        mAnnouncementsViewModel.setConstraints(price,distance,brands)

    }

    override fun onFilterCleared() {
        val priceRange  = Pair(MIN_PRICE, MAX_PRICE)
        val distanceRange  = Pair(MIN_DISTANCE, MAX_DISTANCE)
        mAnnouncementsViewModel.setConstraints(priceRange,distanceRange, mutableListOf())
    }

    override fun onSelectVehicleClicked() {
        if( mCarSelectionFragment.isVisible) return
        mCarSelectionFragment.show(supportFragmentManager,"bottomSheet")

    }

    override fun onVehicleSelectionCompleted(version: Version, brand: Brand) {
        val fragment = mVisibleFragment() as NewAnnouncementFragment
        fragment.setVehicleProviderCredential(brand,version)
    }

    override fun onSelectPhotoClicked() {


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
        }else{
            mImagePicker()
        }

    }

    override fun onAnnouncementSubmitted() {
        val user = mUserViewModel.getCarDriverLiveData().value ?: return
        mAnnouncementsViewModel.setAnnouncement(user)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    mImagePicker()
                }
                return
            }
            else -> { }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_ANNOUNCEMENT_PHOTO) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK && data != null) {
                val selectionResult = data.getStringArrayListExtra("result")
                val fragment = mVisibleFragment() as NewAnnouncementFragment

                fragment.setVehiclePhoto(selectionResult[0])

            }
        }
    }


}