package com.example.ricca.zap;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class Elemento {
    private String Nome;
    private String Collegamento;

    String getMiniatura() {
        return Miniatura;
    }

    public void setMiniatura(String miniatura) {
        Miniatura = miniatura;
    }

    private String Miniatura;

    Elemento(String nome, String collegamento, String miniatura) {
        Nome = nome;
        Collegamento = collegamento;
        Miniatura = miniatura;
    }

    String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    String getCollegamento() {
        return Collegamento;
    }

    public void setCollegamento(String collegamento) {
        Collegamento = collegamento;
    }
}

class ListaElementi {

    private ArrayList<Elemento> listaelementi;
    private String file;
    private Context context;


    ListaElementi(Context context, String file) {
        this.context = context;
        listaelementi=new ArrayList<>();
        this.file=file;

        FileInputStream fis = null;



        try {
            File curr=new File(file);
            if(!curr.exists())curr.createNewFile();
            fis = context.openFileInput(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String nome, collegamento, miniatura="";
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

    boolean isPresent(String collegamento)    {
        for(Elemento e:listaelementi)
            if(e.getCollegamento().equals(collegamento)) return true;
        return false;
    }

    private void updatefile() {

        FileOutputStream fos = null;

        try {
            fos = context.openFileOutput(file, MODE_PRIVATE);

            String temp="";

            for(Elemento A: listaelementi)
            {
                temp= temp.concat(A.getNome()+"@"+A.getCollegamento()+"@"+A.getMiniatura()+"\n");
            }
            fos.write(temp.getBytes());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    void add(String nome, String collegamento, String miniatura) {
        for(Elemento A:listaelementi){
            if (A.getCollegamento().equals(collegamento))
                return;
        }
        listaelementi.add(new Elemento(nome, collegamento, miniatura));
        updatefile();
        if(file.equals("preferiti.txt"))
            Toast.makeText(context,"Aggiunto "+nome+" ai preferiti",Toast.LENGTH_SHORT).show();
    }

    void remove(String collegamento) {
        int i=0;
        while(i<listaelementi.size())
            if(listaelementi.get(i).getCollegamento().equals(collegamento))
            {
                listaelementi.remove(i);
                break;
            }

        updatefile();
        if(file.equals("preferiti.txt"))
            Toast.makeText(context,"Elemento rimosso dai preferiti",Toast.LENGTH_SHORT).show();
    }

    Elemento get(int i) {
        return listaelementi.get(i);
    }

    int size() {
        return listaelementi.size();
    }

}