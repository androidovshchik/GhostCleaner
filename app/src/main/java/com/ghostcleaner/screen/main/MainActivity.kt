package com.ghostcleaner.screen.main

import android.content.Intent
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.ghostcleaner.ACTION_BATTERY
import com.ghostcleaner.ACTION_TEMPERATURE
import com.ghostcleaner.ACTION_TRASH
import com.ghostcleaner.R
import com.ghostcleaner.screen.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : BaseActivity(), ViewPager.OnPageChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter = TabsAdapter(supportFragmentManager)
        vp_main.adapter = adapter
        vp_main.offscreenPageLimit = adapter.count
        vp_main.addOnPageChangeListener(this)
        bn_main.setOnNavigationItemSelectedListener {
            Timber.e("setOnNavigationItemSelectedListener ")
            when (it.itemId) {
                R.id.action_rocket -> vp_main.setCurrentItem(0, true)
                R.id.action_battery -> vp_main.setCurrentItem(1, true)
                R.id.action_temperature -> vp_main.setCurrentItem(2, true)
                R.id.action_trash -> vp_main.setCurrentItem(3, true)
            }
            true
        }
        notifyBottomNav(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        notifyBottomNav(intent)
    }

    private fun notifyBottomNav(intent: Intent) {
        Timber.e("getStringExtra ${intent.getStringExtra("action")}")
        when (intent.getStringExtra("action")) {
            ACTION_BATTERY -> vp_main.setCurrentItem(1, false)
            ACTION_TEMPERATURE -> vp_main.setCurrentItem(2, false)
            ACTION_TRASH -> vp_main.setCurrentItem(3, false)
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageSelected(position: Int) {
        Timber.e("onPageSelected $position")
        bn_main.menu.findItem(
            when (position) {
                0 -> R.id.action_rocket
                1 -> R.id.action_battery
                2 -> R.id.action_temperature
                else -> R.id.action_trash
            }
        ).isChecked = true
    }

    override fun onDestroy() {
        vp_main.removeOnPageChangeListener(this)
        super.onDestroy()
    }
}

when (position) {
    0 -> bn_main.selectedItemId = R.id.action_rocket
    1 -> bn_main.selectedItemId = R.id.action_battery
    2 -> bn_main.selectedItemId = R.id.action_temperature
    3 -> bn_main.selectedItemId = R.id.action_trash
}
}

override fun onDestroy() {
    vp_main.removeOnPageChangeListener(this)
    super.onDestroy()
}
}
