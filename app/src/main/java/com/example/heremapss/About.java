package com.example.heremapss;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class About extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        EditText edit1=(EditText) findViewById(R.id.edit1);
        EditText edit2=(EditText) findViewById(R.id.edit2);
        Button btn=(Button) findViewById(R.id.button);
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
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.about:
                        startActivity(new Intent(getApplicationContext(), About.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }

        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Intent.ACTION_SENDTO);
                i.setType("message/html");
                i.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] { "ST10121910@vcconnect.edu.za" });
                i.putExtra(Intent.EXTRA_SUBJECT,"Feedback From App");
                i.putExtra(Intent.EXTRA_TEXT,"Name:"+edit1.getText()+"\n Message:"+edit2.getText());
                try {
                    startActivity(Intent.createChooser(i,"Please select Email"));
                }

                catch (android.content.ActivityNotFoundException ex)
                {
                    Toast.makeText(About.this, "There are no Email Clients", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}