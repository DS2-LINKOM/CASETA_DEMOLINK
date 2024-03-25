package mx.linkom.caseta_demolink.adaptadores;

public class ListasClassReservaciones {

    private String amenidad;
    private String fecha;
    private String up;
    private int id_amenidad;

    public ListasClassReservaciones(String amenidad, String fecha, String up, int id_amenidad) {
        this.amenidad = amenidad;
        this.fecha = fecha;
        this.up = up;
        this.id_amenidad = id_amenidad;
    }

    public String getAmenidad() {
        return amenidad;
    }

    public void setAmenidad(String amenidad) {
        this.amenidad = amenidad;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getUp() {
        return up;
    }

    public void setUp(String up) {
        this.up = up;
    }

    public int getId_amenidad() {
        return id_amenidad;
    }

    public void setId_amenidad(int id_amenidad) {
        this.id_amenidad = id_amenidad;
    }
}
