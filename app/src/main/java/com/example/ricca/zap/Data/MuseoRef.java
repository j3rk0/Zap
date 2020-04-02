package com.example.ricca.zap.Data;

import android.text.TextUtils;

import org.w3c.dom.Text;

public class MuseoRef
{
    private String nome;
    private String path;
    private String cover;

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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public void set (String nome, String path, String cover){
            this.nome = nome;
            this.path = path;
            this.cover = cover;
    }
    public boolean empty (){
        if (TextUtils.isEmpty(this.path))
            return true;
        return false;
    }

}
