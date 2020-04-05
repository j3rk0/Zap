package com.example.ricca.zap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Objects;

public class SplashScreen extends AppCompatActivity {

    private Activity context=this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

      ArrayList<String> permissionList=new ArrayList<>(); //Lista dei permessi

       if(ContextCompat.checkSelfPermission(SplashScreen.this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED)
           permissionList.add(Manifest.permission.CAMERA);//aggiungi permesso camera

       if(ContextCompat.checkSelfPermission(SplashScreen.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
           permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);//aggiungi permesso scrittura

       if(ContextCompat.checkSelfPermission(SplashScreen.this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
           permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);//aggiungi permesso lettura

       String [] permissions=new String[permissionList.size()];//costruisce array permessi necessari
       for (int i=0; i< permissionList.size(); i++)
           permissions[i]=permissionList.get(i);

       if(permissionList.size()>0)
       ActivityCompat.requestPermissions(SplashScreen.this,permissions,1);//richiede permessi

       new Thread(new Runnable()
       {
           @Override
           public void run()
           {
               try
               {
                   while(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED
                   || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                       Thread.sleep(50); //aspetta finchè non sono garantiti tutti i permessi

                   Thread.sleep(500);
                   SplashScreen.this.startActivity(new Intent(SplashScreen.this, HomeActivity.class));
                   SplashScreen.this.finish();//vai alla home
               } catch (InterruptedException e)
               {
                   Log.e("SplashScreen", Objects.requireNonNull(e.getMessage()));
               }
           }
        }).start();
    }
}
