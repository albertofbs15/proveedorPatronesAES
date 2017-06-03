package domain.disponibilidad;

import domain.disponibilidad.commands.CancelarReserva;
import domain.disponibilidad.commands.ConsultarDisponibilidad;
import domain.disponibilidad.commands.ReservarServicio;
import domain.disponibilidad.entity.DetalleServicio;
import domain.disponibilidad.entity.RespuestaConsultaDisponibilidadConID;
import domain.disponibilidad.entity.RespuestaOperacion;
import domain.disponibilidad.entity.RespuestaConsultaDisponibilidad;
import servicios.datos.PersistenciaService;

import java.util.List;

/**
 * Created by AHernandezS on 3/05/2017.
 */
public class DisponibilidadServiceProvider implements DisponibilidadService {

    PersistenciaService persistenciaService;

    public DisponibilidadServiceProvider(PersistenciaService persistenciaService) {
        this.persistenciaService = persistenciaService;
    }

    @Override
    public RespuestaConsultaDisponibilidad consultarDisponibilidad(ConsultarDisponibilidad consultarDisponibilidad) {
        List<DetalleServicio> list =
                persistenciaService.consultarDisponibilidad(consultarDisponibilidad.getDestino(), consultarDisponibilidad.getFechaInicial(), consultarDisponibilidad.getFechaFinal());

        RespuestaConsultaDisponibilidad respuesta = new RespuestaConsultaDisponibilidad();
        respuesta.setNombreConvenio(persistenciaService.getNombreConvenio());
        respuesta.setTipoServivio(persistenciaService.getTipoServicio());

        respuesta.setCostoTotal(list.stream().mapToLong(optionalDetalle -> optionalDetalle.getCosto()).sum());
        respuesta.setDetalles(list);

        return respuesta;
    }

    @Override
    public RespuestaConsultaDisponibilidadConID reservarServicio(ReservarServicio reservarServicio) {
        RespuestaConsultaDisponibilidadConID respuesta = persistenciaService.reservarServicio(reservarServicio.getDestino(), reservarServicio.getFechaInicial(), reservarServicio.getFechaFinal());
        respuesta.setNombreConvenio(persistenciaService.getNombreConvenio());
        respuesta.setTipoServivio(persistenciaService.getTipoServicio());
        if (respuesta.getId()>-1)
            respuesta.setCostoTotal(respuesta.getDetalles().stream().mapToLong(optionalDetalle -> optionalDetalle.getCosto()).sum());
        return respuesta;
    }

    @Override
    public RespuestaOperacion cancelarReserva(CancelarReserva cancelarReserva) {
        RespuestaOperacion respuestaOperacion = new RespuestaOperacion();
        respuestaOperacion.setIdReserva(cancelarReserva.getIdReserva());
        respuestaOperacion.setRespuesta(persistenciaService.cancelarServicio(cancelarReserva.getIdReserva()));
        return respuestaOperacion;
    }
}
