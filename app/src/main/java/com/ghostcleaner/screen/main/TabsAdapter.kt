package com.ghostcleaner.screen.main

import android.util.SparseArray
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class TabsAdapter(manager: FragmentManager) :
    FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragments = SparseArray<Fragment>()

    override fun getItem(position: Int): Fragment {
        var fragment = fragments.get(position)
        if (fragment == null) {
            fragment = when (position) {
                0 -> BoosterFragment.newInstance()
                1 -> BatteryFragment.newInstance()
                2 -> CoolFragment.newInstance()
                else -> JankFragment.newInstance()
            }
            fragments.put(position, fragment)
            return fragment
        }
        return fragment
    }

    override fun getCount(): Int = 4

    override fun getPageTitle(position: Int): String? = null

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        fragments.remove(position)
        super.destroyItem(container, position, obj)
    }
}