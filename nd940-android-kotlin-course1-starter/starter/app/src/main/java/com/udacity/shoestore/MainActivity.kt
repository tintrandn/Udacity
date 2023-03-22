package com.udacity.shoestore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.udacity.shoestore.databinding.ActivityMainBinding
import com.udacity.shoestore.screen.shoes_list.ShoesListFragment
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navController = this.findNavController(R.id.myNavHostFragment)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // hide navigation up button at shoes list fragment
            if (destination.id == R.id.shoesListFragment) {
                binding.toolbar.navigationIcon = null
            }
        }
        Timber.plant(Timber.DebugTree())
    }

    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.myNavHostFragment);
        val fragment = navHostFragment?.childFragmentManager?.fragments?.get(0)
        if (fragment is ShoesListFragment) {
            // disable back button on shoes list fragment
           return
        } else {
            super.onBackPressed()
        }
    }
}
