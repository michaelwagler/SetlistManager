package michaelwagler.setlistmanager.model;

/**
 * Created by michaelwagler on 2014-12-22.
 */
public class Band {

    private String name;
    private int id;

    public Band(String name) {
        this.name = name;
    }

    public Band() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString() {
        return getName();
    }


}
