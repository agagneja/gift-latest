package com.example.agagneja.newgiftingapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;

import android.app.Activity;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class ChooseContact extends Activity {

    SimpleCursorAdapter mAdapter;
    MatrixCursor mMatrixCursor;
    MatrixCursor reader;
    EditText name_enter ;
    public static String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_contact);
        // The contacts from the contacts content provider is stored in this cursor
        mMatrixCursor = new MatrixCursor(new String[] { "_id","photo","name"} );

        // Adapter to set data in the listview
        mAdapter = new SimpleCursorAdapter(getBaseContext(),
                R.layout.lv_layout,
                null,
               // new String[] { "name","photo","details"},
                //new int[] { R.id.tv_name,R.id.iv_photo,R.id.tv_details}, 0);
                new String[] { "photo","name"},
                new int[] { R.id.iv_photo,R.id.tv_name}, 0);

        // Getting reference to listview
        ListView lstContacts = (ListView) findViewById(R.id.lst_contacts);
        name_enter = (EditText) findViewById(R.id.name_enter);
        name_enter.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    name=name_enter.getText().toString();
                    Toast.makeText(getApplicationContext(), name_enter.getText(), Toast.LENGTH_SHORT).show();
                    returnToCreateGift(name);
                    return true;
                }
                return false;
            }

        });

        // Setting the adapter to listview
        lstContacts.setAdapter(mAdapter);

        // Creating an AsyncTask object to retrieve and load listview with contacts
        ListViewContactsLoader listViewContactsLoader = new ListViewContactsLoader();

        // Starting the AsyncTask process to retrieve and load listview with contacts
        listViewContactsLoader.execute();

        lstContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = parent.getItemAtPosition(position);
               reader = (MatrixCursor)o;
               String result[] = reader.getColumnNames();
                for(int i =0; i<result.length;i++)
                {
                    Log.e("i",result[i]);


                }
               name = reader.getString(2);
                Log.e("name",name);
              String photo = reader.getString(1);
               Log.e("photo",photo);

                returnToCreateGift(name);



            }
        });
    }

    public void returnToCreateGift(String name)
    {
        Intent intent = new Intent(this,CreateGift.class);
        intent.putExtra("name",name);
        startActivity(intent);

    }
    /** An AsyncTask class to retrieve and load listview with contacts */
    private class ListViewContactsLoader extends AsyncTask<Void, Void, Cursor>{

        @Override
        protected Cursor doInBackground(Void... params) {
            Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;

            // Querying the table ContactsContract.Contacts to retrieve all the contacts
            Cursor contactsCursor = getContentResolver().query(contactsUri, null, null, null,
                    ContactsContract.Contacts.DISPLAY_NAME + " ASC ");

            if(contactsCursor.moveToFirst()){
                do{
                    long contactId = contactsCursor.getLong(contactsCursor.getColumnIndex("_ID"));

                    Uri dataUri = ContactsContract.Data.CONTENT_URI;

                    // Querying the table ContactsContract.Data to retrieve individual items like
                    // home phone, mobile phone, work email etc corresponding to each contact
                    Cursor dataCursor = getContentResolver().query(dataUri, null,
                            ContactsContract.Data.CONTACT_ID + "=" + contactId,
                            null, null);

                    String displayName="";
                    String nickName="";
                    String homePhone="";
                    String mobilePhone="";
                    String workPhone="";
                    String photoPath="" + R.drawable.blank;
                    byte[] photoByte=null;
                    String homeEmail="";
                    String workEmail="";
                    String companyName="";
                    String title="";

                    if(dataCursor.moveToFirst()){
                        // Getting Display Name
                        displayName = dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME ));
                        do{

                            // Getting NickName
                            if(dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE))
                                nickName = dataCursor.getString(dataCursor.getColumnIndex("data1"));

                            // Getting Phone numbers
                            if(dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)){
                                switch(dataCursor.getInt(dataCursor.getColumnIndex("data2"))){
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME :
                                        homePhone = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE :
                                        mobilePhone = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK :
                                        workPhone = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                        break;
                                }
                            }

                            // Getting EMails
                            if(dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE ) ) {
                                switch(dataCursor.getInt(dataCursor.getColumnIndex("data2"))){
                                    case ContactsContract.CommonDataKinds.Email.TYPE_HOME :
                                        homeEmail = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                        break;
                                    case ContactsContract.CommonDataKinds.Email.TYPE_WORK :
                                        workEmail = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                        break;
                                }
                            }

                            // Getting Organization details
                            if(dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)){
                                companyName = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                title = dataCursor.getString(dataCursor.getColumnIndex("data4"));
                            }

                            // Getting Photo
                            if(dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)){
                                photoByte = dataCursor.getBlob(dataCursor.getColumnIndex("data15"));

                                if(photoByte != null) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(photoByte, 0, photoByte.length);

                                    // Getting Caching directory
                                    File cacheDirectory = getBaseContext().getCacheDir();

                                    // Temporary file to store the contact image
                                    File tmpFile = new File(cacheDirectory.getPath() + "/wpta_"+contactId+".png");

                                    // The FileOutputStream to the temporary file
                                    try {
                                        FileOutputStream fOutStream = new FileOutputStream(tmpFile);

                                        // Writing the bitmap to the temporary file as png file
                                        bitmap.compress(Bitmap.CompressFormat.PNG,100, fOutStream);

                                        // Flush the FileOutputStream
                                        fOutStream.flush();

                                        //Close the FileOutputStream
                                        fOutStream.close();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    photoPath = tmpFile.getPath();
                                }
                            }
                        }while(dataCursor.moveToNext());
                        String details = "";

                        // Concatenating various information to single string
                        if(homePhone != null && !homePhone.equals("") )
                            details = "HomePhone : " + homePhone + "\n";
                        if(mobilePhone != null && !mobilePhone.equals("") )
                            details += "MobilePhone : " + mobilePhone + "\n";
                        if(workPhone != null && !workPhone.equals("") )
                            details += "WorkPhone : " + workPhone + "\n";
                        if(nickName != null && !nickName.equals("") )
                            details += "NickName : " + nickName + "\n";
                        if(homeEmail != null && !homeEmail.equals("") )
                            details += "HomeEmail : " + homeEmail + "\n";
                        if(workEmail != null && !workEmail.equals("") )
                            details += "WorkEmail : " + workEmail + "\n";
                        if(companyName != null && !companyName.equals("") )
                            details += "CompanyName : " + companyName + "\n";
                        if(title != null && !title.equals("") )
                            details += "Title : " + title + "\n";

                        // Adding id, display name, path to photo and other details to cursor
                        Contacts contacts = new Contacts();
                        contacts.setName(displayName);
                        contacts.setPhoto(photoPath);
                      //  contacts.setDetails(details);
                        mMatrixCursor.addRow(new Object[]{ Long.toString(contactId),photoPath,displayName});
                    }
                }while(contactsCursor.moveToNext());
            }
            return mMatrixCursor;
        }

        @Override
        protected void onPostExecute(Cursor result) {
            // Setting the cursor containing contacts to listview
            mAdapter.swapCursor(result);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_contact, menu);
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
