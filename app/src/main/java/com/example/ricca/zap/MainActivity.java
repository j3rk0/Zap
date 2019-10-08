package com.example.ricca.zap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;  //disabilita status bar
        decorView.setSystemUiVisibility(uiOptions);


        int timeout=1000;
        final Handler handler= new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Intent start=new Intent(MainActivity.this,QrScanner.class); //fa partire wall
                //start.putExtra(EXTRA_MESSAGE,"music museum/chuck berry");      //inoltra il riferimento all'opera


                MainActivity.this.startActivity(start);
                MainActivity.this.finish();
                handler.removeCallbacks(this);
            }
        }, timeout);


    }
}
