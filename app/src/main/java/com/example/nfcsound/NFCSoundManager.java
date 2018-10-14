package com.example.nfcsound;

import android.app.Application;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;

import com.example.nfcsound.infra.DataPaths;
import com.example.nfcsound.infra.PathFinder;


public class NFCSoundManager extends Application {

    private static final String BASE_ASSETS_PATH = "nfc_name";
    private static final String DEFAULT_TAG_TEXT = "nothing";

    private boolean is_initialized = false;
    private boolean delayed_playing = false;
    private MediaPlayer media;
    private PathFinder path_finder;

    private DataPaths active_name;

    @Override
    public void onCreate () {
        super.onCreate();

        path_finder = new PathFinder(getResources(), BASE_ASSETS_PATH);
        media = new MediaPlayer();
        active_name = path_finder.find(DEFAULT_TAG_TEXT);
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
            active_name = path_finder.find(text);
        } catch (Exception e) {
            active_name = path_finder.find(DEFAULT_TAG_TEXT);
            throw e;
        } finally {
            delayedPlaySound();
        }
    }

    private boolean isPlaying() {
        return media.isPlaying() || delayed_playing;
    }

    private void delayedPlaySound() {
        delayed_playing = true;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                delayed_playing = false;
                playSound();
            }
        }, 300);
    }

    private void playSound() {
        try{
            media = new MediaPlayer();
            AssetFileDescriptor descriptor = getResources().getAssets().openFd(active_name.getSoundPath());
            media.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength() );
            descriptor.close();
            media.prepare();
            media.start();
        } catch(Exception e){
            // handle error here..
        }
    }

    public String getImagePathToShow() {
        return active_name.getImagePath();
    }

}

