package com.bachmann.nfcsound;

import android.app.Application;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import com.bachmann.nfcsound.infra.DataManager;
import com.bachmann.nfcsound.infra.DataPaths;
import com.bachmann.nfcsound.infra.SizeLimitedList;


public class NFCSoundManager {

    public static final int PLAYLIST_HISTORY_SIZE = 6;

    private static final String DEFAULT_TAG_TEXT = "nothing";

    private boolean is_initialized = false;
    private boolean delayed_playing = false;
    private Resources resources;
    private MediaPlayer media;
    private DataManager data_manager;

    private DataPaths active_name;

    private SizeLimitedList last_played = new SizeLimitedList(PLAYLIST_HISTORY_SIZE);


    public NFCSoundManager (Resources res, DataManager dm) {
        resources = res;
        data_manager = dm;
        media = new MediaPlayer();
        active_name = data_manager.find(DEFAULT_TAG_TEXT);
    }

    @Deprecated
    public boolean isInitialized() {
        return is_initialized;
    }

    @Deprecated
    public void initializeApplication() {
        is_initialized = true;
    }

    public void soundTagDetected(String text) {
        if (isPlaying()) {
            return;
        }

        try {
            active_name = data_manager.find(text);
            last_played.add(text);
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

    // delayed playing is used because some devices have a "tag scanned" sound which can not be turned of
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
        if (active_name.getVoicePath().isEmpty()) {
            Log.i("player", "no voice to play --> playSound");
        }
        try {
            media = new MediaPlayer();
            AssetFileDescriptor descriptor = resources.getAssets().openFd(active_name.getVoicePath());
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
            Log.e("player", "failed to play: "+ active_name.getVoicePath() +"\n" + e.toString());
        }
    }

    private void playSound() {
        if (active_name.getSoundPath().isEmpty()) {
            Log.i("player", "no sound to play");
        }
        try{
            media = new MediaPlayer();
            AssetFileDescriptor descriptor = resources.getAssets().openFd(active_name.getSoundPath());
            media.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength() );
            descriptor.close();
            media.prepare();
            Log.i("player", "start playing sound: "+ active_name.getSoundPath());
            media.start();
        } catch(Exception e){
            Log.e("player", "failed to play: "+ active_name.getSoundPath() +"\n" + e.toString());
        }
    }


    public String getImagePathToShow() {
        return active_name.getImagePath();
    }

    public String[] getRecentlyPlayed() {
        return last_played.getAllAsArray();
    }
}

