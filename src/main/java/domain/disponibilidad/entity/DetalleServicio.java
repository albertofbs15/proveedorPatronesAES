package domain.disponibilidad.entity;

import java.time.LocalDate;

/**
 * Created by AHernandezS on 2/06/2017.
 */
public class DetalleServicio {
    private String destino;
    private String fecha;
    private long costo;

    public DetalleServicio(String destino, String fecha, long costo) {
        this.destino = destino;
        this.fecha = fecha;
        this.costo = costo;
    }

    public String getDestino() {
        return destino;
    }

    public String getFecha() {
        return fecha;
    }

    public long getCosto() {
        return costo;
    }
}
