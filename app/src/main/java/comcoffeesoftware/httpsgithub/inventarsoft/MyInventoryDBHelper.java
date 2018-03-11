package comcoffeesoftware.httpsgithub.inventarsoft;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Clasa JAVA care extinde clasa SQLiteOpenHelper si care va fi folosita pentru crearea si manipularea bazei de date
 */

public class MyInventoryDBHelper extends SQLiteOpenHelper {

    // Constante pentru numele si versiunea bazei de date
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    // Constructorul
    public MyInventoryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Creare comanda SQLite pentru crearea tabelului
        final String SQL_CREATE_DATABASE = "CREATE TABLE " +
                DbContract.Produs.TABLE_NAME + " (" +
                DbContract.Produs._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DbContract.Produs.COLUMN_NAME + " TEXT NOT NULL," +
                DbContract.Produs.COLUMN_IMAGE + " BLOB," +
                DbContract.Produs.COLUMN_COD + " INTEGER," +
                DbContract.Produs.COLUMN_COD_COMPLET + " STRING" +
                ");";

        // Executarea comenzii SQLite create
        sqLiteDatabase.execSQL(SQL_CREATE_DATABASE);

    }

    // Functie pentru upgradarea bazei de date
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DbContract.Produs.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
