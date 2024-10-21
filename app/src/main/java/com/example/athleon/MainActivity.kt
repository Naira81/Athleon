package com.example.athleon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.athleon.LoginActivity.Companion.providerSession
import com.example.athleon.LoginActivity.Companion.useremail
import com.example.athleon.Utility.setHeightLinearLayout
import com.facebook.login.LoginManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer:DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initToolBar()
        initObjects()
        initNavigationView()
    }

    private fun initToolBar(){
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.bar_title,
            R.string.navigation_drawer_close)

        drawer.addDrawerListener(toggle)

        toggle.syncState()
    }

    private fun initNavigationView(){
        var navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        var headerView: View = LayoutInflater.from(this).inflate(R.layout.nav_header_main, navigationView, false)
        navigationView.removeHeaderView(headerView)
        navigationView.addHeaderView(headerView)

        var tvUser: TextView = headerView.findViewById(R.id.tvUser)
        tvUser.text = useremail
    }

    private fun initObjects(){
        var lyMap=findViewById<LinearLayout>(R.id.lyMap)
        var lyFragmentMap=findViewById<LinearLayout>(R.id.lyFragmentMap)
        setHeightLinearLayout(lyMap, 0)
        lyFragmentMap.translationY=-300f

        val lyIntervalModeSpace= findViewById<LinearLayout>(R.id.lyIntervalModeSpace)
        val lyIntervalMode= findViewById<LinearLayout>(R.id.lyIntervalMode)
        val lyChallengesSpace= findViewById<LinearLayout>(R.id.lyChallengesSpace)
        val lyChallenges= findViewById<LinearLayout>(R.id.lyChallenges)
        val lySettingsVolumesSpace= findViewById<LinearLayout>(R.id.lySettingsVolumesSpace)
        val lySettingsVolumes= findViewById<LinearLayout>(R.id.lySettingsVolumes)

        setHeightLinearLayout(lyIntervalModeSpace, 0)
        setHeightLinearLayout(lyChallengesSpace, 0)
        setHeightLinearLayout(lySettingsVolumesSpace, 0)

        lyIntervalMode.translationY= -300f
        lyChallenges.translationY= -300f
        lySettingsVolumes.translationY= -300f





    }

    fun callSignPut(view:View){
        signOut()
    }
    private fun signOut(){
        useremail=""
        if (providerSession=="Facebook") LoginManager.getInstance().logOut()

        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }
//capturamos que elemento del menu se ha pulsado
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
            when(item.itemId){
                //le mando a parte de registros
                R.id.nav_item_record-> callRecordActivity()
                R.id.nav_item_signout->signOut()
            }
        drawer.closeDrawer(GravityCompat.START)

        return true
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            // Muestra un diálogo para confirmar el cierre de sesión
            AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                .setPositiveButton("Sí") { _, _ ->
                    signOut()
                    super.onBackPressed() // Comportamiento predeterminado si se confirma
                }
                .setNegativeButton("No", null)
                .show()
        }
    }
    private fun callRecordActivity(){
        val intent= Intent(this, RecordActivity::class.java)
        startActivity(intent)
    }


}