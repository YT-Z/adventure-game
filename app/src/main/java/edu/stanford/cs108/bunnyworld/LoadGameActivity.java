package edu.stanford.cs108.bunnyworld;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class LoadGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_game);

        db = SingletonData.getInstance().db;
        refresh();
    }

    private SQLiteDatabase db;
    private List<String> gameList;
    private String selectedGame = null;

    class GameListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectedGame = gameList.get(position);
        }
    }

    public static final String LOAD_GAME_ACTIVITY = "LOAD_GAME_ACTIVITY";

    public void onPlayGame(View view) {
        if (selectedGame == null) return;
        SingletonData.getInstance().game = GameDatabase.loadGame(selectedGame, db);
        if (!SingletonData.getInstance().game.checkError()) {
            Intent checkErrorIntent = new Intent(this, CheckErrorActivity.class);
            startActivity(checkErrorIntent);
        } else {
            Intent playGameIntent = new Intent(this, PlayGameActivity.class);
            playGameIntent.putExtra(PlayGameActivity.ACTIVITY_IDENTIFIER_EXTRA, LOAD_GAME_ACTIVITY);
            startActivity(playGameIntent);
        }
    }

    public void onEditGame(View view) {
        if (selectedGame == null) return;
        SingletonData.getInstance().game = GameDatabase.loadGame(selectedGame, db);
        Intent intent = new Intent(this, CreateGameActivity.class);
        startActivity(intent);
    }

    public void onDeleteGame(View view) {
        if (selectedGame == null) return;
        GameDatabase.deleteGame(selectedGame, db);
        refresh();
        selectedGame = null;
    }

    private void refresh() {
        ListView listView = (ListView) findViewById(R.id.games_list);
        gameList = GameDatabase.getGameList(db);
        listView.setAdapter(new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_list_item_single_choice, gameList));
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new GameListener());
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
