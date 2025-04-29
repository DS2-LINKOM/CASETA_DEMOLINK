package mx.linkom.caseta_demolink;

/**
 * Created by one on 19/8/16.
 */
public class DashClassGridRondin {

    private int add;
    private String title;
    private String subtitle;
    private String colorCode;


    public DashClassGridRondin(int add, String title, String subtitle, String colorCode) {
        this.add = add;
        this.title = title;
        this.subtitle = subtitle;
        this.colorCode = colorCode;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.title = subtitle;
    }

    public int getImagen() {
        return add;
    }

    public void setImagen(int add) {
        this.add = add;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

}
