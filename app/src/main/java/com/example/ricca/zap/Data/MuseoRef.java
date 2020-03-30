package com.example.ricca.zap.Data;

import android.text.TextUtils;

import org.w3c.dom.Text;

public class MuseoRef
{
    private String nome;
    private String path;

    public MuseoRef (){}

    public MuseoRef(String nome, String path) {
        this.nome = nome;
        this.path = path;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void set (String nome, String path){
            this.nome = nome;
            this.path = path;
    }
    public boolean empty (){
        if (TextUtils.isEmpty(this.path))
            return true;
        return false;
    }

}
