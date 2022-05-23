package com.example.reproductor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Listas extends AppCompatActivity {

    private ServicioMusica servicioMusica;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listas);

        servicioMusica = ServicioMusica.getInstance();

        if(servicioMusica.getCancionUrl().isEmpty()){

            FragmentManager manager = getSupportFragmentManager();
            Fragment f = manager.findFragmentById(R.id.fl_reproductorL);
            manager.beginTransaction().hide(f).commit();
        }

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.listas);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.buscar:
                        startActivity(new Intent(getApplicationContext(),Buscar.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.listas:
                        return true;
                    case R.id.canciones:
                        startActivity(new Intent(getApplicationContext(),Canciones.class));
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

    public void onBackPressed() {
    }
}