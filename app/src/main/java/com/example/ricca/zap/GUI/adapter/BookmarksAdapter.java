package com.example.ricca.zap.GUI.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ricca.zap.ArtWorkActivity;
import com.example.ricca.zap.Data.Opera;
import com.example.ricca.zap.Data.ListaOpere;
import com.example.ricca.zap.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class BookmarksAdapter extends BaseAdapter {

    private ListaOpere list;
    private Context context;
    private ListView lista_gestita;

    public BookmarksAdapter(Context context,ListView lista_gestita)
    {
        this.context=context;
        this.list = new ListaOpere(context,"preferiti.txt");
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
            view= LayoutInflater.from(context).inflate(R.layout.vista_opera,null);
        }
        final Opera temp=(Opera)getItem(position);
        ((TextView)view.findViewById(R.id.nome)).setText(temp.getNome());
        ((TextView)view.findViewById(R.id.desc)).setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit sed.");
        Glide.with(this.context).load(temp.getMiniatura()).into((CircularImageView)view.findViewById(R.id.copertina));
        view.findViewById(R.id.remove_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                list.remove(position);
                if(lista_gestita!=null)
                    lista_gestita.invalidateViews();
            }
        });
        view.findViewById(R.id.copertina).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent start=new Intent(context, ArtWorkActivity.class);
                start.putExtra(EXTRA_MESSAGE,temp.getCollegamento());
                context.startActivity(start);
            }
        });
        return view;
    }
}
