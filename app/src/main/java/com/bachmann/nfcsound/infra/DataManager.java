package com.bachmann.nfcsound.infra;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class DataManager {

    private String[] audio_patterns = {".mp3", ".wav", ".ogg"};
    private String[] image_patterns = {".jpg", ".png", ".gif"};

    private String[] selected_languages = {"en_us", "de_ch"};

    private Resources res;
    private Context context;
    private String base_assets_path;
    private String[] available_names;

    ArrayList<DataEntry> data;

    public DataManager(Context context, String base_assets_path) {
        this.res = context.getResources();
        this.context = context;
        this.base_assets_path = base_assets_path;

        try {
            available_names = res.getAssets().list(base_assets_path);
        } catch (IOException e) {
            throw new UnknownPathException("Could not list content of '" + base_assets_path + "'!");
        }

        findData();
    }


    private void findData() {
        data = new ArrayList<>();
        for ( String name : available_names) {
            ArrayList<String> files = getFilesFromDirectory(name);
            ArrayList<String> sounds = extractListWithMatchingEndings(files, audio_patterns);
            ArrayList<String> images = extractListWithMatchingEndings(files, image_patterns);

            ArrayList<String> voices = getAllSelectedVoices(name);

            ArrayList<String> names = getAllTranslations(name);

            String line_drawing = getLineDrawing(name);

            data.add(new DataEntry(name,
                    images.toArray(new String[0]),
                    sounds.toArray(new String[0]),
                    voices.toArray(new String[0]),
                    line_drawing,
                    names.toArray(new String[0])));
        }
    }

    private ArrayList<String> getAllTranslations(String name) {
        ArrayList<String> names = new ArrayList<>();

        int id = res.getIdentifier(name, "string", context.getPackageName());
        if (id == 0) {
            Log.e("Translation", "Could not get identifier for " + name);
            names.add(name);
            return names;
        }

        for (String local : selected_languages) {
            try {
                String n = getStringByLocal(context, id, local);
                names.add(n);
            } catch (Resources.NotFoundException e) {
                Log.e("Translation", "Could not get translation for " + name + " in " + local);
                names.add(name);
                return names;
            }
        }
        return names;
    }

    static Locale getLocaleFromString(String locale) {
        String split_locale[] = locale.split("_");
        if (split_locale.length == 1) {
            return new Locale(split_locale[0]);
        } else if (split_locale.length == 2) {
            return new Locale(split_locale[0], split_locale[1]);
        } else {
            throw new RuntimeException("The locale " + locale + " can not be interpreted!");
        }
    }

    @NonNull
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static String getStringByLocal(Context context, int id, String locale) {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        Locale l = getLocaleFromString(locale);
        configuration.setLocale(l);
        return context.createConfigurationContext(configuration).getResources().getString(id);
    }

    private String getLineDrawing(String name) {
        ArrayList<String> line_drawing_path = new ArrayList<String>();
        try {
            line_drawing_path = getFilesFromDirectory(name + "/line_drawing" );
        } catch (UnknownPathException e) {
            Log.w("Line Drawing", "No drawing found for '" + name +"'found!");
        }
        if (line_drawing_path.size() >= 1) {
            return line_drawing_path.get(0);
        }
        return "";
    }

    private ArrayList<String> getAllSelectedVoices(String name) {
        ArrayList<String> voices = new ArrayList<>();
        for (String language : selected_languages) {
            String language_path = name + "/" + language;
            try {
                ArrayList<String> language_voices = getFilesFromDirectory(language_path);
                voices.addAll(language_voices);
            } catch (UnknownPathException e) {
                Log.w("Voices", "No voice for '" + language +"'found!");
            }
        }

        return extractListWithMatchingEndings(voices, audio_patterns);
    }

    private ArrayList<String>  extractListWithMatchingEndings(ArrayList<String>  input, String[] pattern) {
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

    public int size() {
        return data.size();
    }

    public DataEntry get(int index) {
        return data.get(index);
    }

    public DataEntry find(String name) {
        if (name.isEmpty()) {
            throw new UnknownPathException("Name is empty!");
        }

        for (DataEntry d : data) {
            if (d.equals(name)){
                return d;
            }
        }
        int i = data.indexOf(name);
        if (i<0) {
            throw new UnknownPathException("Name not found!");
        }
        return data.get(i);
    }


    private ArrayList<String> getFilesFromDirectory(String name) {
        String source_directory = base_assets_path + "/" + name;
        String[] files;
        try {
            files = res.getAssets().list(source_directory);
        } catch (IOException e) {
            throw new UnknownPathException("Could not list files from '" + source_directory + "'!");
        }

        ArrayList<String> filepaths = new ArrayList<>();
        for (String f: files) {
            filepaths.add(source_directory + "/" + f);
        }

        return filepaths;
    }


}
