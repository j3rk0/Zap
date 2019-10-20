package com.example.ricca.zap.pager.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ricca.zap.QrScanner;
import com.example.ricca.zap.R;

import java.util.Objects;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;


public
class Home extends Fragment {


   public static Home newInstance() {
       Home fragment = new Home();
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

       View myFragmentView = inflater.inflate(R.layout.fragment_zap, container, false);
       ((PulsatorLayout)myFragmentView.findViewById(R.id.pulsator)).start();
       myFragmentView.findViewById(R.id.camera_button).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {startActivity(new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), QrScanner.class));
           }
       });


       return myFragmentView;
   }

}
