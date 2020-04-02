package com.example.ricca.zap.GUI.fragment.home;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ricca.zap.ArtWorkActivity;
import com.example.ricca.zap.Data.ListaMusei;
import com.example.ricca.zap.Data.MuseoRef;
import com.example.ricca.zap.GUI.adapter.MusemAdapter;
import com.example.ricca.zap.HomeActivity;
import com.example.ricca.zap.MuseumActivity;
import com.example.ricca.zap.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class Home extends Fragment {

    private Context context;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("museums");
    private SearchView searchView;
    private ListaMusei listaMusei = new ListaMusei();
    private ArrayList<String> listaNomi = new ArrayList<String>();

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v= inflater.inflate(R.layout.fragment_home,container,false);
        searchView = v.findViewById(R.id.searchView);

        final ListView listView = v.findViewById(R.id.listMusem);
        final ImageView imageView = v.findViewById(R.id.museumLogo);
        final View line = v.findViewById(R.id.line);
        final SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final View cardMuseum = v.findViewById(R.id.cardMuseum);
        final CircularImageView cover = (CircularImageView)cardMuseum.findViewById(R.id.copertina);
        final TextView textMuseum = cardMuseum.findViewById(R.id.nome);
        final TextView descMuseum = cardMuseum.findViewById(R.id.desc);
        final View buttonMuseum = cardMuseum.findViewById(R.id.remove_button);
        descMuseum.setText("Continua il tour");
        buttonMuseum.setVisibility(View.GONE);

        setVisibilyListOff(listView, imageView, line, cardMuseum);
        //searchView.setIconifiedByDefault(true);
        searchView.setFocusable(false);
        searchView.setIconified(false);
        searchView.clearFocus();

        ////////////////////////LISTENER DATABASE///////////////////////////////////////
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 MuseoRef museoRef;
                 //////////////////DOWNLOAD MUSEI/////////////////////////////////////
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    museoRef = new MuseoRef();
                    museoRef.set(ds.child("nome").getValue(String.class), ds.getRef().toString(), ds.child("cover").getValue(String.class));
                    listaMusei.add(museoRef);
                    listaNomi.add(museoRef.getNome());
                }

                final MusemAdapter musemAdapter = new MusemAdapter(context, listaMusei, listView);

                if (!sharedPreferences.getString("key", "0").equals("0")) {
                    museoRef = listaMusei.find(sharedPreferences.getString("key", "0"));
                    Glide.with(context).load(museoRef.getCover()).into((CircularImageView) cover);
                    textMuseum.setText(museoRef.getNome());
                }

               listView.setAdapter(musemAdapter);

               listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                   @Override
                   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                       String value =  ((MuseoRef) parent.getItemAtPosition(position)).getNome();
                       MuseoRef museoRef1 =  (listaMusei.find(value));
                       if (!TextUtils.isEmpty(museoRef1.getPath())) {
                           editor.putString("key", museoRef1.getNome());
                           editor.commit();
                           setVisibilyListOff(listView, imageView, line, cardMuseum);
                           Toast.makeText(context, sharedPreferences.getString("key", "0"), Toast.LENGTH_SHORT).show();
                           Glide.with(context).load(museoRef1.getCover()).into((CircularImageView) cover);
                           textMuseum.setText(museoRef1.getNome());

                       }
                   }
               });

               searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                   @Override
                   public void onFocusChange(View v, boolean hasFocus) {
                       setVisibilyListOn(listView, imageView, line, cardMuseum);
                   }
               });
               searchView.setOnSearchClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       setVisibilyListOn(listView, imageView, line, cardMuseum);
                   }
               });
               searchView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       setVisibilyListOn(listView, imageView, line, cardMuseum);
                   }
               });
               searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                   @Override
                   public boolean onQueryTextSubmit(String query) {
                       return false;
                   }

                   @Override
                   public boolean onQueryTextChange(String newText) {
                       setVisibilyListOn(listView, imageView, line, cardMuseum);
                       musemAdapter.getFilter().filter(newText);
                       return false;
                   }
               });

               searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                   @Override
                   public boolean onClose() {
                       searchView.setIconified(false);
                       setVisibilyListOff(listView, imageView, line, cardMuseum);
                       return false;
                   }
               });

               cardMuseum.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       if (!sharedPreferences.getString("key", "0").equals("0"))
                       openMuseum(new MuseoRef("",sharedPreferences.getString("key", "0")));
                       else {
                           searchView.requestFocus();
                           setVisibilyListOn(listView, imageView, line, cardMuseum);
                           ((InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE)).
                                   toggleSoftInput(InputMethodManager.SHOW_FORCED,
                                           InputMethodManager.HIDE_IMPLICIT_ONLY);
                       }
                   }
               });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        return v;
    }

    final private void setVisibilyListOff (ListView listView, ImageView image, View textView, View card){
        listView.setVisibility(View.GONE);
        image.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
        card.setVisibility(View.VISIBLE);
    }

    final private void setVisibilyListOn (ListView listView, ImageView image, View textView, View card){
        listView.setVisibility(View.VISIBLE);
        image.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
        card.setVisibility(View.GONE);

    }

}
