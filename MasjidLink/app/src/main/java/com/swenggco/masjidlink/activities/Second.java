package com.swenggco.masjidlink.activities;

import android.app.ActionBar;
import android.app.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.swenggco.masjidlink.R;
import android.R.*;

/**
 * Created by Danish on 31/08/15.
 */
public class Second extends AppCompatActivity {
    TextView tx1;
    TextView tx2;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        tx1 = (TextView) findViewById(R.id.textView2);
        tx2 = (TextView) findViewById(R.id.textView3);
        //Intent intent = getIntent();
        //String fName = intent.getStringExtra("country");
        //String lName = intent.getStringExtra("number");
        //tx2.setText("Verfication Code: " + lName );


        /***************
         * CENTER TEXT IN ACTION BAR
         */

        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abx);


    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_second, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.submiter) {


            //return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void profilesave(MenuItem item) {
        Intent intent = new Intent(Second.this, Profile.class);
        startActivity(intent);

    }
}
