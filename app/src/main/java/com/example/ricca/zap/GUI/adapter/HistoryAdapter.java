package com.example.ricca.zap.GUI.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class HistoryAdapter extends BaseAdapter {

    private ListaOpere list;
    private Context context;
    private ListView lista_gestita;


    public HistoryAdapter(Context context,ListView lista_gestita)
    {
        this.context=context;
        this.list = new ListaOpere(context,"cronologia.txt");
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

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        if (view==null)
        {
            view= LayoutInflater.from(context).inflate(R.layout.vista_opera, null);
        }

        final Opera temp = (Opera) getItem(i);


        ((TextView)view.findViewById(R.id.nome)).setText(temp.getNome());
        ((TextView)view.findViewById(R.id.desc)).setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit sed.");
        Glide.with(this.context).load(temp.getMiniatura()).into((CircularImageView)view.findViewById(R.id.copertina));
        view.findViewById(R.id.remove_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                new AlertDialog.Builder(context)
                        .setTitle("elimina cronologia")
                        .setMessage("Sicuro di voler eliminare?")
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                list.remove(i);
                                if(lista_gestita!=null)
                                    lista_gestita.invalidateViews();
                            }
                        })
                        .setNegativeButton("NO",null).create().show();

            }
        });
        view.setOnClickListener(new View.OnClickListener() {
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