package michaelwagler.setlistmanager;

import android.app.Application;
import android.test.ApplicationTestCase;

import michaelwagler.setlistmanager.model.Band;
import michaelwagler.setlistmanager.model.Set;
import michaelwagler.setlistmanager.model.Song;
import michaelwagler.setlistmanager.model.Venue;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);

    /*
    public void populateDB() {
        int p1 = (int) createSong(new Song("Along The Way"));
        int p2 = (int) createSong(new Song("Can't Win em All"));
        int p3 = (int) createSong(new Song("Down Country"));
        int p4 = (int) createSong(new Song("Wicked Heart"));
        int p5 = (int) createSong(new Song("Aged in Oak"));

        int r1 = (int) createSong(new Song("Jumping with Symphony Sid"));
        int r2 = (int) createSong(new Song("In a Mellow Tone"));
        int r3 = (int) createSong(new Song("Flying Home"));
        int r4 = (int) createSong(new Song("I Found a New Baby"));

        int o1 = (int) createSong(new Song("Always Look on the Bright Side of Life"));
        int o2 = (int) createSong(new Song("Safety Dance"));

        int biltmore_id = (int) createVenue(new Venue("The Biltmore Cabaret"));
        int stmichael_id = (int) createVenue(new Venue("St Michael's Hall"));
        int mcdonalds_id = (int) createVenue(new Venue("Macdonald's"));

        int ponchos_id = (int) createBand(new Band("Real Ponchos"));
        int rugcutter_id = (int) createBand(new Band("Rugcutter Jazz Band"));

        int s1 = (int) createSet(new Set("ponchos1", ponchos_id, biltmore_id ));
        int s2 = (int) createSet(new Set("rugcutters1", rugcutter_id, stmichael_id));
        int s3 = (int) createSet(new Set("nobandNoVenue"));
        int s4 = (int) createSet(new Set("twoSongSet"));

        createSongSet(p1, s1, 0);
        createSongSet(p2, s1, 1);
        createSongSet(p3, s1, 2);
        createSongSet(p4, s1, 3);
        createSongSet(p5, s1, 4);

        createSongSet(r1, s2, 0);
        createSongSet(r2, s2, 1);
        createSongSet(r3, s2, 2);
        createSongSet(r4, s2, 3);

        createSongSet(o1, s4, 0);
        createSongSet(o2, s4, 1);
    } */

    }
}