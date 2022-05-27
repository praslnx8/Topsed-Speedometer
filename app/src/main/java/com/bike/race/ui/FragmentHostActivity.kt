package com.bike.race.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bike.race.R
import com.bike.race.databinding.ActivityFragmentHostBinding
import com.bike.race.ui.myAllDrive.MyAllDrivesFragment

class FragmentHostActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityFragmentHostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityFragmentHostBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        setSupportActionBar(viewBinding.toolbar)

        when (intent.getIntExtra(FRAG_HOST_KEY, 0)) {
            FRAG_HOST_MY_ALL_DRIVES -> {
                loadFragment(MyAllDrivesFragment(), R.string.my_rides)
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun loadFragment(fragment: Fragment, @StringRes title: Int) {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.add(viewBinding.container.id, fragment).commit()
        setTitle(title)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {

        const val FRAG_HOST_MY_ALL_DRIVES = 4

        private const val FRAG_HOST_KEY = "FRAG_HOST"

        fun open(context: Context, fragHost: Int) {
            val intent = Intent(context, FragmentHostActivity::class.java)
            intent.putExtra(FRAG_HOST_KEY, fragHost)
            context.startActivity(intent)
        }
    }
}