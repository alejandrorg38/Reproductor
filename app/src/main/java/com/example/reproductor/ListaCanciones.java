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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reproductor.adapters.RecyclerAdapter;
import com.example.reproductor.adapters.RecyclerAdapterListas;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ListaCanciones extends AppCompatActivity {

    private ServicioMusica servicioMusica;

    private CircularProgressIndicator progressIndicator;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ArrayList<CancionInfo> listaCanciones;

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter = null;
    private String userId;

    private ImageButton ib_eliminarLC;
    private TextView et_sinCancionesLC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_canciones);

        //Menu control de musica
        servicioMusica = ServicioMusica.getInstance();
        if(servicioMusica.getCancionUrl().isEmpty()){
            FragmentManager manager = getSupportFragmentManager();
            Fragment f = manager.findFragmentById(R.id.fl_reproductorLC);
            manager.beginTransaction().hide(f).commit();
        }

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();

        et_sinCancionesLC = findViewById(R.id.et_sinCancionesLC);
        ib_eliminarLC = findViewById(R.id.ib_eliminarLC);
        progressIndicator = findViewById(R.id.progress_circularLC);

        // Rellenar RecyclerView
        recyclerView = findViewById(R.id.rv_lista_canciones);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, GridLayoutManager.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        listaCanciones = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("canciones").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    et_sinCancionesLC.setVisibility(View.INVISIBLE);
                    listaCanciones.clear();

                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        CancionInfo cancionInfo = dataSnapshot.getValue(CancionInfo.class);
                        listaCanciones.add(cancionInfo);
                    }
                    recyclerAdapter = new RecyclerAdapter(getApplicationContext(),ListaCanciones.this, (ArrayList<CancionInfo>) listaCanciones);
                    recyclerView.setAdapter(recyclerAdapter);
                    recyclerAdapter.notifyDataSetChanged();
                    progressIndicator.setVisibility(View.GONE);

                } else {
                    progressIndicator.setVisibility(View.GONE);
                    et_sinCancionesLC.setVisibility(View.VISIBLE);
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
}