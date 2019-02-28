package com.junipersys.musicthreads;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final String KEY_SONG = "song";
    private boolean mBound = false;

    private Button mDownloadButton;
    private Button mPlayButton;
    private Messenger mServiceMesenger;
    private Messenger mActivityMesenger = new Messenger(new ActivityHandler(this));

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mBound = true;
            mServiceMesenger = new Messenger(binder);
            Message message = Message.obtain();
            message.arg1 = 2;
            message.arg2 = 1;
            message.replyTo = mActivityMesenger;
            try {
                mServiceMesenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //final DownloadThread thread = new DownloadThread();
        //thread.setName("DownloadThread");
        //thread.start();

        mDownloadButton = findViewById(R.id.downloadButton);
        mPlayButton = findViewById(R.id.playButton);

        mDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Downloading...", Toast.LENGTH_SHORT).show();
                // Send Messages to Handler for processing
                for(String song : Playlist.songs){
                    //Message message = Message.obtain();
                    //message.obj = song;
                    //thread.mHandler.sendMessage(message);

                    Intent intent = new Intent(MainActivity.this, DownloadIntentService.class);
                    intent.putExtra(KEY_SONG, song);
                    startService(intent);
                }
            }
        });

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBound){
                    Intent intent = new Intent(MainActivity.this, PlayerService.class);
                    startService(intent);
                    mBound = true;
                    Message message = Message.obtain();
                    message.arg1 = 2;
                    message.replyTo = mActivityMesenger;
                    try {
                        mServiceMesenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void changePlayButtonText(String text){
        mPlayButton.setText(text);
    }
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, PlayerService.class);
        bindService(intent, mServiceConnection , Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }
    }


}
