/**
 * This file is part of FileSpace for Android, an app for managing your server (files, talks...).
 *
 * Copyright (c) 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 *
 * LICENSE:
 *
 * FileSpace for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * FileSpace for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.ui.fragments.genealogy;

import android.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.listeners.IListener;
import mercandalli.com.filespace.listeners.IModelGenealogyUserListener;
import mercandalli.com.filespace.models.ModelGenealogyPerson;
import mercandalli.com.filespace.ui.activities.Application;
import mercandalli.com.filespace.ui.fragments.BackFragment;
import mercandalli.com.filespace.ui.fragments.FabFragment;
import mercandalli.com.filespace.ui.views.NonSwipeableViewPager;

import static mercandalli.com.filespace.utils.NetUtils.isInternetConnection;

public class GenealogyFragment extends BackFragment implements ViewPager.OnPageChangeListener {

    private static final int NB_FRAGMENT = 4;
    private static final int INIT_FRAGMENT = 0;
    public static FabFragment listFragment[] = new FabFragment[NB_FRAGMENT];
    private NonSwipeableViewPager mViewPager;
    private FileManagerFragmentPagerAdapter mPagerAdapter;
    private TabLayout tabs;

    private FloatingActionButton circle, circle2;
    private View coordinatorLayoutView;
    private Snackbar snackbar;

    private AppBarLayout mAppBarLayout;

    public GenealogyFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_genealogy, container, false);

        app.setTitle(R.string.tab_genealogy);
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.my_toolbar);
        app.setToolbar(mToolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            app.getWindow().setStatusBarColor(ContextCompat.getColor(app, R.color.notifications_bar));
        setHasOptionsMenu(true);

        mAppBarLayout = (AppBarLayout) rootView.findViewById(R.id.fragment_genealogy_app_bar_layout);
        this.coordinatorLayoutView = (View) rootView.findViewById(R.id.snackBarPosition);

        mPagerAdapter = new FileManagerFragmentPagerAdapter(this.getChildFragmentManager(), app);

        tabs = (TabLayout) rootView.findViewById(R.id.fragment_genealogy_tab_layout);
        mViewPager = (NonSwipeableViewPager) rootView.findViewById(R.id.pager);
        mViewPager.setNonSwipeableItem(2);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        mViewPager.setOffscreenPageLimit(NB_FRAGMENT - 1);
        mViewPager.setCurrentItem(INIT_FRAGMENT);

        tabs.setupWithViewPager(mViewPager);

        this.circle = ((FloatingActionButton) rootView.findViewById(R.id.circle));
        this.circle.setVisibility(View.GONE);

        return rootView;
    }

    public int getCurrentFragmentIndex() {
        if(mViewPager == null)
            return -1;
        int result = mViewPager.getCurrentItem();
        if(result >= listFragment.length)
            return -1;
        return mViewPager.getCurrentItem();
    }

    @Override
    public boolean back() {
        int currentFragmentId = getCurrentFragmentIndex();
        if (listFragment == null || currentFragmentId == -1)
            return false;
        BackFragment backFragment = listFragment[currentFragmentId];
        return backFragment != null && backFragment.back();
    }

    @Override
    public void onFocus() {

    }

    private void refreshFab() {
        refreshFab(getCurrentFragmentIndex());
    }

    private void refreshFab(int currentFragmentId) {
        if(listFragment == null || currentFragmentId== -1)
            return;
        FabFragment fragment = listFragment[currentFragmentId];
        if(fragment==null)
            return;
        refreshFab(fragment);
    }

    private void refreshFab(final FabFragment currentFragment) {
        if (currentFragment.isFabVisible(0)) {
            this.circle.show();
            int imageResource = currentFragment.getFabImageResource(0);
            if(imageResource == -1)
                imageResource = android.R.drawable.ic_input_add;
            this.circle.setImageResource(imageResource);
            this.circle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentFragment.onFabClick(0, circle);
                }
            });
        }
        else
            this.circle.hide();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        GenealogyFragment.this.app.invalidateOptionsMenu();
        mAppBarLayout.setExpanded(true);
        if (listFragment[position] instanceof GenealogyTreeFragment) {
            ((GenealogyTreeFragment) listFragment[position]).update();
        }
        if (position < NB_FRAGMENT)
            if (listFragment[position] != null)
                listFragment[position].onFocus();
        updateNoInternet();
        refreshFab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class FileManagerFragmentPagerAdapter extends FragmentPagerAdapter {
        Application app;

        public FileManagerFragmentPagerAdapter(FragmentManager fm, Application app) {
            super(fm);
            this.app = app;
        }

        @Override
        public BackFragment getItem(int i) {
            FabFragment fragment;
            switch(i) {
                case 0:
                    GenealogyListFragment fr = GenealogyListFragment.newInstance();
                    fr.setOnSelect(new IModelGenealogyUserListener() {
                        @Override
                        public void execute(ModelGenealogyPerson modelPerson) {
                            for(BackFragment fr : listFragment) {
                                if (fr instanceof GenealogyTreeFragment)
                                    ((GenealogyTreeFragment) fr).select(modelPerson);
                                else if (fr instanceof GenealogyBigTreeFragment)
                                    ((GenealogyBigTreeFragment) fr).select(modelPerson);
                            }
                        }
                    });
                    fragment = fr;
                    break;
                case 1:		fragment = GenealogyTreeFragment.newInstance();         break;
                case 2:		fragment = GenealogyBigTreeFragment.newInstance();      break;
                case 3:		fragment = GenealogyStatisticsFragment.newInstance();   break;
                default:    fragment = GenealogyTreeFragment.newInstance();
            }
            fragment.setRefreshFab(new IListener() {
                @Override
                public void execute() {
                    refreshFab();
                }
            });
            listFragment[i] = fragment;
            return fragment;
        }

        @Override
        public int getCount() {
            return NB_FRAGMENT;
        }

        @Override
        public CharSequence getPageTitle(int i) {
            String title = "null";
            switch(i) {
                case 0:		title = "LIST";         break;
                case 1:		title = "TREE";	        break;
                case 2:		title = "BIG TREE";	    break;
                case 3:		title = "STATISTICS";   break;
            }
            return title;
        }
    }

    public void refreshListServer() {
        refreshListServer(null);
    }

    public void refreshListServer(String search) {
        if(listFragment[0]!=null)
            if(listFragment[0] instanceof GenealogyListFragment) {
                GenealogyListFragment fragmentFileManagerFragment = (GenealogyListFragment) listFragment[0];
                fragmentFileManagerFragment.refreshList(search);
            }
    }

    private void updateNoInternet() {
        if(!isInternetConnection(app)) {
            this.snackbar = Snackbar.make(this.coordinatorLayoutView, getString(R.string.no_internet_connection), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.refresh), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(isInternetConnection(app))
                                listFragment[getCurrentFragmentIndex()].onFocus();
                            else
                                updateNoInternet();
                        }
                    });
            this.snackbar.show();
        }
    }
}