/**
 * This file is part of Jarvis for Android, an app for managing your server (files, talks...).
 *
 * Copyright (c) 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 *
 * LICENSE:
 *
 * Jarvis for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * Jarvis for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.jarvis.ui.fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.config.Const;
import mercandalli.com.jarvis.ia.action.ENUM_Action;
import mercandalli.com.jarvis.model.ModelSetting;
import mercandalli.com.jarvis.ui.activity.Application;
import mercandalli.com.jarvis.ui.adapter.AdapterModelSetting;
import mercandalli.com.jarvis.util.TimeUtils;

public class SettingsFragment extends Fragment {

	private Application app;
	private View rootView;
	
	private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    List<ModelSetting> list;
	int click_version;

	public SettingsFragment(Application app) {
		this.app = app;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_settings, container, false);
		
		recyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
		recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
		click_version = 0;
        
        refreshList();
		
        return rootView;
	}
	
	public void refreshList() {
		list = new ArrayList<ModelSetting>();
		list.add(new ModelSetting(app, "Settings", Const.TAB_VIEW_TYPE_SECTION));
		list.add(new ModelSetting(app, "Auto connection", new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				app.getConfig().setAutoConnection(isChecked);
			}
		}, app.getConfig().isAutoConncetion()));
        list.add(new ModelSetting(app, "Web application"));
        list.add(new ModelSetting(app, "Welcome on home screen"));
        list.add(new ModelSetting(app, "Change password"));

		try {
			PackageInfo pInfo = app.getPackageManager().getPackageInfo(app.getPackageName(), 0);
            list.add(new ModelSetting(app, "Last update date GMT", TimeUtils.getGMTDate(pInfo.lastUpdateTime)));
            list.add(new ModelSetting(app, "Version", pInfo.versionName));
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		updateAdapter();		
	}
	
	public void updateAdapter() {
		if(recyclerView!=null && list!=null) {
            AdapterModelSetting adapter = new AdapterModelSetting(app, list);
            adapter.setOnItemClickListener(new AdapterModelSetting.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (position < list.size()) {
                        switch (position) {
                            case 2:
                                ENUM_Action.WEB_SEARCH.action.action(app, app.getConfig().webApplication);
                                break;
                            case 3:
                                Toast.makeText(app, "Welcome message enabled.", Toast.LENGTH_SHORT).show();
                                app.getConfig().setHomeWelcomeMessage(true);
                                break;
                            case 4:
                                //TODO Change passord
                                Toast.makeText(app, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                                break;
                            case 6:
                                if (click_version == 11) {
                                    Toast.makeText(app, "Development settings activated.", Toast.LENGTH_SHORT).show();
                                } else if (click_version < 11) {
                                    if (click_version >= 1) {
                                        final Toast t = Toast.makeText(app, "" + (11 - click_version), Toast.LENGTH_SHORT);
                                        t.show();
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                t.cancel();
                                            }
                                        }, 700);
                                    }
                                    click_version++;
                                }
                                break;
                        }

                    }
                }
            });
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public boolean back() {
        return false;
    }
}
