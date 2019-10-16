package com.example.ricca.zap.pager.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ricca.zap.DAO.Elemento;
import com.example.ricca.zap.DAO.ListaElementi;
import com.example.ricca.zap.R;
import com.mikhaellopez.circularimageview.CircularImageView;

public class BookmarksAdapter extends BaseAdapter {

    private ListaElementi list;
    private Context context;
    private ListView lista_gestita;

    public BookmarksAdapter(Context context,ListView lista_gestita)
    {
        this.context=context;
        this.list = new ListaElementi(context,"preferiti.txt");
        this.lista_gestita=lista_gestita;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if(view==null)
        {
            view= LayoutInflater.from(context).inflate(R.layout.raw_history,null);
        }
        final Elemento temp=(Elemento)getItem(position);
        ((TextView)view.findViewById(R.id.nome)).setText(temp.getNome());
        ((TextView)view.findViewById(R.id.desc)).setText(temp.getCollegamento());
        Glide.with(this.context).load(temp.getMiniatura()).into((CircularImageView)view.findViewById(R.id.copertina));
        view.findViewById(R.id.remove_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                list.remove(position);
                if(lista_gestita!=null)
                    lista_gestita.invalidateViews();
            }
        });
        return view;
    }
}
