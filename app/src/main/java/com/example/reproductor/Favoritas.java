package com.example.reproductor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Favoritas extends AppCompatActivity {

    private ServicioMusica servicioMusica;

    private CircularProgressIndicator progressIndicator;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ArrayList<CancionInfo> listaCanciones;

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter = null;
    private String userId;

    private ImageButton ib_eliminarF;
    private TextView tv_sinCancionesF, tv_nListaF;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritas);
        //Menu control de musica
        servicioMusica = ServicioMusica.getInstance();
        if(servicioMusica.getCancionUrl().isEmpty()){
            FragmentManager manager = getSupportFragmentManager();
            Fragment f = manager.findFragmentById(R.id.fl_reproductorF);
            manager.beginTransaction().hide(f).commit();
        }

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();

        tv_nListaF = findViewById(R.id.tv_nListaF);

        tv_sinCancionesF = findViewById(R.id.et_sinCancionesF);
        ib_eliminarF = findViewById(R.id.ib_eliminarLC);
        progressIndicator = findViewById(R.id.progress_circularF);

        // Rellenar RecyclerView
        recyclerView = findViewById(R.id.rv_favoritas);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, GridLayoutManager.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        listaCanciones = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference().child(userId).child("canciones");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    tv_sinCancionesF.setVisibility(View.INVISIBLE);
                    listaCanciones.clear();

                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        CancionInfo cancionInfo = dataSnapshot.getValue(CancionInfo.class);
                        listaCanciones.add(cancionInfo);
                    }

                    //Filtrar RecyclerView segun la lista
                    ArrayList<CancionInfo> lista = new ArrayList<>();

                    for(CancionInfo obj:listaCanciones){
                        if(obj.isFavorita()) lista.add(obj);
                    }

                    recyclerAdapter = new RecyclerAdapter(getApplicationContext(), Favoritas.this, (ArrayList<CancionInfo>) lista);
                    recyclerView.setAdapter(recyclerAdapter);
                    recyclerAdapter.notifyDataSetChanged();
                    progressIndicator.setVisibility(View.GONE);

                } else {
                    progressIndicator.setVisibility(View.GONE);
                    tv_sinCancionesF.setVisibility(View.VISIBLE);
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
                        return true;
                }
                return false;
            }
        });
    }

    public void opciones(View view){
        Intent i = new Intent(getApplicationContext(),OpcionesFavoritas.class);
        i.putExtra("nLista", tv_nListaF.getText().toString());
        startActivity(i);
        overridePendingTransition(0,0);
    }
}