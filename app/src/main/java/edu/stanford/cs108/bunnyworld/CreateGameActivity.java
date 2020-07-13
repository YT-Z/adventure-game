package edu.stanford.cs108.bunnyworld;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CreateGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        spinner = (Spinner) findViewById(R.id.pages_spinner);
        textView = (TextView) findViewById(R.id.page_name_text);
        gameView = (GameView) findViewById(R.id.gameview);

        spinner.setOnItemSelectedListener(new PageListener());
        textView.setText(SingletonData.getInstance().game.getCurrentPage().getName());
        textView.setOnEditorActionListener(new NameListener());

        refreshSpinner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_game_menu, menu);
        return true;
    }

    private static GameView gameView;
    private Spinner spinner;
    private TextView textView;

    public static GameView getGameView() {
        return gameView;
    }

    class PageListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Game game = SingletonData.getInstance().game;
            if (game.getCurrentPage() != game.getPages().get(position)) {
                game.setSelectedShape(null);
            }
            game.setCurrentPage(game.getPages().get(position));
            textView.setText(game.getCurrentPage().getName());
            gameView.invalidate();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class NameListener implements TextView.OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            Game game = SingletonData.getInstance().game;
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (v.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Name cannot be blank", Toast.LENGTH_SHORT);
                    toast.show();
                    return false;
                }
                for (Page page : game.getPages()) {
                    if (page == game.getCurrentPage()) continue;
                    if (v.getText().toString().toLowerCase().equals(page.getName().toLowerCase())) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Name already exists", Toast.LENGTH_SHORT);
                        toast.show();
                        return false;
                    }
                }
                game.getCurrentPage().setName(textView.getText().toString());
                refreshSpinner();
            }
            return false;
        }
    }

    private void refreshSpinner() {
        Game game = SingletonData.getInstance().game;
        List<String> pages = new ArrayList<>();
        for (Page page : game.getPages()) {
            pages.add(page.getName());
        }
        spinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, pages));
        spinner.setSelection(game.getPages().indexOf(game.getCurrentPage()));
    }

    public void onAddShape(View view) {
        SingletonData.getInstance().game.setSelectedShape(null);
        Intent intent = new Intent(this, AddShape.class);
        startActivity(intent);
    }

    public void onEditShape(View view) {
        if (SingletonData.getInstance().game.getSelectedShape() == null) return;
        Intent intent = new Intent(this, AddShape.class);
        startActivity(intent);
    }

    public void onDeleteShape(View view) {
        Game game = SingletonData.getInstance().game;
        if (game.getSelectedShape() == null) return;
        game.getCurrentPage().getShapes().remove(game.getSelectedShape());
        game.getShapes().remove(game.getSelectedShape());
        game.setSelectedShape(null);
        gameView.invalidate();
    }

    public void onAddPage(View view) {
        Game game = SingletonData.getInstance().game;
        game.setSelectedShape(null);
        game.getPages().add(new Page(game));
        game.setCurrentPage(game.getPages().get(game.getPages().size() - 1));
        refreshSpinner();
        gameView.invalidate();
    }

    public void onDeletePage(View view) {
        Game game = SingletonData.getInstance().game;
        game.setSelectedShape(null);
        if (game.getPages().size() <= 1) return;
        for (Shape shape : game.getCurrentPage().getShapes()) {
            game.getShapes().remove(shape);
        }
        game.getPages().remove(game.getCurrentPage());
        game.setCurrentPage(game.getPages().get(0));
        refreshSpinner();
        gameView.invalidate();
    }

    public void onBackgroundImage(View view) {
        AlertDialog.Builder backgroundImageBuilder = new AlertDialog.Builder(this);
        backgroundImageBuilder.setTitle("Set Background Image Below");
        LayoutInflater inflater = LayoutInflater.from(this);

        final View backgroundImageView = inflater.inflate(R.layout.background_image, null);
        backgroundImageBuilder.setView(backgroundImageView);

        final Game game = SingletonData.getInstance().game;
        final GameView gameView = CreateGameActivity.gameView;

        final Spinner spinner = (Spinner) backgroundImageView.findViewById(R.id.background_spinner);
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                SingletonData.getInstance().images.keySet().toArray(new String[0])));
        ((Button) backgroundImageView.findViewById(R.id.set_current_button)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        game.getCurrentPage().setBackgroundImage(
                                spinner.getSelectedItem().toString());
                        gameView.invalidate();
                    }
                });
        ((Button) backgroundImageView.findViewById(R.id.set_all_button)).setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        for (Page page : game.getPages()) {
                            page.setBackgroundImage(spinner.getSelectedItem().toString());
                        }
                        gameView.invalidate();
                    }
                });
        backgroundImageBuilder.setNegativeButton("Back", null);
        AlertDialog alertDialog = backgroundImageBuilder.create();
        alertDialog.show();
    }

    private Shape clipboard;

    public void onCut(MenuItem item) {
        Game game = SingletonData.getInstance().game;
        if (game.getSelectedShape() == null) return;
        clipboard = game.getSelectedShape();
        clipboard.setPage(null);
        game.getCurrentPage().getShapes().remove(clipboard);
        game.getShapes().remove(clipboard);
        gameView.invalidate();
        Toast toast = Toast.makeText(getApplicationContext(),
                game.getSelectedShape().getName() + " Cut", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onCopy(MenuItem item) {
        Game game = SingletonData.getInstance().game;
        if (game.getSelectedShape() == null) return;
        clipboard = game.getSelectedShape().clone();
        clipboard.setPage(null);
        gameView.invalidate();
        Toast toast = Toast.makeText(getApplicationContext(),
                game.getSelectedShape().getName() + " Copied", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onPaste(MenuItem item) {
        if (clipboard == null) return;
        Game game = SingletonData.getInstance().game;
        clipboard.setPage(game.getCurrentPage());
        game.getCurrentPage().getShapes().add(clipboard);
        game.getShapes().add(clipboard);
        gameView.invalidate();
        Toast toast = Toast.makeText(getApplicationContext(),
                clipboard.getName() + " Pasted", Toast.LENGTH_SHORT);
        toast.show();
        clipboard = null;
    }

    public static final String CREATE_GAME_ACTIVITY = "CREATE_GAME_ACTIVITY";

    public void onSaveGame(MenuItem item) {
        GameDatabase.saveGame(SingletonData.getInstance().game,
                SingletonData.getInstance().db, true);
        Toast toast = Toast.makeText(getApplicationContext(),
                "GAME SAVED", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onPlayGame(MenuItem item) {
        if (!SingletonData.getInstance().game.checkError()) {
            Intent checkErrorIntent = new Intent(this, CheckErrorActivity.class);
            startActivity(checkErrorIntent);
        } else {
            GameDatabase.saveGame(SingletonData.getInstance().game,
                    SingletonData.getInstance().db, false);
            Intent playGameIntent = new Intent(this, PlayGameActivity.class);
            playGameIntent.putExtra(PlayGameActivity.ACTIVITY_IDENTIFIER_EXTRA,
                    CREATE_GAME_ACTIVITY);
            startActivity(playGameIntent);
        }
        spinner.setSelection(0);
    }

    @Override
    public void onBackPressed() {
        SingletonData.getInstance().game = null;
        finish();
    }

}
