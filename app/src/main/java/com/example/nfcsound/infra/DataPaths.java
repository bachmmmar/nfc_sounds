package com.example.nfcsound.infra;

public class DataPaths {

    private String sound_path;
    private String image_path;
    private String voice_path;

    public DataPaths(String sound_path, String image_path, String voice_path) {
        this.sound_path = sound_path;
        this.image_path = image_path;
        this.voice_path = voice_path;
    }

    public String getSoundPath() {
        return sound_path;
    }

    public String getImagePath() {
        return image_path;
    }

    public String getVoicePath() { return voice_path; }



}
