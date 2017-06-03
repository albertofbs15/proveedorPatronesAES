package servicios.datos;

import domain.disponibilidad.entity.Datos;
import domain.disponibilidad.entity.DetalleServicio;
import domain.disponibilidad.entity.Reservas;
import domain.disponibilidad.entity.RespuestaConsultaDisponibilidadConID;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by alber on 5/24/2017.
 */
public class aereoProvider implements PersistenciaService {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private Reservas reservas = new Reservas();
    private static Datos datos = Datos.datos;

    @Override
    public List<DetalleServicio> consultarDisponibilidad(String destino, LocalDate fechaInicial, LocalDate fechaFinal) {

        Optional<DetalleServicio> ida = consultarDisponibilidad(destino, fechaInicial);
        Optional<DetalleServicio> vuelta = consultarDisponibilidad("bogota", fechaFinal);

        if (ida.isPresent() && vuelta.isPresent()) {
            return Arrays.asList(ida.get(), vuelta.get());
        }
        return Collections.EMPTY_LIST;

    }

    private Optional<DetalleServicio> consultarDisponibilidad(String destino, LocalDate fecha) {
        return
        datos.getDisponibilidad().getItem().stream()
                .filter(item -> fecha.equals(item.getDia().toGregorianCalendar().toZonedDateTime().toLocalDate())
                        && destino.equals(item.getDestino()) && item.getDisponibles().intValue() > 0)
                .map(item -> new DetalleServicio(item.getDestino(), fecha.format(DateTimeFormatter.ofPattern(DATE_FORMAT)), item.getCostos().longValue()))
                .findFirst();
    }

    @Override
    public Boolean cancelarServicio(int idReserva) {
        if (reservas.exist(idReserva) && reservas.reservaIsActive(idReserva)) {
            reservas.getDetalleServicio(idReserva).stream().forEach(detalle ->
                    modificarDisponibilidad(detalle.getDestino(), LocalDate.parse(detalle.getFecha(), formatter), BigInteger.ONE));
            reservas.cancelarReserva(idReserva);
            return true;
        }
        return false;
    }

    @Override
    public RespuestaConsultaDisponibilidadConID reservarServicio(String destino, LocalDate fechaInicial, LocalDate fechaFinal) {
        List<DetalleServicio> detalleServicios = consultarDisponibilidad(destino, fechaInicial, fechaFinal);
        RespuestaConsultaDisponibilidadConID respuesta = new RespuestaConsultaDisponibilidadConID();
        if (detalleServicios.isEmpty()) {
            respuesta.setId(-1);
        } else {

            respuesta.setDetalles(Stream.iterate(fechaInicial, d -> d.plusDays(1))
                    .limit(ChronoUnit.DAYS.between(fechaInicial, fechaFinal) + 1)
                    .map(date -> modificarDisponibilidad(destino, date, BigInteger.valueOf(-1))).collect(Collectors.toList())
            );
            respuesta.setId(reservas.guardarReserva(detalleServicios));
        }
        return respuesta;
    }

    private DetalleServicio modificarDisponibilidad(String destino, LocalDate fecha, BigInteger cantidad) {
        final DetalleServicio[] detalleServicio = {null};
        datos.getDisponibilidad().getItem().stream()
                .filter(item -> fecha.equals(item.getDia().toGregorianCalendar().toZonedDateTime().toLocalDate()) && destino.equals(item.getDestino()))
                .forEach(item -> {item.setDisponibles(item.getDisponibles().add(cantidad)); detalleServicio[0] = new DetalleServicio(item.getDestino(), gregorianToString(item.getDia()), item.getCostos().longValue());});

        return detalleServicio[0];
    }

    private LocalDate stringToLocalDate (String localDate) {
        return LocalDate.parse(localDate, formatter);
    }

    private String localDateToString (LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    private LocalDate gregorianToLocalDate (XMLGregorianCalendar gregorian) {
        return gregorian.toGregorianCalendar().toZonedDateTime().toLocalDate();
    }

    private String gregorianToString (XMLGregorianCalendar gregorian) {
        return localDateToString(gregorianToLocalDate(gregorian));
    }

    @Override
    public String getNombreConvenio() {
        return datos.getEmpresa();
    }

    @Override
    public String getTipoServicio() {
        return datos.getServicio();
    }

}
