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
    private static final int IMG_SIZE=224;
    private int LABEL_NUM=0;

    private FirebaseModelInterpreter interpreter;
    private FirebaseModelInputOutputOptions model_options;
    private float[] probabilities;
    private Activity context;

    public TFLiteInterpreter(Activity context)
    {
        this.context = context;
        FirebaseCustomRemoteModel remoteModel=configureHostedModelSource();
        FirebaseCustomLocalModel localModel=configureLocalModelSource();
        startModelDownloadTask(remoteModel);
        createInterpreter(remoteModel,localModel);
        model_options=createInputOutputOptions();

        //conta il numero di label
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(LABELS_NAME)));
            while (br.readLine() !=null)LABEL_NUM++;
        } catch (IOException e)
        {
            e.printStackTrace();
        }


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
                            .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 224, 224, 3})
                            .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 5})
                            .build();

        } catch (FirebaseMLException e)
        {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            return null;
        }
    }

    private float[][][][] bitmapToInputArray(Bitmap bitmap) {
        // [START mlkit_bitmap_input]
        bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, false);

        int batchNum = 0;
        float[][][][] input = new float[1][224][224][3];
        for (int x = 0; x < 224; x++) {
            for (int y = 0; y < 224; y++) {
                int pixel = bitmap.getPixel(x, y);
                // Normalize channel values to [-1.0, 1.0]. This requirement varies by
                // model. For example, some models might require values to be normalized
                // to the range [0.0, 1.0] instead.
                input[batchNum][x][y][0] = (Color.red(pixel) - 127) / 128.0f;
                input[batchNum][x][y][1] = (Color.green(pixel) - 127) / 128.0f;
                input[batchNum][x][y][2] = (Color.blue(pixel) - 127) / 128.0f;
            }
        }
        // [END mlkit_bitmap_input]

        return input;
    }

    public void runInference(Bitmap bitmap) {

        float[][][][] input = bitmapToInputArray(bitmap);

        // [START mlkit_run_inference]
        FirebaseModelInputs inputs = null;
        try {
            inputs = new FirebaseModelInputs.Builder()
                    .add(input)  // add() as many input arrays as your model requires
                    .build();
        } catch (FirebaseMLException e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
        interpreter.run(Objects.requireNonNull(inputs), model_options)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseModelOutputs>() {
                            @Override
                            public void onSuccess(FirebaseModelOutputs result) {
                                // [START_EXCLUDE]
                                // [START mlkit_read_result]
                                float[][] output = result.getOutput(0);
                                probabilities = output[0];
                                // [END mlkit_read_result]
                                // [END_EXCLUDE]
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                                // Task failed with an exception
                            }
                        });
        // [END mlkit_run_inference]
    }

    public List<InferenceResult> getResult() {
        // [START mlkit_use_inference_result]

        try
        {
            List<InferenceResult> inferenceResult =new ArrayList<>();
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(context.getAssets().open(LABELS_NAME)));
            for (float probability : probabilities)
            {
                String label=reader.readLine();
                Objects.requireNonNull(inferenceResult).add(new InferenceResult(label, probability));
                Log.i("MLKit", String.format("%s: %1.4f", label, probability));

            }

            Collections.sort(inferenceResult);
            return inferenceResult;
        } catch (IOException e)
        {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            return null;
        }catch (NullPointerException e)
        {
            Log.e(TAG,"inference stilll running");
            return null;
        }

        // [END mlkit_use_inference_result]
    }
}

