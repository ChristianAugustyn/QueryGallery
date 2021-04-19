package ca.yorku.eecs.mack.democamera71179;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import static ca.yorku.eecs.mack.democamera71179.ImageGridViewActivity.MYDEBUG;

public class ResultsPage extends Activity implements View.OnClickListener {

    ArrayList<String> results;
    ListView list;
    Button goHome;
    ArrayAdapter<String> arrayAdapter;
    private BufferedWriter sd1, sd2;
    private File f1, f2;
    private final static String DATA_DIRECTORY = "/QueryGalleryData/";
    private final static String SD2_HEADER = "Time";
    private String sd2Leader;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));
        Bundle b = getIntent().getExtras();
        results = b.getStringArrayList("testscores");

        list = (ListView) findViewById(R.id.list);
        goHome = (Button) findViewById(R.id.go_home);


        arrayAdapter = new ArrayAdapter<String>(ResultsPage.this, android.R.layout.simple_list_item_1, results);
        list.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();

        goHome.setOnClickListener(this);

        File dataDirectory = new File(Environment.getExternalStorageDirectory() +
                DATA_DIRECTORY);
        if (!dataDirectory.exists() && !dataDirectory.mkdirs()) {
            Log.e(MYDEBUG, "ERROR --> FAILED TO CREATE DIRECTORY: " + DATA_DIRECTORY);
            super.onDestroy(); // cleanup
            this.finish(); // terminate
        }


        int blockNumber = 0;
        do {
            ++blockNumber;
            String blockCode = String.format(Locale.CANADA, "B%02d", blockNumber);
            String baseFilename = String.format("%s", "Results");
            f1 = new File(dataDirectory, baseFilename + "sd1" + blockNumber + ".txt");
            f2 = new File(dataDirectory, baseFilename + "sd2" + blockNumber + ".txt");

            // also make a comma-delimited leader that will begin each data line written to the sd2 file
            sd2Leader = String.format("%s-%s", "Times", blockNumber);
        } while (f1.exists() || f2.exists());

        try {
            sd1 = new BufferedWriter(new FileWriter(f1));
            sd2 = new BufferedWriter(new FileWriter(f2));

            // output header in sd2 file
            sd2.write(SD2_HEADER, 0, SD2_HEADER.length());
            sd2.flush();

        } catch (IOException e) {
            Log.e(MYDEBUG, "ERROR OPENING DATA FILES! e=" + e.toString());
            super.onDestroy();
            this.finish();

        }
        StringBuilder sd2Data = new StringBuilder(100);
        StringBuilder sd1Stuff = new StringBuilder(100);
        sd1Stuff.append("SD1STUFF");
        for (int i = 0; i < results.size(); i++) {
            sd2Data.append(String.format("%s\n", results.get(i)));
        }

        // write to data files
        try {
            sd1.write(sd1Stuff.toString(), 0, sd1Stuff.length());
            sd1.flush();
            sd2.write(sd2Data.toString(), 0, sd2Data.length());
            sd2.flush();
        } catch (IOException e) {
            Log.e("MYDEBUG", "ERROR WRITING TO DATA FILE!\n" + e);
            super.onDestroy();
            this.finish();
        }
    }


    @Override
    public void onClick(View view) {
        Intent i = new Intent(getApplicationContext(), QueryGallery.class);
        startActivity(i);

    }


}
