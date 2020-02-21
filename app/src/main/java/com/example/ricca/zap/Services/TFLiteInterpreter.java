package com.example.ricca.zap.Services;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ricca.zap.Data.InferenceResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions;
import com.google.firebase.ml.custom.FirebaseModelOutputs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TFLiteInterpreter
{
    //model settings
    private static final String TAG="TFLiteInterpreter";
    private static List<String> labels;
    private static final int DIM_BATCH_SIZE=1;
    private static final int NUM_PIXEL_CHANNEL=3;
    private static final int IMG_SIZE_X=224;
    private static final int IMG_SIZE_Y=224;
    private int LABELS_NUM=0;

    private FirebaseModelInterpreter interpreter;
    private FirebaseModelInputOutputOptions model_options;
    private float[] probabilities;


    public TFLiteInterpreter(String model_name, final String label_path)
    {
        //configura modello remoto
        final FirebaseCustomRemoteModel remoteModel=
                new FirebaseCustomRemoteModel.Builder(model_name).build();

        //scarica il modello
        FirebaseModelDownloadConditions conditions =
                new FirebaseModelDownloadConditions.Builder().build();

        FirebaseModelManager.getInstance().download(remoteModel, conditions)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {

                    //quando è completo
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        try
                        {
                            //instanziate interpreter
                            FirebaseModelInterpreterOptions options =
                                new FirebaseModelInterpreterOptions.Builder(remoteModel).build();
                            interpreter = FirebaseModelInterpreter.getInstance(options);

                            //configure label list
                            FirebaseDatabase.getInstance().getReference(label_path).orderByKey()
                                .addListenerForSingleValueEvent(new ValueEventListener()
                                {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                    {
                                        labels=new ArrayList<>();
                                        for(DataSnapshot snapshot : dataSnapshot.getChildren())
                                        {
                                            //add labels
                                            labels.add(snapshot.getValue(String.class));
                                            LABELS_NUM++;
                                        }

                                        try
                                        {
                                            //specifing input/output model options
                                            model_options= new FirebaseModelInputOutputOptions.Builder()
                                                .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{DIM_BATCH_SIZE, IMG_SIZE_X, IMG_SIZE_Y, NUM_PIXEL_CHANNEL})
                                                .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{DIM_BATCH_SIZE, LABELS_NUM})
                                                .build();

                                        } catch (FirebaseMLException e)
                                            { Log.e(TAG, Objects.requireNonNull(e.getMessage()));}
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                                });


                        } catch (FirebaseMLException e)
                            { Log.e(TAG, Objects.requireNonNull(e.getMessage())); }

                    }
                });
    }


    private float[][][][] bitmapToInputArray(Bitmap bitmap)
    {
        // converte l'immagine in un array
        bitmap = Bitmap.createScaledBitmap(bitmap, IMG_SIZE_X, IMG_SIZE_Y, false);

        int batchNum = 0;
        float[][][][] input = new float[DIM_BATCH_SIZE][IMG_SIZE_X][IMG_SIZE_Y][NUM_PIXEL_CHANNEL];
        for (int x = 0; x < IMG_SIZE_X; x++)
        {
            for (int y = 0; y < IMG_SIZE_Y; y++)
            {
                // Normalize channel values to [-1.0, 1.0]
                int pixel = bitmap.getPixel(x, y);
                input[batchNum][x][y][0] = (Color.red(pixel) - 127) / 128.0f;
                input[batchNum][x][y][1] = (Color.green(pixel) - 127) / 128.0f;
                input[batchNum][x][y][2] = (Color.blue(pixel) - 127) / 128.0f;
            }
        }
        return input;
    }

    public void runInference(Bitmap bitmap)
    {

        float[][][][] input = bitmapToInputArray(bitmap);

        // inizia l'inferenza
        FirebaseModelInputs inputs = null;
        try
        {
            inputs = new FirebaseModelInputs.Builder()
                    .add(input)  // aggiunge l'input
                    .build();

        } catch (FirebaseMLException e) {Log.e(TAG, Objects.requireNonNull(e.getMessage()));}

        interpreter.run(Objects.requireNonNull(inputs), model_options)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseModelOutputs>()
                        {
                            @Override
                            public void onSuccess(FirebaseModelOutputs result)
                            {
                                //in caso di successo legge i risultati
                                float[][] output = result.getOutput(0);
                                probabilities = output[0];
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener()
                        {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                            }
                        });
    }

    public List<InferenceResult> getResult()
    {
            //elabora il risultato

            List<InferenceResult> inferenceResult =new ArrayList<>();
            int i=0;
            try {
                for (float probability : probabilities) {

                    //per ogni elemento dell'output aggiunge un risultato alla lista
                    Objects.requireNonNull(inferenceResult)
                            .add(new InferenceResult(labels.get(i), probability));
                    i++;

                }

                //ordina la lista
                Collections.sort(inferenceResult);
            }catch (NullPointerException e) {Log.e(TAG, Objects.requireNonNull(e.getMessage()));}
            return inferenceResult;
    }
}