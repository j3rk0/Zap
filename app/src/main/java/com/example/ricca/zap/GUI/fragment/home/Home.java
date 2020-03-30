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
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class Home extends Fragment {

    private Context context;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("museums");
    private SearchView searchView;
    private ListaMusei listaMusei = new ListaMusei();
    private ArrayList<String> listaNomi = new ArrayList<String>();
    private SearchView.OnQueryTextListener queryTextListener;

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
        final Button button = v.findViewById(R.id.goto_museum);
        final TextView textView = v.findViewById(R.id.textViewIntro);
        setVisibilyListOff(listView, imageView, button, textView);

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
                    museoRef.set(ds.child("nome").getValue(String.class), ds.getRef().toString());
                    listaMusei.add(museoRef);
                    listaNomi.add(museoRef.getNome());
                }

                final SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = sharedPreferences.edit();

                //String ref = sharedPreferences.getString("key", "seleziona museo");
                //final MusemAdapter musemAdapter = new MusemAdapter(context, listaNomi, listView);


                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.vista_museo, R.id.textViewMuseo, listaNomi);
               listView.setAdapter(arrayAdapter);

               listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                   @Override
                   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                       String value = (String)parent.getItemAtPosition(position);
                       MuseoRef museoRef1 =  (listaMusei.find(value, context));
                       if (!TextUtils.isEmpty(museoRef1.getPath())) {
                           editor.putString("key", museoRef1.getPath());
                           editor.commit();
                           button.setText(museoRef1.getNome());
                           setVisibilyListOff(listView, imageView, button, textView);
                       }
                   }
               });

               searchView.setOnSearchClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       setVisibilyListOn(listView, imageView, button, textView);
                   }
               });
               searchView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       setVisibilyListOn(listView, imageView, button, textView);
                   }
               });
               searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                   @Override
                   public boolean onQueryTextSubmit(String query) {
                       return false;
                   }

                   @Override
                   public boolean onQueryTextChange(String newText) {
                       setVisibilyListOn(listView, imageView, button, textView);
                       arrayAdapter.getFilter().filter(newText);
                       return false;
                   }
               });
               searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                   @Override
                   public boolean onClose() {
                       setVisibilyListOff(listView, imageView, button, textView);
                       return false;
                   }
               });

               button.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       if (!sharedPreferences.getString("key", "0").equals("0"))
                       openMuseum(new MuseoRef("",sharedPreferences.getString("key", "0")));
                   }
               });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        return v;
    }

    private void setVisibilyListOff (ListView listView, ImageView image, Button button, TextView textView){
        listView.setVisibility(View.GONE);
        image.setVisibility(searchView.VISIBLE);
        button.setVisibility(searchView.VISIBLE);
        textView.setVisibility(searchView.VISIBLE);
    }

    private void setVisibilyListOn (ListView listView, ImageView image, Button button, TextView textView){
        listView.setVisibility(View.VISIBLE);
        image.setVisibility(searchView.GONE);
        button.setVisibility(searchView.GONE);
        textView.setVisibility(searchView.GONE);

    }

}
