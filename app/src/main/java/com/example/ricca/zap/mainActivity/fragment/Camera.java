package com.example.ricca.zap.mainActivity.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.renderscript.RenderScript;

import com.bumptech.glide.Glide;
import com.example.ricca.zap.ArtWorkActivity;
import com.example.ricca.zap.R;
import com.example.ricca.zap.TFMobile.Classifier;
import com.example.ricca.zap.TFMobile.TensorFlowImageClassifier;
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

import java.io.IOException;
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

public class Camera extends Fragment {

    private Fotoapparat fotoapparat;
    private Context context;
    private Activity activity;
    private DialogPlus currDialog=null;
    private Classifier classifier=null;
    private List<Classifier.Recognition> results;
    private boolean isFounded=false;
    private boolean isActive=false;
    private DataSnapshot ds=null;


    public static Camera newInstance() {
        Camera fragment = new Camera();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    private void showDialog(final String ref)
    {
        Log.v("Found artwork",ref);
        isFounded=true;

        ViewHolder holder=new ViewHolder(R.layout.sample_recognition);
        final DialogPlus dialog= DialogPlus.newDialog(context)
                .setGravity(Gravity.CENTER)
                .setCancelable(true)
                .setContentHolder(holder)
                .setContentBackgroundResource(Color.TRANSPARENT)
                .setOverlayBackgroundResource(Color.TRANSPARENT)
                .setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogPlus dialog) {
                        isFounded=false;
                        currDialog=null;
                    }
                })
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        isFounded=false;
                        currDialog=null;
                    }
                })
                .create();
        currDialog=dialog;
        final CircularImageView copertina= holder.getInflatedView().findViewById(R.id.copertina);
        final TextView titolo=holder.getInflatedView().findViewById(R.id.element_name);


        copertina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityOptionsCompat transition= ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                Pair.create((View)copertina,"miniatura"),
                Pair.create((View)titolo,"titolo"));

                Intent start=new Intent(context, ArtWorkActivity.class);
                start.putExtra(EXTRA_MESSAGE,ref);
                startActivity(start,transition.toBundle());
            }
        });

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


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && fotoapparat!=null)fotoapparat.start();
        else if(fotoapparat!=null) fotoapparat.stop();
        if(currDialog!=null)currDialog.dismiss();
       if(isActive)
       {getView().findViewById(R.id.clicca).setVisibility(View.VISIBLE);
       getView().findViewById(R.id.activeCamera).setVisibility(View.VISIBLE);
       ((PulsatorLayout)getView().findViewById(R.id.pulsator)).start();
       isActive=false;}
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
        this.activity=getActivity();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_scanner, container, false);

        final PulsatorLayout pulsator=v.findViewById(R.id.pulsator);
        pulsator.start();
        ///////////////////////////////////////FIREBASE
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
        try {
            classifier= TensorFlowImageClassifier.create(context.getAssets(),
                    "file:///android_asset/graph.pb",
                    "file:///android_asset/labels.txt",
                    224,224,128,128f,
                    "input","final_result");
        } catch (IOException e) {
            Log.e("Main Activity", Objects.requireNonNull(e.getMessage()));
        }

        /////////////////////////////////////////////FOTOCAMERA

        final View clicca=v.findViewById(R.id.clicca);
        CameraView cameraView = v.findViewById(R.id.camera);
        v.findViewById(R.id.activeCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isActive=true;
                v.setVisibility(View.GONE);
                clicca.setVisibility(View.GONE);
                pulsator.stop();
            }
        });
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
                        if(isActive && !isFounded && ds!=null) {//se non è già stato trovato un possibile risultato
                            //analizzo il frame
                            results = classifier.recognizeImage(Nv21Image.nv21ToBitmap(rs, frame.getImage(), frame.getSize().width, frame.getSize().height));

                            if( //se il risultato:
                                    results!=null && //non è null
                                    results.size()>0 && // ha almeno una voce
                                    results.get(0).getConfidence()>0.8 && // la precisione è sopra l'80%
                                    ds.hasChild(Objects.requireNonNull(results.get(0).getTitle())) //esiste nel db
                            )
                            showDialog(results.get(0).getTitle());//mostra il dialog

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


}

