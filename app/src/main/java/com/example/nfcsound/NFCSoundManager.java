package com.example.nfcsound;

import android.app.Application;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;
import android.content.res.Resources;


import com.example.nfcsound.infra.DataPaths;
import com.example.nfcsound.infra.PathFinder;

import java.util.Arrays;
import java.util.List;


public class NFCSoundManager extends Application {

    public static final int PLAYLIST_HISTORY_SIZE = 4;

    private static final String TAG_PROGRAMM = "Program Flow";

    private static final String BASE_ASSETS_PATH = "nfc_name";

    private boolean is_initialized = false;
    private MediaPlayer media;
    private PathFinder path_finder;

    private DataPaths active_name;

    @Override
    public void onCreate () {
        super.onCreate();
        initializeApplication();
    }

    public boolean isInitialized() {
        return is_initialized;
    }

    public void initializeApplication() {
        path_finder = new PathFinder(getResources(), BASE_ASSETS_PATH);
        media = new MediaPlayer();
        active_name = path_finder.find("dog");
        is_initialized = true;
    }

    public void soundTagDetected(String sound_name) {
        playSound();
    }

    private void playSound() {
        try{
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

