package com.autolink.sayaradz.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.autolink.sayaradz.R
import com.autolink.sayaradz.ui.adapter.brand.BrandsAdapter
import com.autolink.sayaradz.ui.fragment.ModelsFragment
import com.autolink.sayaradz.util.RepositoryKey
import com.autolink.sayaradz.util.getViewModel
import com.autolink.sayaradz.util.setupWithNavController
import com.autolink.sayaradz.util.writeToSharedPreference
import com.autolink.sayaradz.viewmodel.BrandsViewModel
import com.autolink.sayaradz.viewmodel.ModelsViewModel
import com.autolink.sayaradz.viewmodel.UserViewModel
import com.autolink.sayaradz.vo.Brand
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity:AppCompatActivity(),BrandsAdapter.OnBrandsClickListener{

    companion object {
        private const val TAG  = "MainActivity"
    }

    private lateinit var mUserViewModel: UserViewModel

    private var currentNavController: LiveData<NavController>? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        } // Else, need to wait for onRestoreInstanceState

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = ""


        mUserViewModel = getViewModel(this, RepositoryKey.USER_REPOSITORY) as UserViewModel




        if(!mUserViewModel.isUserSignIn()){
            val intent = Intent(this,AuthActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP shl Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        mUserViewModel.getCarDriverLiveData().observe(this, Observer {
            Log.d(TAG,"updating the user token")
            it.token?.let { it1 -> writeToSharedPreference("TOKEN", it1) }
        })


    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()
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
        bundle.putParcelable("brand",brand)
        currentNavController?.value?.navigate(R.id.action_brandsFragment_to_modelsFragment,bundle)
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    override fun onBackPressed() {
        if (currentNavController?.value?.popBackStack() != true) {
            super.onBackPressed()
        }
    }
}