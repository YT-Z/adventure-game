package edu.stanford.cs108.bunnyworld;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class CheckErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_error);

        ListView errorList = (ListView) findViewById(R.id.error_list);
        errorList.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1,
                SingletonData.getInstance().game.getErrorMessages()));
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
