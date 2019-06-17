package com.bachmann.nfcsound.infra;

import android.content.res.Resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class PathFinder {

    private String[] available_names;
    private Resources res;
    private String base_assets_path;

    public PathFinder(Resources res, String base_assets_path) {
        this.res = res;
        this.base_assets_path = base_assets_path;

        try {
            available_names = res.getAssets().list(base_assets_path);
        } catch (IOException e) {
            throw new UnknownPathException("Could not list content of '" + base_assets_path + "'!");
        }
    }

    public DataPaths find(String name) {

        if (name.isEmpty()) {
            throw new UnknownPathException("Name is empty!");
        }

        if (!doesNameExists(name)) {
            throw new UnknownPathException("Name not found!");
        }

        String[] files = getFilesFromDirectory(name);

        String[] audio_patterns = {".mp3", ".wav"};
        String[] image_patterns = {".jpg", ".png", ".gif"};
        ArrayList<String> audio = extractListWithMatchingEndings(files, audio_patterns);
        ArrayList<String> image = extractListWithMatchingEndings(files, image_patterns);


        return new DataPaths(getRandomItem(audio), getRandomItem(image), "", "");
    }


    private boolean doesNameExists(String name) {
        if (available_names.length == 0) {
            return false;
        }

        for (String n : available_names) {
            if (n.startsWith(name)) {
                return true;
            }
        }
        return false;
    }

    public String getRandomItem(ArrayList<String> input) {
        if (input.size() < 1) {
            throw new UnknownPathException("Cannot get random item from empty list");
        }
        Random r = new Random();
        int index = r.nextInt(input.size());
        return input.get(index);
    }

    private ArrayList<String> extractListWithMatchingEndings(String[] input, String[] pattern) {
        ArrayList<String> output = new ArrayList<String>();
        for (String i : input) {
            for (String p : pattern) {
                if (i.endsWith(p)) {
                    output.add(i);
                }
            }
        }
        return output;
    }

    private String[] getFilesFromDirectory(String name) {
        String source_directory = base_assets_path + "/" + name;
        String[] files;
        try {
            files = res.getAssets().list(source_directory);
        } catch (IOException e) {
            throw new UnknownPathException("Could not list files from '" + source_directory + "'!");
        }

        for (int i=0; i< files.length; i++) {
            files[i] = source_directory + "/" + files[i];
        }

        return files;
    }
}