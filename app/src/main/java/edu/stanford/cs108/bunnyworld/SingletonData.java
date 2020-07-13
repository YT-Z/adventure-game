package edu.stanford.cs108.bunnyworld;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;

import java.util.Map;
import java.util.TreeMap;

class SingletonData {
    private static final SingletonData ourInstance = new SingletonData();

    static SingletonData getInstance() {
        return ourInstance;
    }

    private SingletonData() {
        game = null;
        db = null;
        images = new TreeMap<>();
        images.put("(None)", null);
        sounds = new TreeMap<>();
    }

    Game game;
    SQLiteDatabase db;
    Map<String, BitmapDrawable> images;
    Map<String, MediaPlayer> sounds;

}
