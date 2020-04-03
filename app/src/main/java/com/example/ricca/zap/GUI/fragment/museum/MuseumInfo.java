package com.example.ricca.zap.GUI.fragment.museum;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.ricca.zap.Data.MuseumMetaData;
import com.example.ricca.zap.Data.MuseumMetaDataWaiter;
import com.example.ricca.zap.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;


public class MuseumInfo extends Fragment implements MuseumMetaDataWaiter {

    private View v;

    public static MuseumInfo newIstance()
    {
        MuseumInfo fragment= new MuseumInfo();
        Bundle args=new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        v= inflater.inflate(R.layout.fragment_museum_info, container, false);

        return v;
    }

    //acquire metadata
    @Override
    public void init(final MuseumMetaData metaData)
    {

        //load map
      new Thread()
      {
          @Override
          public void run()
          {
              //get map reference
              final SubsamplingScaleImageView mapView= v.findViewById(R.id.mapView);
              Bitmap bpm;
              //try to download map into bitmap
              try
              {
                  URL url=new URL(metaData.getMappa());
                  bpm=BitmapFactory.decodeStream(url.openConnection().getInputStream());
              } catch (IOException e) {
                  //in case of fail set the bitmap to the logo of the app
                  bpm=BitmapFactory.decodeResource(getResources(),R.drawable.logo);
              }

              //on UI thread set map image
              final Bitmap finalBpm = bpm;
              Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable()
              {
                  @Override
                  public void run() {
                      mapView.setImage(ImageSource.bitmap(finalBpm));
                  }
              });

          }
      }.start();

        ((TextView)v.findViewById(R.id.museum_name)).setText(metaData.getNome());
    }
}
