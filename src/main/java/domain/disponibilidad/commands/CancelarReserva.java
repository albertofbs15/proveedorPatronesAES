package domain.disponibilidad.commands;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by AHernandezS on 2/06/2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class CancelarReserva implements Serializable{

    @JsonProperty
    private int idReserva;

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }
}
