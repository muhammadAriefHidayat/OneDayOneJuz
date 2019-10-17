package org.odoj.onedayonejuzapp.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import org.odoj.onedayonejuzapp.Fragments.ChatsFragment
import org.odoj.onedayonejuzapp.Fragments.SetoranFragment


class SectionPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        when(position){
            0-> return SetoranFragment()
            1 -> return ChatsFragment()
        }
        return null!!
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position){
            0 -> return "Laporan"
            1-> return "Pesan"
        }
        return null
    }
}