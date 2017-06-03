package servicios.aviajar;

import akka.actor.ActorSystem;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.disponibilidad.entity.Datos;
import http.HttpAesDirective;
import http.core.HttpServiceClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

/**
 * Created by alber on 5/24/2017.
 */
public class AviajarServiceProvider implements AviajarService {
    private static final ActorSystem system = ActorSystem.create();
    private final int BUFFER_SIZE = 5000;
    final HttpServiceClient httpClient;

    public AviajarServiceProvider() {
        this.httpClient = new HttpServiceClient(system, BUFFER_SIZE);
    }

    @Override
//    public CompletableFuture<String> registrarProveedor(String host, String port) {
//        String uri = "http://localhost:9080";
//        return httpClient.doGet(uri, String.class).toCompletableFuture();
//    }

    public void registrarProveedor(String host, int port){
        try {
            URL url = new URL("http://localhost:9080/servicios/registrarProveedor");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            String input = "{\"tipoServicio\":\""+ Datos.datos.getServicio()+"\",\"host\":\""+HttpAesDirective.host+"\",\"puerto\":\""+HttpAesDirective.port+"\"}";
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String output;
            StringBuffer sb;
            sb = new StringBuffer();
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            conn.disconnect();
        } catch (JsonGenerationException e){
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        }catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public Respuesta pagar(Pago pago){
//        Respuesta response = new Respuesta();
//        Factura factura= new Factura();
//        factura.setReferenciaFactura(pago.getFactura().getReferenciaFactura());
//        response.setFactura(factura);
//        response.setMensaje("COMPLETO!");
//        try {
//            URL url = new URL("http://localhost:8080/RESTfulExample/r/router/pagar");
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setDoOutput(true);
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Type", "application/json");
//            String input = "{\"referenciaFactura\":"+pago.getFactura().getReferenciaFactura()+",\"totalPagar\":\""+pago.getFactura().getValor()+"\"}";
//            OutputStream os = conn.getOutputStream();
//            os.write(input.getBytes());
//            os.flush();
//            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
//                throw new RuntimeException("Failed : HTTP error code : "
//                        + conn.getResponseCode());
//            }
//            BufferedReader br = new BufferedReader(new InputStreamReader(
//                    (conn.getInputStream())));
//            String output;
//            StringBuffer sb;
//            sb = new StringBuffer();
//            while ((output = br.readLine()) != null) {
//                sb.append(output);
//            }
//            ObjectMapper mapper = new ObjectMapper();
//            response = mapper.readValue(output, Respuesta.class);
//            conn.disconnect();
//        } catch (JsonGenerationException e){
//            e.printStackTrace();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//        }catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return response;
//    }
}
