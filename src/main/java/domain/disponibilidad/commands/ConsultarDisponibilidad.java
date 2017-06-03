package domain.disponibilidad.commands;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

/**
 * Created by AHernandezS on 2/06/2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsultarDisponibilidad implements Serializable{

    @JsonProperty
    private String destino;

    @JsonProperty
    private LocalDate fechaInicial;

    @JsonProperty
    private LocalDate fechaFinal;

    public String getDestino() {
        return destino;
    }

    public LocalDate getFechaInicial() {
        return fechaInicial;
    }

    public LocalDate getFechaFinal() {
        return fechaFinal;
    }
}
