package edu.stanford.cs108.bunnyworld;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class AddShape extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shape);

        game = SingletonData.getInstance().game;

        TextView nameText = (TextView) findViewById(R.id.shape_name_text);
        TextView textText = (TextView) findViewById(R.id.text_text);
        TextView xText = (TextView) findViewById(R.id.x_text);
        TextView yText = (TextView) findViewById(R.id.y_text);
        TextView wText = (TextView) findViewById(R.id.w_text);
        TextView hText = (TextView) findViewById(R.id.h_text);
        Spinner imageSpinner = (Spinner) findViewById(R.id.image_spinner);
        Switch visibleSwitch = (Switch) findViewById(R.id.visible_switch);
        Switch movableSwitch = (Switch) findViewById(R.id.movable_switch);

        if (tempShape == null) {
            if (game.getSelectedShape() == null) tempShape = new Shape(game);
            else tempShape = game.getSelectedShape();
        }

        nameText.setText(tempShape.getName());
        nameText.addTextChangedListener(new NameListener());

        textText.setText(tempShape.getText());
        textText.addTextChangedListener(new TextListener());

        xText.setText(String.valueOf(tempShape.getX()));
        xText.addTextChangedListener(new XListener());

        yText.setText(String.valueOf(tempShape.getY()));
        yText.addTextChangedListener(new YListener());

        wText.setText(String.valueOf(tempShape.getWidth()));
        wText.addTextChangedListener(new WListener());

        hText.setText(String.valueOf(tempShape.getHeight()));
        hText.addTextChangedListener(new HListener());

        images = SingletonData.getInstance().images.keySet().toArray(new String[0]);
        imageSpinner.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, images));
        imageSpinner.setSelection(Arrays.asList(images).indexOf(tempShape.getImage()));
        imageSpinner.setOnItemSelectedListener(new ImageListener());

        scriptsSpinner = (Spinner) findViewById(R.id.scripts_spinner);
        scriptsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, tempShape.getScripts());
        scriptsAdapter.notifyDataSetChanged();
        scriptsSpinner.setAdapter(scriptsAdapter);
        if (index != -1) scriptsSpinner.setSelection(index);

        scriptsSpinner.setOnItemSelectedListener(new ScriptListener());

        visibleSwitch.setChecked(tempShape.getVisible());
        visibleSwitch.setOnCheckedChangeListener(new VisibleListener());

        movableSwitch.setChecked(tempShape.getMovable());
        movableSwitch.setOnCheckedChangeListener(new MovableListener());
    }

    private Game game;
    private String[] images;

    private static Shape tempShape;
    private static int index;
    private static Spinner scriptsSpinner;
    private static ArrayAdapter scriptsAdapter;
    public static final String SCRIPT_INDEX_EXTRA = "SCRIPT_INDEX";

    public static Shape getTempShape() {
        return tempShape;
    }

    public static int getIndex() {
        return index;
    }

    public static void setIndex(int index) {
        AddShape.index = index;
    }

    public static Spinner getScriptsSpinner() {
        return scriptsSpinner;
    }

    public static ArrayAdapter getScriptsAdapter() {
        return scriptsAdapter;
    }

    class ImageListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            tempShape.setImage(images[position]);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class ScriptListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            index = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class NameListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            tempShape.setName(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    class TextListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            tempShape.setText(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    class XListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().equals("")) tempShape.setX(0.0f);
            else tempShape.setX(Float.parseFloat(s.toString()));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    class YListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().equals("")) tempShape.setY(0.0f);
            else tempShape.setY(Float.parseFloat(s.toString()));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    class WListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().equals("")) tempShape.setWidth(0.0f);
            else tempShape.setWidth(Float.parseFloat(s.toString()));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    class HListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().equals("")) tempShape.setHeight(0.0f);
            else tempShape.setHeight(Float.parseFloat(s.toString()));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    class VisibleListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            tempShape.setVisible(isChecked);
        }
    }

    class MovableListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            tempShape.setMovable(isChecked);
        }
    }

    public void onAdd(View view) {
        Intent intent = new Intent(this, AddScript.class);
        intent.putExtra(SCRIPT_INDEX_EXTRA, -1);
        startActivity(intent);
    }

    public void onEdit(View view) {
        if (tempShape.getScripts().isEmpty()) return;
        if (index == -1) return;
        Intent intent = new Intent(this, AddScript.class);
        intent.putExtra(SCRIPT_INDEX_EXTRA, index);
        startActivity(intent);
    }

    public void onDelete(View view) {
        if (tempShape.getScripts().isEmpty()) return;
        if (index == -1) return;
        tempShape.getScripts().remove(index);
        tempShape.getSplitScripts().remove(index);
        scriptsAdapter.notifyDataSetChanged();
    }

    public void onRichText(View view) {
        AlertDialog.Builder richTextBuilder = new AlertDialog.Builder(this);
        richTextBuilder.setTitle("Set Rich Text Below");
        LayoutInflater inflater = LayoutInflater.from(this);

        final View richTextView = inflater.inflate(R.layout.rich_text, null);
        richTextBuilder.setView(richTextView);

        final Shape shape = tempShape;

        final TextView textText = findViewById(R.id.text_text);
        final TextView contentText = richTextView.findViewById(R.id.text_content);
        contentText.setText(textText.getText());

        final Switch boldSwitch = richTextView.findViewById(R.id.bold_switch);
        boldSwitch.setChecked(shape.getRichText().getBold());

        final Switch italicSwitch = richTextView.findViewById(R.id.italic_switch);
        italicSwitch.setChecked(shape.getRichText().getItalic());

        String[] sizes = Shape.SIZE_MAP.keySet().toArray(new String[0]);
        final Spinner textSizeSpinner = richTextView.findViewById(R.id.text_size_spinner);
        int sizeSelection = Arrays.asList(sizes).indexOf(shape.getRichText().getSize());
        textSizeSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, sizes));
        textSizeSpinner.setSelection(sizeSelection);

        String[] colors = Shape.COLOR_MAP.keySet().toArray(new String[0]);
        final Spinner textColorSpinner = richTextView.findViewById(R.id.text_color_spinner);
        int colorSelection = Arrays.asList(colors).indexOf(shape.getRichText().getColor());
        textColorSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, colors));
        textColorSpinner.setSelection(colorSelection);

        richTextBuilder.setNegativeButton("Cancel", null);
        richTextBuilder.setPositiveButton("Confirm",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                shape.setText(contentText.getText().toString());
                textText.setText(contentText.getText());
                shape.setRichText(shape.new RichText(
                        boldSwitch.isChecked(),
                        italicSwitch.isChecked(),
                        textSizeSpinner.getSelectedItem().toString(),
                        textColorSpinner.getSelectedItem().toString()
                ));
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = richTextBuilder.create();
        alertDialog.show();
    }

    public void onConfirm(View view) {
        if (tempShape == null) {
            finish();
            return;
        }
        if (tempShape.getName().equals("")) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Name cannot be blank", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (!tempShape.isDistinct()) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Name already exists", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        tempShape.setPage(game.getCurrentPage());
        if (!game.getShapes().contains(tempShape)) {
            game.getShapes().add(tempShape);
            game.getCurrentPage().getShapes().add(tempShape);
        }
        game.setSelectedShape(tempShape);
        tempShape = null;
        index = -1;
        scriptsSpinner = null;
        scriptsAdapter = null;
        CreateGameActivity.getGameView().invalidate();
        finish();
    }

    @Override
    public void onBackPressed() {
        if (!game.getShapes().contains(tempShape)) Shape.setCount(Shape.getCount() - 1);
        tempShape = null;
        index = -1;
        scriptsSpinner = null;
        scriptsAdapter = null;
        CreateGameActivity.getGameView().invalidate();
        finish();
    }

}
