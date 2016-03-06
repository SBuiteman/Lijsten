package com.example.stijn.lists;
/**
 * Created by Stijn on 23/02/2016.
 */
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * This class contains an getView method which takes in the toDo list from MainActivity and
 * builds the ListView, same for the EditListActivity.
 */
public class AdapterActivity extends ArrayAdapter<String> {
    public Context layoutMAContext;
    public MainActivity mainActivity;

    public AdapterActivity(Context context, ArrayList<String> toDos) {
        super(context, R.layout.single_row_layout, toDos);

        layoutMAContext = context;
    }

    /**
     * Handles the layout of the ListView for both MainActivity and EditListActivity.
     */
    public View getView(int position, View view, ViewGroup parent) {

        final String toDo = getItem(position);
        mainActivity = new MainActivity();

        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) layoutMAContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.single_row_layout, parent, false);
        }

        TextView textview = (TextView) view.findViewById(R.id.toDOTextView);

        textview.setText(toDo);

        return view;
    }
}