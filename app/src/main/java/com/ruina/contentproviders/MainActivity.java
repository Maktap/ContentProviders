package com.ruina.contentproviders;

import android.Manifest;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.ContactsContract;
import android.provider.Settings;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab =null;
    ListView listView;
    ArrayList<String> contractList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fabListener();
        listView = findViewById(R.id.listView);
        contractList = new ArrayList<>();

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},1000);
        }

    }

    public void fabListener(){
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                checkPermission(view);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void checkPermission(View view){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){

            ContentResolver contentResolver = getContentResolver();
            String[] projection = {ContactsContract.Contacts.DISPLAY_NAME};

            Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                    projection,
                    null,
                    null,
                    ContactsContract.Contacts.DISPLAY_NAME);

            if(cursor!=null){
                String coloumIx = ContactsContract.Contacts.DISPLAY_NAME;

                while (cursor.moveToNext()){
                    contractList.add(cursor.getString(cursor.getColumnIndex(coloumIx)));
                }
                cursor.close();

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.select_dialog_item,contractList);
                listView.setAdapter(arrayAdapter);
            }

        }else{
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_INDEFINITE).setAction("Action", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_CONTACTS)){
                        //Eğer "Bir daha sorma " seçeneği seçilmemiş ise ,izin isteme gösterilebiliniyorsa --> izin iste
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_CONTACTS},1000);
                    }else{
                        // Kullanıcı, "bir daha gösterme seçeneğini seçmiş ise --> App özelliklerine yönlendir."
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package",MainActivity.this.getPackageName(),null);
                        intent.setData(uri);
                        MainActivity.this.startActivity(intent);

                    }

                }
            }).show();

            //
        }
    }

}
