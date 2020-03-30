package com.example.ricca.zap.GUI.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Filter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ricca.zap.R;

import java.util.ArrayList;
import java.util.List;



public class MusemAdapter extends BaseAdapter implements Filter {

    private ArrayList<String> list;
    private Context context;
    private ListView listView;

    public MusemAdapter(Context context,ArrayList<String> list, ListView listView) {
        this.list = list;
        this.context = context;
        this.listView = listView;
    }

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
            view= LayoutInflater.from(context).inflate(R.layout.vista_museo, null);
        }
        String string = (String) getItem(i);
        ((TextView)view.findViewById(R.id.textViewMuseo)).setText(string);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                            }
        });

        return view;
    }

    @Override
    public boolean onLoadClass(Class clazz) {
        return false;
    }


}
