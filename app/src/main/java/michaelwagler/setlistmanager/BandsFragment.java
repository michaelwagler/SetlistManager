package michaelwagler.setlistmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
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

import michaelwagler.setlistmanager.db.DBContract;
import michaelwagler.setlistmanager.db.DBHelper;
import michaelwagler.setlistmanager.model.Band;


public class BandsFragment extends ListFragment {
    private DBHelper helper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        updateUI();
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_bands, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        final ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // selected item
                View child = view.findViewById(R.id.bandTextView);

                final String bandName = ((TextView) child).getText().toString();

                String[] options = new String[3];
                options[0] = "view setlists";
                options[1] = "edit";
                options[2] = "delete";
                AlertDialog.Builder builder = new AlertDialog.Builder(BandsFragment.super.getActivity());

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0:
                                // view setlists

                                Fragment fragment = new SetsFragment();


                                Bundle bundle = new Bundle();
                                bundle.putString("band", bandName);
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
                                        BandsFragment.super.getActivity());
                                editBuilder.setTitle("Edit band");
                                final EditText editName = new EditText(
                                        BandsFragment.super.getActivity());
                                editBuilder.setView(editName);
                                editName.setText(bandName, TextView.BufferType.EDITABLE);
                                editBuilder.setPositiveButton("Done editing", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String name = editName.getText().toString();
                                        Band band = helper.getBandByName(bandName);
                                        band.setName(name);
                                        helper.updateBand(band);
                                        updateUI();
                                    }
                                });
                                editBuilder.setNegativeButton("Cancel", null);
                                editBuilder.create().show();
                                break;

                            case 2:
                                // Delete Band
                                AlertDialog.Builder builder = new AlertDialog.Builder(
                                        BandsFragment.super.getActivity());
                                builder.setTitle("Delete Band");
                                builder.setMessage("Are you sure you want to delete this band?");
                                builder.setNegativeButton("Cancel", null);
                                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        helper.deleteBand(bandName);
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

            }
        });
    }

    public void updateUI() {
        String title = "all bands";
        Activity main = super.getActivity();
        main.setTitle(title);
        main.getActionBar().setTitle(title);


        helper = DBHelper.getInstance(BandsFragment.super.getActivity());
        SQLiteDatabase sqlDB = helper.getReadableDatabase();

        Cursor cursor = sqlDB.query(DBContract.BandTable.TABLE,
                new String[]{DBContract._ID, DBContract.BandTable.COLUMN_NAME},
                null, null, null, null, null);

        ListAdapter listAdapter = new SimpleCursorAdapter(
                BandsFragment.super.getActivity(),
                R.layout.band_view,
                cursor,
                new String[] {DBContract.BandTable.COLUMN_NAME},
                new int[] {R.id.bandTextView},
                0);
        this.setListAdapter(listAdapter);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_bands, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_new_band:
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        BandsFragment.super.getActivity());
                builder.setTitle("Create Band");
                final EditText nameField = new EditText(
                        BandsFragment.super.getActivity());

                builder.setView(nameField);

                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Log.d("DeleteActivity", inputField.getText().toString());
                        String name = nameField.getText().toString();

                        Band band = new Band(name);

                        DBHelper db = DBHelper.getInstance(BandsFragment.super.getActivity());

                        db.createBand(band);
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
