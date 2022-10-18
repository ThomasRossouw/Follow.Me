package com.example.heremapss;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ProfilePage extends AppCompatActivity {



    private Button logout;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    String type = "";
    User users;
    private FirebaseAuth mAuth;
    //variables
    ListView catlist;
    EditText cat;
    private Button create;
    TextView title;
    ArrayList<String> List;
    ArrayAdapter<String> arrayAdapter;
    DatabaseHelper mydb;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        mAuth = FirebaseAuth.getInstance();
        cat = findViewById(R.id.editTextcategory);
        catlist = findViewById(R.id.lvlist);
        create = findViewById(R.id.btncreate);
        List = new ArrayList<String>();

        //object for database
        mydb = new DatabaseHelper(this, "Items.sqlite", null, 1);


        // initialize navigation View
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        //set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.home);
        // perform itemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), ProfilePage.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.about:
                        startActivity(new Intent(getApplicationContext(), About.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }

        });

        //   radioGroup = findViewById(R.id.myRadioGroup);
        logout = (Button) findViewById(R.id.btn_Logout_Profile);



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth.signOut();
                Intent intent = new Intent(ProfilePage.this, Login.class);
                startActivity(intent);
                finish();
                Toast.makeText(ProfilePage.this, "Logout Successful !", Toast.LENGTH_SHORT).show();

            }
        });



        //gathers category name from user input and stores it into database


        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String category = cat.getText().toString();
                if (!category.equals("") && mydb.insertData(category))
                {
                    Toast.makeText(ProfilePage.this, "Favourite Added", Toast.LENGTH_SHORT).show();
                    cat.setText("");
                    viewData();
                    //List.clear();
                }
                else{
                    Toast.makeText(ProfilePage.this, "Favourite Not Added", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    //displays category names into list view from database
    private void viewData() {
        Cursor cursor = mydb.ViewData();
        if (cursor.getCount()==0)
        {
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }
        else{
            while(cursor.moveToNext()){
                List.add(cursor.getString(1));
            }

            arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,List);
            catlist.setAdapter(arrayAdapter);
        }
    }

}
