package com.example.ricca.zap.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ricca.zap.DAO.ListaElementi;
import com.example.ricca.zap.QrScanner;
import com.example.ricca.zap.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.Objects;

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
       CircularImageView mini = myFragmentView.findViewById(R.id.miniatura);

       (mini).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {startActivity(new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), QrScanner.class));
           }
       });
       ListaElementi lista=new ListaElementi(myFragmentView.getContext(),"cronologia.txt");
       String tocomplete="";
       for (int i=0;i<lista.size();i++)tocomplete.concat(lista.get(i).getNome()+"\n");

       ((TextView)myFragmentView.findViewById(R.id.prova)).setText(tocomplete);

       return myFragmentView;
   }

}