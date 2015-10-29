/**
 * This file is part of Jarvis for Android, an app for managing your server (files, talks...).
 * <p/>
 * Copyright (c) 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 * <p/>
 * LICENSE:
 * <p/>
 * Jarvis for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p/>
 * Jarvis for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.config.Config;
import mercandalli.com.filespace.listener.IFileModelListener;
import mercandalli.com.filespace.listener.IPostExecuteListener;
import mercandalli.com.filespace.manager.file.FileManager;
import mercandalli.com.filespace.model.file.FileModel;
import mercandalli.com.filespace.net.TaskPost;
import mercandalli.com.filespace.ui.activitiy.ApplicationCallback;
import mercandalli.com.filespace.util.StringPair;

public class DialogUpload extends Dialog {

    private final Activity mActivity;
    private final ApplicationCallback mApplicationCallback;
    DialogFileChooser dialogFileChooser;
    File file;
    FileModel mFileModel;
    int id_file_parent;

    public DialogUpload(final Activity activity, final ApplicationCallback applicationCallback, final int id_file_parent, final IPostExecuteListener listener) {
        super(activity);
        mActivity = activity;
        mApplicationCallback = applicationCallback;
        this.id_file_parent = id_file_parent;

        this.setContentView(R.layout.dialog_upload);
        this.setTitle(R.string.app_name);
        this.setCancelable(true);

        this.findViewById(R.id.request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file != null) {
                    List<StringPair> parameters = null;
                    if (mFileModel != null) {
                        parameters = FileManager.getForUpload(mFileModel);
                    }
                    (new TaskPost(mActivity, mApplicationCallback, mApplicationCallback.getConfig().getUrlServer() + Config.routeFile, new IPostExecuteListener() {
                        @Override
                        public void onPostExecute(JSONObject json, String body) {
                            if (listener != null)
                                listener.onPostExecute(json, body);
                        }
                    }, parameters, file)).execute();
                } else
                    Toast.makeText(mActivity, mActivity.getString(R.string.no_file), Toast.LENGTH_SHORT).show();

                DialogUpload.this.dismiss();
            }
        });

        this.findViewById(R.id.fileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFileChooser = new DialogFileChooser(mActivity, mApplicationCallback, new IFileModelListener() {
                    @Override
                    public void executeFileModel(FileModel fileModel) {
                        fileModel.setIdFileParent(id_file_parent);
                        ((TextView) DialogUpload.this.findViewById(R.id.label)).setText(fileModel.getUrl());
                        DialogUpload.this.file = new File(fileModel.getUrl());
                        DialogUpload.this.mFileModel = fileModel;
                    }
                });
            }
        });

        show();
    }
}
