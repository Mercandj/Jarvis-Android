/**
 * This file is part of FileSpace for Android, an app for managing your server (files, talks...).
 * <p/>
 * Copyright (c) 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 * <p/>
 * LICENSE:
 * <p/>
 * FileSpace for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p/>
 * FileSpace for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 */
package com.mercandalli.android.apps.files.common.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;

import com.mercandalli.android.apps.files.main.ApplicationCallback;

/**
 * A simple {@link Fragment} that forwards the back button click.
 */
public abstract class BackFragment extends Fragment {

    protected Activity mActivity;
    protected ApplicationCallback mApplicationCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) getContext();
        if (context instanceof ApplicationCallback) {
            mApplicationCallback = (ApplicationCallback) context;
        } else {
            throw new IllegalArgumentException("Must be attached to a HomeActivity. Found: " + context);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
        mApplicationCallback = null;
    }

    /**
     * Activity follow back to fragment.
     *
     * @return true if action
     */
    public abstract boolean back();

    /**
     * Get the focus.
     * For example: on a ViewPager.
     */
    public abstract void onFocus();

    public interface ISortMode {

        /**
         * Define the list sort.
         *
         * @param sortMode {@link com.mercandalli.android.apps.files.main.Constants#SORT_ABC},
         *                 {@link com.mercandalli.android.apps.files.main.Constants#SORT_SIZE} or
         *                 {@link com.mercandalli.android.apps.files.main.Constants#SORT_DATE_MODIFICATION}
         */
        void setSortMode(int sortMode);
    }


    /**
     * Set the status bar transparency.
     *
     * @param context     current context
     * @param translucent if true the status bar will be transparent else
     *                    the status bar will be opaque
     */
    public void setStatusBarTranslucent(@NonNull Context context, boolean translucent) {
        setStatusBarTranslucent(((Activity) context).getWindow(), translucent);
    }

    /**
     * Set the status bar transparency.
     *
     * @param window      current window
     * @param translucent if true the status bar will be transparent else
     *                    the status bar will be opaque
     */
    @SuppressLint("NewApi")
    public void setStatusBarTranslucent(@NonNull final Window window, boolean translucent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (translucent) {
                window.setFlags(
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            } else {
                // Remove transparent status bar
                WindowManager.LayoutParams attrs = window
                        .getAttributes();
                attrs.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setAttributes(attrs);
                window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        }
    }

    /**
     * Change the status bar color if {@link Build.VERSION#SDK_INT} >= {@link Build.VERSION_CODES#LOLLIPOP}.
     * Remove the translucent flag.
     *
     * @param context       current context
     * @param colorResource a color resource
     */
    public void setStatusBarColor(@NonNull Context context, @ColorRes int colorResource) {
        setStatusBarColor((Activity) context, colorResource);
    }

    /**
     * Change the status bar color if {@link Build.VERSION#SDK_INT} >= {@link Build.VERSION_CODES#LOLLIPOP}.
     * Remove the translucent flag.
     *
     * @param activity      current activity
     * @param colorResource a color resource
     */
    @SuppressLint("NewApi")
    public void setStatusBarColor(@NonNull Activity activity, @ColorRes int colorResource) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = activity.getWindow();

            // Opaque status bar
            setStatusBarTranslucent(window, false);

            // Add status bar color
            window.setStatusBarColor(ContextCompat.getColor(activity, colorResource));
        }
    }
}
