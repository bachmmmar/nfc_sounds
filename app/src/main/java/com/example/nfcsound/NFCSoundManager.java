package com.example.nfcsound;

import android.app.Application;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import com.example.nfcsound.infra.DataManager;
import com.example.nfcsound.infra.DataPaths;
import com.example.nfcsound.infra.PathFinder;


public class NFCSoundManager extends Application {

    private static final String BASE_ASSETS_PATH = "nfc_name";
    private static final String DEFAULT_TAG_TEXT = "nothing";

    private boolean is_initialized = false;
    private boolean delayed_playing = false;
    private MediaPlayer media;
    private DataManager data_manager;

    private DataPaths active_name;

    @Override
    public void onCreate () {
        super.onCreate();

        data_manager = new DataManager(getResources(), BASE_ASSETS_PATH);
        media = new MediaPlayer();
        active_name = data_manager.find(DEFAULT_TAG_TEXT);
    }

    public boolean isInitialized() {
        return is_initialized;
    }

    public void initializeApplication() {
        is_initialized = true;
    }

    public void soundTagDetected(String text) {
        if (isPlaying()) {
            return;
        }

        try {
            active_name = data_manager.find(text);
        } catch (Exception e) {
            active_name = data_manager.find(DEFAULT_TAG_TEXT);
            throw e;
        } finally {
            delayedPlayVoice();
        }
    }

    private boolean isPlaying() {
        return media.isPlaying() || delayed_playing;
    }

    private void delayedPlayVoice() {
        delayed_playing = true;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                delayed_playing = false;
                playVoice();
            }
        }, 300);
    }

    private void playVoice() {
        try {
            media = new MediaPlayer();
            AssetFileDescriptor descriptor = getResources().getAssets().openFd(active_name.getVoicePath());
            media.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength() );
            descriptor.close();
            media.prepare();
            Log.i("player", "start playing voice: "+ active_name.getVoicePath());
            media.start();
            media.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    playSound();
                }
            });
        } catch(Exception e){
            // handle error here..
        }
    }

    private void playSound() {
        try{
            media = new MediaPlayer();
            AssetFileDescriptor descriptor = getResources().getAssets().openFd(active_name.getSoundPath());
            media.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength() );
            descriptor.close();
            media.prepare();
            Log.i("player", "start playing sound: "+ active_name.getSoundPath());
            media.start();
        } catch(Exception e){
            // handle error here..
        }
    }


    public String getImagePathToShow() {
        return active_name.getImagePath();
    }

}

