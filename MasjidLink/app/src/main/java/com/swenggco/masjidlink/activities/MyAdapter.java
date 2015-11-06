package com.swenggco.masjidlink.activities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.swenggco.masjidlink.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;

public class MyAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater inflater;
    private List<HashMap<String, String>> formList2 = null;
    private ArrayList<HashMap<String, String>> formList;
    private Filter filter;


    public MyAdapter(Context context, List<HashMap<String, String>> formList2) {
        mContext = context;
        this.formList2 = formList2;
        inflater = LayoutInflater.from(mContext);
        this.formList = new ArrayList<HashMap<String, String>>();
        this.formList.addAll(formList2);
    }


    public class ViewHolder {
        TextView text;

    }


    @Override
    public int getCount() {
        return formList2.size();
    }

    @Override
    public Object getItem(int position) {
        return formList2.get(position);

    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.listitems, null);
            holder.text = (TextView) view.findViewById(R.id.country);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        /***************
         * CLICK LISTNER TO GEETIGN THE DATA FROM KEY AND VALUE
         */

holder.text.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent myIntent = new Intent(mContext, MainActivity.class);
        myIntent.putExtra("name", formList2.get(position).get("name"));
        myIntent.putExtra("dial_code", formList2.get(position).get("dial_code"));

        mContext.startActivity(myIntent);
    }
});
        holder.text.setText(formList2.get(position).get("key"));




        return view;
    }







    public void filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        formList2.clear();
        if (charText.length() == 0) {
            formList2.addAll(formList);
        } else {
            for (HashMap<String, String> wp : formList) {
                if (wp.get("key").toLowerCase(Locale.getDefault()).contains(charText)) {

                    formList2.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}












