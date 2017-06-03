package domain.disponibilidad;

import domain.disponibilidad.commands.CancelarReserva;
import domain.disponibilidad.commands.ConsultarDisponibilidad;
import domain.disponibilidad.commands.ReservarServicio;
import domain.disponibilidad.entity.RespuestaConsultaDisponibilidadConID;
import domain.disponibilidad.entity.RespuestaOperacion;
import domain.disponibilidad.entity.RespuestaConsultaDisponibilidad;

import java.util.concurrent.CompletableFuture;

/**
 * Created by alber on 5/24/2017.
 */
public interface DisponibilidadService {
    RespuestaConsultaDisponibilidad consultarDisponibilidad(ConsultarDisponibilidad consultarDisponibilidad);
    RespuestaConsultaDisponibilidadConID reservarServicio(ReservarServicio reservarServicio);
    RespuestaOperacion cancelarReserva(CancelarReserva cancelarReserva);
}
