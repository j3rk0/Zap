package com.example.ricca.zap.Data;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

public class ListaMusei {

    private ArrayList<MuseoRef> arrayList;

    public ListaMusei (ArrayList<MuseoRef> arrayList) {
        this.arrayList = arrayList;
    }

    public ListaMusei() {
        this.arrayList = new ArrayList<MuseoRef>();
    }

    public void add (MuseoRef museoRef){
        this.arrayList.add(museoRef);
    }

    public MuseoRef find (String string){
        for (MuseoRef ref : this.arrayList ){
            if (ref.getNome().equals(string))
                return ref;
        }
        return null;
    }

    public int size (){
        return arrayList.size();
    }

    public MuseoRef get (int i){
        return arrayList.get(i);
    }
}
