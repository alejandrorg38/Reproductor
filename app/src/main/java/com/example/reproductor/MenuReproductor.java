package com.example.reproductor;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.bumptech.glide.load.engine.Resource;

import java.io.Serializable;
import java.util.ArrayList;

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

        ib_play = view.findViewById(R.id.ib_play);
        ib_prev = view.findViewById(R.id.ib_prev);
        ib_next = view.findViewById(R.id.ib_next);

        servicioMusica = ServicioMusica.getInstance();

        if(servicioMusica.isPlaying()){
            ib_play.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
        } else {
            ib_play.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
        }


        ib_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                servicioMusica.playOrPause();

                if(servicioMusica.isPlaying()){
                    ib_play.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                } else {
                    ib_play.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                }
            }
        });

        ib_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                servicioMusica.previous();
            }
        });

        ib_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                servicioMusica.next();
            }
        });

        return view;
    }
}