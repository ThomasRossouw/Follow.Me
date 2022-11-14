package com.example.heremapss;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.grpc.Context;

public class SOS extends AppCompatActivity {

    //Variable

    EditText etPhone;
    EditText etMessage;
    Button btSend;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);


        //typecasting
        etPhone = findViewById(R.id.et_phone);
        etMessage = findViewById(R.id.et_message);
        btSend = findViewById(R.id.et_send);

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check condition
                if(ContextCompat.checkSelfPermission(SOS.this, Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED){
            //permission granted
                    SendMessage();

                } else {
                    ActivityCompat.requestPermissions(SOS.this, new String[]{Manifest.permission.SEND_SMS},
                         100);
                }
            }
        });
}

    private void SendMessage() {
   String sPhone = etPhone.getText().toString().trim();
   String sMessage = etMessage.getText().toString().trim();
   //check condition
        if(!sPhone.equals("") && !sMessage.equals("")){
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(sPhone,null,sMessage,null,null);

            Toast.makeText(getApplicationContext(), "SMS sent successfully", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "Enter Value First", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 100 && grantResults.length > 0 && grantResults[0]
        == PackageManager.PERMISSION_GRANTED){
            SendMessage();
        }else {
                Toast.makeText(getApplicationContext(),"Permission Denied!",Toast.LENGTH_SHORT).show();
    }
    }

    public void Back(View view){
        Intent SOSIntent  = new Intent(SOS.this,MainActivity.class);
        startActivity(SOSIntent);
    }
}