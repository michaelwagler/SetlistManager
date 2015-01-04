package michaelwagler.setlistmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


import michaelwagler.setlistmanager.db.DBContract;
import michaelwagler.setlistmanager.db.DBHelper;
import michaelwagler.setlistmanager.model.Venue;


public class VenuesFragment extends ListFragment {

    private DBHelper helper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        updateUI();
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.activity_venues, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        final ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // selected item
                View child = view.findViewById(R.id.venueTextView);

                final String venueName = ((TextView) child).getText().toString();

                String[] options = new String[3];
                options[0] = "view setlists";
                options[1] = "edit";
                options[2] = "delete";
                AlertDialog.Builder builder = new AlertDialog.Builder(VenuesFragment.super.getActivity());

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0:
                                // view setlists

                                Fragment fragment = new SetsFragment();

                                Bundle bundle = new Bundle();
                                bundle.putString("venue", venueName);
                                fragment.setArguments(bundle);
                                // Insert the fragment by replacing any existing fragment
                                FragmentManager fragmentManager = getFragmentManager();

                                fragmentManager.beginTransaction()
                                        .replace(R.id.content_frame, fragment)
                                        .addToBackStack(null)
                                        .commit();

                                break;

                            case 1:
                                // Edit
                                AlertDialog.Builder editBuilder = new AlertDialog.Builder(
                                        VenuesFragment.super.getActivity());
                                editBuilder.setTitle("Edit venue");
                                final EditText editName = new EditText(
                                        VenuesFragment.super.getActivity());
                                editBuilder.setView(editName);
                                editName.setText(venueName, TextView.BufferType.EDITABLE);
                                editBuilder.setPositiveButton("Done editing", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String name = editName.getText().toString();
                                        Venue venue = helper.getVenueByName(venueName);
                                        venue.setName(name);
                                        helper.updateVenue(venue);
                                        updateUI();
                                    }
                                });
                                editBuilder.setNegativeButton("Cancel", null);
                                editBuilder.create().show();
                                break;

                            case 2:
                                // Delete Venue
                                helper.deleteVenue(venueName);
                                updateUI();
                                break;

                            default:
                                break;
                        }
                    }
                });
                builder.create().show();

            }
        });
    }

    public void updateUI() {
        String title = "all venues";
        Activity main = super.getActivity();
        main.setTitle(title);
        main.getActionBar().setTitle(title);

        helper = new DBHelper(VenuesFragment.super.getActivity());
        SQLiteDatabase sqlDB = helper.getReadableDatabase();

        Cursor cursor = sqlDB.query(DBContract.VenueTable.TABLE,
                new String[]{DBContract._ID, DBContract.VenueTable.COLUMN_NAME},
                null, null, null, null, null);

        ListAdapter listAdapter = new SimpleCursorAdapter(
                VenuesFragment.super.getActivity(),
                R.layout.venue_view,
                cursor,
                new String[] {DBContract.BandTable.COLUMN_NAME},
                new int[] {R.id.venueTextView},
                0);
        this.setListAdapter(listAdapter);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_venues, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_new_venue:
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        VenuesFragment.super.getActivity());
                builder.setTitle("Create Venue");
                final EditText nameField = new EditText(
                        VenuesFragment.super.getActivity());

                builder.setView(nameField);

                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Log.d("DeleteActivity", inputField.getText().toString());
                        String name = nameField.getText().toString();

                        Venue venue = new Venue(name);

                        DBHelper db = new DBHelper(VenuesFragment.super.getActivity());

                        db.createVenue(venue);
                        updateUI();
                    }
                });

                builder.setNegativeButton("Cancel", null);
                builder.create().show();
                return true;
            default:
                return false;
        }
    }
}
