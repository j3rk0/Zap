package com.example.ricca.zap.DAO;

public class Elemento {
    private String Nome;
    private String Collegamento;

    public String getMiniatura() {
        return Miniatura;
    }

    private String Miniatura;

    Elemento(String nome, String collegamento, String miniatura) {
        Nome = nome;
        Collegamento = collegamento;
        Miniatura = miniatura;
    }

    public String getNome() {
        return Nome;
    }

    public String getCollegamento() {
        return Collegamento;
    }

}

