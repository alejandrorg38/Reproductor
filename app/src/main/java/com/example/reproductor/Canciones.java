package com.example.reproductor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

public class Canciones extends AppCompatActivity {

    CircularProgressIndicator progressIndicator;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private RecyclerView recyclerView;
    private ArrayList<CancionInfo> listaCanciones;
    private RecyclerAdapter recyclerAdapter = null;
    private TextView et_sinCancionesC;

    private String userId;
    private ServicioMusica servicioMusica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canciones);

        servicioMusica = ServicioMusica.getInstance();

        if(servicioMusica.getCancionUrl().isEmpty()){

            FragmentManager manager = getSupportFragmentManager();
            Fragment f = manager.findFragmentById(R.id.fl_reproductorC);
            manager.beginTransaction().hide(f).commit();
        }



        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();

        et_sinCancionesC = findViewById(R.id.et_sinCancionesC);
        progressIndicator = findViewById(R.id.progress_circular);

        recyclerView = findViewById(R.id.rv_canciones);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, GridLayoutManager.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        listaCanciones = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("canciones").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                listaCanciones.clear();

                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    CancionInfo cancionInfo = dataSnapshot.getValue(CancionInfo.class);
                    listaCanciones.add(cancionInfo);
                }
                recyclerAdapter = new RecyclerAdapter(getApplicationContext(),Canciones.this, (ArrayList<CancionInfo>) listaCanciones);
                recyclerView.setAdapter(recyclerAdapter);
                recyclerAdapter.notifyDataSetChanged();
                progressIndicator.setVisibility(View.GONE);

                for (CancionInfo i:listaCanciones) {
                    Log.d("msgError", i.getNombre());
                }
                Log.d("msgError", "---");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getApplicationContext(), "Archivo subido", Toast.LENGTH_SHORT).show();
            }
        });

        // Inicializacion del menu de navegacion inferior
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.canciones);
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

    //Inicializacion del Action Bar Menu
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return true;
    }
    // Respondiendo a las acciones del menu
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id){
            case R.id.ab_miCuenta:
                    Intent i = new Intent(this, MiCuenta.class);
                    startActivity(i);
                break;

            case R.id.ab_anadir:
                Intent j = new Intent(this, AnadirCancion.class);
                startActivity(j);
                break;
        }
        return  super.onOptionsItemSelected(item);
    }

    public void abrirDetalles(View view){
        Intent i = new Intent(view.getContext(), DetallesReproductor.class);
        view.getContext().startActivity(i);
    }

    public void onBackPressed() {
    }
}