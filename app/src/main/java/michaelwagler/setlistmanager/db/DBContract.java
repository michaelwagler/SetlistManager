package michaelwagler.setlistmanager.db;

import android.provider.BaseColumns;

/**
 * Created by michaelwagler on 2014-12-13.
 */
public final class DBContract {
    public static final String DB_NAME = "michaelwagler.setlistmanager.db.setlistmanager";
    public static final int DB_VERSION = 5;
    public static final String TEXT_TYPE = " TEXT";
    public static final String INTEGER_TYPE = " INTEGER";
    public static final String DATETIME_TYPE = " DATETIME";

    public static final String  _ID = BaseColumns._ID;

    public class SongTable {
        public static final String TABLE = "song";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_LENGTH = "length";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE + "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                // COLUMN_NAME_OTHERCOL + TEXT_TYPE + "," +
                COLUMN_NAME + TEXT_TYPE + "," +
                COLUMN_LENGTH + INTEGER_TYPE +
                ")";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE;
    }

    public class SetTable {
        public static final String TABLE = "setlist";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DATETIME = "datetime";
        public static final String BAND_ID = "band_id";
        public static final String VENUE_ID = "venue_id";


        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE + "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + TEXT_TYPE + "," +
                COLUMN_DATETIME + DATETIME_TYPE + "," +
                BAND_ID + TEXT_TYPE + "," +
                VENUE_ID + TEXT_TYPE + "," +
                "FOREIGN KEY (" + BAND_ID + ") REFERENCES band(" + _ID + ")" +
                "FOREIGN KEY (" + VENUE_ID + ") REFERENCES venue(" + _ID + ")" +
                ")";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE;
    }

    public class SongSetTable {
        public static final String TABLE = "songs_sets";
        public static final String SONG_ID = "song_id";
        public static final String SET_ID = "set_id";
        public static final String POSITION = "position";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE + "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SONG_ID + INTEGER_TYPE + "," +
                SET_ID + INTEGER_TYPE + "," +
                POSITION + INTEGER_TYPE +
                ")";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE;
    }

    public class BandTable {
        public static final String TABLE = "band";
        public static final String COLUMN_NAME = "name";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE + "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + TEXT_TYPE +
                ")";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE;
    }

    public class VenueTable {
        public static final String TABLE = "venue";
        public static final String COLUMN_NAME = "name";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE + "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + TEXT_TYPE +
                ")";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE;
    }

}
