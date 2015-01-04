package michaelwagler.setlistmanager.model;

/**
 * Created by michaelwagler on 2014-12-26.
 */
public class Venue {
    private String name;
    private int id;

    public Venue(String name) {
        this.name = name;
    }
    public Venue() {
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
