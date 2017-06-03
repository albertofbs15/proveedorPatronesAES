package servicios.datos;

import domain.disponibilidad.entity.DetalleServicio;
import domain.disponibilidad.entity.RespuestaConsultaDisponibilidadConID;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by alber on 5/24/2017.
 */
public interface PersistenciaService {
    String getNombreConvenio();
    String getTipoServicio();
    List<DetalleServicio> consultarDisponibilidad(String destino, LocalDate fechaInicial, LocalDate fechaFinal);
    RespuestaConsultaDisponibilidadConID reservarServicio(String destino, LocalDate fechaInicial, LocalDate fechaFinal);
    Boolean cancelarServicio(int idReserva);
}

