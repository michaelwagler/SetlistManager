package michaelwagler.setlistmanager.model;

import org.joda.time.DateTime;

/**
 * Created by michaelwagler on 2014-12-17.
 */
public class Set {

    private int id;
    private String name;
    private DateTime dateTime;
    private int band_id;
    private int venue_id;

    public Set(String name, int band_id, int venue_id, DateTime dateTime) {
        this.name = name;
        this.band_id = band_id;
        this.venue_id = venue_id;
        this.dateTime = dateTime;
    }

    public Set(String name) {
        this.name = name;
    }

    public Set() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBandId() {
        return band_id;
    }

    public void setBandId(int band_id) {
        this.band_id = band_id;
    }

    public int getVenueId() {
        return venue_id;
    }

    public void setVenueId(int venue_id) {
        this.venue_id = venue_id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public String getDateTimeString() {
        if (dateTime != null) {
            return dateTime.toString();
        }
        return null;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    // need this if passing an array of Sets to ArrayAdapter
    public String toString() {
        return this.getName();
    }
}


