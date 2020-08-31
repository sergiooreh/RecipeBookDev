package ua.co.myrecipes

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toggle = ActionBarDrawerToggle(this,drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.item1 -> Toast.makeText(applicationContext,"Clicked item 1",Toast.LENGTH_LONG).show()
                R.id.item2 -> Toast.makeText(applicationContext,"Clicked item 2",Toast.LENGTH_LONG).show()
                R.id.item3 -> Toast.makeText(applicationContext,"Clicked item 3",Toast.LENGTH_LONG).show()
            }
            true
        }

        bottomNavigationView.setupWithNavController(NavHostFragment.findNavController())
        bottomNavigationView.setOnNavigationItemReselectedListener { /*NO OPERATIONS*/ }
        setupNav()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }

    private fun setupNav() {
        val navController = findNavController(R.id.NavHostFragment)
        findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            .setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.newRecipeFragment, R.id.profileFragment, R.id.regFragment ->
                    bottomNavigationView.visibility = View.VISIBLE
                else -> bottomNavigationView.visibility = View.GONE
            }
        }

    }

}