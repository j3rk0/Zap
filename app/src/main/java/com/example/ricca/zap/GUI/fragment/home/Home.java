package com.example.ricca.zap.GUI.fragment.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.ricca.zap.Data.ListaMusei;
import com.example.ricca.zap.Data.MuseoRef;
import com.example.ricca.zap.GUI.adapter.MusemAdapter;
import com.example.ricca.zap.MuseumActivity;
import com.example.ricca.zap.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.Objects;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class Home extends Fragment {

    private Context context;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("museums");

    private ListaMusei listaMusei = new ListaMusei();

    private SearchView searchView;
    private ListView listView;
    private ImageView imageView;
    private View cardMuseum;
    private View line;
    private View continua;
    private MusemAdapter musemAdapter;
    private View underline;
    private TextView mock_hint;



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

    private void openMuseum(String ref)
    {
        Intent start=new Intent(context, MuseumActivity.class);
        start.putExtra(EXTRA_MESSAGE,ref);
        startActivity(start);
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v= inflater.inflate(R.layout.fragment_home,container,false);

        //shared preferences
        final SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        //binding interfaccia
        underline=v.findViewById(R.id.mock_underline);
        mock_hint=v.findViewById(R.id.mock_hint);
        searchView = v.findViewById(R.id.searchView);
        listView = v.findViewById(R.id.listMusem);
        imageView = v.findViewById(R.id.museumLogo);
        line = v.findViewById(R.id.line);
        cardMuseum = v.findViewById(R.id.cardMuseum);
        continua=v.findViewById(R.id.continua_text);
        final CircularImageView cover = cardMuseum.findViewById(R.id.copertina_museo);
        final TextView textMuseum = cardMuseum.findViewById(R.id.museum_name);
        final TextView descMuseum = cardMuseum.findViewById(R.id.museum_desc);


        /////////////////////////INIZIALIZZO LA CARD////////////////////////////////////

        if(sharedPreferences.getString("key","0").equals("0"))
        {//se è la prima volta che si apre l'app

            textMuseum.setText("Seleziona un museo");
            descMuseum.setText("per incominciare la tua visita");
            cover.setImageResource(R.drawable.ic_placeholder);
            continua.setVisibility(View.GONE);

        }else{
            textMuseum.setText("Loading...");
            cardMuseum.setOnClickListener(new View.OnClickListener() {  //se è la prima volta che apri l'app
                @Override
                public void onClick(View v) {  //onclick
                    setVisibilyListOn();
                    searchView.onActionViewExpanded(); //apri la lista
                }
            });
        }

        ////////////////////////LISTENER DATABASE///////////////////////////////////////
        mDatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                 MuseoRef museoRef;
                 //////////////////DOWNLOAD MUSEI/////////////////////////////////////
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    museoRef = new MuseoRef();
                    museoRef.set(
                            ds.child("nome").getValue(String.class),
                            "/museums/"+ds.getKey(),
                            ds.child("cover").getValue(String.class),
                            ds.child("descrizione").getValue(String.class));

                    listaMusei.add(museoRef);
                }
                musemAdapter = new MusemAdapter(context, listaMusei);


               /////////////////////////ELEMENTI LISTA////////////////////////////////////
               listView.setAdapter(musemAdapter);

               listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
               {
                   @Override
                   public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                   {
                       String value =  ((MuseoRef) parent.getItemAtPosition(position)).getPath();
                       MuseoRef museoRef1 =  (listaMusei.find(value));
                       if (!TextUtils.isEmpty(museoRef1.getPath()))
                       {
                           editor.putString("key", museoRef1.getPath());
                           editor.apply();

                           museoRef1 = listaMusei.find(sharedPreferences.getString("key", "0"));
                           Glide.with(context).load(museoRef1.getCover()).into(cover);
                           textMuseum.setText(museoRef1.getNome());
                           descMuseum.setText(museoRef1.getDesc());

                           openMuseum(museoRef1.getPath());
                       }
                   }
               });


               searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
               {
                   @Override
                   public boolean onQueryTextSubmit(String query) {
                       return false;
                   }

                   @Override
                   public boolean onQueryTextChange(String newText) {
                       setVisibilyListOn();
                       musemAdapter.getFilter().filter(newText);
                       return false;
                   }
               });

                ///////////////////////CARD CONTINUA VISITA////////////////////////////////////////
                if (!sharedPreferences.getString("key", "0").equals("0")) //se non è la prima volta che si apre l'app
                {

                    museoRef = listaMusei.find(sharedPreferences.getString("key", "0")); //riempi card
                    Glide.with(context).load(museoRef.getCover()).into(cover);
                    textMuseum.setText(museoRef.getNome());
                    descMuseum.setText(museoRef.getDesc());
                    cardMuseum.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            //non so perchè ma se si toglie questa istruzione se si continua la visita all'avvio dell'app non si carica il museo
                            listView.setVisibility(View.INVISIBLE);

                            openMuseum(sharedPreferences.getString("key","0")); //onclick apri l'ultimo museo
                        }
                    });

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        ////////////////////////////BARRA DI RICERCA/////////////////////////
        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setVisibilyListOn();
            }
        });

        mock_hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibilyListOn();
                searchView.onActionViewExpanded();
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibilyListOn();
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibilyListOn();
            }
        });

        return v;
    }


    @Override
    public void onResume() { //qundo la home riprende ci assicuriamo di nascondere la lista
        super.onResume();
        setVisibilyListOff();
    }

    public boolean isListOpen() //la lista dei musei è aperta?
    {
        return listView.getVisibility()==View.VISIBLE;
    }

    public void setVisibilyListOff (){  //nasconde la lista dei musei
        listView.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
        cardMuseum.setVisibility(View.VISIBLE);
        line.setVisibility(View.VISIBLE);
        continua.setVisibility(View.VISIBLE);
        mock_hint.setVisibility(View.VISIBLE);
        underline.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.INVISIBLE);
    }

    private void setVisibilyListOn (){  //mostra la lista dei musei
        listView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
        cardMuseum.setVisibility(View.GONE);
        line.setVisibility(View.GONE);
        continua.setVisibility(View.GONE);
        mock_hint.setVisibility(View.GONE);
        underline.setVisibility(View.GONE);
        searchView.setVisibility(View.VISIBLE);
    }

}
