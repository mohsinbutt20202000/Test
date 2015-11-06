package com.swenggco.masjidlink.activities;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.swenggco.masjidlink.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.os.AsyncTask;


import android.text.Editable;
import android.text.TextWatcher;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.SimpleAdapter;





public class Profile extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    EditText edit1;
    EditText edit2;
    AutoCompleteTextView edit3;
    TextView con1;
    GoogleMap mMap;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final double PAK_LAT = 32.5014840,
            PAK_LNG = 74.5285470;

    private GoogleApiClient mLocationClient;
    private Marker marker;
    String Country = "";
    double MyLat;
    double MyLan;


    DownloadTask placesDownloadTask;
    DownloadTask placeDetailsDownloadTask;
    ParserTask placesParserTask;
    ParserTask placeDetailsParserTask;


    final int PLACES = 0;
    final int PLACES_DETAILS = 1;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_profile);


        edit1 = (EditText)findViewById(R.id.editText3);
        edit2 = (EditText)findViewById(R.id.editText4);
        con1 = (TextView)findViewById(R.id.country);
        edit3 = (AutoCompleteTextView)findViewById(R.id.auto);
        edit3.setThreshold(2);
        edit3.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                return false;
            }

        });


        edit3.addTextChangedListener(new TextWatcher() {


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Creating a DownloadTask to download Google Places matching "s"


                if (placesDownloadTask != null) {
                    if (placesDownloadTask.getStatus() == AsyncTask.Status.PENDING ||
                            placesDownloadTask.getStatus() == AsyncTask.Status.RUNNING ||
                            placesDownloadTask.getStatus() == AsyncTask.Status.FINISHED) {
                        Log.i("--placesDownloadTask--", "progress_status : " + placesDownloadTask.getStatus());
                        placesDownloadTask.cancel(true);
                    }
                }

            }


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                String chterm;
                chterm=edit3.getText().toString();
                //Log.i("---final selected text---", ""+chterm);
                placesDownloadTask = new DownloadTask(PLACES);

                // Getting url to the Google Places Autocomplete api
                String url = getAutoCompleteUrl(s.toString());

                // Start downloading Google Places
                // This causes to execute doInBackground() of DownloadTask class
                placesDownloadTask.execute(url);


            }
        });




        // Setting an item click listener for the AutoCompleteTextView dropdown list
        edit3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index,
                                    long id) {

                ListView lv = (ListView) arg0;
                SimpleAdapter adapter = (SimpleAdapter) arg0.getAdapter();

                HashMap<String, String> hm = (HashMap<String, String>) adapter.getItem(index);

                // Creating a DownloadTask to download Places details of the selected place
                placeDetailsDownloadTask = new DownloadTask(PLACES_DETAILS);

                // Getting url to the Google Places details api
                String url = getPlaceDetailsUrl(hm.get("reference"));

                // Start downloading Google Place Details
                // This causes to execute doInBackground() of DownloadTask class
                placeDetailsDownloadTask.execute(url);

            }
        });

        //setUpXml();
        if (initMap()) {


            mLocationClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            mLocationClient.connect();


            //mMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(this, "Map not connected!", Toast.LENGTH_SHORT).show();
        }


        /***************
         * CENTER TEXT IN ACTION BAR*/


        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abz);
    }


    private String getAutoCompleteUrl(String place) {

        // Obtain browser key from https://code.google.com/apis/console
        String key = "key=AIzaSyCZq0c0pJb4M4jQ264aU893HEUAiq1jAug";

        // place to be be searched
        String input = "input=" + place;

        // place type to be searched
        String types = "types=geocode";

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = input + "&" + types + "&" + sensor + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;


        return url;
    }

    private String getPlaceDetailsUrl(String ref) {

        // Obtain browser key from https://code.google.com/apis/console
        String key = "key=AIzaSyCZq0c0pJb4M4jQ264aU893HEUAiq1jAug";

        // reference of place
        String reference = "reference=" + ref;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = reference + "&" + sensor + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/details/" + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception dow", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        private int downloadType = 0;

        // Constructor
        public DownloadTask(int type) {

            this.downloadType = type;
        }

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            switch (downloadType) {
                case PLACES:
                    // Creating ParserTask for parsing Google Places
                    placesParserTask = new ParserTask(PLACES);

                    // Start parsing google places json data
                    // This causes to execute doInBackground() of ParserTask class
                    placesParserTask.execute(result);

                    break;

                case PLACES_DETAILS:
                    // Creating ParserTask for parsing Google Places
                    placeDetailsParserTask = new ParserTask(PLACES_DETAILS);

                    // Starting Parsing the JSON string
                    // This causes to execute doInBackground() of ParserTask class
                    placeDetailsParserTask.execute(result);
            }
        }


    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        int parserType = 0;

        public ParserTask(int type) {
            this.parserType = type;
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<HashMap<String, String>> list = null;

            try {
                jObject = new JSONObject(jsonData[0]);

                switch (parserType) {
                    case PLACES:
                        PlaceJSONParser placeJsonParser = new PlaceJSONParser();
                        // Getting the parsed data as a List construct
                        list = placeJsonParser.parse(jObject);
                        break;
                    case PLACES_DETAILS:
                        PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();
                        // Getting the parsed data as a List construct
                        list = placeDetailsJsonParser.parse(jObject);
                }

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            switch (parserType) {
                case PLACES:
                    String[] from = new String[]{"description"};
                   int[] to = new int[]{android.R.id.text1};


                    // Creating a SimpleAdapter for the AutoCompleteTextView
                    SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);

                    Log.v("Adapter", adapter.toString());
                    // Setting the adapter
                    edit3.setAdapter(adapter);
                    break;
                case PLACES_DETAILS:
                    HashMap<String, String> hm = result.get(0);

                    // Getting latitude from the parsed data
                    double latitude = Double.parseDouble(hm.get("lat"));

                    // Getting longitude from the parsed data
                    double longitude = Double.parseDouble(hm.get("lng"));

                    // Getting reference to the SupportMapFragment of the activity_main.xml
                    SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

                    // Getting GoogleMap from SupportMapFragment
                    mMap = fm.getMap();

                    LatLng point = new LatLng(latitude, longitude);

                    CameraUpdate cameraPosition = CameraUpdateFactory.newLatLng(point);
                    CameraUpdate cameraZoom = CameraUpdateFactory.zoomBy(5);

                    // Showing the user input location in the Google Map
                   mMap.moveCamera(cameraPosition);
                    mMap.animateCamera(cameraZoom);
                    if(marker!=null)
                    {
                        marker.remove();
                    }


                    MarkerOptions options = new MarkerOptions();
                    options.position(point);
                    options.title("Position");
                    options.snippet("Latitude:" + latitude + ",Longitude:" + longitude);

                    // Adding the marker in the Google Map
                 marker=mMap.addMarker(options);

                    break;

            }
        }
    }


    public void showlocation(MenuItem item) {

        location();
        //address();

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.saver2) {


            //return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public boolean servicesOK() {

        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog =
                    GooglePlayServicesUtil.getErrorDialog(isAvailable, this, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Can't connect to mapping service", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    private boolean initMap() {
        if (mMap == null) {
            SupportMapFragment mapFragment =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mMap = mapFragment.getMap();
        }
        return (mMap != null);
    }


    private void hideSoftKeyboard(View v) {
        InputMethodManager imm =
                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    public void location() {

        Location currentLocation = LocationServices.FusedLocationApi
                .getLastLocation(mLocationClient);

        if (currentLocation == null) {
            Toast.makeText(this, "Couldn't connect!", Toast.LENGTH_SHORT).show();
        } else {
            LatLng latLng = new LatLng(
                    MyLat = currentLocation.getLatitude(),
                    MyLan = currentLocation.getLongitude()

            );
            Log.v("MyLocation", String.valueOf(MyLat));
            // Log.v("MyLocation",String.valueOf(MyLa));
            address(MyLat, MyLan);

            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
                    latLng, 15
            );


            mMap.animateCamera(update);

            if(marker!=null)
            {
                marker.remove();
            }

            MarkerOptions options = new MarkerOptions()
                    .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));


            marker = mMap.addMarker(options);
        }
    }

    public void address(double lan, double lang) {


        try {


            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(this, Locale.getDefault());
            addresses = geocoder.getFromLocation(lan, lang, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);

                Country = address.getCountryName();
                String city = address.getLocality();

                edit3.setText(address.getAddressLine(0) + "(" + Country + ")");

            }

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

}





