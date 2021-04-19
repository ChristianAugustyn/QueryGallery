package ca.yorku.eecs.mack.democamera71179;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class ResultsPage extends Activity implements View.OnClickListener {

        ArrayList<String> results;
        ListView list;
        Button goHome;
        ArrayAdapter<String> arrayAdapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);
        Bundle b = getIntent().getExtras();
        results = b.getStringArrayList("testscores");

        list = (ListView) findViewById(R.id.list);
        goHome = (Button) findViewById(R.id.go_home);



        arrayAdapter = new ArrayAdapter<String>(ResultsPage.this, android.R.layout.simple_list_item_1, results);
        list.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();

        goHome.setOnClickListener(this);

    }



    @Override
    public void onClick(View view) {
        Intent i = new Intent(getApplicationContext(), QueryGallery.class);
        startActivity(i);

    }
}
