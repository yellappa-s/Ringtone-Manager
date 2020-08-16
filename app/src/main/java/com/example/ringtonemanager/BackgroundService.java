package com.example.ringtonemanager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class BackgroundService extends Service {

    secondActivity ob = new secondActivity();
    Uri ring;
    boolean isRinging = false;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private PhoneStateListener listener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            super.onCallStateChanged(state, phoneNumber);
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                ring = ob.RandomRingtone();
                isRinging = true;
            }
            if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                if (isRinging) {
                    RingtoneManager.setActualDefaultRingtoneUri(BackgroundService.this, RingtoneManager.TYPE_RINGTONE, ring);
                    isRinging = false;
                }
            }
        }
    };
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onTaskRemoved(intent);
        super.onStartCommand(intent,flags,startId);
        TelephonyManager tele = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if(tele != null){
            tele.listen(listener,PhoneStateListener.LISTEN_CALL_STATE);
        }
        return START_STICKY;
    }

        @Override
        public void onCreate () {
        super.onCreate();
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
        startMyForeground();
        else
            startForeground(2,new Notification());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyForeground() {
        String NOTFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTFICATION_CHANNEL_ID,channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder nb = new NotificationCompat.Builder(this,NOTFICATION_CHANNEL_ID);
        Notification notification = nb.setOngoing(true)
                .setContentTitle("App is running")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2,notification);

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartService = new Intent(getApplicationContext(),this.getClass());
        restartService.setPackage(getPackageName());
        startService(restartService);
        super.onTaskRemoved(rootIntent);
    }

    @Override
        public void onDestroy () {
            super.onDestroy();
            TelephonyManager tele = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if(tele != null){
            tele.listen(listener,PhoneStateListener.LISTEN_CALL_STATE);
            }
            Intent BroadIntent = new Intent(this,BackgroundService.class);
            startService(BroadIntent);
            }
        }

