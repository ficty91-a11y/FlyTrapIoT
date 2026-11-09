package com.example.flytrapiot;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

/**
 * Pantalla "Nueva trampa" (CREAR): formulario que inserta una trampa en SQLite.
 */
public class AgregarTrampaActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private EditText etCodigoSector;
    private EditText etNivelCaptura;
    private EditText etObservaciones;
    private Spinner spTipoTrampa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_trampa);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return insets;
        });

        dbHelper = new DBHelper(this);

        etCodigoSector = findViewById(R.id.etCodigoSector);
        etNivelCaptura = findViewById(R.id.etNivelCaptura);
        etObservaciones = findViewById(R.id.etObservaciones);
        spTipoTrampa = findViewById(R.id.spTipoTrampa);

        // Opciones del Spinner (están en Trampa.TIPOS)
        ArrayAdapter<String> adaptadorTipos = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, Trampa.TIPOS);
        adaptadorTipos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoTrampa.setAdapter(adaptadorTipos);

        Button btnGuardar = findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(v -> guardarTrampa());
    }

    private void guardarTrampa() {
        // 1. Leer lo que escribió el usuario
        String codigoSector = etCodigoSector.getText().toString().trim().toUpperCase(Locale.ROOT);
        String tipoTrampa = spTipoTrampa.getSelectedItem().toString();
        String textoNivel = etNivelCaptura.getText().toString().trim();
        String observaciones = etObservaciones.getText().toString().trim();

        // 2. Validar los campos obligatorios
        if (codigoSector.isEmpty()) {
            etCodigoSector.setError("Ingresa el código del sector");
            return;
        }
        if (textoNivel.isEmpty()) {
            etNivelCaptura.setError("Ingresa el nivel de captura");
            return;
        }
        int nivelCaptura = Integer.parseInt(textoNivel);

        // 3. Insertar en SQLite (el id lo genera la base de datos)
        Trampa nueva = new Trampa(0, codigoSector, tipoTrampa, nivelCaptura, observaciones);
        long idNuevo = dbHelper.insertarTrampa(nueva);

        if (idNuevo == -1) {
            Toast.makeText(this, "No se pudo guardar la trampa", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Trampa guardada con ID " + idNuevo, Toast.LENGTH_SHORT).show();
            finish(); // vuelve a la lista
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}