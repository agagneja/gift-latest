package com.example.agagneja.newgiftingapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.StaticLayout;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;


public class CreateGift extends Activity implements AdapterView.OnItemSelectedListener{

    Spinner spinner;
    EditText amount;
    String val;
    String cur;
    ImageButton camera_btn;
    ImageButton gallery_btn;
    Button preview;
    EditText msgText;
    String message;
    String picturePath;
    String cameraString;
    Bitmap bp;
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static int RESULT_LOAD_IMAGE = 1;
    private Uri fileUri;
    private static final int CAMERA_CAPTURE = 100;
    private static final int GALLERY_UPLOAD = 200;
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
    static int flag =0;
    String name;
    ImageView showImg;
    Button back;
    Button send;
    String FUNDING_ID;
    String FULFIL_ID;
    String FULFIL_BODY;
    TextView tView;
    TextView textViewA;
    TextView textViewM;
    private static String imgval;
    String contact_Id;
   Uri photoPath;
    String photo;
    SimpleCursorAdapter mAdapter;
    MatrixCursor mMatrixCursor;
    MatrixCursor reader;
    SharedPreferences prefs;
    List<NameValuePair> params;
    Button openGift;
    String res;
    String gift_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_gift);
        amount = (EditText) findViewById(R.id.amount);
        spinner = (Spinner) findViewById(R.id.spinner);
        camera_btn = (ImageButton) findViewById(R.id.camera_btn);
        gallery_btn = (ImageButton) findViewById(R.id.gallery_button);
        preview = (Button) findViewById(R.id.preview);
        msgText=(EditText) findViewById(R.id.msgText);
        showImg = (ImageView)findViewById(R.id.viewImg);
        back = (Button) findViewById(R.id.back_btn);
        send = (Button) findViewById(R.id.gift_send);
        textViewA = (TextView) findViewById(R.id.textViewA);
        textViewM = (TextView) findViewById(R.id.textViewM);
        openGift = (Button) findViewById(R.id.openGift);
        openGift.setEnabled(false);
        //prefs = getSharedPreferences("Chat", 0);
      //  tView = (TextView) findViewById(R.id.textView);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.currencies_array,android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        Bundle bd = getIntent().getExtras();
        if(bd!=null)
        {
            if(bd.getString("name")!= null)
            {
                name = bd.getString("name");
                //nameText.setText(name);
                Toast.makeText(this,("Selected user :"+name).toString(),Toast.LENGTH_SHORT).show();
                ActionBar actionBar = getActionBar();
                actionBar.setTitle(name);
                actionBar.setIcon(R.drawable.gift);

            }
        }

        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 2;
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                startActivityForResult(intent, CAMERA_CAPTURE);

            }
        });

        gallery_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 1;
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
               // startActivityForResult(i,GALLERY_UPLOAD);
            }
        });

        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                val = amount.getText().toString();
                message = msgText.getText().toString();
                amount.setVisibility(View.INVISIBLE);
                spinner.setVisibility(View.INVISIBLE);
                msgText.setVisibility(View.INVISIBLE);
                camera_btn.setVisibility(View.INVISIBLE);
                gallery_btn.setVisibility(View.INVISIBLE);
                preview.setVisibility(View.INVISIBLE);
                showImg.setVisibility(View.VISIBLE);
                back.setVisibility(View.VISIBLE);
                send.setVisibility(View.VISIBLE);

                back.setEnabled(true);
                send.setEnabled(true);
                camera_btn.setEnabled(false);
                gallery_btn.setEnabled(false);
                preview.setEnabled(false);
                textViewM.setVisibility(View.VISIBLE);
                textViewA.setVisibility(View.VISIBLE);
                StringBuffer sb = new StringBuffer("Amount :");
                sb.append(val);
                sb.append(" ");
                sb.append(cur);
                textViewA.setText(sb.toString());
                StringBuffer s = new StringBuffer("Message: ");
                s.append(message);
                textViewM.setText(s.toString());
              //  tView.setText("Here is what your gift looks like!!");

                if(flag == 1)
                {
                    showImg.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                    //Bitmap img = BitmapFactory.decodeFile(picturePath);
                    try {
                        InputStream inputStream = new FileInputStream(picturePath);
                        byte[] bytes;
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        ByteArrayOutputStream output = new ByteArrayOutputStream();
                        try {
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                output.write(buffer, 0, bytesRead);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        bytes = output.toByteArray();
                        String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
                        Log.e("Value1",encodedString);
                        imgval = encodedString;
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    //imgval= convertToString(img);
                    Log.e("Value1",imgval);
                }

                if(flag == 2)
                {
                    showImg.setImageBitmap(bp);
                    imgval= convertToString(bp);
                    Log.e("Value2",imgval);

                }

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                amount.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);
                msgText.setVisibility(View.VISIBLE);
                camera_btn.setVisibility(View.VISIBLE);
                gallery_btn.setVisibility(View.VISIBLE);
                preview.setVisibility(View.VISIBLE);
                showImg.setVisibility(View.INVISIBLE);
                back.setVisibility(View.INVISIBLE);
                send.setVisibility(View.INVISIBLE);
                back.setEnabled(false);
                send.setEnabled(false);
                camera_btn.setEnabled(true);
                gallery_btn.setEnabled(true);
                preview.setEnabled(true);
                msgText.setText("");
                amount.setText("");
                textViewA.setText("Amount: ");
                textViewM.setText("Message: ");
                textViewA.setVisibility(View.INVISIBLE);
                textViewM.setVisibility(View.INVISIBLE);
             //   tView.setText("Customize your gift here!!");

            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CreateGiftCall().execute();

            }
        });
        openGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    new getGifts().execute();
            }
        });

    }

    public String convertToString(Bitmap img)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();

        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

        }

        if (requestCode == CAMERA_CAPTURE) {

            if (resultCode == RESULT_OK) {
                super.onActivityResult(requestCode, resultCode, data);
                bp = (Bitmap) data.getExtras().get("data");

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }


    }
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        }  else {
            return null;
        }

        return mediaFile;
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_gift, menu);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(parent.getContext(),
                "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),
                Toast.LENGTH_SHORT).show();
        cur = parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    private class CreateGiftCall extends AsyncTask<Void, Void, String>
    {
        public String doInBackground(Void... params)
        {
           String body = getBodyCreateGift();
           //String body = "{ \"receiver\":{ \"id\":\"cdayanand-pre@paypal.com\", \"type\":\"EMAIL\" }, \"amount\":{ \"value\":\"5\", \"currency\":\"USD\" }, \"template\" : { \"id\":\"1\", \"background_color\":\"#ffeeff\" }, \"comments\":[ { \"message\":\"Here is your birthday gift!!\", \"location\":{ \"latitude\":\"37.779568\", \"longitude\":\"-122.41387\", \"place\":\"San Francisco, CA\" }, \"media\":{ \"images\":[ { \"image\":\"R0lGODlhAQABAIAAAP///////yH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" } ], \"audio\":[ ], \"video\":[ ] } } ] }";
           //String body = "{ \"receiver\":{ \"id\":\"cdayanand-pre@paypal.com\", \"type\":\"EMAIL\" }, \"amount\":{ \"value\":\"5\", \"currency\":\"USD\" }, \"template\" : { \"id\":\"1\", \"background_color\":\"#ffeeff\" }, \"comments\":[ { \"message\":\"Here is your birthday gift!!\", \"location\":{ \"latitude\":\"37.779568\", \"longitude\":\"-122.41387\", \"place\":\"San Francisco, CA\" }, \"media\":{ \"images\":[ { \"image\":\"R0lGODlhAQABAIAAAP///////yH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\" } ], \"audio\":[ ], \"video\":[ ] } } ] }";
            JSONObject response = ConnectionUtils.createGift(body);
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
                Log.d("Response:", result);
                //Toast.makeText(getApplicationContext(),"gift created!",Toast.LENGTH_SHORT).show();
                try
                {
                    JSONObject jobj = new JSONObject(result);
                    FUNDING_ID = jobj.getString("id");

                    Log.e("ID",FUNDING_ID);
                    Toast.makeText(getApplicationContext(),"gift sent!",Toast.LENGTH_SHORT).show();
                    amount.setText("");
                    openGift.setVisibility(View.VISIBLE);
                    openGift.setEnabled(true);

                    //  new CreateFundingCall().execute();
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }

            }
            else
            {
                Log.d("Response:","failed");
                Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_SHORT).show();


            }

        }
    }

    private class CreateFundingCall extends AsyncTask<Void, Void, String>
    {
        public String doInBackground(Void... params)
        {
            // String body = getBodyCreateGift();
            JSONObject response = ConnectionUtils.fundingOptionsRequest(FUNDING_ID);
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
                Log.d("Response:", result);
                Toast.makeText(getApplicationContext(),"funding call completed!",Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jobj = new JSONObject(result);
                    JSONObject jobo = jobj.getJSONObject("funding_options");
                    JSONArray jAry = jobo.getJSONArray("options");
                    JSONObject job = (JSONObject)jAry.get(0);
                    FULFIL_ID = job.getString("id");
                    Log.e("FULFIL ID",FULFIL_ID);
                    new CreateFulfillmentCall().execute();

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
            else
            {
                Log.d("Response:","failed");
                Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_SHORT).show();


            }

        }
    }



    private class CreateFulfillmentCall extends AsyncTask<Void, Void, String>
    {
        public String doInBackground(Void... params)
        {
            // String body = getBodyCreateGift();
            FULFIL_BODY = getBodyFulfill();
            JSONObject response = ConnectionUtils.fulfillmentOptionsRequest(FUNDING_ID, FULFIL_BODY);
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
                Log.d("Response:", result);
                Toast.makeText(getApplicationContext(),"gift sent!",Toast.LENGTH_SHORT).show();
                amount.setText("");
                openGift.setVisibility(View.VISIBLE);
                openGift.setEnabled(true);

               // new getGifts().execute();
                   // new Send().execute();

            }
            else
            {
                Log.d("Response:","failed");
                Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_SHORT).show();


            }

        }
    }
    public String getBodyCreateGift()
    {

        StringBuffer sb = new StringBuffer("{\"receiver\":{\"id\":\"");
        sb.append(name);
        sb.append("\",\"type\":\"EMAIL\"},\"amount\":{\"value\":\"");
        sb.append(val);
        sb.append("\",\"currency\":\"");
        sb.append(cur);
        sb.append("\"},\"template\":{\"id\":\"1\",\"background_color\":\"#ffeeff\"},\"comments\":[{\"message\":\"");
        sb.append(message);
        sb.append("\",\"location\":{\"latitude\":\"37.779568\",\"longitude\":\"-122.41387\",\"place\":\"San Francisco, CA\"},\"media\":{\"images\":[{\"image\":\"");
       imgval = imgval.replace("\n","").replace("\r","");
      sb.append(imgval);
      //sb.append("R0lGODlhAQABAIAAAP///////yH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==");
      //  sb.append("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");
        sb.append("\"}],\"audio\":[],\"video\":[]}}]}");
        Log.e("Without breaks",imgval);
        Log.e("Printing body",sb.toString());
        return sb.toString();
    }

    public String getBodyFulfill()
    {
        StringBuffer sb = new StringBuffer("{\"funding_option_id\":\"");
        sb.append(FULFIL_ID);
        sb.append("\"}");
        return sb.toString();
    }

    public void launchContacts(String result)
    {
        Intent intent = new Intent(getApplicationContext(), GetGifts.class);
        intent.putExtra("response",result);

        startActivity(intent);
    }

    private class getGifts extends AsyncTask<Void, Void, String>
    {
        public String doInBackground(Void... params)
        {

            JSONArray response = ConnectionUtils.getGifts();
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
                res = result;
                new viewGiftCall().execute();
                //launchContacts(result);
            }
            else
            {
                Log.d("Response:","failed");
                Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_SHORT).show();


            }

        }
    }
    private class viewGiftCall extends AsyncTask<Void, Void, String>
    {
        public String doInBackground(Void... params)
        {

            JSONObject response = ConnectionUtils.viewGift(FUNDING_ID);
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
                    launchGift(imgString,receipient,value,cur,b,messa);




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

}
