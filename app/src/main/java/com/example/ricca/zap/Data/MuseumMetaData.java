package com.example.ricca.zap.Data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MuseumMetaData {

    private String nome;
    private String descrizione;
    private String labels;
    private String nomeModello;
    private String mappa;
    private String copertina;


    private MuseumMetaData metaData =this;


    //retrive metadata of museum in path and send it back to the waiter
    public MuseumMetaData(final String path, final MuseumMetaDataWaiter waiter) {

        FirebaseDatabase.getInstance().getReference(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nome=dataSnapshot.child("nome").getValue(String.class);
                descrizione=dataSnapshot.child("descrizione").getValue(String.class);
                mappa=dataSnapshot.child("mappa").getValue(String.class);
                labels=path+"/labels";
                nomeModello=dataSnapshot.child("modello").getValue(String.class);
                copertina=dataSnapshot.child("cover").getValue(String.class);
                Log.v("MUSEUM METADATA","download completed");
                waiter.init(metaData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public String getCopertina() {return copertina;}

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getLabels() {
        return labels;
    }

    public String getNomeModello() {
        return nomeModello;
    }

    public String getMappa() {
        return mappa;
    }
}
