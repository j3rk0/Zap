package com.example.ricca.zap.GUI.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ricca.zap.Data.ListaMusei;
import com.example.ricca.zap.Data.MuseoRef;
import com.example.ricca.zap.R;
import com.mikhaellopez.circularimageview.CircularImageView;



public class MusemAdapter extends BaseAdapter implements Filterable {

    private ListaMusei list;
    private ListaMusei baseList;
    private Context context;

    public MusemAdapter(Context context, ListaMusei list) {
        this.list = list;
        this.baseList = list;
        this.context = context;
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

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        if (view==null)
        {
            view= LayoutInflater.from(context).inflate(R.layout.vista_museo, null);
        }
        final MuseoRef temp = (MuseoRef) getItem(i);

            ((TextView) view.findViewById(R.id.museum_name)).setText(temp.getNome());
            ((TextView) view.findViewById(R.id.museum_desc)).setText(temp.getDesc());
            Glide.with(this.context).load(temp.getCover()).into((CircularImageView) view.findViewById(R.id.copertina_museo));
        return view;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {

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
    }
}
