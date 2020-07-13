package edu.stanford.cs108.bunnyworld;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        preload png images in R.drawable
         */
        String[] imageNames = {"carrot", "carrot2", "death", "duck", "fire", "mystic"};
        Class drawableClass = R.drawable.class;
        for (String imageName : imageNames) {
            Field field = null;
            try {
                field = drawableClass.getField(imageName);
            } catch (NoSuchFieldException e) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        imageName + ".png not found", Toast.LENGTH_SHORT);
                toast.show();
            }
            try {
                int drawableId = field.getInt(null);
                SingletonData.getInstance().images.put(
                        imageName, (BitmapDrawable) getResources().getDrawable(drawableId));
            } catch (IllegalAccessException e) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Cannot preload " + imageName + ".png", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        /*
        preload mp3 sounds in R.raw
         */
        String[] soundNames = {"carrotcarrotcarrot", "evillaugh", "fire", "hooray", "munch", "munching", "woof"};
        Class rawClass = R.raw.class;
        for (String soundName : soundNames) {
            Field field = null;
            try {
                field = rawClass.getField(soundName);
            } catch (NoSuchFieldException e) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        soundName + ".mp3 not found", Toast.LENGTH_SHORT);
                toast.show();
            }
            try {
                int soundId = field.getInt(null);
                SingletonData.getInstance().sounds.put(
                        soundName, MediaPlayer.create(getApplicationContext(), soundId));
            } catch (IllegalAccessException e) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Cannot preload " + soundName + ".mp3", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        System.out.println(SingletonData.getInstance().images);

        SingletonData.getInstance().db = GameDatabase.initDatabase(getApplicationContext());
        GameDatabase.cleanCache(SingletonData.getInstance().db);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public void onImportImage(MenuItem item) {
        Intent intent = new Intent(this, ImportImage.class);
        startActivity(intent);
    }

    public void onCreateGame(View view) {
        String gameName = ((TextView) findViewById(R.id.game_name_text)).getText().toString();
        if (GameDatabase.getGameList(SingletonData.getInstance().db).contains(gameName)) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Name already exists", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        Game game = new Game();
        game.setName(gameName);
        Page.setCount(0);
        Shape.setCount(0);
        Page page = new Page(game);
        game.getPages().add(page);
        game.setCurrentPage(page);
        SingletonData.getInstance().game = game;
        Intent intent = new Intent(this, CreateGameActivity.class);
        startActivity(intent);
    }

    public void onLoadGame(View view) {
        Intent intent = new Intent(this, LoadGameActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        // disabled
    }

}
