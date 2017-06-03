package domain.disponibilidad.entity;

import java.util.HashMap;
import java.util.List;

/**
 * Created by AHernandezS on 3/06/2017.
 */
public class Reservas {

    private static int idCounter = 0;
    HashMap<Integer, Reserva> reservas = new HashMap<>();

    public int guardarReserva(List<DetalleServicio> detalleServicios) {
        reservas.put(idCounter, new Reserva(detalleServicios));
        return idCounter++;
    }

    public void cancelarReserva(int id) {
        reservas.get(id).cancelarReserva();
    }

    public boolean reservaIsActive(int id) {
        return reservas.get(id).reservaActiva;
    }

    public List<DetalleServicio> getDetalleServicio(int id) {
        return reservas.get(id).detalleServicio;
    }

    public boolean exist(int reservaID) {
        return reservas.get(reservaID) != null;
    }

    private class Reserva {
        private boolean reservaActiva = true;
        private List<DetalleServicio> detalleServicio;

        public Reserva(List<DetalleServicio> detalleServicio) {
            this.reservaActiva = reservaActiva;
            this.detalleServicio = detalleServicio;
        }

        public void cancelarReserva () {
            reservaActiva = false;
        }
    }
}
