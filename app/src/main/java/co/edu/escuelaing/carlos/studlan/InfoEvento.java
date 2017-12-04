package co.edu.escuelaing.carlos.studlan;

/**
 * Created by 2105537 on 12/4/17.
 */

public class InfoEvento {
    private String mensajeEvento;
    private long fecha;


    public InfoEvento(String mensajeEvento, long fecha) {
        this.mensajeEvento = mensajeEvento;
        this.fecha = fecha;
    }

    public String getMensajeEvento() {
        return mensajeEvento;
    }

    public void setMensajeEvento(String mensajeEvento) {
        this.mensajeEvento = mensajeEvento;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }
}