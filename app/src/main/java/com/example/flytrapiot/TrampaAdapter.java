package com.example.flytrapiot;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter del RecyclerView: pone los datos de cada Trampa en una fila item_trampa.xml.
 */
public class TrampaAdapter extends RecyclerView.Adapter<TrampaAdapter.TrampaViewHolder> {

    // Avisa a MainActivity qué trampa se presionó
    public interface OnTrampaClickListener {
        void onTrampaClick(Trampa trampa);
    }

    private List<Trampa> trampas;
    private final OnTrampaClickListener listener;

    public TrampaAdapter(List<Trampa> trampas, OnTrampaClickListener listener) {
        this.trampas = trampas;
        this.listener = listener;
    }

    // Reemplaza los datos por los recién leídos de la base de datos
    public void actualizarDatos(List<Trampa> nuevasTrampas) {
        this.trampas = nuevasTrampas;
        notifyDataSetChanged();
    }

    // 1) Crea la vista de una fila a partir de item_trampa.xml
    @NonNull
    @Override
    public TrampaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trampa, parent, false);
        return new TrampaViewHolder(vista);
    }

    // 2) Llena la fila con los datos de la trampa en esa posición
    @Override
    public void onBindViewHolder(@NonNull TrampaViewHolder holder, int position) {
        Trampa trampa = trampas.get(position);

        holder.tvSector.setText("Sector " + trampa.getCodigoSector());
        holder.tvTipo.setText("Trampa " + trampa.getTipoTrampa() + " · ID " + trampa.getId());

        if (trampa.getObservaciones().isEmpty()) {
            holder.tvObservaciones.setText("Sin observaciones");
        } else {
            holder.tvObservaciones.setText(trampa.getObservaciones());
        }

        // Verde si no hay moscas, rojo si hay al menos una
        int capturas = trampa.getNivelCaptura();
        if (capturas == 0) {
            holder.tvCaptura.setText("Sin capturas");
            holder.tvCaptura.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            holder.tvCaptura.setText(capturas == 1 ? "1 captura" : capturas + " capturas");
            holder.tvCaptura.setTextColor(Color.parseColor("#D32F2F"));
        }

        // Al presionar la fila se entrega la trampa (con su id) a MainActivity
        holder.itemView.setOnClickListener(v -> listener.onTrampaClick(trampa));
    }

    // 3) Cantidad de filas
    @Override
    public int getItemCount() {
        return trampas.size();
    }

    // Guarda las vistas de una fila para no buscarlas cada vez
    static class TrampaViewHolder extends RecyclerView.ViewHolder {
        TextView tvSector;
        TextView tvTipo;
        TextView tvObservaciones;
        TextView tvCaptura;

        TrampaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSector = itemView.findViewById(R.id.tvSector);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvObservaciones = itemView.findViewById(R.id.tvObservaciones);
            tvCaptura = itemView.findViewById(R.id.tvCaptura);
        }
    }
}