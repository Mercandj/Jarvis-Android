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

package mercandalli.com.jarvis.ui.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.ui.adapter.AdapterModelConnversationMessage;
import mercandalli.com.jarvis.listener.IPostExecuteListener;
import mercandalli.com.jarvis.model.ModelConversationMessage;
import mercandalli.com.jarvis.net.TaskGet;
import mercandalli.com.jarvis.net.TaskPost;
import mercandalli.com.jarvis.ui.view.DividerItemDecoration;

/**
 * Created by Jonathan on 14/12/2014.
 */
public class ActivityConversation extends Application {

    private String login, password, url;
    private String id_conversation;

    private RecyclerView listView;
    private AdapterModelConnversationMessage adapter;
    private ArrayList<ModelConversationMessage> list = new ArrayList<>();
    private ProgressBar circularProgressBar;
    private TextView message;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText input;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_conversation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if(toolbar!=null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            Log.e(""+getClass().getName(), "extras == null");
            this.finish();
            this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return;
        }

        this.input = (EditText) findViewById(R.id.input);

        this.id_conversation = extras.getString("ID_CONVERSATION");
        this.url = this.getConfig().getUrlServer() + this.getConfig().routeUserMessage + "/" + this.id_conversation;

        this.circularProgressBar = (ProgressBar) findViewById(R.id.circularProgressBar);
        this.message = (TextView) findViewById(R.id.message);

        this.listView = (RecyclerView) findViewById(R.id.listView);
        this.listView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setStackFromEnd(true);
        this.listView.setLayoutManager(mLayoutManager);

        this.swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        this.swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });

        this.adapter = new AdapterModelConnversationMessage(this, list, null);
        this.listView.setAdapter(adapter);
        this.listView.setItemAnimator(/*new SlideInFromLeftItemAnimator(mRecyclerView)*/new DefaultItemAnimator());
        this.listView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        this.adapter.setOnItemClickListener(new AdapterModelConnversationMessage.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });

        this.adapter.setOnItemLongClickListener(new AdapterModelConnversationMessage.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {

                return true;
            }
        });

        this.input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    String url = getConfig().getUrlServer() + getConfig().routeUserMessage + "/" + id_conversation;
                    List <BasicNameValuePair> parameters = new ArrayList<>();
                    parameters.add(new BasicNameValuePair("message", "" + input.getText().toString()));
                    input.setText("");

                    new TaskPost(ActivityConversation.this, url, new IPostExecuteListener() {
                        @Override
                        public void execute(JSONObject json, String body) {
                            refreshList();
                        }
                    }, parameters).execute();

                    return true;
                }
                return false;
            }
        });

        refreshList();
    }

    @Override
    public void refreshAdapters() {

    }

    @Override
    public void updateAdapters() {
        if(this.listView!=null && this.list!=null) {
            if(this.list.size()==0) {
                if(this.url==null)
                    this.message.setText(getString(R.string.no_file_server));
                else if(this.url.equals(""))
                    this.message.setText(getString(R.string.no_file_server));
                else
                    this.message.setText(getString(R.string.no_file_directory));
                this.message.setVisibility(View.VISIBLE);
            }
            else
                this.message.setVisibility(View.GONE);

            this.adapter.remplaceList(this.list);
            listView.scrollToPosition(list.size()-1);

            this.circularProgressBar.setVisibility(View.GONE);
            this.swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void refreshList() {
        refreshList(null);
    }

    public void refreshList(String search) {
        if(url == null) {
            this.finish();
            this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return;
        }

        List<BasicNameValuePair> parameters = null;
        if(this.isInternetConnection()) {
            new TaskGet(
                    this,
                    this.getConfig().getUser(),
                    this.url,
                    new IPostExecuteListener() {
                        @Override
                        public void execute(JSONObject json, String body) {
                            list = new ArrayList<>();
                            try {
                                if (json != null) {
                                    if (json.has("result")) {
                                        JSONArray array = json.getJSONArray("result");
                                        for (int i = 0; i < array.length(); i++) {
                                            ModelConversationMessage modelFile = new ModelConversationMessage(ActivityConversation.this, array.getJSONObject(i));
                                            list.add(modelFile);
                                        }
                                    }
                                }
                                else
                                    Toast.makeText(ActivityConversation.this, ActivityConversation.this.getString(R.string.action_failed), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            updateAdapters();
                        }
                    },
                    parameters
            ).execute();
        }
        else {
            this.circularProgressBar.setVisibility(View.GONE);
            this.message.setText(getString(R.string.no_internet_connection));
            this.message.setVisibility(View.VISIBLE);
            this.swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.finish();
            this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
        }
        return super.onKeyDown(keyCode, event);
    }
}
