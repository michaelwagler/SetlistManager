package michaelwagler.setlistmanager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.joda.time.DateTime;

import michaelwagler.setlistmanager.db.DBContract.SongTable;
import michaelwagler.setlistmanager.db.DBContract.SetTable;
import michaelwagler.setlistmanager.db.DBContract.SongSetTable;
import michaelwagler.setlistmanager.db.DBContract.BandTable;
import michaelwagler.setlistmanager.db.DBContract.VenueTable;


import java.util.ArrayList;
import java.util.List;

import michaelwagler.setlistmanager.model.Band;
import michaelwagler.setlistmanager.model.Set;
import michaelwagler.setlistmanager.model.Song;
import michaelwagler.setlistmanager.model.Venue;

/**
 * Created by michaelwagler on 2014-12-13.
 */


public class DBHelper extends SQLiteOpenHelper {

    private String LOG = "DBHelper";
    private DatabaseUtils dbutils;


    public DBHelper(Context context) {
        super(context, DBContract.DB_NAME, null, DBContract.DB_VERSION);
    }


    public String escape_sql (String value) {
        if (dbutils == null) {
            dbutils = new DatabaseUtils();
        }
        return dbutils.sqlEscapeString(value);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SongTable.CREATE_TABLE);
        db.execSQL(SetTable.CREATE_TABLE);
        db.execSQL(SongSetTable.CREATE_TABLE);
        db.execSQL(BandTable.CREATE_TABLE);
        db.execSQL(VenueTable.CREATE_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SongTable.DELETE_TABLE);
        db.execSQL(SetTable.DELETE_TABLE);
        db.execSQL(SongSetTable.DELETE_TABLE);
        db.execSQL(BandTable.DELETE_TABLE);
        db.execSQL(VenueTable.DELETE_TABLE);

        onCreate(db);
    }

    // CRUD operations, based on code taken from
    // http://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/
    // Dec 19th 2014


    // return song if it exists, or create it if it doesn't
    public long returnSongOrCreate(Song song) {
        long id = createSong(song);
        if (id == -1) {
            // song already exists, return it
            return getSongByName(song.getName()).getId();
        }
        return id;
    }

    // create song if it doesn't exist, or return -1 if it does
    public long createSong(Song song) {

        Song s = getSongByName(song.getName());
        if (s != null) {
            return -1;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SongTable.COLUMN_NAME, song.getName());
        values.put(SongTable.COLUMN_LENGTH, song.getLength());

        // insert row
        return db.insert(SongTable.TABLE, null, values);
    }

    public Song getSongById(long song_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + SongTable.TABLE + " WHERE "
                + DBContract._ID + " = " + song_id;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c == null || !c.moveToFirst()) {

            return null;
        }

        Song song = new Song();
        song.setId(c.getInt(c.getColumnIndex(DBContract._ID)));
        song.setName(c.getString(c.getColumnIndex(SongTable.COLUMN_NAME)));
        song.setLength(c.getInt(c.getColumnIndex(SongTable.COLUMN_LENGTH)));

        return song;

    }

    public Song getSongByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + SongTable.TABLE + " WHERE "
                + SongTable.COLUMN_NAME + " =?";

        Cursor c = db.rawQuery(selectQuery, new String[] {name} );

        if (c == null || !c.moveToFirst()) {
            return null;
        }

        Song song = new Song();
        song.setId(c.getInt(c.getColumnIndex(DBContract._ID)));
        song.setName(c.getString(c.getColumnIndex(SongTable.COLUMN_NAME)));
        song.setLength(c.getInt(c.getColumnIndex(SongTable.COLUMN_LENGTH)));


        return song;

    }


    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<Song>();
        String selectQuery = "SELECT * FROM " + SongTable.TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Song song = new Song();
                song.setId(c.getInt(c.getColumnIndex(DBContract._ID)));
                song.setName(c.getString(c.getColumnIndex(SongTable.COLUMN_NAME)));
                song.setLength(c.getInt(c.getColumnIndex(SongTable.COLUMN_LENGTH)));
                songs.add(song);
            } while (c.moveToNext());
        }
        return songs;
    }

    public List<Song> getAllSongsBySet(String set_name) {
        List<Song> songs = new ArrayList<Song>();

        String selectQuery = "SELECT s.name as song_name, s._id, s.length, st.*, ss.* FROM " + SongTable.TABLE + " s, "
                + SetTable.TABLE + " st, " + SongSetTable.TABLE + " ss WHERE st."
                + SetTable.COLUMN_NAME + " =? "+ " AND st." + DBContract._ID
                + " = " + "ss." + SongSetTable.SET_ID + " AND s." + DBContract._ID + " = "
                + "ss." + SongSetTable.SONG_ID + " ORDER BY " + SongSetTable.POSITION;


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, new String[] {set_name});

        if (c.moveToFirst()) {
            do {
                Song song = new Song();
                song.setId(c.getInt(c.getColumnIndex(DBContract._ID)));
                song.setName(c.getString(c.getColumnIndex("song_name")));
                song.setLength(c.getInt(c.getColumnIndex(SongTable.COLUMN_LENGTH)));
                songs.add(song);
            } while (c.moveToNext());
        }
        return songs;

    }



    public int getSongCount() {
        String countQuery = "SELECT  * FROM " + SongTable.TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }



    public int updateSong(Song song) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SongTable.COLUMN_NAME, song.getName());
        values.put(SongTable.COLUMN_LENGTH, song.getLength());

        return db.update(SongTable.TABLE, values, DBContract._ID + " = ?",
                new String[]{String.valueOf(song.getId())});
    }

    public void deleteSong(long song_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SongTable.TABLE, DBContract._ID + "=?",
                new String[]{String.valueOf(song_id)});
        // also delete the song from all sets
        db.delete(SongSetTable.TABLE, SongSetTable.SONG_ID + "=?",
                new String[]{String.valueOf(song_id)});
    }

    public long createSet(Set set) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SetTable.COLUMN_NAME, set.getName());
        values.put(SetTable.COLUMN_DATETIME, set.getDateTimeString());
        values.put(SetTable.BAND_ID, set.getBandId());
        values.put(SetTable.VENUE_ID, set.getVenueId());

        return db.insert(SetTable.TABLE, null, values);
    }

    public Set getSetByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + SetTable.TABLE + " WHERE "
                + SetTable.COLUMN_NAME + " =? ";

        Cursor c = db.rawQuery(selectQuery, new String[]{name});

        if (c == null || !c.moveToFirst()) {
            return null;
        }

        Set set = new Set();

        set.setId(c.getInt(c.getColumnIndex(DBContract._ID)));
        set.setName(c.getString(c.getColumnIndex(SetTable.COLUMN_NAME)));
        set.setDateTime(getSetDateTimeFromCursor(c));
        set.setBandId(c.getInt(c.getColumnIndex(SetTable.BAND_ID)));
        set.setVenueId(c.getInt(c.getColumnIndex(SetTable.VENUE_ID)));

        return set;
    }

    public Set getSetById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + SetTable.TABLE + " WHERE " +
                SetTable.TABLE + "." + DBContract._ID +  " = " + id;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c == null || !c.moveToFirst()) {
            return null;
        }

        Set set = new Set();

        set.setId(c.getInt(c.getColumnIndex(DBContract._ID)));
        set.setName(c.getString(c.getColumnIndex(SetTable.COLUMN_NAME)));
        set.setDateTime(getSetDateTimeFromCursor(c));
        set.setBandId(c.getInt(c.getColumnIndex(SetTable.BAND_ID)));
        set.setVenueId(c.getInt(c.getColumnIndex(SetTable.VENUE_ID)));

        return set;
    }




    // helper method for getting DateTime from cursor, deals with a null datetime column
    public DateTime getSetDateTimeFromCursor(Cursor c) {
        String toParse = getFieldFromCursor(c, SetTable.COLUMN_DATETIME);
        if (toParse != null) {
            return DateTime.parse(toParse);
        }
        return null;
    }

    public String getFieldFromCursor(Cursor c, String field) {
        int index = c.getColumnIndex(field);
        return c.getString(index);
    }

    public List<Set> getAllSets() {
        List<Set> sets = new ArrayList<Set>();
        String selectQuery = "SELECT * FROM " + SetTable.TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Set s = new Set();
                s.setId(c.getInt(c.getColumnIndex(DBContract._ID)));
                s.setName(c.getString(c.getColumnIndex(SetTable.COLUMN_NAME)));
                s.setDateTime(getSetDateTimeFromCursor(c));
                s.setBandId(c.getInt(c.getColumnIndex(SetTable.BAND_ID)));
                s.setVenueId(c.getInt(c.getColumnIndex(SetTable.VENUE_ID)));

                sets.add(s);
            } while (c.moveToNext());

        }

        return sets;
    }

    public int updateSet(Set set) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SetTable.COLUMN_NAME, set.getName());
        values.put(SetTable.COLUMN_DATETIME, set.getDateTimeString());
        values.put(SetTable.BAND_ID, set.getBandId());
        values.put(SetTable.VENUE_ID, set.getVenueId());

        return db.update(SetTable.TABLE, values, DBContract._ID + "= ?",
                new String[]{String.valueOf(set.getId())});
    }


    public void deleteSet(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        long id = getSetByName(name).getId();
        db.delete(SetTable.TABLE, DBContract._ID + " = ?",
                new String[]{String.valueOf(id)});

        // delete the songs_sets associations for this set
        db.delete(SongSetTable.TABLE, SongSetTable.SET_ID + " = ?",
                new String[]{String.valueOf(id)});

    }

    // Creating song_set

    public long createSongSet(long song_id, long set_id, int pos) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SongSetTable.SONG_ID, song_id);
        values.put(SongSetTable.SET_ID, set_id);
        // default
        values.put(SongSetTable.POSITION, pos);

        return db.insert(SongSetTable.TABLE, null, values);
    }


    public boolean setContainsSong(long set_id, long song_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + SongSetTable.TABLE + " WHERE "
                + SongSetTable.SONG_ID + " = " + song_id + " AND " +
                SongSetTable.SET_ID + " = " + set_id;

        Cursor c = db.rawQuery(selectQuery, null);
        return (c.getCount() > 0);
    }


    public long getSongSetPosition(long song_id, long set_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + SongSetTable.TABLE + " WHERE "
                + SongSetTable.SONG_ID + " = " + song_id + " AND " +
                SongSetTable.SET_ID + " = " + set_id;


        Cursor c = db.rawQuery(selectQuery, null);

        if (c == null || !c.moveToFirst()) {
            return -1;
        }
        return  c.getInt(c.getColumnIndex(SongSetTable.POSITION));
    }


  //Updating a song set

    public void updateSongSet(long id, long set_id, int position) {
        SQLiteDatabase db = this.getWritableDatabase();

        String updateSQL = "UPDATE " + SongSetTable.TABLE + " SET " +
                SongSetTable.SET_ID + " = " + set_id +
                ", " + SongSetTable.POSITION + " = " + position +
                " WHERE " + SongSetTable.SONG_ID
                + " = " + id;
        db.execSQL(updateSQL);
    }


    //Delete a song set

    public void deleteSongSet(long song_id, long set_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        String deleteQuery = "DELETE FROM " + SongSetTable.TABLE + " WHERE "
                + SongSetTable.SONG_ID + " = " + song_id + " AND " +
                SongSetTable.SET_ID + " = " + set_id;

        Log.d(LOG, "deleteQuery: " + deleteQuery);

        db.execSQL(deleteQuery);


    }

    public List<Band> getAllBands() {
        List<Band> bands = new ArrayList<Band>();
        String selectQuery = "SELECT * FROM " + BandTable.TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Band b = new Band();
                b.setId(c.getInt(c.getColumnIndex(DBContract._ID)));
                b.setName(c.getString(c.getColumnIndex(BandTable.COLUMN_NAME)));

                bands.add(b);
            } while (c.moveToNext());

        }

        return bands;
    }

    public long createBand(Band band) {
        SQLiteDatabase db = this.getWritableDatabase();

        Band b = getBandByName(band.getName());
        if (b != null) {
            return b.getId();
        }

        ContentValues values = new ContentValues();
        values.put(BandTable.COLUMN_NAME, band.getName());

        return db.insert(BandTable.TABLE, null, values);
    }

    public Band getBandByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + BandTable.TABLE + " WHERE "
                + BandTable.COLUMN_NAME + " =?";

        Cursor c = db.rawQuery(selectQuery, new String[]{name});

        if (c == null || !c.moveToFirst()) {
            return null;
        }

        Band band = new Band();
        band.setId(c.getInt(c.getColumnIndex(DBContract._ID)));
        band.setName(c.getString(c.getColumnIndex(BandTable.COLUMN_NAME)));

        return band;
    }

    public Band getBandById(long band_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + BandTable.TABLE + " WHERE "
                + DBContract._ID + " = " + band_id;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c == null || !c.moveToFirst()) {
            return null;
        }

        Band band = new Band();
        band.setId(c.getInt(c.getColumnIndex(DBContract._ID)));
        band.setName(c.getString(c.getColumnIndex(BandTable.COLUMN_NAME)));
        return band;

    }

    public int updateBand(Band band) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BandTable.COLUMN_NAME, band.getName());

        return db.update(BandTable.TABLE, values, DBContract._ID + " = ?",
                new String[]{String.valueOf(band.getId())});
    }


    public List<Set> getAllSetsByBand(String band_name) {
        List<Set> sets = new ArrayList<Set>();
        int id = getBandByName(band_name).getId();
        String selectQuery = "SELECT * FROM " + SetTable.TABLE + " WHERE " +
                SetTable.TABLE + "." + SetTable.BAND_ID + " = " + id;


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Set set = new Set();
                set.setId(c.getInt(c.getColumnIndex(DBContract._ID)));
                set.setName(c.getString(c.getColumnIndex(SetTable.COLUMN_NAME)));
                set.setDateTime(DateTime.parse(c.getString(c.getColumnIndex(SetTable.COLUMN_DATETIME))));
                set.setBandId(c.getInt(c.getColumnIndex(SetTable.BAND_ID)));
                set.setVenueId(c.getInt(c.getColumnIndex(SetTable.VENUE_ID)));

                sets.add(set);
            } while (c.moveToNext());
        }
        return sets;

    }



    public void deleteBand(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        long id = getBandByName(name).getId();
        db.delete(BandTable.TABLE, DBContract._ID + " = ?",
                new String[]{String.valueOf(id)});

        // also, disassociate sets from this band
        String updateSQL = "UPDATE " + SetTable.TABLE + " SET " +
                SetTable.BAND_ID + " = " + "NULL" +
                " WHERE " + SetTable.BAND_ID
                + " = " + id;
        db.execSQL(updateSQL);
    }

    public Venue getVenueByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + VenueTable.TABLE + " WHERE "
                + VenueTable.COLUMN_NAME + " =?";

        Cursor c = db.rawQuery(selectQuery, new String[]{name});

        if (c == null || !c.moveToFirst()) {
            return null;
        }

        Venue venue = new Venue();
        venue.setId(c.getInt(c.getColumnIndex(DBContract._ID)));
        venue.setName(c.getString(c.getColumnIndex(VenueTable.COLUMN_NAME)));

        return venue;
    }

    public Venue getVenueById(long venue_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + VenueTable.TABLE + " WHERE "
                + DBContract._ID + " = " + venue_id;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c == null || !c.moveToFirst()) {
            return null;
        }

        Venue venue = new Venue();
        venue.setId(c.getInt(c.getColumnIndex(DBContract._ID)));
        venue.setName(c.getString(c.getColumnIndex(VenueTable.COLUMN_NAME)));
        return venue;
    }



    public int updateVenue(Venue venue) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(VenueTable.COLUMN_NAME, venue.getName());

        return db.update(VenueTable.TABLE, values, DBContract._ID + " = ?",
                new String[]{String.valueOf(venue.getId())});
    }

    public void deleteVenue(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        long id = getVenueByName(name).getId();
        db.delete(VenueTable.TABLE, DBContract._ID + " = ?",
                new String[]{String.valueOf(id)});

        // also, disassociate sets from this venue
        String updateSQL = "UPDATE " + SetTable.TABLE + " SET " +
                SetTable.VENUE_ID + " = " + "NULL" +
                " WHERE " + SetTable.VENUE_ID
                + " = " + id;
        db.execSQL(updateSQL);
    }

    public long createVenue(Venue venue) {
        SQLiteDatabase db = this.getWritableDatabase();

        Venue v = getVenueByName(venue.getName());
        if (v != null) {
            return v.getId();
        }

        ContentValues values = new ContentValues();
        values.put(VenueTable.COLUMN_NAME, venue.getName());

        return db.insert(VenueTable.TABLE, null, values);
    }

    public List<Venue> getAllVenues() {
        List<Venue> venues = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + VenueTable.TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Venue v = new Venue();
                v.setId(c.getInt(c.getColumnIndex(DBContract._ID)));
                v.setName(c.getString(c.getColumnIndex(VenueTable.COLUMN_NAME)));

                venues.add(v);
            } while (c.moveToNext());

        }

        return venues;
    }



    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }


}