package com.example.agagneja.newgiftingapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ViewGift extends Activity {
    String id;
    String imgId;
    Integer img_id;
    ImageView giftView;
    EditText sent;
    EditText rec;
    EditText msg;
    TextView val;
    Button back;
    String res;
    String name;
    EditText chatText;
    Button chatSend;
    ListView chatList;
    private ChatArrayAdapter chatArrayAdapter;
    String imgString;
    Boolean is_me_send;
    String msgDisp;
    String valDisp;
    String curDisp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_gift);
        Bundle bd = getIntent().getExtras();
        //id = bd.getString("id");
        imgId = bd.getString("img_id");
        res = bd.getString("response");
        name = bd.getString("name");
        curDisp = bd.getString("currency");
        msgDisp = bd.getString("message");
        valDisp = bd.getString("value");
        is_me_send = bd.getBoolean("sender");

        getActionBar().setTitle(name);
        getActionBar().setIcon(R.drawable.gift);



        //img_id = Integer.parseInt(imgId);
        giftView = (ImageView) findViewById(R.id.imgSelGift);

        val = (TextView) findViewById(R.id.giftSelAmount);
        back = (Button) findViewById(R.id.giftSelBack);
        chatText = (EditText) findViewById(R.id.giftSelChatText);
        chatList = (ListView) findViewById(R.id.giftSelChat);
        chatSend = (Button) findViewById(R.id.giftSelChatSend);
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(),R.layout.single_message);
        chatList.setAdapter(chatArrayAdapter);



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivity();
            }
        });
        chatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = chatText.getText().toString();
                chatArrayAdapter.add(new ChatMessage(true,str));
                chatText.setText("");
            }
        });

        Bitmap bitmap = StringToBitMap(imgId);
        giftView.setImageBitmap(bitmap);
        if(is_me_send == true)
        {
            chatArrayAdapter.add(new ChatMessage(false,msgDisp));
        }
        else {
            chatArrayAdapter.add(new ChatMessage(true,msgDisp));
        }
        StringBuffer sb = new StringBuffer(valDisp);
        sb.append(" ");
        sb.append(curDisp);
        val.setText(sb.toString());



    }

    public void launchActivity()
    {
        Intent intent = new Intent(this,GetGifts.class);
        intent.putExtra("response",res);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_gift, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }

}
