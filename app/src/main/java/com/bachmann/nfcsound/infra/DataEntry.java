package com.bachmann.nfcsound.infra;

import java.util.Random;

public class DataEntry {
    private class IntContainer {
        private int value;
        private int max;

        IntContainer( int max) {
            if (max>0) {
                Random r = new Random();
                this.value = r.nextInt(max);
            } else {
                value = 0;
            }
            this.max = max;
        }

        public int inc() {
            if (max < 1) {
                return 0;
            }
            value = (value + 1) % max;
            return value;
        }
    }
    String name;
    String[] images;
    String[] sounds;
    String[] voices;
    String line_drawing;
    IntContainer images_cnt;
    IntContainer sounds_cnt;
    IntContainer voices_cnt;

    public DataEntry(String name, String[] images, String[] sounds, String[] voices, String line_drawing) {
        this.name = name;
        this.images = images;
        this.sounds = sounds;
        this.voices = voices;
        this.line_drawing = line_drawing;

        images_cnt = new IntContainer(images.length);
        sounds_cnt = new IntContainer(sounds.length);
        voices_cnt = new IntContainer(voices.length);
    }

    private String select_next_from_list(String [] list, IntContainer counter) {
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
                select_next_from_list(sounds, sounds_cnt),
                select_next_from_list(images, images_cnt),
                select_next_from_list(voices, voices_cnt),
                line_drawing);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object v) {
        if(v!=null && v instanceof String) {
            return this.name.startsWith(String.valueOf(v));
        }
        return false;
    }
}
