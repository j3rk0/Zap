package com.example.ricca.zap;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Objects;

public class SplashScreen extends AppCompatActivity {

    private Context context=this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //richiedo permessi
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},2);
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {       //aspetto finchè non sono garantiti i permessi
                        while(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                        {
                            Thread.sleep(50);
                        }
                        Thread.sleep(500);
                        SplashScreen.this.startActivity(new Intent(SplashScreen.this,MainActivity.class));
                        SplashScreen.this.finish();

                } catch (InterruptedException e)
                {
                    Log.e("SplashScreen", Objects.requireNonNull(e.getMessage()));
                }
            }
        }).start();
    }
}
