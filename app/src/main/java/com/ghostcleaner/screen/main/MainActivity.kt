package com.ghostcleaner.screen.main

import android.os.Bundle
import com.ghostcleaner.R
import com.ghostcleaner.screen.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter = TabsAdapter(supportFragmentManager)
        vp_main.adapter = TabsAdapter(supportFragmentManager)
        vp_main.offscreenPageLimit = adapter.count
        bn_main.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_arrow -> vp_main.setCurrentItem(0, true)
                R.id.action_battery -> vp_main.setCurrentItem(1, true)
                R.id.action_temperature -> vp_main.setCurrentItem(2, true)
                R.id.action_trash -> vp_main.setCurrentItem(3, true)
            }
            true
        }
    }
}
