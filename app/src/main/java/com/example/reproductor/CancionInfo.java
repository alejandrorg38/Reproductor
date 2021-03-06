package com.example.reproductor;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class CancionInfo {

    private String nombre;
    private String artista;
    private String album;
    private String genero;
    private String portadaUrl;
    private String cancionUrl;
    private String key;
    private boolean favorita;
    private ArrayList<String> listas = new ArrayList<>();

    @Override
    public String toString() {
        return "CancionInfo{" +
                "nombre='" + nombre + '\'' +
                ", artista='" + artista + '\'' +
                ", album='" + album + '\'' +
                ", genero='" + genero + '\'' +
                ", portadaUrl='" + portadaUrl + '\'' +
                ", cancionUrl='" + cancionUrl + '\'' +
                ", key='" + key + '\'' +
                '}';
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getPortadaUrl() {
        return portadaUrl;
    }

    public void setPortadaUrl(String portadaUrl) {
        this.portadaUrl = portadaUrl;
    }

    public String getCancionUrl() {
        return cancionUrl;
    }

    public void setCancionUrl(String cancionUrl) {
        this.cancionUrl = cancionUrl;
    }

    public boolean isFavorita() {
        return favorita;
    }

    public void setFavorita(boolean favorita) {
        this.favorita = favorita;
    }

    public ArrayList<String> getListas() {
        return listas;
    }

    public void setListas(ArrayList<String> listas) {
        this.listas = listas;
    }

    public void setLista(String lista) {
        Log.d("msgError", "------>"+lista);
        this.listas.add(lista); }
}
