package com.cuhk.travelligent

import android.content.Context
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.fragment.app.FragmentManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import androidx.appcompat.widget.SearchView
import com.cuhk.travelligent.page.sights.SightsFragment
import kotlinx.android.synthetic.main.nav_header_home.view.*
import androidx.core.view.MenuItemCompat
import com.cuhk.travelligent.page.hotels.HotelsFragment


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Nav
        navView.setNavigationItemSelectedListener(this)

        val headerView = navView.getHeaderView(0)
        val nameView = headerView.name
        val emailAddressView = headerView.email_address

        val prefs = getSharedPreferences(Configs.PREFS, MODE_PRIVATE)
        nameView.text = prefs.getString(
            Configs.PREFS_FIRST_NAME,
            "FIRST"
        ) + " " + prefs.getString(Configs.PREFS_LAST_NAME, "FIRST")
        emailAddressView.text = prefs.getString(Configs.PREFS_EMAIL_ADDRESS, "EMAIL_ADDRESS")

        // Default
        supportFragmentManager
            .popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        val sightsFragment = SightsFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.home_fragment, sightsFragment)
            .commit()

    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_search -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                supportFragmentManager
                    .popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

                val sightsFragment = SightsFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.home_fragment, sightsFragment)
                    .commit()
            }
            R.id.nav_hotel -> {
                supportFragmentManager
                    .popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

                val sightsFragment = HotelsFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.home_fragment, sightsFragment)
                    .commit()
            }
            R.id.nav_log_out -> {
                val editor = getSharedPreferences(Configs.PREFS, Context.MODE_PRIVATE).edit()
                editor.clear()
                editor.apply()

                finish()
            }
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)

        return true
    }
}
