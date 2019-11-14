package com.example.ricca.zap.mainActivity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.ricca.zap.R;
import com.example.ricca.zap.mainActivity.fragment.adapter.BookmarksAdapter;

public class Bookmarks extends Fragment {

    private BaseAdapter adapter;

    public static Bookmarks newInstance() {
        Bookmarks fragment = new Bookmarks();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
       // adapter.notifyDataSetChanged();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_bookmarks,container,false);
        ListView listView = view.findViewById((R.id.list_bookmarks));
        adapter=new BookmarksAdapter(getActivity(), listView);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
