package ua.co.myrecipes.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_header.view.*
import ua.co.myrecipes.R
import ua.co.myrecipes.viewmodels.UserViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels()
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toggle = ActionBarDrawerToggle(this,drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)

        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.item1 -> Toast.makeText(applicationContext,"Clicked item 1",Toast.LENGTH_LONG).show()
                R.id.settings_item -> navController.navigate(R.id.settingsFragment)
                R.id.about_item -> navController.navigate(R.id.aboutUsFragment)
            }
            drawerLayout.closeDrawer(GravityCompat.START, false)
            true
        }

        bottomNavigationView.setupWithNavController(NavHostFragment.findNavController())
        bottomNavigationView.setOnNavigationItemReselectedListener { /*NO OPERATIONS*/ }
        setupNav()

        val navHeader = navView.getHeaderView(0)

        if (userViewModel.getUserEmail()==""){
            navHeader.nickName_drawer_tv.text = "guest"
            navHeader.log_out_btn.visibility = View.GONE
        } else{
            navHeader.nickName_drawer_tv.text = userViewModel.getUserEmail().substringBefore("@")
            navHeader.log_out_btn.visibility = View.VISIBLE
        }

        navHeader.log_out_btn.setOnClickListener {
            userViewModel.logOut()
            val navOptions = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .build()
            navController.navigate(R.id.regFragment,savedInstanceState,navOptions)
                recreate()
        }

    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        } else{
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }

    private fun setupNav() {
        navController = findNavController(R.id.NavHostFragment)
        findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            .setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.newRecipeFragment, R.id.profileFragment, R.id.regFragment ->{
                    toggle.isDrawerIndicatorEnabled = true
                    toggle.syncState()
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    bottomNavigationView.visibility = View.VISIBLE
                }
                else -> {
                    toggle.isDrawerIndicatorEnabled = false
                    toggle.syncState()
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    bottomNavigationView.visibility = View.GONE
                }
            }
        }

    }

}