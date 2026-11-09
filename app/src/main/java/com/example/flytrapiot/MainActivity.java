package com.example.flytrapiot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Pantalla principal (LEER): muestra todas las trampas guardadas en SQLite.
 */
public class MainActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private TrampaAdapter adaptador;
    private TextView tvContador;
    private TextView tvVacio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DBHelper(this);
        tvContador = findViewById(R.id.tvContador);
        tvVacio = findViewById(R.id.tvVacio);

        // RecyclerView: lista vertical + Adapter
        RecyclerView rvTrampas = findViewById(R.id.rvTrampas);
        rvTrampas.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new TrampaAdapter(new ArrayList<>(), trampa -> abrirDetalle(trampa));
        rvTrampas.setAdapter(adaptador);

        // Botón para agregar una trampa nueva
        Button btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(this, AgregarTrampaActivity.class);
            startActivity(intent);
        });
    }

    // onResume se ejecuta al abrir la pantalla y CADA VEZ que vuelves a ella,
    // así la lista siempre muestra lo que hay en la base de datos
    @Override
    protected void onResume() {
        super.onResume();
        cargarTrampas();
    }

    private void cargarTrampas() {
        List<Trampa> trampas = dbHelper.obtenerTrampas();
        adaptador.actualizarDatos(trampas);

        tvContador.setText("Trampas registradas: " + trampas.size());
        if (trampas.isEmpty()) {
            tvVacio.setVisibility(View.VISIBLE);
        } else {
            tvVacio.setVisibility(View.GONE);
        }
    }

    // Se ejecuta al presionar una trampa de la lista
    private void abrirDetalle(Trampa trampa) {
        // Captura del ID (clave primaria) de la trampa presionada
        int idTrampa = trampa.getId();

        // Envío del ID a la pantalla de detalle con putExtra
        Intent intent = new Intent(this, EditarTrampaActivity.class);
        intent.putExtra("id_trampa", idTrampa);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}