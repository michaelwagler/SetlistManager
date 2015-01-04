package michaelwagler.setlistmanager.model;

/**
 * Created by michaelwagler on 2014-12-17.
 */
public class Song {

    private int id;
    private String name;
    private int length;

    // constructors

    public Song(String name, int length) {
        this.name = name;
        this.length = length;
    }

    public Song(String name) {
        this.name = name;
    }

    public Song() {

    }

    // methods

    // setters
    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLength(int length) {
        this.length = length;
    }

    // getters

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getLength() {
        return length;
    }

    // need this if passing a list of songs to ArrayAdapter
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Song song = (Song) o;

        if (name != null ? !name.equals(song.name) : song.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
