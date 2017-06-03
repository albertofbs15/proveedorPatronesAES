package http;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.*;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import domain.disponibilidad.DisponibilidadService;
import domain.disponibilidad.DisponibilidadServiceProvider;
import domain.disponibilidad.commands.CancelarReserva;
import domain.disponibilidad.commands.ConsultarDisponibilidad;
import domain.disponibilidad.commands.ReservarServicio;
import domain.disponibilidad.entity.Datos;
import domain.disponibilidad.entity.RespuestaConsultaDisponibilidadConID;
import domain.disponibilidad.entity.RespuestaOperacion;
import domain.disponibilidad.entity.RespuestaConsultaDisponibilidad;
import servicios.aviajar.AviajarService;
import servicios.aviajar.AviajarServiceProvider;
import servicios.datos.*;
import util.JacksonJdk8;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.concurrent.CompletionStage;

/**
 * Created by AHernandezS on 22/03/2017.
 */

public class HttpAesDirective extends AllDirectives {

    public static DisponibilidadService disponibilidadService;
    public static AviajarService aviajarService = new AviajarServiceProvider();
    public static String host = "localhost";
    public static int port = 9090;

    public static String hostAviajar = "localhost";
    public static int portAviajar = 9090;

    public static void main(String[] args) throws Exception {

        if (args.length > 2) {
            Datos.FILE_PROVEEDOR = args[0];
            hostAviajar = args[1];
            portAviajar = Integer.parseInt(args[2]);
        }

        //host = getIp();

        Datos.cargarDatos();
        aviajarService.registrarProveedor(host, port);
        PersistenciaService persistenciaService = null;
        if (Datos.datos.getServicio().equals("aereo"))
            persistenciaService = new aereoProvider();
        else if (Datos.datos.getServicio().equals("terrestre"))
            persistenciaService = new terrestreProvider();
        else if (Datos.datos.getServicio().equals("alojamiento"))
            persistenciaService = new alojamientoProvider();
        else if (Datos.datos.getServicio().equals("paseo"))
            persistenciaService = new paseosProvider();


        disponibilidadService = new DisponibilidadServiceProvider(persistenciaService);
        ActorSystem system = ActorSystem.create("routes");

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        HttpAesDirective app = new HttpAesDirective();

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.createRoute().flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow,
                ConnectHttp.toHost(host, port), materializer);

        System.out.println("Server online at http://"+host+":"+port+"/\nPress RETURN to stop...");
        System.in.read();

        binding.thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate());
    }

    private Route createRoute() {
        return route(
                pathPrefix("servicios", () ->
                        pathPrefix("disponibilidad", () ->
                                route(
                                        post(() ->
                                                route(
                                        path("consultar" , () -> entity(JacksonJdk8.unmarshaller(ConsultarDisponibilidad.class), consultarDisponibilidad -> handleConsultarDisponbilidad(consultarDisponibilidad))),
                                        path("reservar" , () -> entity(JacksonJdk8.unmarshaller(ReservarServicio.class), reservarServicio -> handleReservarServicio(reservarServicio))),
                                        pathPrefix(reservaID ->  route(path("cancelar", () -> handleCancelarReserva(reservaID)))))
                                ))
                        )
                )
        );
    }

    private Route handleConsultarDisponbilidad(ConsultarDisponibilidad consultarDisponibilidad) {
        System.out.println(LocalDate.now() + ": handleConsultarDisponbilidad ");
        RespuestaConsultaDisponibilidad respuesta = disponibilidadService.consultarDisponibilidad(consultarDisponibilidad);;
        return complete(StatusCodes.OK, respuesta, Jackson.<RespuestaConsultaDisponibilidad>marshaller());
    }

    private Route handleReservarServicio(ReservarServicio reservarServicio) {
        System.out.println(LocalDate.now() + ": handleReservarServicio ");
        RespuestaConsultaDisponibilidadConID respuesta = disponibilidadService.reservarServicio(reservarServicio);
        return complete(StatusCodes.OK, respuesta, Jackson.<RespuestaConsultaDisponibilidadConID>marshaller());
    }

    private Route handleCancelarReserva(String reservaID) {
        CancelarReserva cancelarReserva = new CancelarReserva();
        cancelarReserva.setIdReserva(Integer.parseInt(reservaID));
        System.out.println(LocalDate.now() + ": handleCancelarReserva ");
        RespuestaOperacion respuesta = disponibilidadService.cancelarReserva(cancelarReserva);
        return complete(StatusCodes.OK, respuesta, Jackson.<RespuestaOperacion>marshaller());
    }

    private static String getIp() {
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            System.out.println("Current IP address : " + ip.getHostAddress());
            return  ip.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

}
