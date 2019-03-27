package com.autolink.sayaradz.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.autolink.sayaradz.R
import com.autolink.sayaradz.ui.adapter.brand.BrandsAdapter
import com.autolink.sayaradz.ui.adapter.model.ModelsAdapter
import com.autolink.sayaradz.ui.adapter.version.VersionsAdapter
import com.autolink.sayaradz.ui.fragment.newcar.BrandsFragment
import com.autolink.sayaradz.ui.fragment.newcar.ModelsFragment
import com.autolink.sayaradz.ui.fragment.newcar.VersionProfileFragment
import com.autolink.sayaradz.ui.fragment.newcar.VersionsFragment
import com.autolink.sayaradz.util.RepositoryKey
import com.autolink.sayaradz.util.getViewModel
import com.autolink.sayaradz.util.setupWithNavController
import com.autolink.sayaradz.viewmodel.UserViewModel
import com.autolink.sayaradz.vo.Brand
import com.autolink.sayaradz.vo.Model
import com.autolink.sayaradz.vo.Version
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity: AppCompatActivity(),
                    BrandsAdapter.OnBrandsClickListener,
                    ModelsAdapter.OnModelClickListener,
                    VersionsAdapter.OnVersionClickListener{

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



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar,menu)
        return true
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

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )

        controller.observe(this, Observer { navController ->
            setupWithToolBar(navController)
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

}