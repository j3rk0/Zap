package com.example.ricca.zap.GUI.fragment.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ricca.zap.ArtWorkActivity;
import com.example.ricca.zap.Data.MuseoRef;
import com.example.ricca.zap.HomeActivity;
import com.example.ricca.zap.MuseumActivity;
import com.example.ricca.zap.R;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class Home extends Fragment {

    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }

    public static Home newInstance()
    {
        Home fragment= new Home();
        Bundle args=new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void openMuseum(MuseoRef ref)
    {
        Intent start=new Intent(context, MuseumActivity.class);
        start.putExtra(EXTRA_MESSAGE,ref.getPath());
        startActivity(start);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v= inflater.inflate(R.layout.fragment_home,container,false);

        v.findViewById(R.id.goto_museum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMuseum(new MuseoRef("","museums/music_museum"));
            }
        });

        return v;
    }


}
