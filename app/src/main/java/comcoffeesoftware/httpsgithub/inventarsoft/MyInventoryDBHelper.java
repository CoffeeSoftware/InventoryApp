package comcoffeesoftware.httpsgithub.inventarsoft;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Db Helper for SQLite
 */

public class MyInventoryDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "journalEntries.db";
    private static final int DATABASE_VERSION = 1;

    public MyInventoryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_DATABASE = "CREATE TABLE " +
                DbContract.Produse.TABLE_NAME + " (" +
                DbContract.Produse._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DbContract.Produse.COLUMN_NAME + " TEXT NOT NULL," +
                DbContract.Produse.COLUMN_IMAGE + " BLOB," +
                DbContract.Produse.COLUMN_COD + " INTEGER" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_DATABASE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DbContract.Produse.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }

    public long insertEntry(String name, int cod) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.Produse.COLUMN_NAME, name);
        values.put(DbContract.Produse.COLUMN_COD, cod);

        long rowId = db.insert(DbContract.Produse.TABLE_NAME, null, values);
        return rowId;
    }

    public Cursor readEntry() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                DbContract.Produse._ID,
                DbContract.Produse.COLUMN_NAME,
                DbContract.Produse.COLUMN_COD
        };

        Cursor cursor = db.query(
                DbContract.Produse.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        return cursor;
    }
}
