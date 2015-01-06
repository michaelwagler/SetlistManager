package michaelwagler.setlistmanager;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mobeta.android.dslv.DragSortListView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import michaelwagler.setlistmanager.db.DBHelper;
import michaelwagler.setlistmanager.model.Band;
import michaelwagler.setlistmanager.model.Set;
import michaelwagler.setlistmanager.model.Song;
import michaelwagler.setlistmanager.model.Venue;

public class SingleSetFragment extends ListFragment{
    private String LOG = "SingleSetFragment";
    private DBHelper helper;
    final DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yy, h:mmaa");
    private Set set;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        helper = DBHelper.getInstance(SingleSetFragment.super.getActivity());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String id_str = bundle.getString("set_id");
            if (id_str != null) {
                int setID = Integer.parseInt(id_str);
                set = helper.getSetById(setID);
            }
        }

        View v = inflater.inflate(R.layout.fragment_single_set, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        updateUI();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    private void updateUI() {
        // populate the ListView with the appropriate songs
        updateTitle(SingleSetFragment.super.getActivity());

        helper = DBHelper.getInstance(SingleSetFragment.super.getActivity());
        List<Song> songs = helper.getAllSongsBySet(set.getName());

        ListAdapter listadapter = new SongSetArrayAdapter(
                SingleSetFragment.super.getActivity(), R.layout.set_song_view,
                R.id.setSongTextView, songs, set);
        this.setListAdapter(listadapter);


        final DragSortListView DSLV = (DragSortListView) getListView();
        DSLV.setDropListener(new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                ListView lv = getListView();

                if (from == to) {
                    return;
                }
                if (from > to) {
                    for (int i = from; i >= to; i--) {
                        Song s = helper.getSongByName(lv.getItemAtPosition(i).toString());
                        helper.updateSongSet(s.getId(), set.getId(), i + 1);
                    }
                }
                else {
                    for (int i = from; i <= to; i++) {
                        Song s = helper.getSongByName(lv.getItemAtPosition(i).toString());

                        helper.updateSongSet(s.getId(), set.getId(), i - 1);

                    }
                }
                Song s = helper.getSongByName(lv.getItemAtPosition(from).toString());
                helper.updateSongSet(s.getId(), set.getId(), to);
                updateUI();
            }
        });
    }

    // pass in the parent activity to make sure that it exists when this is called
    private void updateTitle(Activity activity) {
        ActionBar aB = activity.getActionBar();
        aB.setTitle(set.getName());
        aB.setSubtitle(null);

        Band band = helper.getBandById( set.getBandId() );
        if (band != null) {
            aB.setTitle(set.getName());
            aB.setSubtitle(band.getName());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.set_view_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch(item.getItemId()) {
            case R.id.action_add_song:
                AlertDialog.Builder builder0 = new AlertDialog.Builder(
                        SingleSetFragment.super.getActivity());
                builder0.setTitle("Add Song");
                final AutoCompleteTextView inputField = new AutoCompleteTextView(
                        SingleSetFragment.super.getActivity());
                ArrayList<Song> songs = (ArrayList<Song>) helper.getAllSongs();
                ArrayAdapter adapter =
                        new ArrayAdapter<Song>(SingleSetFragment.super.getActivity(),
                                android.R.layout.simple_list_item_1, songs);

                inputField.setAdapter(adapter);

                builder0.setView(inputField);
                builder0.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = inputField.getText().toString();
                        Song song = new Song(name);
                        DBHelper helper = DBHelper.getInstance(
                                SingleSetFragment.super.getActivity());

                        long song_id = helper.returnSongOrCreate(song);

                        int pos = getListView().getCount();

                        // Show dialog if song is already in this set.
                        if (helper.setContainsSong(set.getId(), song_id)) {
                            AlertDialog.Builder warning = new AlertDialog.Builder(
                                    SingleSetFragment.super.getActivity());
                            warning.setMessage("This song is already in the set.");
                            warning.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            warning.create().show();
                        }
                        else {
                            helper.createSongSet(song_id, set.getId(), pos);
                            updateUI();
                        }
                    }
                });

                builder0.setNegativeButton("Cancel", null);
                builder0.create().show();
                return true;

            case R.id.set_edit:
                Fragment fragment = new NewSetFragment();

                Bundle bundle = new Bundle();
                bundle.putString("set_id", String.valueOf(set.getId()));
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .commit();
                return true;

            case R.id.set_info:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(
                        SingleSetFragment.super.getActivity());
                builder1.setTitle("Set Info");

                Band band = helper.getBandById(set.getBandId());
                String band_info = ((band !=null) ? band.getName(): "None");

                Venue venue = helper.getVenueById(set.getVenueId());
                String venue_info = ((venue != null) ? venue.getName(): "None");
                DateTime dt = set.getDateTime();
                String dt_string = ((dt !=null) ? "\nDate: " + dt.toString(fmt): "");

                String message =
                        "Band: " + band_info
                                + "\nVenue: " + venue_info +
                                dt_string;
                builder1.setMessage(message);

                builder1.setNegativeButton("Okay", null);
                builder1.create().show();
                return true;

            case R.id.action_duplicate:
                // create a duplicate of this set
                AlertDialog.Builder builder2 = new AlertDialog.Builder(
                        SingleSetFragment.super.getActivity());
                builder2.setTitle("Duplicate set");


                final EditText editName = new EditText(
                        SingleSetFragment.super.getActivity());
                builder2.setView(editName);
                editName.setText(set.getName() + " DUPLICATE", TextView.BufferType.EDITABLE);
                builder2.setPositiveButton("Create", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = editName.getText().toString();
                        Set duplicate = new Set(name);
                        duplicate.setBandId(set.getBandId());
                        duplicate.setVenueId(set.getVenueId());
                        // the duplicate's date is set to now by default
                        duplicate.setDateTime(DateTime.now());
                        long duplicate_id = helper.createSet(duplicate);
                        List<Song> songs = helper.getAllSongsBySet(set.getName());
                        for (int i = 0; i < songs.size(); i++) {
                            helper.createSongSet(songs.get(i).getId(), duplicate_id, i);
                        }

                        // Now replace current content_frame with the new fragment
                        Fragment fragment = new SingleSetFragment();

                        Bundle bundle = new Bundle();
                        //bundle.putString("set", duplicate.getName());
                        bundle.putString("set_id", String.valueOf(duplicate_id));
                        fragment.setArguments(bundle);

                        // Insert the fragment by replacing any existing fragment
                        FragmentManager fragmentManager = getFragmentManager();


                        fragmentManager.beginTransaction()
                                .replace(R.id.content_frame, fragment)
                                .addToBackStack(null)
                                .commit();

                    }
                });
                builder2.setNegativeButton("Cancel", null);
                builder2.create().show();


                return true;

            case R.id.action_export:
                String body = "";
                String body_date = (set.getDateTime() != null) ?
                        "\n" + set.getDateTime().toString(fmt) + "\n" :
                        "\n";

                String body_title = set.getName();

                String body_venue = (helper.getVenueById(set.getVenueId()) !=null) ?
                        "Venue: " + helper.getVenueById(set.getVenueId()).getName() + "\n \n"  :
                        "\n";
                String body_songs = "";
                List<Song> set_songs = helper.getAllSongsBySet(set.getName());
                for (Song song:set_songs) {
                    body_songs = body_songs + song.getName() + "\n";
                }

                body = body_title + body_date + body_venue + body_songs;
                Band set_band = helper.getBandById(set.getBandId());
                String message_title = (set_band != null) ?
                        set_band.getName() + " - Setlist" :
                        "Setlist";


                Intent exportIntent = new Intent(Intent.ACTION_SEND);
                exportIntent.putExtra(Intent.EXTRA_TEXT, body);
                exportIntent.setType("text/plain");
                exportIntent.putExtra(Intent.EXTRA_TITLE, message_title);
                exportIntent.putExtra(Intent.EXTRA_SUBJECT, message_title);
                String title = getResources().getString(R.string.chooser_title);
                Intent chooser = Intent.createChooser(exportIntent, title);

                if (exportIntent.resolveActivity(SingleSetFragment.super.getActivity().getPackageManager()) != null) {
                    startActivity(chooser);

                }
                else {
                    Log.e(LOG, "resolve activity did not work!");
                }
                return true;

            case R.id.action_delete_set:
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        SingleSetFragment.super.getActivity());
                builder.setTitle("Delete Set");
                builder.setMessage("Are you sure you want to delete this set?");
                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        helper.deleteSet(set.getName());
                        getFragmentManager().popBackStack();
                    }
                });

                builder.create().show();
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

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }



}
