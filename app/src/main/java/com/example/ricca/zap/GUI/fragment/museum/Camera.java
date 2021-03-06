package com.example.ricca.zap.GUI.fragment.museum;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.renderscript.RenderScript;

import com.bumptech.glide.Glide;
import com.example.ricca.zap.ArtWorkActivity;
import com.example.ricca.zap.Data.InferenceResult;
import com.example.ricca.zap.Data.MuseumMetaData;
import com.example.ricca.zap.Data.MuseumMetaDataWaiter;
import com.example.ricca.zap.R;
import com.example.ricca.zap.Services.TFLiteInterpreter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.ViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.preview.Frame;
import io.fotoapparat.preview.FrameProcessor;
import io.fotoapparat.selector.LensPositionSelectorsKt;
import io.fotoapparat.selector.ResolutionSelectorsKt;
import io.fotoapparat.view.CameraView;
import io.github.silvaren.easyrs.tools.Nv21Image;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class Camera extends Fragment implements MuseumMetaDataWaiter {

    private Fotoapparat fotoapparat;
    private Context context;
    private Activity activity;
    private DialogPlus currDialog=null;
    private TFLiteInterpreter classifier=null;
    private List<InferenceResult> inferenceResults;
    private boolean isFounded=false;
    private boolean isActive=false;
    private DataSnapshot ds=null;
    private View to_hide=null;
    private View v=null;

    public static Camera newInstance() {
        Camera fragment = new Camera();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //viene chiamato quando viene trovata un opera
    private void showDialog(final String ref)
    {
        Log.v("Found artwork",ref);
        //blocca il riconoscitore
        isFounded=true;

        //creazione del dialog
        ViewHolder holder=new ViewHolder(R.layout.dialog_recognition);
        final DialogPlus dialog= DialogPlus.newDialog(context)
                .setGravity(Gravity.CENTER)
                .setCancelable(true)
                .setContentHolder(holder)
                .setContentBackgroundResource(Color.TRANSPARENT)
                .setOverlayBackgroundResource(Color.TRANSPARENT)
                .setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogPlus dialog) {//se il dialog viene dismesso attiva il riconoscitore
                        isFounded=false;
                        currDialog=null;
                    }
                })
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {//se viene premuto indietro riattiva il riconoscitore
                        isFounded=false;
                        currDialog=null;
                    }
                })
                .create();
        currDialog=dialog;


        final CircularImageView copertina= holder.getInflatedView().findViewById(R.id.copertina);
        final TextView titolo=holder.getInflatedView().findViewById(R.id.element_name);

        //se si clicca la copertina apre artworkactivity
        copertina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //passa ad artworkactivity il riferimento all'opera
                Intent start=new Intent(context, ArtWorkActivity.class);
                start.putExtra(EXTRA_MESSAGE,ref);
                startActivity(start);
            }
        });

        //imposta la grafica del dialog
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                titolo.setText(ds.child(ref+"/nome").getValue(String.class));
                Glide.with(context).load(ds.child(ref+"/miniatura").getValue(String.class)).into(copertina);
                copertina.setScaleType(ImageView.ScaleType.CENTER_CROP);
                dialog.show();
            }
        });
    }


    //viene chiamato quando si cambia fragment
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        //fa partire la camera
        if(isVisibleToUser && fotoapparat!=null)fotoapparat.start();
        //se il fragment non è visibile blocca la camera
        else if(fotoapparat!=null) fotoapparat.stop();
        //se il fragment non è visibile nasconde i dialog
        if(currDialog!=null)currDialog.dismiss();
        //se la camera era attiva la blocca e mostra il bottone per attivarla
        if(isActive)
        {
            //mostra il bottone con un animazione di fadein
            to_hide.animate()
                    .alpha(1.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            to_hide.setVisibility(View.VISIBLE);
                        }
                    });
            //fa partire l'animazione
            ((PulsatorLayout) Objects.requireNonNull(getView()).findViewById(R.id.pulsator)).start();
            //attiva camera
            isActive=false;
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
        this.activity=getActivity();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        v = inflater.inflate(R.layout.fragment_camera, container, false);

        //fa partire l'animazione
        ((PulsatorLayout)v.findViewById(R.id.pulsator)).start();
        ///////////////////////////////////////FIREBASE

        //inizializza database
        FirebaseApp.initializeApp(context);
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ds=dataSnapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ///////////////////////////////////////////// AI

        //creazione classificatore
        final RenderScript rs=RenderScript.create(context);




        to_hide=v.findViewById(R.id.to_hide);

        //quando si preme il bottone attiva la camera e lo nasconde con un fade
        v.findViewById(R.id.activeCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //attiva camera
                isActive=true;
                //animazione scomprsa
                to_hide.animate()
                        .alpha(0.0f)
                        .setDuration(500)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                to_hide.setVisibility(View.GONE);
                            }
                        });
            }
        });

        CameraView cameraView = v.findViewById(R.id.camera);
        fotoapparat=Fotoapparat
                .with(cameraView.getContext())
                .into(cameraView)           // view which will draw the camera preview
                .previewScaleType(ScaleType.CenterCrop)  // we want the preview to fill the view
                .photoResolution(ResolutionSelectorsKt.highestResolution())   // we want to have the biggest photo possible
                .lensPosition(LensPositionSelectorsKt.back())       // we want back camera
                .frameProcessor(new FrameProcessor()
                {          //creazione processore dei frame
                    @Override
                    public void process(@NotNull Frame frame)
                    {
                        if(isActive && !isFounded && ds!=null && classifier!=null) {//se non è già stato trovato un possibile risultato
                            //analizzo il frame
                            classifier.runInference(Nv21Image.nv21ToBitmap(rs, frame.getImage(), frame.getSize().width, frame.getSize().height));
                            inferenceResults = classifier.getResult();

                            if(inferenceResults!=null && inferenceResults.size()>0)
                                Log.v("RESULT",inferenceResults.get(0).getTitle()+" "+inferenceResults.get(0).getConfidence());
                            if( //se il risultato:
                                    inferenceResults !=null && //non è null
                                    inferenceResults.size()>0 && // ha almeno una voce
                                    ds.hasChild(Objects.requireNonNull(inferenceResults.get(0).getTitle())) && //esiste nel db
                                    !inferenceResults.get(0).getTitle().equals("noise") &&
                                    inferenceResults.get(0).getConfidence() >= 0.95
                            )
                                showDialog(inferenceResults.get(0).getTitle());//mostra il dialog

                        }
                    }
                })
                .build();
        fotoapparat.start();
        return v;

    }

    ////////////////////// ciclo di vita dell'activity
    @Override
    public void onResume() {
        super.onResume();
        fotoapparat.start();
    }

    @Override
    public void onPause() {
        fotoapparat.stop();
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        fotoapparat.start();
    }

    @Override
    public void onStop()  {
        super.onStop();
        fotoapparat.stop();
    }


    //acquire metadata
    @Override
    public void init(MuseumMetaData metaData) {

        Log.v("MUSEUM METADATA","INIT CAMERA");
        classifier= new TFLiteInterpreter(metaData.getNomeModello(),metaData.getLabels());

        Glide.with(context).load(metaData.getCopertina()).into((CircularImageView)v.findViewById(R.id.copertina_museo_camera));
        ((TextView)v.findViewById(R.id.nome_museo_camera)).setText(metaData.getNome());

    }
}

