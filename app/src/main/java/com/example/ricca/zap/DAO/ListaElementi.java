package com.example.ricca.zap.DAO;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.tensorflow.Session;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class ListaElementi {

    private ArrayList<Elemento> listaelementi;
    private String file;
    private Context context;


    public ListaElementi(final Context context,final String file) {
        this.context = context;
        listaelementi=new ArrayList<>();
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

                            listaelementi.add(new Elemento(nome, collegamento, miniatura));

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
                    }
                }
            }
        }).start();

    }

    public boolean isPresent(String collegamento)    {
        for(Elemento e:listaelementi)
            if(e.getCollegamento().equals(collegamento)) return true;
        return false;
    }

    private void updatefile()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Calendar time=Calendar.getInstance();
                FileOutputStream fos = null;
                try
                {
                    fos = context.openFileOutput(file, MODE_PRIVATE);
                    String temp = "";

                    for (Elemento A : listaelementi)
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
        for(Elemento A:listaelementi){
            if (A.getCollegamento().equals(collegamento))
                return;
        }
        listaelementi.add(new Elemento(nome, collegamento, miniatura));
        updatefile();
        if(file.equals("preferiti.txt"))
            Toast.makeText(context,"Aggiunto "+nome+" ai preferiti",Toast.LENGTH_SHORT).show();
    }

    public void remove(int i) {

        listaelementi.remove(i);
        updatefile();
        if(file.equals("preferiti.txt"))
            Toast.makeText(context,"Elemento rimosso dai preferiti",Toast.LENGTH_SHORT).show();
    }

    public void remove(String collegamento)
    {
        int i=0;
        while(i<listaelementi.size())
            if(listaelementi.get(i).getCollegamento().equals(collegamento))
                listaelementi.remove(i);

        updatefile();
        if(file.equals("preferiti.txt"))
            Toast.makeText(context,"Elemento rimosso dai preferiti",Toast.LENGTH_SHORT).show();
    }

    public  Elemento get(int i) {
        return listaelementi.get(i);
    }

    public int size() {
        return listaelementi.size();
    }
}