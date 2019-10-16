package com.example.ricca.zap.pager.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ricca.zap.DAO.Elemento;
import com.example.ricca.zap.DAO.ListaElementi;
import com.example.ricca.zap.R;
import com.mikhaellopez.circularimageview.CircularImageView;

public class HistoryAdapter extends BaseAdapter {

    private ListaElementi list;
    private Context context;
    private ListView lista_gestita;


    public HistoryAdapter(Context context,ListView lista_gestita)
    {
        this.context=context;
        this.list = new ListaElementi(context,"cronologia.txt");
        this.lista_gestita=lista_gestita;
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
    public View getView(final int i, View view, ViewGroup viewGroup) {

        if (view==null)
        {
            view= LayoutInflater.from(context).inflate(R.layout.raw_history, null);
        }

        final Elemento temp = (Elemento) getItem(i);


        ((TextView)view.findViewById(R.id.nome)).setText(temp.getNome());
        ((TextView)view.findViewById(R.id.desc)).setText(temp.getCollegamento());
        Glide.with(this.context).load(temp.getMiniatura()).into((CircularImageView)view.findViewById(R.id.copertina));
        view.findViewById(R.id.remove_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                list.remove(i);
                if(lista_gestita!=null)
                lista_gestita.invalidateViews();
            }
        });

        return view;
    }
}