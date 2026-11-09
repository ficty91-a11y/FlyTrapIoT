package com.example.flytrapiot;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

/**
 * Pantalla "Detalle de trampa" (ACTUALIZAR y ELIMINAR).
 * Recibe el id por Intent, carga la trampa desde SQLite, la actualiza o la elimina.
 */
public class EditarTrampaActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private int idTrampa;

    private TextView tvIdTrampa;
    private EditText etCodigoSector;
    private EditText etNivelCaptura;
    private EditText etObservaciones;
    private Spinner spTipoTrampa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_trampa);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return insets;
        });

        dbHelper = new DBHelper(this);

        tvIdTrampa = findViewById(R.id.tvIdTrampa);
        etCodigoSector = findViewById(R.id.etCodigoSector);
        etNivelCaptura = findViewById(R.id.etNivelCaptura);
        etObservaciones = findViewById(R.id.etObservaciones);
        spTipoTrampa = findViewById(R.id.spTipoTrampa);

        ArrayAdapter<String> adaptadorTipos = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, Trampa.TIPOS);
        adaptadorTipos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoTrampa.setAdapter(adaptadorTipos);

        // 1. Recibir el id que mandó MainActivity con putExtra
        idTrampa = getIntent().getIntExtra("id_trampa", -1);

        // 2. Buscar esa trampa en SQLite
        Trampa trampa = dbHelper.obtenerTrampaPorId(idTrampa);
        if (trampa == null) {
            Toast.makeText(this, "No se encontró la trampa", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 3. Mostrar sus datos en el formulario
        mostrarDatos(trampa);

        Button btnActualizar = findViewById(R.id.btnActualizar);
        btnActualizar.setOnClickListener(v -> actualizarTrampa());

        Button btnEliminar = findViewById(R.id.btnEliminar);
        btnEliminar.setOnClickListener(v -> eliminarTrampa());
    }

    private void mostrarDatos(Trampa trampa) {
        tvIdTrampa.setText("ID de la trampa: " + trampa.getId());
        etCodigoSector.setText(trampa.getCodigoSector());
        etNivelCaptura.setText(String.valueOf(trampa.getNivelCaptura()));
        etObservaciones.setText(trampa.getObservaciones());

        // Dejar marcado en el Spinner el tipo guardado
        for (int i = 0; i < Trampa.TIPOS.length; i++) {
            if (Trampa.TIPOS[i].equals(trampa.getTipoTrampa())) {
                spTipoTrampa.setSelection(i);
            }
        }
    }

    // ---------- ACTUALIZAR ----------
    private void actualizarTrampa() {
        String codigoSector = etCodigoSector.getText().toString().trim().toUpperCase(Locale.ROOT);
        String tipoTrampa = spTipoTrampa.getSelectedItem().toString();
        String textoNivel = etNivelCaptura.getText().toString().trim();
        String observaciones = etObservaciones.getText().toString().trim();

        if (codigoSector.isEmpty()) {
            etCodigoSector.setError("Ingresa el código del sector");
            etCodigoSector.requestFocus();
            Toast.makeText(this, "Falta el código de sector", Toast.LENGTH_SHORT).show();
            return;
        }
        if (textoNivel.isEmpty()) {
            etNivelCaptura.setError("Ingresa el nivel de captura");
            etNivelCaptura.requestFocus();
            Toast.makeText(this, "Falta el nivel de captura", Toast.LENGTH_SHORT).show();
            return;
        }
        int nivelCaptura = Integer.parseInt(textoNivel);

        // El UPDATE se hace sobre el id recibido por Intent
        Trampa editada = new Trampa(idTrampa, codigoSector, tipoTrampa, nivelCaptura, observaciones);
        int filas = dbHelper.actualizarTrampa(editada);

        if (filas > 0) {
            Toast.makeText(this, "Trampa ID " + idTrampa + " actualizada", Toast.LENGTH_SHORT).show();
            finish(); // vuelve a la lista
        } else {
            Toast.makeText(this, "No se pudo actualizar", Toast.LENGTH_SHORT).show();
        }
    }

    // ---------- ELIMINAR (sin diálogo de confirmación) ----------
    private void eliminarTrampa() {
        int filas = dbHelper.eliminarTrampa(idTrampa);

        if (filas > 0) {
            Toast.makeText(this, "Trampa ID " + idTrampa + " eliminada", Toast.LENGTH_SHORT).show();
            finish(); // vuelve a la lista
        } else {
            Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}