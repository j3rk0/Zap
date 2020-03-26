package com.example.ricca.zap.GUI.fragment.museum;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ricca.zap.R;


public class MuseumInfo extends Fragment {


    public static MuseumInfo newIstance()
    {
        MuseumInfo fragment= new MuseumInfo();
        Bundle args=new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_museum_info, container, false);
        return v;
    }
}
