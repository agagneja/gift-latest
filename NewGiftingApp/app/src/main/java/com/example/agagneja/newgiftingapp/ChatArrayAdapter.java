package com.example.agagneja.newgiftingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

    private TextView chatText;
    private List<ChatMessage> chats = new ArrayList<ChatMessage>();
    private LinearLayout wrapper;

    @Override
    public void add(ChatMessage object) {
        chats.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public int getCount() {
        return this.chats.size();
    }

    public ChatMessage getItem(int index) {
        return this.chats.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.single_message, parent, false);
        }

        wrapper = (LinearLayout) row.findViewById(R.id.wrapper);

        ChatMessage coment = getItem(position);

        chatText = (TextView) row.findViewById(R.id.singleMessage);

        chatText.setText(coment.message);

        chatText.setBackgroundResource(coment.left ? R.drawable.bubble_b : R.drawable.bubble_a);
        wrapper.setGravity(coment.left ? Gravity.RIGHT : Gravity.LEFT);

        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

}