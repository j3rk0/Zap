package com.example.ricca.zap.Services;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;

import com.example.ricca.zap.R;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

public class ConnectionListener
{
    private Activity context;
    private boolean connected;
    private DialogPlus connectionDialog;
    private ConnectivityManager connectivityManager;
    public ConnectionListener(Activity context)
    {
        this.context=context;
        connected=true;
        connectionDialog=DialogPlus.newDialog(context) //crea dialog
                .setGravity(Gravity.CENTER)
                .setCancelable(false)
                .setContentHolder(new ViewHolder(R.layout.dialog_connecting))
                .create();
        connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

    }

    public void start()
    {
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                while(true)
                {
                    connected=isNetworkAvailable(); //stato connessione
                    context.runOnUiThread(new Runnable() //sul thread dell ui mostra o nasconde il dialog
                    {
                        @Override
                        public void run()
                        {
                            if(!connected && !connectionDialog.isShowing()) //se non c'è connessione e nemmeno il dialog
                                connectionDialog.show();
                            else if(connected && connectionDialog.isShowing()) //se c'è connessione e anche il dialog
                                connectionDialog.dismiss();
                        }
                    });
                    try {
                        Thread.sleep(100); //fa dormire il thread per 0,1 secondi
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }}).start();
    }

    private boolean isNetworkAvailable()
    {  //controlla se la connessione è presente
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}