package com.example.ricca.zap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ricca.zap.DAO.Contenuto;
import com.example.ricca.zap.DAO.ListaElementi;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;
import java.util.Objects;
import java.util.Vector;

import static android.provider.AlarmClock.EXTRA_MESSAGE;


public class ArtWorkActivity extends AppCompatActivity
{
    private Context context=this;
    private FirebaseStorage riferimentoS = null;   //riferimento allo storage
    private FirebaseDatabase riferimentoDB = null; //riferimento al real time database
    private LinearLayout wall = null;
    private ProgressDialog loading = null;
    private TextView title=null;
    private CircularImageView miniatura=null;

    //////////////////////////////////////////////////////////////////////////////////////////
    private String nome;
    private String link_miniatura;
    ListaElementi cronologia;
    ListaElementi preferiti;


    //////////////////////////////////////////////////////////////////////////////////////////
    public void fillWall(final String opera) {
        loading = ProgressDialog.show(ArtWorkActivity.this, "",
                "Loading Content, Please Wait", true);

                riferimentoDB.getReference().child(opera).addListenerForSingleValueEvent
                        (
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                        Vector contenuti = new Vector();
                                        int i = 0;
                                        Contenuto temp;
                                        TextView temptext;
                                        View tempview;

                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                            if(Objects.equals(ds.getKey(), "nome"))
                                            {
                                               TextView t=findViewById(R.id.artwork_title);
                                               t.setText(ds.getValue(String.class));
                                               nome=ds.getValue(String.class);
                                            }
                                            else if(Objects.equals(ds.getKey(), "miniatura"))
                                            {
                                                Glide.with(context).load(ds.getValue(String.class)).into(miniatura);
                                                link_miniatura=ds.getValue(String.class);
                                            }
                                            else
                                            {
                                                temp = new Contenuto();
                                                temp.setTipo(ds.child("tipo").getValue(String.class));
                                                temp.setTitolo(ds.child("titolo").getValue(String.class));
                                                temp.setValore(ds.child("valore").getValue(String.class));
                                                contenuti.add(temp);
                                                Log.d("contenuto" + i, "aggiunto");
                                                i++;
                                            }
                                        }

                                        for (i = 0; i < contenuti.size(); i++) {
                                            temp = (Contenuto) contenuti.get(i);
                                            switch (temp.getTipo()) {
                                                case "foto":
                                                    imageTask(temp.getValore(), temp.getTitolo());
                                                    break;

                                                case "testo":
                                                    tempview = addTesto();
                                                    temptext = tempview.findViewById(R.id.contenutoTesto);
                                                    temptext.setText(temp.getValore());
                                                    break;

                                                case "brano":
                                                    musicTask(temp.getValore(), temp.getTitolo());
                                                    break;

                                                case "video":
                                                    break;

                                                case "titolo":
                                                    title.setText(temp.getValore());
                                                    //setta titolo pagina
                                                    break;
                                            }
                                        }

                                        loading.dismiss();

                                        cronologia.add(nome,opera,link_miniatura);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                }
                        );

            }

    ///////////////////////////////////////////////////////////////////////////////////////////
    //aggiunge un elemento brano alla pagina e richiama streamingmusic per inizializzarlo

    private void musicTask(final String riferimento, String titolo) {
        final View vbrano = LayoutInflater.from(ArtWorkActivity.this).inflate(R.layout.sample_vista_brano, null);
        wall.addView(vbrano);
        TextView temptext = vbrano.findViewById(R.id.titoloBrano);
        temptext.setText(titolo);
        StorageReference ref = riferimentoS.getReference().child(riferimento);

        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                streamingMusic((SeekBar) vbrano.findViewById(R.id.barra), (Button) vbrano.findViewById(R.id.pausebtn), uri.toString());
            }
        });


    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    //aggiunge un elemento immagine alla pagina e la inizializza

    private void imageTask(String riferimento, String titolo) {
        View vfoto = LayoutInflater.from(ArtWorkActivity.this).inflate(R.layout.sample_vista_foto, null);
        wall.addView(vfoto);
        TextView temptext = vfoto.findViewById(R.id.titoloFoto);
        temptext.setText(titolo);
        StorageReference fotoref = riferimentoS.getReference().child(riferimento);
        Glide.with(this).using(new FirebaseImageLoader()).load(fotoref).into((ImageView) vfoto.findViewById(R.id.immagine));
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //aggiunge un elemento testo alla pagina e lo restituisce

    private View addTesto() {
        View vtesto = LayoutInflater.from(ArtWorkActivity.this).inflate(R.layout.sample_vista_testo, null);
        wall.addView(vtesto);
        return vtesto;
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //inizializza un riproduttore musicale in streaming

    private void streamingMusic(final SeekBar bar, final Button playPause, String url) {
        final MediaPlayer player = new MediaPlayer();                 //inizializza nuovo player
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);       //impostalo

        try {
            player.setDataSource(url);
        }                            //setta il file audio
        catch (IOException e) {
            e.printStackTrace();
        }


        //////////////////////////////////////////////imposta seekbar

        final Handler barHandler = new Handler();
        final Runnable updateBar = new Runnable() {
            @Override
            public void run() {
                //setta il runnable di aggiornamento della barra
                bar.setProgress(player.getCurrentPosition());
                barHandler.postDelayed(this, 1000);
            }
        };


        //////////////////////////////////////////////////////// prepara il player alla riproduzione
        new Thread() {
            @Override
            public void run() {
                try {
                    player.prepare();
                    bar.setMax(player.getDuration());
                    bar.setProgress(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { //listener per la barra
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {//imposta il cambiamento di tempo in base al trascinamento della barra
                        if (b) player.seekTo(i);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

                playPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {     //imposta comandi bottone
                        if (player.isPlaying()) {
                            player.pause();
                            barHandler.removeCallbacks(updateBar);
                            playPause.setBackgroundResource(R.drawable.ic_play_circle_outline_black_36dp);
                        } else {
                            player.start();
                            playPause.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_36dp);
                            barHandler.postDelayed(updateBar, 0);
                        }
                    }
                });
            }
        }.start();
    }

    /////////////////////////////////////////////////////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setContentView(R.layout.activity_art_work);


        findViewById(R.id.back_artwork).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArtWorkActivity.this.finish();
            }
        });


        riferimentoS = FirebaseStorage.getInstance();         //inizializza riferimenti
        riferimentoDB = FirebaseDatabase.getInstance();
        wall = findViewById(R.id.bacheca);
        title= findViewById(R.id.artwork_title);
        miniatura=findViewById(R.id.miniatura);

        Intent intent = getIntent();
        final String opera = intent.getStringExtra(EXTRA_MESSAGE); //prende in input la stringa riferimento dell'opera

        cronologia = new ListaElementi(context.getApplicationContext(),"cronologia.txt");
        fillWall(opera);      //crea pagina

        preferiti = new ListaElementi(this,"preferiti.txt");
        //SETTA IMAGINE BOTTONE PREFERITI
        if(preferiti.isPresent(opera)) findViewById(R.id.bookmark_artwork).setBackgroundResource(R.drawable.ic_bookmark_white_36dp);
        else findViewById(R.id.bookmark_artwork).setBackgroundResource(R.drawable.ic_bookmark_border_white_36dp);



        (findViewById(R.id.bookmark_artwork)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cosa accade quando premi il bottone

                if(preferiti.isPresent(opera)) {
                    preferiti.remove(opera);
                    findViewById(R.id.bookmark_artwork).setBackgroundResource(R.drawable.ic_bookmark_border_white_36dp);
                }
                else
                {preferiti.add(nome,opera,link_miniatura);
                    findViewById(R.id.bookmark_artwork).setBackgroundResource(R.drawable.ic_bookmark_white_36dp);
                }
            }
        });
    }

}
