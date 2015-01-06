package michaelwagler.setlistmanager;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import michaelwagler.setlistmanager.db.DBContract;
import michaelwagler.setlistmanager.db.DBHelper;
import michaelwagler.setlistmanager.model.Band;
import michaelwagler.setlistmanager.model.Set;
import michaelwagler.setlistmanager.model.Venue;


public class SetsFragment extends ListFragment {
    private DBHelper helper;
    private final String LOG = "SetsFragment";

    private Band band;
    private Venue venue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);

        helper = DBHelper.getInstance(super.getActivity());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String bandName = bundle.getString("band");
            if (bandName != null) {
                band = helper.getBandByName(bandName);
            }
            String venueName = bundle.getString("venue");
            if (venueName != null) {
                venue = helper.getVenueByName(venueName);
            }
        }
        updateUI();

        return inflater.inflate(R.layout.fragment_sets, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ListView lv = getListView();


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                View child = view.findViewById(R.id.setTextView);

                String setlist = ((TextView) child).getText().toString();

                Fragment fragment = new SingleSetFragment();

                Bundle bundle = new Bundle();
                bundle.putString("set_id", String.valueOf(helper.getSetByName(setlist).getId()));
                fragment.setArguments(bundle);
                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();

                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .commit();

                //ListView lv = (ListView) SetsFragment.super.getActivity().
                //        findViewById(R.id.left_drawer);
                //lv.setItemChecked(0, true);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                View child = view.findViewById(R.id.setTextView);

                final String setName = ((TextView) child).getText().toString();
                final Set thisSet = helper.getSetByName(setName);
                String[] options = new String[2];

                options[0] = "edit";
                options[1] = "delete";

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        SetsFragment.super.getActivity());

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0:
                                //EDIT
                                Fragment fragment = new NewSetFragment();

                                Bundle bundle = new Bundle();
                                bundle.putString("set_id", String.valueOf(thisSet.getId()));
                                fragment.setArguments(bundle);
                                // Insert the fragment by replacing any existing fragment
                                FragmentManager fragmentManager = getFragmentManager();

                                fragmentManager.beginTransaction()
                                        .replace(R.id.content_frame, fragment)
                                        .addToBackStack(null)
                                        .commit();

                                break;

                            case 1:
                                // Delete Set

                                AlertDialog.Builder builder = new AlertDialog.Builder(
                                        SetsFragment.super.getActivity());
                                builder.setTitle("Delete Set");
                                builder.setMessage("Are you sure you want to delete this set?");
                                builder.setNegativeButton("Cancel", null);
                                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        helper.deleteSet(setName);
                                        updateUI();
                                    }
                                });

                                builder.create().show();
                                break;

                            default:
                                break;
                        }
                    }
                });
                builder.create().show();
                return true;
            }
        });


    }

    private void updateUI() {
        SQLiteDatabase sqlDB = helper.getReadableDatabase();
        Cursor cursor;
        SimpleCursorAdapter cursorAdapter;

        Activity main = super.getActivity();
        ActionBar aB = main.getActionBar();
        aB.setTitle(getString(R.string.sets_activity_label));
        main.setTitle(R.string.sets_activity_label);

        // Branch depending on if we're viewing all sets or just one band's sets, or one venue's sets
        if (band != null) {
            // viewing sets by band
            aB.setSubtitle(band.getName());

            String queryString = "SELECT s._id, s.name as set_name, s.datetime, b.name as band_name FROM " +
                    DBContract.SetTable.TABLE + " s JOIN "
                    + DBContract.BandTable.TABLE + " b ON b._id = s.band_id WHERE s.band_id=? ORDER BY s.datetime DESC" ;

            cursor = sqlDB.rawQuery(queryString,
                    new String[]{String.valueOf(band.getId())});


            cursorAdapter = new SimpleCursorAdapter(
                    SetsFragment.super.getActivity(),
                    R.layout.set_view,
                    cursor,
                    new String[] {"set_name", "band_name", DBContract.SetTable.COLUMN_DATETIME},
                    new int[] {R.id.setTextView, R.id.setBandView, R.id.setDateView},
                    0);
        }


        else if (venue != null) {
            // viewing sets by venue
            aB.setSubtitle(venue.getName());

            String queryString = "SELECT s._id, s.name as set_name, s.datetime, v.name as venue_name, b.name as band_name FROM " +
                    DBContract.SetTable.TABLE + " s JOIN "
                    + DBContract.VenueTable.TABLE + " v ON v._id = s.venue_id LEFT JOIN " +
                    DBContract.BandTable.TABLE + " b ON b._id = s.band_id WHERE s.venue_id=? ORDER BY s.datetime DESC";

            Log.d(LOG, "queryString:" + queryString);

            cursor = sqlDB.rawQuery(queryString,
                    new String[]{String.valueOf(venue.getId())});


            cursorAdapter = new SimpleCursorAdapter(
                    SetsFragment.super.getActivity(),
                    R.layout.set_view,
                    cursor,
                    new String[] {"set_name", "band_name", DBContract.SetTable.COLUMN_DATETIME},
                    new int[] {R.id.setTextView, R.id.setBandView, R.id.setDateView},
                    0);

        }

        else {
            // viewing all sets
            aB.setTitle("all sets");
            main.setTitle("all sets");
            aB.setSubtitle(null);

            String queryString = "SELECT s._id, s.name as set_name, s.datetime, b.name as band_name FROM " +
                    DBContract.SetTable.TABLE + " s LEFT JOIN "
                    + DBContract.BandTable.TABLE + " b ON b._id = s.band_id ORDER BY s.datetime DESC";

            cursor = sqlDB.rawQuery(queryString, null);

            cursorAdapter = new SimpleCursorAdapter(
                    SetsFragment.super.getActivity(),
                    R.layout.set_view,
                    cursor,
                    new String[] {"set_name", "band_name", DBContract.SetTable.COLUMN_DATETIME},
                    new int[] {R.id.setTextView, R.id.setBandView, R.id.setDateView},
                    0);

        }
        // make it so date displays in the list in the correct format
        cursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.setDateView) {
                    String text;
                    DateTime dt = helper.getSetDateTimeFromCursor(cursor);
                    if (dt==null) {
                        text = "";

                    }
                    else {
                       DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yy, h:mmaa");
                       text = dt.toString(fmt);
                    }
                    ((TextView) view).setText(text);
                    return true;
                }
                return false;
            }

        });

        this.setListAdapter(cursorAdapter);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_sets, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch(item.getItemId()) {
            case R.id.action_add_set:

                //View child = view.findViewById(R.id.setTextView);

                //String setlist = ((TextView) child).getText().toString();

                Fragment fragment = new NewSetFragment();
                FragmentManager fragmentManager = getFragmentManager();

                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .commit();

                return true;


            default:
                return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

}
