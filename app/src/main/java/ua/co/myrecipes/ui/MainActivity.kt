package ua.co.myrecipes.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.bumptech.glide.RequestManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.drawer_header.view.*
import kotlinx.coroutines.launch
import ua.co.myrecipes.R
import ua.co.myrecipes.util.ConnectionType
import ua.co.myrecipes.util.NetworkMonitorUtil
import ua.co.myrecipes.viewmodels.UserViewModel
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels()
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navController: NavController
    private val networkMonitor = NetworkMonitorUtil(this)

    @Inject
    lateinit var glide: RequestManager
    private var wasDisconnected = false
    private var backPressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        setting()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.settings_item -> {
                    navController.navigate(R.id.settingsFragment)
                    title = getString(R.string.settings)
                }
                R.id.about_item -> {
                    navController.navigate(R.id.aboutUsFragment)
                    title = getString(R.string.about_us)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START, false)
            true
        }

        bottomNavigationView.setupWithNavController(NavHostFragment.findNavController())
        bottomNavigationView.setOnNavigationItemReselectedListener { /*NO OPERATIONS*/ }
        setupNav()

        val navHeader = navView.getHeaderView(0)

        if (userViewModel.getUserEmail() == "") {
            navHeader.nickName_drawer_tv.text = getString(R.string.guest)
            navHeader.log_out_btn.visibility = View.GONE
        } else {
            lifecycleScope.launch {
                if (userViewModel.getUserImg().await() != "") {
                    glide.load(userViewModel.getUserImg().await()).into(navHeader.drawer_user_img)
                }
            }
            navHeader.nickName_drawer_tv.text = userViewModel.getUserEmail().substringBefore("@")
            navHeader.log_out_btn.visibility = View.VISIBLE
        }

        navHeader.log_out_btn.setOnClickListener {
            userViewModel.logOut()
            val navOptions = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .build()
            navController.navigate(R.id.regFragment, savedInstanceState, navOptions)
            recreate()
        }

        networkMonitor.result = { isAvailable, type ->
            runOnUiThread {
                when (isAvailable) {
                    true -> {
                        when (type) {
                            ConnectionType.Wifi -> {
                                if (wasDisconnected) {
                                    Snackbar.make(
                                        findViewById(R.id.drawerLayout),
                                        getString(R.string.connected),
                                        Snackbar.LENGTH_LONG
                                    ).show()
                                    wasDisconnected = false
                                }
                                flFragment.internetLayout.visibility = View.INVISIBLE
                                NavHostFragment.view?.visibility = View.VISIBLE
                            }
                            ConnectionType.Cellular -> {
                                if (wasDisconnected) {
                                    Snackbar.make(
                                        findViewById(R.id.drawerLayout),
                                        getString(R.string.connected),
                                        Snackbar.LENGTH_LONG
                                    ).show()
                                    wasDisconnected = false
                                }
                                flFragment.internetLayout.visibility = View.INVISIBLE
                                NavHostFragment.view?.visibility = View.VISIBLE
                            }
                            else -> {
                            }
                        }
                    }
                    false -> {
                        Snackbar.make(
                            findViewById(R.id.drawerLayout),
                            getString(R.string.disconnected),
                            Snackbar.LENGTH_LONG
                        ).show()
                        flFragment.internetLayout.visibility = View.VISIBLE
                        NavHostFragment.view?.visibility = View.INVISIBLE
                        wasDisconnected = true
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        networkMonitor.register()
    }

    override fun onStop() {
        super.onStop()
        networkMonitor.unregister()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
            return
        }

        if (navController.currentDestination?.id == R.id.homeFragment ||
            navController.currentDestination?.id == R.id.newRecipeFragment){
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                finish()
            } else {
                Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
            }
            backPressedTime = System.currentTimeMillis()
        } else super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }

    private fun setupNav() {
        navController = findNavController(R.id.NavHostFragment)
        findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            .setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, args ->
            when (destination.id) {
                R.id.homeFragment, R.id.newRecipeFragment, R.id.profileFragment, R.id.regFragment -> {
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
                    title = getString(R.string.app_name)
                }
            }
        }
    }

    private fun setting() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        val lang = sharedPreferences.getString("language", "")
        if (lang != ""){
            resources.configuration.setLocale(Locale(lang as String))
            resources.updateConfiguration(resources.configuration, resources.displayMetrics)
        }

        val theme = sharedPreferences.getBoolean("theme", false)
        if (theme){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            setTheme(R.style.DarkTheme)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            setTheme(R.style.AppTheme)
        }
    }
}