package ua.co.myrecipes.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import ua.co.myrecipes.R
import ua.co.myrecipes.databinding.ActivityMainBinding
import ua.co.myrecipes.util.*
import ua.co.myrecipes.viewmodels.UserViewModel
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    private val userViewModel: UserViewModel by viewModels()
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navController: NavController
    private val networkMonitor = NetworkMonitorUtil(this)

    @Inject
    lateinit var glide: RequestManager
    private var wasDisconnected = false
    private var backPressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        preferencesSetting()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUpdateFromCoreLibrary()
        setupNavigationDrawer(savedInstanceState)
        setupNav()
        setupNetworkMonitor()
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
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            return
        }

        if (navController.currentDestination?.id == R.id.homeFragment ||
            navController.currentDestination?.id == R.id.newRecipeFragment ||
            navController.currentDestination?.id == R.id.regFragment){
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                finish()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.press_back_again_to_exit),
                    Toast.LENGTH_SHORT
                ).show()
            }
            backPressedTime = System.currentTimeMillis()
        } else super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }

    private fun setupUpdateFromCoreLibrary(){
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        // Create a listener to track request state updates.
        val listener = InstallStateUpdatedListener { state ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                Snackbar.make(
                    findViewById(R.id.drawerLayout),
                    "An update has just been downloaded.",
                    Snackbar.LENGTH_INDEFINITE
                ).apply {
                    setAction("RESTART") { appUpdateManager.completeUpdate() }
                    show()
                }
            }
        }

        // Before starting an update, register a listener for updates.
        appUpdateManager.registerListener(listener)

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && (appUpdateInfo.clientVersionStalenessDays() ?: -1) >= 5
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.FLEXIBLE,
                    this,
                    // Include a request code to later monitor this update request.
                    453)
            }
        }
    }

    private fun setupNavigationDrawer(savedInstanceState: Bundle?){
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.settings_item -> navController.navigate(R.id.settingsFragment)
                R.id.about_item -> navController.navigate(R.id.aboutUsFragment)
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START, false)
            true
        }
        val navHeader = binding.navView.getHeaderView(0)
        val nickNameTv = navHeader.findViewById<TextView>(R.id.nickName_drawer_tv)
        val logOutBtn = navHeader.findViewById<Button>(R.id.log_out_btn)
        val drawerUser = navHeader.findViewById<CircleImageView>(R.id.drawer_user_img)

        if (FirebaseAuth.getInstance().uid == null) {
            nickNameTv.text = getString(R.string.guest)
            logOutBtn.visibility = View.GONE
        } else {
            val currentUserNickName = AuthUtil.email.substringBefore("@")
            userViewModel.getUser(currentUserNickName)
            userViewModel.user.observe(this, EventObserver {
                if (it.img.isNotEmpty()){
                    glide.load(it.img).into(drawerUser)
                }
            })
            nickNameTv.text = currentUserNickName
            logOutBtn.visibility = View.VISIBLE
        }

        logOutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val navOptions = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .build()
            navController.navigate(R.id.regFragment, savedInstanceState, navOptions)
            recreate()
        }
    }

    private fun setupNav() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.NavHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)
        binding.bottomNavigationView.setOnNavigationItemReselectedListener { /*NO OPERATIONS*/ }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.newRecipeFragment, R.id.profileFragment, R.id.regFragment -> {
                    toggle.isDrawerIndicatorEnabled = true
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }
                else -> {
                    toggle.isDrawerIndicatorEnabled = false
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    binding.bottomNavigationView.visibility = View.GONE
                    title = getString(R.string.app_name)
                }
            }
            toggle.syncState()
        }
    }

    private fun setupNetworkMonitor(){
        networkMonitor.result = { isAvailable, type ->
            runOnUiThread {
                if (isAvailable){
                    when (type) {
                        ConnectionType.Wifi, ConnectionType.Cellular -> {
                            if (wasDisconnected) {
                                Snackbar.make(
                                    findViewById(R.id.drawerLayout),
                                    getString(R.string.connected),
                                    Snackbar.LENGTH_LONG
                                ).show()
                                wasDisconnected = false
                            }
                            binding.internetLayout.layoutNoInternet.visibility = View.INVISIBLE
                            binding.NavHostFragment.visibility = View.VISIBLE
                        }
                        else -> {
                        }
                    }
                }
                else {
                    Snackbar.make(
                        findViewById(R.id.drawerLayout),
                        getString(R.string.disconnected),
                        Snackbar.LENGTH_LONG
                    ).show()
                    binding.internetLayout.layoutNoInternet.visibility = View.VISIBLE
                    binding.NavHostFragment.visibility = View.INVISIBLE
                    wasDisconnected = true
                }
            }
        }
    }

    private fun preferencesSetting() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        val lang = sharedPreferences.getString("language", LocaleHelper.getSystemLang())
        resources.configuration.setLocale(Locale(lang as String))
        resources.updateConfiguration(resources.configuration, resources.displayMetrics)

        val theme = sharedPreferences.getBoolean("theme", true)
        if (theme){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            setTheme(R.style.DarkTheme)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            setTheme(R.style.AppTheme)
        }
    }
}