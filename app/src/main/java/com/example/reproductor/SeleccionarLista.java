package com.example.reproductor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.reproductor.adapters.RecyclerAdapterListas;
import com.example.reproductor.adapters.RecyclerAdapterSelecLista;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SeleccionarLista extends AppCompatActivity {

    private ServicioMusica servicioMusica;

    private CircularProgressIndicator progressIndicator;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private RecyclerView recyclerView;
    private ArrayList<ListaInfo> listadeListas;
    private RecyclerAdapterSelecLista recyclerAdapter = null;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_lista);

        servicioMusica = ServicioMusica.getInstance();

        progressIndicator = findViewById(R.id.progress_circularSL);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();

        // Rellenar RecyclerView
        recyclerView = findViewById(R.id.rv_listasSL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, GridLayoutManager.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        listadeListas = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference().child(userId).child("listas");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    listadeListas.clear();

                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        ListaInfo listaInfo = dataSnapshot.getValue(ListaInfo.class);
                        listadeListas.add(listaInfo);
                    }
                    recyclerAdapter = new RecyclerAdapterSelecLista(getApplicationContext(),SeleccionarLista.this, (ArrayList<ListaInfo>) listadeListas);
                    recyclerView.setAdapter(recyclerAdapter);
                    recyclerAdapter.notifyDataSetChanged();
                    progressIndicator.setVisibility(View.GONE);

                }else {
                    progressIndicator.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getApplicationContext(), "Error al cargar la musica", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void favorita(View view){

        CancionInfo cancionInfo = servicioMusica.getCancionInfo();

        if(!cancionInfo.isFavorita()) {
            cancionInfo.setFavorita(true);

            Map<String, Object> cancionMap  = new HashMap<String, Object>();
            cancionMap.put("nombre", cancionInfo.getNombre());
            cancionMap.put("artista", cancionInfo.getArtista());
            cancionMap.put("album", cancionInfo.getAlbum());
            cancionMap.put("genero", cancionInfo.getGenero());
            cancionMap.put("cancionUrl", cancionInfo.getCancionUrl());
            cancionMap.put("favorita", cancionInfo.isFavorita());
            cancionMap.put("listas", cancionInfo.getListas());

            String portadaUrl=cancionInfo.getPortadaUrl();
            if(portadaUrl!=null)cancionMap.put("portadaUrl", portadaUrl);

            DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
            mReference.child(userId).child("canciones").child("id-"+cancionInfo.getNombre()).updateChildren(cancionMap);

            Toast.makeText(getApplicationContext(), "Se ha añadido la cancion a favoritos", Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(getApplicationContext(), "La cancion ya esta en favoritos", Toast.LENGTH_SHORT).show();
        }
    }
}