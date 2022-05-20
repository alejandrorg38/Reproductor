package com.example.reproductor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private Pattern pat = null;
    private Matcher mat = null;

    private EditText et_emailMA, et_contrasenaMA;
    private TextView tv_errorEmailMA, tv_errorContrasenaMA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_emailMA = findViewById(R.id.et_emailMA);
        et_contrasenaMA = findViewById(R.id.et_contrasenaMA);
        tv_errorEmailMA = findViewById(R.id.tv_errorEmailMA);
        tv_errorContrasenaMA = findViewById(R.id.tv_errorContrasenaMA);

        mAuth = FirebaseAuth.getInstance();
    }

    public void onStart() {
        super.onStart();

        // Comprueba si el usuario esta logeado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            reload();
        }
    }

    public void registro (View view){

        String email = (et_emailMA.getText()).toString();
        String password = (et_contrasenaMA.getText()).toString();

        // Valida que los campos introducidos sean correctos
        if (validar(email, password)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) { //Registrado correctamente
                                Log.d("msgError", "createUserWithEmail:success");

                                mensajeRegistro();

                            } else { // Error en el registro
                                Log.w("msgError", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Ya existe una cuenta con este email.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void mensajeRegistro() {

        new AlertDialog.Builder(this, R.style.Base_Theme_Material3_Dark_Dialog)
                .setTitle((mAuth.getCurrentUser()).getEmail())
                .setMessage("La cuenta se ha registrado correctamente")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) { }
                })
                .show();
    }

    public void accesoCuenta (View view){ // Comprueba si el usuario esta registrado y accede a la cuenta (Al pulsar el boton 'Acceder')

        String email = (et_emailMA.getText()).toString();
        String password = (et_contrasenaMA.getText()).toString();

        if (validar(email, password)) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("msgError", "signInWithEmail:success");

                                acceder();

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("msgError", "signInWithEmail:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }

    public boolean validar(String email, String password){ // Comprueba si el email y contraseña son validos
        boolean emailValido = false;
        boolean pwdValida = false;
        boolean valido = false;

        pat = Pattern.compile("^(.+)@(.+)$");
        mat = pat.matcher(email);

        if(mat.matches()){
            et_emailMA.setBackgroundResource(R.drawable.custom_input);
            tv_errorEmailMA.setVisibility(View.INVISIBLE);
            emailValido = true;
        } else {
            et_emailMA.setBackgroundResource(R.drawable.custom_input_error);
            tv_errorEmailMA.setVisibility(View.VISIBLE);
        }

        if((password.length())>6){
            et_contrasenaMA.setBackgroundResource(R.drawable.custom_input);
            tv_errorContrasenaMA.setVisibility(View.INVISIBLE);
            pwdValida = true;
        } else {
            et_contrasenaMA.setBackgroundResource(R.drawable.custom_input_error);
            tv_errorContrasenaMA.setVisibility(View.VISIBLE);
        }

        if (emailValido&&pwdValida) valido = true;

        return valido;
    }

    public void acceder(){

        Intent i = new Intent(this, Canciones.class);
        startActivity(i);
        finish();
    }

    private void reload() { // Recuerda el email del ultimo usuario conectado al cerrar la app
        mAuth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                actualizarInterfaz(mAuth.getCurrentUser());

                if (!(task.isSuccessful())) {
                    Log.e("msgError", "No se ha podido recordar al usuario", task.getException());
                }
            }
        });
    }

    private void actualizarInterfaz(FirebaseUser user) {

        if (user != null) {
            et_emailMA.setText(user.getEmail());
        }
    }


    public void onBackPressed() {
    }
}