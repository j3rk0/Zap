package com.example.ricca.zap.Data;

public class MuseoRef
{
    private String nome;
    private String path;

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
}
