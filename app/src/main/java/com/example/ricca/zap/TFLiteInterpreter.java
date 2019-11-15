package com.example.ricca.zap;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ricca.zap.DAO.InferenceResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.custom.FirebaseCustomLocalModel;
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions;
import com.google.firebase.ml.custom.FirebaseModelOutputs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TFLiteInterpreter
{
    private static final String TAG="TFLiteInterpreter";
    private static final String REMOTE_MODEL_NAME="model";
    private static final String LOCAL_MODEL_NAME="graph.lite";
    private static final String LABELS_NAME="labels.txt";
    private static final int DIM_BATCH_SIZE=1;
    private static final int NUM_PIXEL_CHANNEL=3;
    private static final int IMG_SIZE_X=224;
    private static final int IMG_SIZE_Y=224;
    private int LABELS_NUM=0;

    private FirebaseModelInterpreter interpreter;
    private FirebaseModelInputOutputOptions model_options;
    private float[] probabilities;
    private Activity context;

    public TFLiteInterpreter(Activity context)
    {
        this.context = context;

        //inizializza il modello
        FirebaseCustomRemoteModel remoteModel=configureHostedModelSource();
        FirebaseCustomLocalModel localModel=configureLocalModelSource();
        startModelDownloadTask(remoteModel);

        //conta il numero di label
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(context.getAssets().open(LABELS_NAME)));
            while (br.readLine() !=null)LABELS_NUM++;
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        //crea interpreter e configura l'i/o
        createInterpreter(remoteModel,localModel);
        model_options=createInputOutputOptions();
    }

    private FirebaseCustomRemoteModel configureHostedModelSource()
    {
        // configura modello remoto
        return new FirebaseCustomRemoteModel.Builder(REMOTE_MODEL_NAME).build();
    }

    private void startModelDownloadTask(FirebaseCustomRemoteModel remoteModel)
    {
        // scarica il modello
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();
        FirebaseModelManager.getInstance().download(remoteModel, conditions)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        Log.v(TAG,"model downloaded");
                    }
                });
    }

    private FirebaseCustomLocalModel configureLocalModelSource()
    {
        //configura il modello locale
        return new FirebaseCustomLocalModel.Builder()
                .setAssetFilePath(LOCAL_MODEL_NAME)
                .build();
    }

    private void createInterpreter(final FirebaseCustomRemoteModel remoteModel,final FirebaseCustomLocalModel localModel)
    {
        // controlla se il modello è stato scaricato
        FirebaseModelManager.getInstance().isModelDownloaded(remoteModel)
                .addOnSuccessListener(new OnSuccessListener<Boolean>()
                {
                    @Override
                    public void onSuccess(Boolean isDownloaded)
                    {
                        FirebaseModelInterpreterOptions options;
                        if (isDownloaded) //se il modello remoto è stato scaricato usalo
                        {
                            options = new FirebaseModelInterpreterOptions.Builder(remoteModel).build();
                        } else //altrimenti usa quello locale
                        {
                            options = new FirebaseModelInterpreterOptions.Builder(localModel).build();
                        }
                        try
                        {   //crea l'interpreter
                            interpreter = FirebaseModelInterpreter.getInstance(options);
                        } catch (FirebaseMLException e)
                        {
                            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                        }
                    }
                });
    }


    private FirebaseModelInputOutputOptions createInputOutputOptions()
    {
        // imposta l' input/output
        try
        {
            return new FirebaseModelInputOutputOptions.Builder()
                            .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{DIM_BATCH_SIZE, IMG_SIZE_X, IMG_SIZE_Y, NUM_PIXEL_CHANNEL})
                            .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{DIM_BATCH_SIZE, LABELS_NUM})
                            .build();

        } catch (FirebaseMLException e)
        {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            return null;
        }
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
        } catch (FirebaseMLException e)
        {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
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
        // elabora il risultato
        try
        {
            List<InferenceResult> inferenceResult =new ArrayList<>();
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(context.getAssets().open(LABELS_NAME)));
            for (float probability : probabilities)
            {
                //per ogni elemento dell'output aggiunge un risultato alla lista
                String label=reader.readLine();
                Objects.requireNonNull(inferenceResult).add(new InferenceResult(label, probability));
            }

            //ordina la lista
            Collections.sort(inferenceResult);
            return inferenceResult;

        } catch (Exception e)
        {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            return null;
        }
    }
}

