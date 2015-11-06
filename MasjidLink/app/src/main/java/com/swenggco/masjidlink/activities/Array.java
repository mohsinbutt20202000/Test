package com.swenggco.masjidlink.activities;

import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.swenggco.masjidlink.activities.MyAdapter;

import com.swenggco.masjidlink.R;
import com.swenggco.masjidlink.activities.MyAdapter;




public class Array extends ActionBarActivity {

    ArrayList<HashMap<String, String>> formList;
    HashMap<String, String> m_li;
    //SimpleAdapter adapter;
    MyAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);


        final ListView categoriesL = (ListView) findViewById(R.id.list_view);
        final TextView send = (TextView) findViewById(R.id.country);


/**************
 * JSON ARRAY TO READ DATA LIKE KEY AND VALUE
 */

        try {

            JSONObject obj = new JSONObject(loadJSONFromAsset());
            final JSONArray m_jArry = obj.getJSONArray("country");
            formList = new ArrayList<HashMap<String, String>>();


            Log.v("My data", m_jArry.toString());

            for (int i = 0; i < m_jArry.length(); i++) {
                final JSONObject jo_inside = m_jArry.getJSONObject(i);

                Log.d("Details-->", jo_inside.getString("name"));

                final String formula_value = jo_inside.getString("name");
                final String formu = jo_inside.getString("dial_code");
                final String result = formula_value + "  " + "(" + formu + ")";


                //Add your values in your `ArrayList` as below:

                m_li = new HashMap<String, String>();
                m_li.put("key", result);


                m_li.put("name", formula_value);
                m_li.put("dial_code", formu);


                formList.add(m_li);

                                                        /*adapter setting ******
                                                             *********/

                //adapter = new SimpleAdapter(this, formList, R.layout.listitems, new String[]{"key"}, new int[]{R.id.country});


        /*    categoriesL.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent myIntent = new Intent(Array.this, MainActivity.class);


                    try {


                        JSONObject jo_inside = m_jArry.getJSONObject(position);

                        Log.v("Country Name is ", jo_inside.getString("name"));


                        Context context = view.getContext();


                        //int s=view.getId();

                        //TextView textViewItem = ((TextView) view.findViewById(R.id.country));
                        //String listItemText = textViewItem.getText().toString();


                        myIntent.putExtra("name", jo_inside.getString("name"));
                        myIntent.putExtra("dial_code", jo_inside.getString("dial_code"));


                        startActivityForResult(myIntent, 0);


                    } catch (JSONException e) {
                        e.printStackTrace();

                    }


                }


            });*/


                /********
                 * SETTIGN CUSTOM ADAPTER
                 */


                adapter = null;
                adapter = new MyAdapter(this, formList);
                // Binds the Adapter to the ListView


                categoriesL.setAdapter(adapter);
                categoriesL.invalidate();


            }

            final EditText input = (EditText) findViewById(R.id.inputSearch);


/*ENABLE SEARCH FILTER
**********
 */

            input.addTextChangedListener(new TextWatcher() {


                @Override
                public void onTextChanged(CharSequence constraints, int start, int before, int count) {
                    /*String searchString = input.getText().toString().toLowerCase(Locale.getDefault());
                    Log.v("searchitem", "" + searchString);
                    Array.this.adapter.notifyDataSetChanged();
                    Log.v("Filter","size "+formList.size());
                    */
                    if (adapter != null) {
                        Array.this.adapter.filter(constraints.toString());

                        String text = input.getText().toString();
                        adapter.filter(text);
                        //Log.v("searchitem", "" + text);
                        Log.v("Filter", "size " + formList.size());
                        adapter.notifyDataSetChanged();
                    }

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {


                }

                @Override
                public void afterTextChanged(Editable arg0) {


                }


            });


        } catch (JSONException e) {
            e.printStackTrace();

        }

        /*
        this is use for center title in action bar
        *******
        ******
        */

        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.aby);

                                  /*Read file from JSON*****
                                            *****
                                            ******
                                                */

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

    /*
    ACTIONBAR********
     */

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_third, menu);

        return super.onCreateOptionsMenu(menu);
    }

}
