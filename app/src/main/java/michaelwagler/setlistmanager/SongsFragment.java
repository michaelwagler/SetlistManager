package michaelwagler.setlistmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

import java.util.List;

import michaelwagler.setlistmanager.db.DBContract;
import michaelwagler.setlistmanager.db.DBHelper;
import michaelwagler.setlistmanager.model.Set;
import michaelwagler.setlistmanager.model.Song;


public class SongsFragment extends ListFragment{

    private final String LOG = "SongsFragment";
    private DBHelper helper;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        updateUI();
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_songs, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final ListView lv = getListView();


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                View child = view.findViewById(R.id.songTextView);

                final String songName = ((TextView) child).getText().toString();


                String[] options = new String[3];
                options[0] = "add to setlist";
                options[1] = "edit song";
                options[2] = "delete song";
                AlertDialog.Builder builder = new AlertDialog.Builder(SongsFragment.super.getActivity());


                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0:

                                AlertDialog.Builder innerBuilder = new AlertDialog.Builder(SongsFragment.super.getActivity());

                                innerBuilder.setTitle("Choose Setlist");
                                final List<Set> sets = helper.getAllSets();
                                String[] setsArray = new String[sets.size()];


                                for (int i = 0; i < sets.size(); i++) {
                                    setsArray[i] = sets.get(i).toString();
                                }

                                innerBuilder.setItems(setsArray, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Set s = sets.get(which);
                                        Song song = helper.getSongByName(songName);
                                        int pos = helper.getAllSongsBySet(s.getName()).size();
                                        helper.createSongSet(song.getId(), s.getId(), pos);

                                    }
                                });

                                innerBuilder.create().show();
                                break;


                            case 1:
                                // Edit Song
                                AlertDialog.Builder editBuilder = new AlertDialog.Builder(SongsFragment.super.getActivity());
                                editBuilder.setTitle("Edit song");
                                final EditText editName = new EditText(SongsFragment.super.getActivity());
                                editBuilder.setView(editName);
                                editName.setText(songName, TextView.BufferType.EDITABLE);
                                editBuilder.setPositiveButton("Done editing", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String name = editName.getText().toString();
                                        Song song = helper.getSongByName(songName);
                                        song.setName(name);
                                        helper.updateSong(song);
                                        updateUI();
                                    }
                                });
                                editBuilder.setNegativeButton("Cancel", null);
                                editBuilder.create().show();
                                break;

                            case 2:
                                // Delete Song
                                Song theSong = helper.getSongByName(songName);
                                helper.deleteSong(theSong.getId());
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


    private void updateUI() {
        String title = "all songs";
        Activity main = super.getActivity();
        main.setTitle(title);
        main.getActionBar().setTitle(title);

        // populate the ListView with all sets

        helper = DBHelper.getInstance(SongsFragment.super.getActivity());
        SQLiteDatabase sqlDB = helper.getReadableDatabase();

        Cursor cursor = sqlDB.query(DBContract.SongTable.TABLE,
                new String[]{DBContract._ID, DBContract.SongTable.COLUMN_NAME},
                null, null, null, null, DBContract.SongTable.COLUMN_NAME);


        // display the songs in the cursor by rendering each in a song_view resource inside
        // this activity's associated ListView
        ListAdapter listAdapter = new SimpleCursorAdapter(
                SongsFragment.super.getActivity(),
                R.layout.song_view,
                cursor,
                new String[] {DBContract.SongTable.COLUMN_NAME},
                new int[] {R.id.songTextView},
                0);
        this.setListAdapter(listAdapter);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.


        inflater.inflate(R.menu.menu_songs, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_new_song:
                AlertDialog.Builder builder = new AlertDialog.Builder(SongsFragment.super.getActivity());
                builder.setTitle("New Song");
                final EditText nameField = new EditText(SongsFragment.super.getActivity());

                builder.setView(nameField);

                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Log.d("DeleteActivity", inputField.getText().toString());
                        String name = nameField.getText().toString();
                        Song song = new Song(name);
                        DBHelper db = DBHelper.getInstance(SongsFragment.super.getActivity());

                        db.createSong(song);
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
