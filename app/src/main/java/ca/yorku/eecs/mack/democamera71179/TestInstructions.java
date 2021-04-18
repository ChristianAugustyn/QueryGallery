package ca.yorku.eecs.mack.democamera71179;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TestInstructions extends Activity implements View.OnClickListener {


    private Bundle b;
    Button startTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testinstructions);
        b = getIntent().getExtras();
        startTest = (Button) findViewById(R.id.start_test);
        startTest.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        Intent i = new Intent(getApplicationContext(), FindThisImage.class);
        i.putExtras(b);
        startActivity(i);
    }
}
