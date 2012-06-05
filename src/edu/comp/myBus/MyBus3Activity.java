package edu.comp.myBus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MyBus3Activity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //main page
        setContentView(R.layout.main);
 
        final Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	onSearchRequested();
            }
        });
        
        final Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
                Intent i = new Intent(MyBus3Activity.this,Favorites.class);
                startActivity(i);
            }
        });
        
        final Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
                Intent i = new Intent(MyBus3Activity.this,About.class);
                startActivity(i);
            }
        });
    }
}