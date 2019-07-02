package com.bachmann.nfcsound.infra;

import java.util.Random;

public class DataEntry {

    private class IntContainer {
        int value;
        int max;

        IntContainer(int max) {
            value = 0;
            this.max = max;
        }

        int inc() {
            if (max < 1) {
                return 0;
            }
            value = (value + 1) % max;
            return value;
        }
    }

    private class RandomIntContainer extends IntContainer {

        RandomIntContainer(int max) {
            super(max);
            if (max>0) {
                Random r = new Random();
                value = r.nextInt(max);
            }
        }
    }

    private String name;
    private String[] images;
    private String[] sounds;
    private String[] voices;
    private String line_drawing;
    private String[] translations;
    private IntContainer images_cnt;
    private IntContainer sounds_cnt;
    private IntContainer voices_cnt;
    private IntContainer translations_cnt;

    public DataEntry(String name, String[] images, String[] sounds, String[] voices, String line_drawing, String[] translations) {
        this.name = name;
        this.images = images;
        this.sounds = sounds;
        this.voices = voices;
        this.line_drawing = line_drawing;
        this.translations = translations;

        images_cnt = new RandomIntContainer(images.length);
        sounds_cnt = new RandomIntContainer(sounds.length);
        voices_cnt = new RandomIntContainer(voices.length);
        translations_cnt = new IntContainer(translations.length);
    }

    private String selectNextFromList(String [] list, IntContainer counter) {
        if (list.length==0) {
            return "";
        }

        return list[counter.inc()];
    }

    public String getLineDrawingPath() {
        return line_drawing;
    }

    public DataPaths getNext() {
        return new DataPaths(
                selectNextFromList(sounds, sounds_cnt),
                selectNextFromList(images, images_cnt),
                selectNextFromList(voices, voices_cnt), line_drawing);
    }

    public String getName() {
        return name;
    }

    public String getNextName() {
        return selectNextFromList(translations, translations_cnt);
    }

    @Override
    public boolean equals(Object v) {
        if(v instanceof String) {
            return this.name.startsWith(String.valueOf(v));
        }
        return false;
    }
}
