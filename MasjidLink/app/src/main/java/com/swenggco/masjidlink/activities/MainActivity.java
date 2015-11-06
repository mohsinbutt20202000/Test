package com.swenggco.masjidlink.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Message;
import android.provider.SyncStateContract;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;



import com.swenggco.masjidlink.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button edit;
    EditText edit2;
    TextView text;
    Button bn;
    EditText editTextTwo;
    private String latitude;
    private String longitde;
    public LocationManager locationManager;
    private String Provider;
    boolean shouldExecuteOnResume;
    ArrayList<HashMap<String, String>> formList;
    HashMap<String, String> m_li;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        edit = (Button) findViewById(R.id.btn2);
        edit2 = (EditText) findViewById(R.id.editText2);
        bn = (Button) findViewById(R.id.button);
        text = (TextView) findViewById(R.id.textView);
        editTextTwo = (EditText) findViewById(R.id.editText);
        bn.setOnClickListener(this);
        editTextTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String CountryCode = "";
                try {

                    JSONObject obj = new JSONObject(loadJSONFromAsset());
                    formList = new ArrayList<HashMap<String, String>>();
                    final JSONArray m_jArry = obj.getJSONArray("country");


                    Log.v("My data", m_jArry.toString());

                    for (int i = 0; i < m_jArry.length(); i++) {
                        final JSONObject jo_inside = m_jArry.getJSONObject(i);

                        Log.d("Details-->", jo_inside.getString("name"));

                        final String formula_value = jo_inside.getString("name");
                        final String formu = jo_inside.getString("dial_code");
                        String text = editTextTwo.getText().toString();
                        if (text.equals(formu)) {
                            CountryCode = "" + formula_value;
                            edit.setText(CountryCode);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }


            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        Intent myintent = getIntent();
        String receivedContentOne = myintent.getStringExtra("name");
        String receivedContentTwo = myintent.getStringExtra("dial_code");


/// Set EditText text to the values passed from the intent.


        edit.setText(receivedContentOne);


        editTextTwo.setText(receivedContentTwo);


        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs);


        //  android.support.v7.app.ActionBar bar = getSupportActionBar();
        //  bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#009688")));

        /*
        Check GPS Enable OR NOT (Function CAll)
         */
        /*if (!enableManager()) {
            // Enable GPS (pop up box
            openDialog();

        } else {
            String svcName = Context.LOCATION_SERVICE;
            locationManager = (LocationManager) getSystemService(svcName);
            String provider = LocationManager.NETWORK_PROVIDER;
            Location location = locationManager.getLastKnownLocation(provider);


            if (location != null) {
                System.out.println("Provider " + Provider + " has been selected.");
                onLocationChanged(location);
            } else {
                Toast.makeText(getApplicationContext(), "Sorry Location Not Found", Toast.LENGTH_LONG).show();
            }
        }

    }
    */



    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.nexter) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void SetCustomTitle(String title) {
        TextView textViewTitle = (TextView) findViewById(R.id.mytext);
        textViewTitle.setText(title);
    }


    public void onClick(View v) {
        turnGPSOn(); // method to turn on the GPS if its in off state.
        getMyCurrentLocation();
        String text = getcode();
        editTextTwo.setText(text);


    }

    public String getcode() {

        String CountryZipCode = "";
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            addresses = geocoder.getFromLocation(MyLat, MyLong, 1);


            //CountryCode = addresses.get(0).getCountryCode();
            CountryName = addresses.get(0).getCountryName();
            edit.setText(CountryName);


            JSONObject obj = new JSONObject(loadJSONFromAsset());
            formList = new ArrayList<HashMap<String, String>>();
            final JSONArray m_jArry = obj.getJSONArray("country");


            Log.v("My data", m_jArry.toString());

            for (int i = 0; i < m_jArry.length(); i++) {
                final JSONObject jo_inside = m_jArry.getJSONObject(i);

                Log.d("Details-->", jo_inside.getString("name"));

                final String formula_value = jo_inside.getString("name");
                final String formu = jo_inside.getString("dial_code");
                if (formula_value.equals(CountryName)) {
                    CountryZipCode = ""+ formu;
                    break;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return CountryZipCode;
    }



    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("countries.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

/*through Telephone manager getting country code
************************
 */
    /* public String getcode() {
        String CountryID = "";
        String CountryZipCode = "";

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = "+"+g[0];
                break;
            }
        }
        return CountryZipCode;
    }
    */

                                                              /*Method to turn on GPS **/

    public void turnGPSOn() {
        try {

            String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

            if (!provider.contains("gps")) { //if gps is disabled
                final Intent poke = new Intent();
                poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                poke.setData(Uri.parse("3"));
                sendBroadcast(poke);
            }
        } catch (Exception e) {

        }
    }


                                 /* Check the type of GPS Provider available at that instance and  collect the location informations**/

    void getMyCurrentLocation() {

        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locListener = new MyLocationListener();
        try {
            gps_enabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (gps_enabled) {
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);

        }
        if (gps_enabled) {
            location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        }

        if (network_enabled && location == null) {

            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);

        }
        if (network_enabled && location == null) {
            location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        }
        if (location != null) {

            MyLat = location.getLatitude();
            MyLong = location.getLongitude();
        } else {
            Location loc = getLastKnownLocation(this);
            if (loc != null) {
                MyLat = loc.getLatitude();
                MyLong = loc.getLongitude();
            }
        }

        locManager.removeUpdates(locListener);




                                                          /**Getting address from found locations.**/
        /* try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            addresses = geocoder.getFromLocation(MyLat, MyLong, 1);



                //CountryCode = addresses.get(0).getCountryCode();
                CountryName = addresses.get(0).getCountryName();
            //CountryCode=addresses.get(0).getCountryCode();





                edit.setText(CountryName);

                // Log.v("location", CountryCode);



        } catch (Exception e) {
            e.printStackTrace();
        }
        */

    }

                                                                      // Location listener class. to get location.

    public class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    private boolean gps_enabled = false;
    private boolean network_enabled = false;
    Location location;
    Double MyLat, MyLong;
    String CountryName = "";


// below method to get the last remembered location. because we don't get locations all the times .At some instances we are unable to get the location from GPS. so at that moment it will show us the last stored location.

    public static Location getLastKnownLocation(Context context) {
        Location location = null;
        LocationManager locationmanager = (LocationManager) context.getSystemService("location");
        List list = locationmanager.getAllProviders();
        boolean i = false;
        Iterator iterator = list.iterator();
        do {

            if (!iterator.hasNext())
                break;
            String s = (String) iterator.next();
            //if(i != 0 && !locationmanager.isProviderEnabled(s))
            if (i != false && !locationmanager.isProviderEnabled(s))
                continue;
            // System.out.println("provider ===> "+s);
            Location location1 = locationmanager.getLastKnownLocation(s);
            if (location1 == null)
                continue;
            if (location != null) {
                //System.out.println("location ===> "+location);
                //System.out.println("location1 ===> "+location);
                float f = location.getAccuracy();
                float f1 = location1.getAccuracy();
                if (f >= f1) {
                    long l = location1.getTime();
                    long l1 = location.getTime();
                    if (l - l1 <= 600000L)
                        continue;
                }
            }
            location = location1;
            // System.out.println("location  out ===> "+location);
            //System.out.println("location1 out===> "+location);
            i = locationmanager.isProviderEnabled(s);

        } while (true);
        return location;
    }


    /*############
    Open POP Up Box
     ############

    public void openDialog() {

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("Please open your location ")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //  open GPS Activity
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // this function call only onetime
        if (shouldExecuteOnResume) {
            // Check  latitude and longitude
            if ((latitude == null || latitude.equalsIgnoreCase("")) && (longitde == null || longitde.equalsIgnoreCase(""))) {
                String locationProvider = LocationManager.NETWORK_PROVIDER;
                locationManager.requestLocationUpdates(locationProvider, 2000, 1, this);
            }
        } else {
            shouldExecuteOnResume = true;

        }

    }*/

    public void nextscreen(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, Second.class);
        //intent.putExtra("country", edit.getText().toString());
        //intent.putExtra("number", edit2.getText().toString());
        startActivity(intent);


    }

    public void selectdata(View view) {
        Intent intent = new Intent(MainActivity.this, Array.class);
        //intent.putExtra("country", edit.getText().toString());
        //intent.putExtra("number", edit2.getText().toString());
        startActivity(intent);


    }

   /* class ServiceUpdate extends AsyncTask<Void, Void, Boolean> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            String svcName = Context.LOCATION_SERVICE;
            locationManager = (LocationManager) getSystemService(svcName);
            String provider = LocationManager.NETWORK_PROVIDER;

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), latitude, Toast.LENGTH_LONG).show();
        }
    }

      /*************
     * BACK PRESS ACTIVITY FINISH WITH DIALOUGE BOX
     */

    public void onBackPressed() {

        backButtonHandler();
        return;
    }

    public void backButtonHandler() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
               MainActivity.this);
        alertDialog.setTitle("Leave application?");
        alertDialog.setMessage("Are you sure you want to leave the application?");
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent setIntent = new Intent(Intent.ACTION_MAIN);
                       setIntent.addCategory(Intent.CATEGORY_HOME);
                        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(setIntent);
                        finish();
                    }
                });
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }



    }


