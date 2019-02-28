package com.junipersys.musicthreads;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class PlayerService extends Service {
    private MediaPlayer mediaPlayer;
    private IBinder mBinder = new localBinder();

    @Override
    public void onCreate() {
        mediaPlayer = MediaPlayer.create(this, R.raw.jingle);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopSelf();
            }
        });
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        mediaPlayer.release();
    }

    public class localBinder extends Binder {
        public PlayerService getService(){
            return PlayerService.this;
        }
    }
    //Client Methods

    public void play(){
        mediaPlayer.start();
    }

    public void pause(){
        mediaPlayer.pause();
    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }
}
