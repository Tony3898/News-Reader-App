package com.android.tony.newsx;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class mFragmentPagerAdapter extends FragmentPagerAdapter {


    mFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 8;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Latest";
            case 1:
                return "World";
            case 2:
                return "Business";
            case 3:
                return "Technology";
            case 4:
                return "Entertainment";
            case 5:
                return "Sports";
            case 6:
                return "Science";
            case 7:
                return "Health";
            default:
                return null;
        }
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new LatestFragment();
            case 1:
                return new WorldFragment();
            case 2:
                return new BusinessFragment();
            case 3:
                return new TechnologyFragment();
            case 4:
                return new EntertainmentFragment();
            case 5:
                return new SportsFragment();
            case 6:
                return new ScienceFragment();
            case 7:
                return new HealthFragment();
            default:
                return null;
        }
    }
}
