package com.android.tony.newsx;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;


public class CategoryFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_category, container, false);
        TabLayout tabLayout = v.findViewById(R.id.catTabLayout);
        ViewPager viewPager = v.findViewById(R.id.catViewPager);
        tabLayout.setupWithViewPager(viewPager);
        mFragmentPagerAdapter mFragmentPagerAdapter = new mFragmentPagerAdapter(getFragmentManager());
        viewPager.setAdapter(mFragmentPagerAdapter);
        return v;
    }


}
