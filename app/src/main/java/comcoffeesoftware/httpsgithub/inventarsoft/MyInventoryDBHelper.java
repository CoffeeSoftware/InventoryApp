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

    public static final String LOG_TAG = MyInventoryDBHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    // Constructorul
    public MyInventoryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_DATABASE = "CREATE TABLE " +
                DbContract.Produs.TABLE_NAME + " (" +
                DbContract.Produs._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DbContract.Produs.COLUMN_NAME + " TEXT NOT NULL," +
                DbContract.Produs.COLUMN_IMAGE + " BLOB," +
                DbContract.Produs.COLUMN_COD + " INTEGER," +
                DbContract.Produs.COLUMN_COD_COMPLET + " STRING" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_DATABASE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DbContract.Produs.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
