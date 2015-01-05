package michaelwagler.setlistmanager;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.joda.time.DateTime;

import michaelwagler.setlistmanager.db.DBHelper;
import michaelwagler.setlistmanager.model.Band;
import michaelwagler.setlistmanager.model.Set;
import michaelwagler.setlistmanager.model.Song;
import michaelwagler.setlistmanager.model.Venue;


public class MainActivity extends Activity {
    private String LOG = "MainActivity";
    private ListView lv;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private String fragmentSubtitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] items = {"sets", "songs", "bands", "venues"};

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        lv = (ListView) findViewById(R.id.left_drawer);

        lv.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, items));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });


        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                 R.string.drawer_open, R.string.drawer_close) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);

                ActionBar aB = getActionBar();
                //aB.setTitle((fragmentTitle!= null)? fragmentTitle: getTitle());
                //aB.setSubtitle(fragmentSubtitle);
                aB.setTitle(getTitle());
                aB.setSubtitle(getSubtitle());


                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                ActionBar aB = getActionBar();
                //fragmentTitle = (aB.getTitle() != null)? aB.getTitle().toString() : null;
                //fragmentSubtitle = (aB.getSubtitle() != null)? aB.getSubtitle().toString(): null;
                //aB.setTitle(R.string.app_name);
                //aB.setSubtitle(null);
                aB.setTitle(R.string.app_name);
                setSubtitle( (aB.getSubtitle()!= null? aB.getSubtitle().toString(): null));
                aB.setSubtitle(null);

                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

        };

        drawerLayout.setDrawerListener(drawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        //aB.setHomeButtonEnabled(true);


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();

    }

    @Override
    protected void onResume() {
        super.onResume();
        openDrawer();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    public void openDrawer() {
        drawerLayout.openDrawer(lv);
    }

    public void closeDrawer() {
        drawerLayout.closeDrawer(lv);
    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = drawerLayout.isDrawerOpen(lv);
        for (int i=0;i<menu.size();i++) {
            menu.getItem(i).setVisible(!drawerOpen);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        Fragment fragment;

        switch (position) {
            case 0:
                // "sets"
                fragment = new SetsFragment();
                break;

            case 1:
                //"songs"
                fragment = new SongsFragment();
                break;

            case 2:
                // "bands"
                fragment = new BandsFragment();
                break;

            case 3:
                // "venues"
                fragment = new VenuesFragment();
                break;

            default:
                fragment = new SetsFragment();
        }

        // Insert the fragment by replacing any existing fragment
        // need to close drawer before setting the content view so that onDrawerClosed doesn't override it
        drawerLayout.closeDrawer(lv);
        setSubtitle(null);
        FragmentManager fragmentManager = getFragmentManager();
        // this should clear anything left in the backstack
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        //lv.setItemChecked(position, true);
    }

    // helper functions for dealing with action bar subtitle and navigation drawer
    public void setSubtitle(String subtitle) {
        fragmentSubtitle = subtitle;
    }

    public String getSubtitle() {
        return fragmentSubtitle;

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // FOR DEBUGGING
        //int id = item.getItemId();
        /*
        if (id == R.id.populate_db) {
            populateDB();
        }
        */

        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    public void populateDB() {
        String deleteSQL = "DELETE FROM song; DELETE FROM setlist; " +
                "DELETE FROM venue; DELETE FROM band; DELETE FROM songs_sets;";

        DBHelper helper = new DBHelper(MainActivity.this);
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        sqlDB.execSQL(deleteSQL);
        sqlDB.close();

        int p1 = (int) helper.createSong(new Song("Along The Way"));
        int p2 = (int) helper.createSong(new Song("Can't Win em All"));
        int p3 = (int) helper.createSong(new Song("Down Country"));
        int p4 = (int) helper.createSong(new Song("Wicked Heart"));
        int p5 = (int) helper.createSong(new Song("Aged in Oak"));

        int r1 = (int) helper.createSong(new Song("Jumping with Symphony Sid"));
        int r2 = (int) helper.createSong(new Song("In a Mellow Tone"));
        int r3 = (int) helper.createSong(new Song("Flying Home"));
        int r4 = (int) helper.createSong(new Song("I Found a New Baby"));

        int o1 = (int) helper.createSong(new Song("Always Look on the Bright Side of Life"));
        int o2 = (int) helper.createSong(new Song("Safety Dance"));

        int biltmore_id = (int) helper.createVenue(new Venue("The Biltmore Cabaret"));
        int stmichael_id = (int) helper.createVenue(new Venue("St Michael's Hall"));
        int mcdonalds_id = (int) helper.createVenue(new Venue("Macdonald's"));

        int ponchos_id = (int) helper.createBand(new Band("Real Ponchos"));
        int rugcutter_id = (int) helper.createBand(new Band("Rugcutter Jazz Band"));

        DateTime now = new DateTime();
        DateTime dt = new DateTime().withDate(2014, 12, 30).withTime(18, 30, 0, 0);

        int s1 = (int) helper.createSet(new Set("ponchos1", ponchos_id, biltmore_id, dt));
        int s2 = (int) helper.createSet(new Set("rugcutters1", rugcutter_id, stmichael_id, now));
        int s3 = (int) helper.createSet(new Set("nobandNoVenue"));
        int s4 = (int) helper.createSet(new Set("twoSongSet"));

        helper.createSongSet(p1, s1, 0);
        helper.createSongSet(p2, s1, 1);
        helper.createSongSet(p3, s1, 2);
        helper.createSongSet(p4, s1, 3);
        helper.createSongSet(p5, s1, 4);

        helper.createSongSet(r1, s2, 0);
        helper.createSongSet(r2, s2, 1);
        helper.createSongSet(r3, s2, 2);
        helper.createSongSet(r4, s2, 3);

        helper.createSongSet(o1, s4, 0);
        helper.createSongSet(o2, s4, 1);
    }

}
