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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;


public class GetGifts extends Activity {

    ListView giftList;
    Button newGift;
    List<String> web = new ArrayList<>();
    List<Integer> imageId = new ArrayList<>();
   HashMap<String,String> map = new HashMap<String,String>();
    HashMap<String,String> users = new HashMap<String, String>();
    String res;
    String gift_id;
    String p_name;
    EditText giftIdEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_gifts);
        giftList = (ListView) findViewById(R.id.giftList);
        newGift = (Button) findViewById(R.id.newGift);

        Bundle bd = getIntent().getExtras();
         res = bd.getString("response");
        JSONArray response = getJSONArray(res);

       // JSONParser parser = new JSONParser();
       try {
          // org.json.JSONArray jary = new org.json.JSONArray(resp);
           for(int i = 0; i<response.length();i++)
           {
               JSONObject job = (JSONObject)response.get(i);
               JSONObject jobs = job.getJSONObject("sender");
               JSONObject jobr = job.getJSONObject("receiver");
               JSONObject joba = job.getJSONObject("amount");
               String id = job.get("id").toString();
               Boolean b = jobs.getBoolean("is_me");
               if(b == true)
               {


                   String rec = jobr.getString("full_name");
                   StringBuffer sb = new StringBuffer(rec);
                   String val = joba.getString("value");
                   String cur = joba.getString("currency");

                   map.put(Integer.toString(i),id);
                   web.add(sb.toString());
                   imageId.add(R.drawable.sentgift);
                   users.put(Integer.toString(i),rec);
               }
               else
               {
                   Boolean r = jobr.getBoolean("is_me");
                   if(r == true)
                   {
                       String sen = jobs.getString("full_name");

                       StringBuffer sb = new StringBuffer(sen);

                       map.put(Integer.toString(i),id);
                      web.add(sb.toString());
                       imageId.add(R.drawable.recievedgift);
                       users.put(Integer.toString(i),sen);
                   }
               }

               Log.e("id",id);

           }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        final CustomList adapter = new CustomList(GetGifts.this, web, imageId);
        giftList.setAdapter(adapter);
        newGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivity();
            }
        });
        giftList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    gift_id = map.get(Integer.toString(position));
                    Integer img = imageId.get(position);
                 p_name = users.get(Integer.toString(position));
                    new viewGiftCall().execute();
            }
        });

    }

    public JSONArray getJSONArray(String response)
    {
        try
        {
            JSONArray jary = new JSONArray(response);
            return jary;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    public void launchActivity()
    {
        Intent myIntent = new Intent(this,ChooseContact.class);
        startActivity(myIntent);
    }

    public void launchGift(String img,String name,String value, String currency,Boolean sender,String msg)
    {
        Intent intent = new Intent(this,ViewGift.class);
        intent.putExtra("value",value);
        intent.putExtra("currency",currency);
        intent.putExtra("sender",sender);
        intent.putExtra("img_id",img);
        intent.putExtra("response",res);
        intent.putExtra("name",name);
        intent.putExtra("message",msg);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_get_gifts, menu);
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
    private class viewGiftCall extends AsyncTask<Void, Void, String>
    {
        public String doInBackground(Void... params)
        {

            JSONObject response = ConnectionUtils.viewGift(gift_id);
            if(response!=null)
            {
                return response.toString();
            }
            else {
                return null;
            }
        }

        public void onPostExecute(String result)
        {
            if(result!=null) {
                try
                {
                    JSONObject job = new JSONObject(result);
                    JSONObject jobs = job.getJSONObject("sender");
                    JSONObject jobr = job.getJSONObject("receiver");
                    JSONObject joba = job.getJSONObject("amount");
                    JSONArray com = job.getJSONArray("comments");
                    JSONObject como = com.getJSONObject(0);
                    JSONObject media = como.getJSONObject("media");
                    JSONArray images = media.getJSONArray("images");
                    JSONObject imageObj = (JSONObject) images.getJSONObject(0);
                    JSONObject thumb = imageObj.getJSONObject("thumbnail");
                    String imgString = thumb.getString("image");
                    Log.e("Image",imgString);
                    String c = "/9j/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAAUCACgAKAEASIAAhEBAxEBBCIA/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADgQBAAIRAxEEAAA/APn+iiigD5/ooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooooooooAKKKKKKKKACiiiiiiigAooor/2Q==";
                    if(c.equals(imgString))
                    {
                        Log.e("Equal","Equal");

                    }
                    else
                    {
                        Log.e("Not equal","Not equal");
                    }
                    //Bitmap bitmap = StringToBitMap(imgString);
                    //giftView.setImageBitmap(bitmap);

                    String messa = como.getString("message");
                    String value = joba.getString("value");
                    String cur = joba.getString("currency");
                    String sender = jobs.getString("full_name");
                    String receipient = jobr.getString("full_name");

                    Boolean b = jobs.getBoolean("is_me");
                    launchGift(imgString,p_name,value,cur,b,messa);




                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                Log.d("Response:", "failed");
                Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();


            }

        }
    }


}
