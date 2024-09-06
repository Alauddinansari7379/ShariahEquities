package com.amtech.shariahEquities.viewPager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.amtech.shariahEquities.fragments.BasketsFragment
import com.amtech.shariahEquities.fragments.FundsFragment
import com.amtech.shariahEquities.fragments.StocksFragment


class PageAdapterMyOrder(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    
    override fun getItemCount(): Int {
        return 4
    }
    
    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> StocksFragment()
            1 -> FundsFragment()
            2 -> BasketsFragment()
             else -> StocksFragment()
        }
    }
}