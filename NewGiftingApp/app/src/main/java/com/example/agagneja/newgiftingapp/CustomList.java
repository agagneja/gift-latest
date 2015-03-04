package com.example.agagneja.newgiftingapp;

/**
 * Created by agagneja on 3/1/2015.
 */
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomList extends ArrayAdapter<String>{
    private final Activity context;
    private final List<String> web;
    private final List<Integer> imageId;
    public CustomList(Activity context,
                      List<String> web, List<Integer> imageId) {
        super(context, R.layout.single_gift, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.single_gift, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txtGift);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imgGift);
        txtTitle.setText(web.get(position));
        imageView.setImageResource(imageId.get(position));
        return rowView;
    }
}