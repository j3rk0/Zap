package com.example.ricca.zap.DAO;

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

    public String getMiniatura() {
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

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public String getCollegamento() {
        return Collegamento;
    }

    public void setCollegamento(String collegamento) {
        Collegamento = collegamento;
    }
}

