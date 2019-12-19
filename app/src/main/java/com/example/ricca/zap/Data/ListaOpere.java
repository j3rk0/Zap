package com.example.ricca.zap.Data;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ListaOpere {

    private ArrayList<Opera> listaopere;
    private String file;
    private Context context;

    public boolean isLoaded() {
        return loaded;
    }

    private boolean loaded=false;


    public ListaOpere(final Context context, final String file) {
        this.context = context;
        listaopere =new ArrayList<>();
        this.file=file;

        new Thread(new Runnable() {
            @Override
            public void run() {
                FileInputStream fis = null;

                try {

                    fis = context.openFileInput(file);
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader br = new BufferedReader(isr);
                    String nome, collegamento, miniatura;
                    String line;
                    String[] campo;

                    while ((line = br.readLine()) != null) {
                        if (line.contains("@")) {
                            campo = line.split("@");
                            nome = campo[0];
                            collegamento = campo[1];
                            miniatura = campo[2];
                            //spezzetti line

                            listaopere.add(new Opera(nome, collegamento, miniatura));

                        }
                    }

                } catch(IOException e){
                    e.printStackTrace();
                } finally{
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    loaded=true;
                    }
                }
            }
        }).start();

    }

    public boolean isPresent(String collegamento)    {

        for(int i = 0; i< listaopere.size(); i++)
            if(listaopere.get(i).getCollegamento().equals(collegamento))return true;
        return false;
    }

    private void updatefile()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {

                FileOutputStream fos = null;
                try
                {
                    fos = context.openFileOutput(file, MODE_PRIVATE);
                    String temp = "";

                    for (Opera A : listaopere)
                    {
                        temp = temp.concat(A.getNome() + "@" + A.getCollegamento() + "@" + A.getMiniatura() + "\n");
                    }
                    fos.write(temp.getBytes());

                } catch (IOException e) { e.printStackTrace();}
                finally
                {
                    if (fos != null) {
                        try { fos.close();}
                        catch (IOException e) {e.printStackTrace();}
                    }
                }

            }
        }).start();

    }


    public void add(String nome, String collegamento, String miniatura) {
        loaded=false;
        for(Opera A: listaopere){
            if (A.getCollegamento().equals(collegamento))
                return;
        }
        listaopere.add(new Opera(nome, collegamento, miniatura));
        updatefile();
        if(file.equals("preferiti.txt"))
            Toast.makeText(context,"Aggiunto "+nome+" ai preferiti",Toast.LENGTH_SHORT).show();
        loaded=true;
    }

    public void remove(int i) {

        loaded=false;
        listaopere.remove(i);
        updatefile();
        if(file.equals("preferiti.txt"))
            Toast.makeText(context,"Opera rimosso dai preferiti",Toast.LENGTH_SHORT).show();
        loaded=true;
    }

    public void remove(String collegamento)
    {
        loaded=false;
        for(int i = 0; i< listaopere.size(); i++)
            if(listaopere.get(i).getCollegamento().equals(collegamento))
                listaopere.remove(i);

        updatefile();
        if(file.equals("preferiti.txt"))
            Toast.makeText(context,"Opera rimosso dai preferiti",Toast.LENGTH_SHORT).show();
        loaded=true;
    }

    public Opera get(int i) {
        return listaopere.get(i);
    }

    public int size() {
        return listaopere.size();
    }
}