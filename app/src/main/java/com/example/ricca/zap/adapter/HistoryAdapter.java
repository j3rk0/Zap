package com.example.ricca.zap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ricca.zap.DAO.Elemento;
import com.example.ricca.zap.DAO.ListaElementi;
import com.example.ricca.zap.R;

public class HistoryAdapter extends BaseAdapter {

    private ListaElementi list;
    private Context context;

    public HistoryAdapter(Context context)
    {
        this.context=context;
        this.list = new ListaElementi(context,"cronologia.txt");

    }



    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) { return list.get(i); }

    @Override
    public long getItemId(int i) {
        return getItem(i).hashCode();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view==null)
        {
            view= LayoutInflater.from(context).inflate(R.layout.raw_history, null);
        }

        Elemento temp = (Elemento) getItem(i);

        TextView txt= view.findViewById(R.id.nome);
        TextView txt2= view.findViewById(R.id.desc);
        ImageView imageView = view.findViewById(R.id.copertina);
        ImageButton remove = view.findViewById(R.id.remove_button);

        txt.setText(temp.getNome());
        txt2.setText(temp.getCollegamento());
        Glide.with(this.context).load(temp.getMiniatura()).into(imageView);
        remove.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //list.remove();
            }
        });



        return view;
    }
}