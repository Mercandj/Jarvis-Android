package com.mercandalli.android.apps.files.file.audio;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

/**
 * A {@link BroadcastReceiver} to receive the {@link FileAudioModel} notification actions.
 * TODO
 */
public class NotificationAudioPlayerReceiver extends BroadcastReceiver {

    /**
     * The action start {@link FileAudioActivity}.
     */
    private static final String ACTION_ACTIVITY = "NotificationAudioPlayerReceiver.Actions.ACTION_ACTIVITY";

    /**
     * The action play or pause.
     */
    private static final String ACTION_PLAY_PAUSE = "NotificationAudioPlayerReceiver.Actions.ACTION_PLAY_PAUSE";

    /**
     * The action previous.
     */
    private static final String ACTION_PREVIOUS = "NotificationAudioPlayerReceiver.Actions.ACTION_PREVIOUS";

    /**
     * The action next.
     */
    private static final String ACTION_NEXT = "NotificationAudioPlayerReceiver.Actions.ACTION_NEXT";

    /**
     * The action close.
     */
    private static final String ACTION_CLOSE = "NotificationAudioPlayerReceiver.Actions.ACTION_CLOSE";

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        final FileAudioPlayer fileAudioPlayer = FileAudioPlayer.getInstance(context);
        if (fileAudioPlayer.isEmpty()) {
            fileAudioPlayer.setNotification(false);
            return;
        }
        switch (action) {
            case ACTION_ACTIVITY:
                FileAudioActivity.resume(context);
                break;
            case ACTION_PLAY_PAUSE:
                if (fileAudioPlayer.isPlaying()) {
                    fileAudioPlayer.pause();
                } else {
                    fileAudioPlayer.play();
                }
                break;
            case ACTION_NEXT:
                fileAudioPlayer.next();
                break;
            case ACTION_PREVIOUS:
                fileAudioPlayer.previous();
                break;
            case ACTION_CLOSE:
                if (fileAudioPlayer.isPlaying()) {
                    fileAudioPlayer.pause();
                }
                break;
        }
    }

    public static PendingIntent getNotificationIntentActivity(@NonNull final Context context) {
        return getPendingIntent(context, ACTION_ACTIVITY);
    }

    public static PendingIntent getNotificationIntentPlayPause(@NonNull final Context context) {
        return getPendingIntent(context, ACTION_PLAY_PAUSE);
    }

    public static PendingIntent getNotificationIntentPrevious(@NonNull final Context context) {
        return getPendingIntent(context, ACTION_PREVIOUS);
    }

    public static PendingIntent getNotificationIntentNext(@NonNull final Context context) {
        return getPendingIntent(context, ACTION_NEXT);
    }

    public static PendingIntent getNotificationIntentClose(@NonNull final Context context) {
        return getPendingIntent(context, ACTION_CLOSE);
    }

    private static PendingIntent getPendingIntent(@NonNull final Context context, String action) {
        final Intent intent = new Intent(context, NotificationAudioPlayerReceiver.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
