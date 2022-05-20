package com.example.reproductor;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MenuReproductor#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuReproductor extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String cancionUrl="";
    private ImageButton ib_play;
    private Boolean musicIsPlaying=false;
    private ArrayList<String> listaUrl;
    private int pos, n=0, nAnterior=0;

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


         // Reproducir la musica al pulsar el boton play
        ib_play = view.findViewById(R.id.ib_play);
        ServicioMusica servicioMusica = new ServicioMusica(cancionUrl);
        ib_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(n>nAnterior){

                        if(n!=1) servicioMusica.stop();
                        servicioMusica.playOrPause();
                        nAnterior=n;
                }else {

                    if (musicIsPlaying) {
                        servicioMusica.playOrPause();
                        musicIsPlaying = false;

                    } else {
                        servicioMusica.resumeMusic();
                        musicIsPlaying = true;
                    }
                }
            }
        });

        return view;
    }

    public void setCancion(ArrayList<String> listaUrl, int pos) {

        this.listaUrl=listaUrl;
        this.pos=pos;

        cancionUrl = listaUrl.get(pos);
        n++;
    }
}