package com.example.ricca.zap.GUI.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ricca.zap.Data.ListaMusei;
import com.example.ricca.zap.Data.MuseoRef;
import com.example.ricca.zap.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;



public class MusemAdapter extends BaseAdapter implements Filterable {

    private ListaMusei list;
    private ListaMusei baseList;
    private Context context;
    private ListView listView;

    public MusemAdapter(Context context, ListaMusei list, ListView listView) {
        this.list = list;
        this.baseList = list;
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
            view= LayoutInflater.from(context).inflate(R.layout.vista_opera, null);
        }
        final MuseoRef temp = (MuseoRef) getItem(i);

        ((TextView)view.findViewById(R.id.nome)).setText(temp.getNome());
        Glide.with(this.context).load(temp.getCover()).into((CircularImageView)view.findViewById(R.id.copertina));

        view.findViewById(R.id.remove_button).setVisibility(View.GONE);
        ((TextView)view.findViewById(R.id.desc)).setVisibility(View.GONE);

        return view;
    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                list = (ListaMusei) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ListaMusei FilteredArrayNames = new ListaMusei();

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < baseList.size(); i++) {
                    String dataNames = baseList.get(i).getNome();
                    if (dataNames.toLowerCase().contains(constraint.toString()))  {
                        FilteredArrayNames.add(baseList.get(i));
                    }
                }

                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;
                return results;
            }
        };

        return filter;
    }
}
