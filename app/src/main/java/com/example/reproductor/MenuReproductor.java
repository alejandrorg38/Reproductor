package com.example.reproductor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MenuReproductor#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuReproductor extends Fragment implements Serializable {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ServicioMusica servicioMusica;
    private ImageButton ib_play, ib_prev, ib_next;
    private TextView tv_artistaMR, tv_nCancionMR;
    private ImageView iv_portadaMR;
    private long mLastClickTime;

    public MenuReproductor() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MenuReproductor.
     */
    // TODO: Rename and change types and number of parameters
    public static MenuReproductor newInstance(String param1, String param2) {
        MenuReproductor fragment = new MenuReproductor();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_menu_reproductor, container, false);

        tv_nCancionMR = view.findViewById(R.id.tv_nCancionMR);
        tv_artistaMR = view.findViewById(R.id.tv_artistaMR);

        ib_play = view.findViewById(R.id.ib_play);
        ib_prev = view.findViewById(R.id.ib_prev);
        ib_next = view.findViewById(R.id.ib_next);
        iv_portadaMR = view.findViewById(R.id.iv_portadaMR);

        servicioMusica = ServicioMusica.getInstance();

        actualizarMenu();


        ib_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                servicioMusica.playOrPause();

                actualizarMenu();
            }
        });

        ib_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Evita pulsar varias veces seguidas
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();

                servicioMusica.setContext(view.getContext());
                servicioMusica.previous();
                actualizarMenu();
                ib_play.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
            }
        });

        ib_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Evita pulsar varias veces seguidas
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                mLastClickTime = SystemClock.elapsedRealtime();

                servicioMusica.setContext(view.getContext());
                servicioMusica.next();
                actualizarMenu();
                ib_play.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
            }
        });

        tv_nCancionMR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirDetalles();
            }
        });

        tv_artistaMR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirDetalles();
            }
        });

        iv_portadaMR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirDetalles();
            }
        });

        return view;
    }

    public void actualizarMenu(){

        if(servicioMusica!=null){
            tv_nCancionMR.setText(servicioMusica.getCancion());
            tv_artistaMR.setText(servicioMusica.getArtista());

            if(servicioMusica.isPlaying()){
                ib_play.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
            } else {
                ib_play.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
            }
        }
    }

    public void abrirDetalles(){
        Intent i = new Intent(getContext(), DetallesReproductor.class);
        startActivity(i);
    }

    public void onResume() {
        super.onResume();
        actualizarMenu();
    }
}