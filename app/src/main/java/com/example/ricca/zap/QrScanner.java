package com.example.ricca.zap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.List;
import java.util.Objects;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.preview.Frame;
import io.fotoapparat.preview.FrameProcessor;
import io.fotoapparat.selector.LensPositionSelectorsKt;
import io.fotoapparat.selector.ResolutionSelectorsKt;
import io.fotoapparat.view.CameraView;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class QrScanner extends AppCompatActivity {

    private Activity activity=this;
    private Fotoapparat fotoapparat;
    private FirebaseVisionBarcodeDetector detector;
    private DataSnapshot ds=null;
    private boolean found=false;


    private void query()
    {
       FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               ds=dataSnapshot;
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {
               Toast.makeText(activity, databaseError.getMessage(),
                       Toast.LENGTH_LONG).show();

           }
       });
    }

    private  void initScanner()
    {
        //impostiamo il formato dello scanner
        FirebaseVisionBarcodeDetectorOptions barcode_options=new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE)
                .build();
        detector = FirebaseVision.getInstance()  //inizializza il detector
                .getVisionBarcodeDetector(barcode_options);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private FirebaseVisionImage convertImage(Frame frame) throws CameraAccessException //converte i frame in un formato utilizzabile da firebase
    {
        FirebaseVisionImageMetadata metadata = new FirebaseVisionImageMetadata.Builder()
                .setWidth(frame.component1().width)
                .setHeight(frame.component1().height)
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .setRotation(getRotationCompensation(getCameraId(),QrScanner.this,this))
                .build();
        return FirebaseVisionImage.fromByteArray(frame.component2(), metadata);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scanQr(Frame frame) throws CameraAccessException {

        FirebaseVisionImage image=convertImage(frame);    //converti il frame

        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)               //inizializza il resultSet
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {

                    //è stato trovato un qr, corrisponde ad un record??
                    @Override
                    public void onSuccess(final List<FirebaseVisionBarcode> barcodes) {  //in caso vi sia un qr

                        if(barcodes.size()>0 && ds.hasChild(Objects.requireNonNull(barcodes.get(0).getRawValue()))&& !found)
                        {

                            found = true;

                            ViewHolder VH=new ViewHolder(R.layout.raw_element);
                            VH.setBackgroundResource(Color.TRANSPARENT);
                            final DialogPlus dialog=DialogPlus.newDialog(activity)
                                    .setGravity(Gravity.CENTER)
                                    .setContentHolder(VH)
                                    .setCancelable(true)
                                    .setOnDismissListener(new OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogPlus dialog) {
                                            found=false;
                                        }
                                    }).setOnBackPressListener(new OnBackPressListener() {
                                        @Override
                                        public void onBackPressed(DialogPlus dialogPlus) {
                                            found=false;
                                        }
                                    }).setContentBackgroundResource(Color.TRANSPARENT)
                                    .setOverlayBackgroundResource(Color.TRANSPARENT)
                                    .create();

                                    VH.getInflatedView().findViewById(R.id.copertina).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            final View miniatura=findViewById(R.id.copertina);
                                            final View titolo=findViewById(R.id.element_name);
                                            ActivityOptionsCompat transition=ActivityOptionsCompat.makeSceneTransitionAnimation
                                                    (activity, Pair.create(miniatura,"miniatura"),Pair.create(titolo,"titolo"));

                                            Intent start=new Intent(QrScanner.this,ArtWorkActivity.class);
                                            start.putExtra(EXTRA_MESSAGE,barcodes.get(0).getRawValue());
                                            //fotoapparat.stop();
                                            ViewGroup vg=(ViewGroup) (findViewById(R.id.camera));
                                            vg.removeView(findViewById(R.id.camera));


                                            startActivity(start,transition.toBundle());
                                            dialog.dismiss();
                                        }
                                    });

                            View V=dialog.getHolderView();


                            TextView t=V.findViewById(R.id.element_name);
                            ImageView i=V.findViewById(R.id.copertina);
                            try {
                                t.setText(ds.child(barcodes.get(0).getRawValue()+"/nome").getValue(String.class));
                                Glide.with(activity).load(ds.child(barcodes.get(0).getRawValue()+"/miniatura").getValue(String.class)).into(i);
                                i.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            }catch (NullPointerException e)
                            {
                                t.setText("Error!");
                            }

                            dialog.show();
                        }
                    }
                });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_qr_scanner);
        askCameraPermission();
        FirebaseApp.initializeApp(this);


        query();

        CameraView cameraView=(CameraView) findViewById(R.id.camera);

        initScanner();


        //creamo il frameprocessor
        FrameProcessor processor=new FrameProcessor()
        {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void process(Frame frame)
            {
                if(!found && ds!=null)
                try {scanQr(frame);}
                catch (CameraAccessException e) {Log.e("frame processor error ",e.getMessage());}
            }
        };

        //inizializzazione fotocamera
        fotoapparat=Fotoapparat
                .with(cameraView.getContext())
                .into(cameraView)           // view which will draw the camera preview
                .previewScaleType(ScaleType.CenterCrop)  // we want the preview to fill the view
                .photoResolution(ResolutionSelectorsKt.highestResolution())   // we want to have the biggest photo possible
                .lensPosition(LensPositionSelectorsKt.back())       // we want back camera
                .frameProcessor(processor)   // receives each frame from preview stream
                .build();
        fotoapparat.start();
    }

    //definizione del ciclo di vita dell'app
    @Override
    protected void onResume() {
        super.onResume();
        fotoapparat.start();
    }

    @Override
    protected void onPause() {
        fotoapparat.stop();
        super.onPause();
    }

    @Override protected void onStart() {
        super.onStart();
        fotoapparat.start();
    }

    @Override protected void onStop()  {
        super.onStop();
        fotoapparat.stop();
    }



    //things that i've no idea how they works
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray(); //copiata da firebase
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int getRotationCompensation(String cameraId, Activity activity, Context context) //copiata da firebase
            throws CameraAccessException {
        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
        int deviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int rotationCompensation = ORIENTATIONS.get(deviceRotation);

        // On most devices, the sensor orientation is 90 degrees, but for some
        // devices it is 270 degrees. For devices with a sensor orientation of
        // 270, rotate the image an additional 180 ((270 + 270) % 360) degrees.
        CameraManager cameraManager = (CameraManager) context.getSystemService(CAMERA_SERVICE);
        int sensorOrientation = cameraManager
                .getCameraCharacteristics(cameraId)
                .get(CameraCharacteristics.SENSOR_ORIENTATION);
        rotationCompensation = (rotationCompensation + sensorOrientation + 270) % 360;

        // Return the corresponding FirebaseVisionImageMetadata rotation value.
        int result;
        switch (rotationCompensation) {
            case 0:
                result = FirebaseVisionImageMetadata.ROTATION_0;
                break;
            case 90:
                result = FirebaseVisionImageMetadata.ROTATION_90;
                break;
            case 180:
                result = FirebaseVisionImageMetadata.ROTATION_180;
                break;
            case 270:
                result = FirebaseVisionImageMetadata.ROTATION_270;
                break;
            default:
                result = FirebaseVisionImageMetadata.ROTATION_0;
                Log.e("bho", "Bad rotation value: " + rotationCompensation);
        }
        return result;
    }                                               //trova la rotazione della fotocamera

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)  //copiata da un sito a caso
    private String getCameraId()                       //trova l'id della fotocamera
    {
        String mCameraId = null;
        CameraManager cameraManager =(CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for(String cameraId:cameraManager.getCameraIdList()){
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                if(cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)==1){
                    mCameraId = cameraId;
                    return mCameraId;
                }

            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return mCameraId;
    }

    private void askCameraPermission()  //chiede permesso camera
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},2);
        }
    }
}

