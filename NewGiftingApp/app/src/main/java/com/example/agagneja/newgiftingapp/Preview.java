package com.example.agagneja.newgiftingapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


public class Preview extends Activity {
    ImageView imgView;
    String name;
    String message;
    String val;
    String cur;
    String photoPath;
    Bitmap bp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        imgView = (ImageView) findViewById(R.id.imgPreView);
        Bundle bd = getIntent().getExtras();
        if(bd!= null)
        {
            if(bd.getString("flag")=="1")
            {
                name = bd.getString("name");
                message= bd.getString("message");
                val = bd.getString("amount");
                cur = bd.getString("cur");
                photoPath = bd.getString("photo");
                imgView.setImageBitmap(BitmapFactory.decodeFile(photoPath));
            }

            if(bd.getString("flag")=="2")
            {
                name = bd.getString("name");
                message= bd.getString("message");
                val = bd.getString("amount");
                cur = bd.getString("cur");
                bp = (Bitmap) getIntent().getParcelableExtra("BitmapImage");
                imgView.setImageBitmap(bp);
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_preview, menu);
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
}
