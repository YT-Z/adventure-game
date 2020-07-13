package edu.stanford.cs108.bunnyworld;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddScript extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_script);

        linearLayout = (LinearLayout) findViewById(R.id.linear_layout);
        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        Game game = SingletonData.getInstance().game;
        tempShape = AddShape.getTempShape();
        index = getIntent().getIntExtra(AddShape.SCRIPT_INDEX_EXTRA, -1);

        pages = new String[game.getPages().size()];
        for (int i = 0; i < game.getPages().size(); i ++) {
            pages[i] = game.getPages().get(i).getName();
        }
        shapes = new String[game.getShapes().size()];
        for (int i = 0; i < game.getShapes().size(); i ++) {
            shapes[i] = game.getShapes().get(i).getName();
        }
        sounds = SingletonData.getInstance().sounds.keySet().toArray(new String[0]);

        triggersAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, triggers);
        actionsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, actions);
        pagesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, pages);
        shapesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, shapes);
        soundsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, sounds);
        emptyAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, empty);

        if (index == -1) {
            onAddAction(null);
            Spinner triggerKey = (Spinner) findViewById(R.id.trigger_key);
            final Spinner triggerValue = (Spinner) findViewById(R.id.trigger_value);
            triggerKey.setAdapter(triggersAdapter);
            triggerKey.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                           int position, long id) {
                    switch (position) {
                        case 0:
                        case 1:
                            triggerValue.setAdapter(emptyAdapter);
                            break;
                        case 2:
                            triggerValue.setAdapter(shapesAdapter);
                            if (shapesAdapter.isEmpty()) {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "No shape found in this game", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    triggerValue.setAdapter(emptyAdapter);
                }
            });
        } else {
            readScript(tempShape.getSplitScripts().get(index));
        }
    }

    private LinearLayout linearLayout;
    private LayoutInflater layoutInflater;
    private Shape tempShape;
    private int index;
    private final static String[] triggers = {
            Game.ONCLICK,
            Game.ONENTER,
            Game.ONDROP
    };
    private final static String[] actions = {
            Game.GOTO,
            Game.PLAY,
            Game.HIDE,
            Game.SHOW
    };
    private String[] pages;
    private String[] shapes;
    private String[] sounds;
    private final static String[] empty = {
            ""
    };
    private ArrayAdapter
            triggersAdapter,
            actionsAdapter,
            pagesAdapter,
            shapesAdapter,
            soundsAdapter,
            emptyAdapter;

    public void onAddAction(View view) {
        View extra = layoutInflater.inflate(R.layout.extra_action, null);
        linearLayout.addView(extra);

        Spinner actionKey = (Spinner) extra.findViewById(R.id.action_key);
        final Spinner actionValue = (Spinner) extra.findViewById(R.id.action_value);

        actionKey.setAdapter(actionsAdapter);
        actionKey.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                       int position, long id) {
                switch (position) {
                    case 0:
                        actionValue.setAdapter(pagesAdapter);
                        if (pagesAdapter.isEmpty()) {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "No page found in this game", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        break;
                    case 1:
                        actionValue.setAdapter(soundsAdapter);
                        if (soundsAdapter.isEmpty()) {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "No sound found in this game", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        break;
                    case 2:
                    case 3:
                        actionValue.setAdapter(shapesAdapter);
                        if (shapesAdapter.isEmpty()) {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "No shape found in this game", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                actionValue.setAdapter(emptyAdapter);
            }
        });
    }

    public void onDeleteAction(View view) {
        if (linearLayout.getChildCount() > 1) {
            linearLayout.removeView((View) view.getParent());
        }
    }

    public void onConfirm(View view) {
        List<String> splitScript = new ArrayList<>();
        StringBuilder script = new StringBuilder();

        Spinner triggerKey = (Spinner) findViewById(R.id.trigger_key);
        Spinner triggerValue = (Spinner) findViewById(R.id.trigger_value);
        if (triggerValue.getAdapter().isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Missing object after trigger", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        splitScript.add(triggerKey.getSelectedItem().toString());
        splitScript.add(triggerValue.getSelectedItem().toString());
        script.append(triggerKey.getSelectedItem().toString()).append(" ");
        script.append(triggerValue.getSelectedItem().toString()).append(" ");

        int numActions = linearLayout.getChildCount();
        for (int i = 0; i < numActions; i ++) {
            View child = linearLayout.getChildAt(i);
            Spinner actionKey = (Spinner) child.findViewById(R.id.action_key);
            Spinner actionValue = (Spinner) child.findViewById(R.id.action_value);
            if (actionValue.getAdapter().isEmpty()) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Missing object after action", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            splitScript.add(actionKey.getSelectedItem().toString());
            splitScript.add(actionValue.getSelectedItem().toString());
            script.append(actionKey.getSelectedItem().toString()).append(" ");
            script.append(actionValue.getSelectedItem().toString()).append(" ");
        }

        if (script.charAt(script.length() - 1) == ' ') {
            script.setCharAt(script.length() - 1, ';');
        }

        if (index == -1) {
            tempShape.getScripts().add(script.toString());
            tempShape.getSplitScripts().add(splitScript);
            AddShape.setIndex(tempShape.getScripts().size() - 1);
        } else {
            tempShape.getScripts().set(index, script.toString());
            tempShape.getSplitScripts().set(index, splitScript);
        }

        AddShape.getScriptsAdapter().notifyDataSetChanged();
        AddShape.getScriptsSpinner().setSelection(AddShape.getIndex());

        finish();
    }

    private void readScript(List<String> splitScript) {
        if (splitScript == null) return;

        Spinner triggerKey = (Spinner) findViewById(R.id.trigger_key);
        final Spinner triggerValue = (Spinner) findViewById(R.id.trigger_value);
        int i = 0;
        triggerKey.setAdapter(triggersAdapter);
        int triggerIndex = Arrays.asList(triggers).indexOf(splitScript.get(i));
        if (triggerIndex != -1) {
            triggerKey.setSelection(triggerIndex, false);
            String triggerObject = splitScript.get(++ i);
            int triggerObjectIndex;
            switch (triggerIndex) {
                case 0:
                case 1:
                    triggerValue.setAdapter(emptyAdapter);
                    triggerValue.setSelection(0);
                    break;
                case 2:
                    triggerValue.setAdapter(shapesAdapter);
                    triggerObjectIndex = Arrays.asList(shapes).indexOf(triggerObject);
                    triggerValue.setSelection(triggerObjectIndex, false);
                    break;
            }
        } else {
            i ++;
        }
        i ++;
        while (i < splitScript.size()) {
            View extra = layoutInflater.inflate(R.layout.extra_action, null);
            linearLayout.addView(extra);
            Spinner actionKey = (Spinner) extra.findViewById(R.id.action_key);
            final Spinner actionValue = (Spinner) extra.findViewById(R.id.action_value);
            actionKey.setAdapter(actionsAdapter);
            int actionIndex = Arrays.asList(actions).indexOf(splitScript.get(i));
            if (actionIndex != -1) {
                actionKey.setSelection(actionIndex, false);
                String actionObject = splitScript.get(++ i);
                int actionObjectIndex;
                switch (actionIndex) {
                    case 0:
                        actionValue.setAdapter(pagesAdapter);
                        actionObjectIndex = Arrays.asList(pages).indexOf(actionObject);
                        if (actionObjectIndex != -1) actionValue.setSelection(actionObjectIndex, false);
                        break;
                    case 1:
                        actionValue.setAdapter(soundsAdapter);
                        actionObjectIndex = Arrays.asList(sounds).indexOf(actionObject);
                        if (actionObjectIndex != -1) actionValue.setSelection(actionObjectIndex, false);
                        break;
                    case 2:
                    case 3:
                        actionValue.setAdapter(shapesAdapter);
                        actionObjectIndex = Arrays.asList(shapes).indexOf(actionObject);
                        if (actionObjectIndex != -1) actionValue.setSelection(actionObjectIndex, false);
                        break;
                }
            } else {
                i ++;
            }
            i ++;
        }

        triggerKey.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                       int position, long id) {
                switch (position) {
                    case 0:
                    case 1:
                        triggerValue.setAdapter(emptyAdapter);
                        break;
                    case 2:
                        triggerValue.setAdapter(shapesAdapter);
                        if (shapesAdapter.isEmpty()) {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "No shape found in this game", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                triggerValue.setAdapter(emptyAdapter);
            }
        });

        int count = linearLayout.getChildCount();
        for (int index = 0; index < count; index ++) {
            View child = linearLayout.getChildAt(index);
            Spinner actionKey = (Spinner) child.findViewById(R.id.action_key);
            final Spinner actionValue = (Spinner) child.findViewById(R.id.action_value);
            actionKey.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                           int position, long id) {
                    switch (position) {
                        case 0:
                            actionValue.setAdapter(pagesAdapter);
                            if (pagesAdapter.isEmpty()) {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "No page found in this game", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            break;
                        case 1:
                            actionValue.setAdapter(soundsAdapter);
                            if (soundsAdapter.isEmpty()) {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "No sound found in this game", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            break;
                        case 2:
                        case 3:
                            actionValue.setAdapter(shapesAdapter);
                            if (shapesAdapter.isEmpty()) {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "No shape found in this game", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    actionValue.setAdapter(emptyAdapter);
                }
            });
        }
    }

//    private void readScript(List<String> splitScript) {
//        if (splitScript == null) return;
//
//        final String[] pages = this.pages;
//        final String[] shapes = this.shapes;
//        final String[] sounds = this.sounds;
//
//        Spinner triggerKey = (Spinner) findViewById(R.id.trigger_key);
//        Spinner triggerValue = (Spinner) findViewById(R.id.trigger_value);
//
//        int i = 0;
//        int triggerIndex = Arrays.asList(triggers).indexOf(splitScript.get(i));
//        if (triggerIndex != -1) {
//            triggerKey.setSelection(triggerIndex, false);
//            String triggerObject = splitScript.get(++ i);
//            int triggerObjectIndex;
//            switch (triggerIndex) {
//                case 0:
//                case 1:
//                    triggerValue.setSelection(0);
//                    break;
//                case 2:
//                    triggerValue.setAdapter(shapesAdapter);
//                    triggerObjectIndex = Arrays.asList(shapes).indexOf(triggerObject);
//                    triggerValue.setSelection(triggerObjectIndex, false);
//                    break;
//            }
//        } else {
//            i ++;
//        }
//        i ++;
//        while (i < splitScript.size()) {
//            onAddAction(null);
//            View child = linearLayout.getChildAt(linearLayout.getChildCount() - 1);
//            Spinner actionKey = (Spinner) child.findViewById(R.id.action_key);
//            final Spinner actionValue = (Spinner) child.findViewById(R.id.action_value);
//            int actionIndex = Arrays.asList(actions).indexOf(splitScript.get(i));
//            if (actionIndex != -1) {
//                actionKey.setSelection(actionIndex, false);
//                String actionObject = splitScript.get(++ i);
//                int actionObjectIndex;
//                switch (actionIndex) {
//                    case 0:
//                        actionObjectIndex = Arrays.asList(pages).indexOf(actionObject);
//                        if (actionObjectIndex != -1) actionValue.setSelection(actionObjectIndex, false);
//                        break;
//                    case 1:
//                        actionObjectIndex = Arrays.asList(sounds).indexOf(actionObject);
//                        if (actionObjectIndex != -1) actionValue.setSelection(actionObjectIndex, false);
//                        break;
//                    case 2:
//                    case 3:
//                        actionObjectIndex = Arrays.asList(shapes).indexOf(actionObject);
//                        if (actionObjectIndex != -1) actionValue.setSelection(actionObjectIndex, false);
//                        break;
//                }
//            } else {
//                i ++;
//            }
//            i ++;
//        }
//    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
