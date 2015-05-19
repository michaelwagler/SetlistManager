package michaelwagler.setlistmanager;
/*
ArrayAdapter used to attach event handler to individual songs in a setlist
 */
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import michaelwagler.setlistmanager.db.DBHelper;
import michaelwagler.setlistmanager.model.Set;
import michaelwagler.setlistmanager.model.Song;

/**
 * Created by michaelwagler on 2015-01-02.
 */
public class SongSetArrayAdapter extends ArrayAdapter {
    private String LOG = "SongSetArrayAdapter";
    private final Context context;
    private List<Song> values;
    private final Set set;


    public SongSetArrayAdapter(Context context, int layout, int textViewResource, List<Song> values, Set set) {
        super(context, layout, textViewResource, values);
        this.context = context;
        this.values = values;
        this.set = set;
    }

    public void refill(List<Song> songs) {
        this.values.clear();
        this.values.addAll(songs);
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View setSongView = inflater.inflate(R.layout.set_song_view, parent, false);
        TextView textView = (TextView) setSongView.findViewById(R.id.setSongTextView);
        Button del = (Button) setSongView.findViewById(R.id.setSongDeleteButton);
        textView.setText(values.get(position).toString());
        final int positionInner = position;
        del.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.setSongDeleteButton:
                        TextView tv = (TextView) ((View) v.getParent()).findViewById(R.id.setSongTextView);
                        DBHelper helper = DBHelper.getInstance(context);
                        Song song = helper.getSongByName(tv.getText().toString());
                        helper.deleteSongSet(song.getId(), set.getId());
                        values.remove(positionInner);
                        Log.d(LOG, "updating position of songs in set after song delete");
                        for (int i = positionInner; i < values.size(); i ++) {
                            //Log.d(LOG, "updating song with name: " + values.get(i).getName());
                            helper.updateSongSet(values.get(i).getId(), set.getId(), i);
                            //Log.d(LOG, "updating songs starting at positionInner: " + positionInner);
                            //Log.d(LOG, "song: " + values.get(i).getName() + " in list at position:" + i);
                        }
                        // need to update the position of all the songs after this one
                        notifyDataSetChanged();
                        break;
                    default:
                        break;
                }

            }
        });

        return setSongView;
    }
}
