package com.example.flytrapiot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Crea la base de datos SQLite "flytrap_db" con la tabla "trampas"
 * y tiene los métodos del CRUD: insertar, listar, buscar por id, actualizar y eliminar.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String NOMBRE_BD = "flytrap_db";
    public static final int VERSION_BD = 1;

    public static final String TABLA_TRAMPAS = "trampas";
    public static final String COL_ID = "id";
    public static final String COL_CODIGO_SECTOR = "codigo_sector";
    public static final String COL_TIPO_TRAMPA = "tipo_trampa";
    public static final String COL_NIVEL_CAPTURA = "nivel_captura";
    public static final String COL_OBSERVACIONES = "observaciones";

    // SQL entregado en el enunciado
    private static final String SQL_CREAR_TABLA =
            "CREATE TABLE trampas (" +
                    " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " codigo_sector TEXT NOT NULL," +
                    " tipo_trampa TEXT NOT NULL," +
                    " nivel_captura INTEGER NOT NULL," +
                    " observaciones TEXT )";

    public DBHelper(Context context) {
        super(context, NOMBRE_BD, null, VERSION_BD);
    }

    // Se ejecuta una sola vez, cuando la base de datos todavía no existe
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREAR_TABLA);
    }

    // Se ejecuta si cambias VERSION_BD: borra la tabla y la vuelve a crear
    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAntigua, int versionNueva) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_TRAMPAS);
        onCreate(db);
    }

    // ---------- CREAR (INSERT) ----------
    // Devuelve el id nuevo, o -1 si hubo un error
    public long insertarTrampa(Trampa trampa) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(COL_CODIGO_SECTOR, trampa.getCodigoSector());
        valores.put(COL_TIPO_TRAMPA, trampa.getTipoTrampa());
        valores.put(COL_NIVEL_CAPTURA, trampa.getNivelCaptura());
        valores.put(COL_OBSERVACIONES, trampa.getObservaciones());

        return db.insert(TABLA_TRAMPAS, null, valores);
    }

    // ---------- LEER (SELECT) ----------
    // Devuelve todas las trampas, de la más nueva a la más antigua
    public List<Trampa> obtenerTrampas() {
        List<Trampa> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLA_TRAMPAS + " ORDER BY " + COL_ID + " DESC", null);
        while (cursor.moveToNext()) {
            lista.add(leerTrampa(cursor));
        }
        cursor.close();
        return lista;
    }

    // Busca una trampa por su id. Devuelve null si no existe
    public Trampa obtenerTrampaPorId(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Trampa trampa = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLA_TRAMPAS + " WHERE " + COL_ID + " = ?",
                new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            trampa = leerTrampa(cursor);
        }
        cursor.close();
        return trampa;
    }

    // ---------- ACTUALIZAR (UPDATE) ----------
    // Devuelve cuántas filas cambió (1 si salió bien)
    public int actualizarTrampa(Trampa trampa) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(COL_CODIGO_SECTOR, trampa.getCodigoSector());
        valores.put(COL_TIPO_TRAMPA, trampa.getTipoTrampa());
        valores.put(COL_NIVEL_CAPTURA, trampa.getNivelCaptura());
        valores.put(COL_OBSERVACIONES, trampa.getObservaciones());

        return db.update(TABLA_TRAMPAS, valores, COL_ID + " = ?",
                new String[]{String.valueOf(trampa.getId())});
    }

    // ---------- ELIMINAR (DELETE) ----------
    // Devuelve cuántas filas borró (1 si salió bien)
    public int eliminarTrampa(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLA_TRAMPAS, COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // Convierte la fila actual del Cursor en un objeto Trampa
    private Trampa leerTrampa(Cursor cursor) {
        int colObs = cursor.getColumnIndexOrThrow(COL_OBSERVACIONES);
        String observaciones = cursor.isNull(colObs) ? "" : cursor.getString(colObs);

        return new Trampa(
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_CODIGO_SECTOR)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_TIPO_TRAMPA)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_NIVEL_CAPTURA)),
                observaciones
        );
    }
}