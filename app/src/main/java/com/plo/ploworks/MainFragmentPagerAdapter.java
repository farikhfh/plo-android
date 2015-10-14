package com.plo.ploworks;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Farikh Fadlul Huda on 10/9/2015.
 */
public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 4;
    private String tabTitles[] = new String[] { "Berita", "Curgas", "Ekspresi","Pesan" };
    private Context context;
    private Fragment fragment;

    public MainFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                fragment = new BeritaFragment();
                break;
            case 1:
                fragment = new CurgasFragment();
                break;
            case 2:
                fragment = new EkspresiFragment();
                break;
            default:
                fragment = new Fragment();
        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
