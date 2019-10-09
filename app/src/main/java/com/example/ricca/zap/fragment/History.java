package com.example.ricca.zap.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ricca.zap.R;
import com.example.ricca.zap.adapter.HistoryAdapter;
public
class History extends Fragment {

    private ListView listview;

    public static History newInstance() {
        History fragment = new History();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.history_fragment, container, false);
        listview = view.findViewById(R.id.list);
        listview.setAdapter(new HistoryAdapter(getActivity()));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        listview.setAdapter(new HistoryAdapter(getActivity()));
    }


}
