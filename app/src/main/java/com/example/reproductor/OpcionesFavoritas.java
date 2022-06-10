package com.example.reproductor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reproductor.adapters.RecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OpcionesFavoritas extends AppCompatActivity {

    private CircularProgressIndicator progressIndicator;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ArrayList<CancionInfo> listaCanciones;

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter = null;

    private TextView tv_sinCancionesOF;
    private boolean hayCanciones = false;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones_favoritas);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();

        tv_sinCancionesOF = findViewById(R.id.et_sinCancionesOF);
        progressIndicator = findViewById(R.id.progress_circularOF);

        // Rellenar RecyclerView
        recyclerView = findViewById(R.id.rv_lista_cancionesOF);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, GridLayoutManager.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        listaCanciones = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference().child(userId).child("canciones");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    tv_sinCancionesOF.setVisibility(View.INVISIBLE);
                    listaCanciones.clear();

                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        CancionInfo cancionInfo = dataSnapshot.getValue(CancionInfo.class);
                        listaCanciones.add(cancionInfo);
                    }

                    //Filtrar RecyclerView segun la lista
                    ArrayList<CancionInfo> lista = new ArrayList<>();

                    for(CancionInfo obj:listaCanciones){
                        if(obj.isFavorita()) lista.add(obj);
                        hayCanciones=true;
                    }

                    if(!hayCanciones) tv_sinCancionesOF.setVisibility(View.VISIBLE);

                        recyclerAdapter = new RecyclerAdapter(getApplicationContext(), OpcionesFavoritas.this, (ArrayList<CancionInfo>) lista, true);
                    recyclerView.setAdapter(recyclerAdapter);
                    recyclerAdapter.notifyDataSetChanged();
                    progressIndicator.setVisibility(View.GONE);

                } else {
                    progressIndicator.setVisibility(View.GONE);
                    tv_sinCancionesOF.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getApplicationContext(), "Error al cargar la musica", Toast.LENGTH_SHORT).show();
            }
        });

        // Inicializacion del menu de navegacion inferior
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.listas);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {


            // Respondiendo a las acciones del menu
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.buscar:
                        startActivity(new Intent(getApplicationContext(),Buscar.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.canciones:
                        startActivity(new Intent(getApplicationContext(),Canciones.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.listas:
                        startActivity(new Intent(getApplicationContext(),Listas.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    public void onBackPressed(){
        finish();
        overridePendingTransition(0,0);
    }
}