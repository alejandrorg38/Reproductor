package com.example.reproductor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

public class Buscar extends AppCompatActivity {

    private CircularProgressIndicator progressIndicator;
    private ServicioMusica servicioMusica;
    private ArrayList<CancionInfo> listaCanciones;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private RecyclerAdapter recyclerAdapter;
    private EditText textoBusqueda;
    private TextView tv_sinCancionesB;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("canciones");

        tv_sinCancionesB = findViewById(R.id.et_sinCancionesB);
        searchView = findViewById(R.id.searchView);
        textoBusqueda = (EditText) searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        textoBusqueda.setTextColor(getResources().getColor(R.color.black));
        textoBusqueda.setHintTextColor(getResources().getColor(R.color.light_blue));
        textoBusqueda.setHint("Buscar por nombre o artista...");
        textoBusqueda.setBackgroundColor(getResources().getColor(R.color.lighter_blue));
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        progressIndicator = findViewById(R.id.progress_circularB);
        recyclerView = findViewById(R.id.rv_buscar);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, GridLayoutManager.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        listaCanciones = new ArrayList<>();

        // Menu control de musica
        servicioMusica = ServicioMusica.getInstance();
        if(servicioMusica.getCancionUrl().isEmpty()){

            FragmentManager manager = getSupportFragmentManager();
            Fragment f = manager.findFragmentById(R.id.fl_reproductorB);
            manager.beginTransaction().hide(f).commit();
        }

        // Rellenar RecyclerView
        databaseReference = FirebaseDatabase.getInstance().getReference().child("canciones").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    listaCanciones.clear();

                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        CancionInfo cancionInfo = dataSnapshot.getValue(CancionInfo.class);
                        listaCanciones.add(cancionInfo);
                    }
                    recyclerAdapter = new RecyclerAdapter(getApplicationContext(),Buscar.this, (ArrayList<CancionInfo>) listaCanciones);
                    recyclerView.setAdapter(recyclerAdapter);
                    recyclerAdapter.notifyDataSetChanged();
                    progressIndicator.setVisibility(View.GONE);
                } else {
                    progressIndicator.setVisibility(View.GONE);
                    tv_sinCancionesB.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error al cargar la musica", Toast.LENGTH_SHORT).show();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(searchView.equals("")) {
                    recyclerView.setVisibility(View.INVISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    buscar(s);
                }
                return true;
            }
        });

        // Inicializacion del menu de navegacion inferior
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.buscar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.canciones:
                        startActivity(new Intent(getApplicationContext(),Canciones.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.buscar:
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

    // Gestion de la busqueda con SearchView
    private void buscar(String s) {

        ArrayList<CancionInfo> lista = new ArrayList<>();

        for(CancionInfo obj: listaCanciones){
            if((obj.getNombre().toLowerCase().contains(s.toLowerCase()))||(obj.getArtista().toLowerCase().contains(s.toLowerCase()))){
                lista.add(obj);
            }
        }
        RecyclerAdapter adapter = new RecyclerAdapter(getApplicationContext(),Buscar.this, (ArrayList<CancionInfo>) lista);
        recyclerView.setAdapter(adapter);
    }

    public void onBackPressed() {}
}