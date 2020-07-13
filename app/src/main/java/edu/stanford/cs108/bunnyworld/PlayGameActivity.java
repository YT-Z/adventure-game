package edu.stanford.cs108.bunnyworld;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class PlayGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        Game game = SingletonData.getInstance().game;
        for (Shape shape : game.getShapes()) {
            shape.parseScripts();
        }
        game.startGame();
        game.setCurrentPage(null);
        game.switchToPage(game.getPages().get(0));
        PlayView playView = (PlayView) findViewById(R.id.playview);
        playView.invalidate();
    }

    public static final String ACTIVITY_IDENTIFIER_EXTRA = "ACTIVITY_IDENTIFIER";

    @Override
    public void onBackPressed() {
        String identifier = getIntent().getStringExtra(ACTIVITY_IDENTIFIER_EXTRA);
        String gameName = SingletonData.getInstance().game.getName();
        if (identifier != null) {
            switch (identifier) {
                case CreateGameActivity.CREATE_GAME_ACTIVITY:
                    SingletonData.getInstance().game = GameDatabase.loadGame(
                            gameName + "_tmp", SingletonData.getInstance().db);
                    GameDatabase.deleteGame(
                            gameName + "_tmp", SingletonData.getInstance().db);
                    SingletonData.getInstance().game.setName(gameName);
                    CreateGameActivity.getGameView().invalidate();
                    finish();
                    break;
                case LoadGameActivity.LOAD_GAME_ACTIVITY:
                    SingletonData.getInstance().game = null;
                    break;
            }
        } else {
            SingletonData.getInstance().game = null;
        }
        Page.Possession.clear();
        finish();
    }

}
