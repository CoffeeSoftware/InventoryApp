package comcoffeesoftware.httpsgithub.inventarsoft;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Clasa Java care extinde content provider si are functile de inserare, stergere, cautare si actualizare a bazei de date
 */

public class ProdusProvider extends ContentProvider {
    // Tag pentru mesaje de eroare
    public static final String LOG_TAG = ProdusProvider.class.getSimpleName();
    private static final int Inventory = 100; // Constanta care arata ca se opereaza cu intreg tabelul
    private static final int Item = 101; // Constanta pentru cazul in care lucram cu un singur rand al bazei de date
    // Creare URI matcher
    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    // Creare a unei instante a clasei mDbHelper
    static MyInventoryDBHelper mDbHelper;

    static {
        mUriMatcher.addURI(DbContract.CONTENT_AUTHORITY, DbContract.PATH_ITEM, Inventory); // URI pentru tot tabelul
        mUriMatcher.addURI(DbContract.CONTENT_AUTHORITY, DbContract.PATH_ITEM + "/#", Item); // URI pentru un singur rand (produs)
    }

    // Functie care ruleaza la unui obiect de tip ProdusProvider
    @Override
    public boolean onCreate() {
        // Initializarea unui obiect de tip MyInventoryDBHelper (clasa extinsa din SQLiteOpenHelper)
        mDbHelper = new MyInventoryDBHelper(getContext());
        return false;
    }

    // Functie pentru cautare in baza de date care returneaza un cursor cu intreg tabelul sau cu un rand anume din tabel
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sort) {
        // Crearea unui obiect SQLiteDatabase citibil
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        // Crearea unui cursor
        Cursor cursor;
        // Verifica daca lucram cu un rand din baza de date (un produs) sau cu intreg tabelul
        int match = mUriMatcher.match(uri);
        switch (match) {
            // Lucram cu tot tabelul
            case Inventory:
                cursor = database.query(DbContract.Produs.TABLE_NAME, projection, selection, selectionArgs, null, null, sort);
                break;
            // Lucram cu un singur rand din tabel (un singur produs)
            case Item:
                selection = DbContract.Produs._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(DbContract.Produs.TABLE_NAME, projection, selection, selectionArgs, null, null, sort);
                break;
            default:
                throw new IllegalArgumentException("Cannot perform query of this URI: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    // Functie care trebuie suprascrisa pentru clasele care extind ContentProvider, returneaza MIME type al datelor oferite de acest ContentProvider, NU ARE IMPLEMENTARI
    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case Inventory:
                return DbContract.Produs.CONTENT_LIST_TYPE;
            case Item:
                return DbContract.Produs.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Problem with URI: " + uri + ". Match: " + match);
        }
    }

    /* Functie pentru inserare in baza de date a unui rand (un produs).
    Primeste ca si parametri:
    * un URI care trebuie sa fie egal cu constanta Inventory (= 100) pentru a se insera randul;
    * un obiect ContentValues care contine valorile randului care va fi introdus
     */
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case Inventory:
                // Get writable database
                SQLiteDatabase database = mDbHelper.getWritableDatabase();
                // Inserare rand nou
                long id = database.insert(DbContract.Produs.TABLE_NAME, null, contentValues);
                // Daca nu s-a inserat un rand nou, afiseaza eroare in Logcat
                if (id == -1) {
                    Log.e(LOG_TAG, "Failed to insert row for " + uri);
                    return null;
                }
                // Returneaza Uri cu id-ul randului inserat
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            case Item:
                break;
            default:
                throw new IllegalArgumentException("Could not insert for the URI: " + uri);
        }
        return null;
    }

    /* Functie pentru stergere din baza de date a unui rand (un produs).
    Functia primeste ca si parametri:
    * Un Uri corespunzator randului care va fi sters
    * Un string cu selectie (echivalentul lui "where" in SQLite) -- e null daca URI-ul e pentru un item anume
    * Un String[] cu argumentele pentru selectie -- e null daca URI-ul e pentru un item anume
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int deletedRows = 0;
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case Inventory:
                // Sterge randurile cu selectiile si argumentele de selectie primite, daca URI-ul e pentru intreg tabelul
                deletedRows = database.delete(DbContract.Produs.TABLE_NAME, selection, selectionArgs);
                break;
            case Item:
                // Sterge un rand specificat prin ID - caz in care selection si selectionArgs[] sunt nulle
                selection = DbContract.Produs._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                deletedRows = database.delete(DbContract.Produs.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Could not delete for the URI: " + uri);
        }
        if (deletedRows != 0) getContext().getContentResolver().notifyChange(uri, null);
        return deletedRows;
    }

    /* Functie pentru updatarea bazei de date
      Functia primeste ca si parametri:
    * Un Uri corespunzator randului care va fi updatat
    * Un obiect ContentValues cu datele randului care va fi editat
    * Un string cu selectie (echivalentul lui "where" in SQLite) -- e null daca URI-ul e pentru un item anume
    * Un String[] cu argumentele pentru selectie -- e null daca URI-ul e pentru un item anume
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArguments) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case Inventory:
                return updateItem(uri, contentValues, selection, selectionArguments);
            case Item:
                selection = DbContract.Produs._ID + "=?";
                selectionArguments = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, contentValues, selection, selectionArguments);
            default:
                throw new IllegalArgumentException("Could not update for the URI: " + uri);
        }
    }

    // Functie ajutatoare pentru updatare
    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Check if there is a name for the item
        if (values.containsKey(DbContract.Produs.COLUMN_NAME)) {
            String name = values.getAsString(DbContract.Produs.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a name.");
            }
        }
        // Verifica daca avem date pentru updatare
        if (values.size() == 0) {
            return 0; // Nu exista date pentru update
        }
        // Get writable database, updateaza si returneaza numarul de randuri updatate
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int updatedRows = 0;
        updatedRows = database.update(DbContract.Produs.TABLE_NAME, values, selection, selectionArgs);
        if (updatedRows != 0) getContext().getContentResolver().notifyChange(uri, null);
        return updatedRows;
    }
}
