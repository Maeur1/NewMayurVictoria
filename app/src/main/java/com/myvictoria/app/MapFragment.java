package com.myvictoria.app;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

public class MapFragment extends Fragment implements View.OnClickListener{

    SubsamplingScaleImageView im;
    Button pip, kel, kar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(com.myvictoria.app.R.layout.fragment_map, container, false);
        initialise(rootView);
        im.setImage(ImageSource.resource(com.myvictoria.app.R.drawable.kelburn_map));
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case com.myvictoria.app.R.id.bPip:
                im.setImage(ImageSource.resource(com.myvictoria.app.R.drawable.pipitea_map));
                break;
            case com.myvictoria.app.R.id.bKelburn:
                im.setImage(ImageSource.resource(com.myvictoria.app.R.drawable.kelburn_map));
                break;
            case com.myvictoria.app.R.id.bKaro:
                im.setImage(ImageSource.resource(com.myvictoria.app.R.drawable.karori_map));
                break;
        }
    }

    private void initialise(View view){
        im = (SubsamplingScaleImageView) view.findViewById(com.myvictoria.app.R.id.map);
        pip = (Button) view.findViewById(com.myvictoria.app.R.id.bPip);
        kel = (Button) view.findViewById(com.myvictoria.app.R.id.bKelburn);
        kar = (Button) view.findViewById(com.myvictoria.app.R.id.bKaro);
        pip.setOnClickListener(this);
        kel.setOnClickListener(this);
        kar.setOnClickListener(this);
    }



}
