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
 * Created by alex on 04.03.2018.
 */

public class ProdusProvider extends ContentProvider {
    public static final String LOG_TAG = ProdusProvider.class.getSimpleName();
    // Create instance of the Db Helper
    static MyInventoryDBHelper mDbHelper;

    private  static final int Inventory = 100; // For the entire table
    private static final int Item = 101; // For one row of the inventory
    // Create URI matcher
    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        mUriMatcher.addURI(DbContract.CONTENT_AUTHORITY, DbContract.PATH_ITEM, Inventory);
        mUriMatcher.addURI(DbContract.CONTENT_AUTHORITY, DbContract.PATH_ITEM + "/#", Item);
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new MyInventoryDBHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sort) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = mUriMatcher.match(uri);
        switch (match) {
            // Case for view all
            case Inventory:
                cursor = database.query(DbContract.Produs.TABLE_NAME, projection, selection, selectionArgs, null, null, sort);
                break;
            // Case for requesting one element
            case Item:
                selection = DbContract.Produs._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(DbContract.Produs.TABLE_NAME, projection, selection, selectionArgs, null, null, sort);
                break;
            default:
                throw new IllegalArgumentException("Cannot perform query of this URI: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }
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

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case Inventory:
                // Get writable database
                SQLiteDatabase database = mDbHelper.getWritableDatabase();
                // Insert new item
                long id = database.insert(DbContract.Produs.TABLE_NAME, null, contentValues);
                if (id == -1) {
                    Log.e(LOG_TAG, "Failed to insert row for " + uri);
                    return null;
                }
                // Return the uri and the row id
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            case Item:
                break;
            default:
                throw new IllegalArgumentException("Could not insert for the URI: " + uri);
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int deletedRows = 0;
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case Inventory:
                // Delete rows with the specified selection and selection args
                deletedRows = database.delete(DbContract.Produs.TABLE_NAME, selection, selectionArgs);
                break;
            case Item:
                // Delete the row with the specified ID
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

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArguments) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case Inventory:
                return updateItem(uri, contentValues, selection, selectionArguments);
            case Item:
                selection = DbContract.Produs._ID + "=?";
                selectionArguments = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateItem(uri, contentValues, selection, selectionArguments);
            default:
                throw new IllegalArgumentException("Could not update for the URI: " + uri);
        }
    }

    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Check if there is a name for the item
        if (values.containsKey(DbContract.Produs.COLUMN_NAME)) {
            String name = values.getAsString(DbContract.Produs.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a name.");
            }
        }


        // Check if we have values to update the table
        if (values.size() == 0) {
            return 0; // No rows updated
        }

        // Get writable database and return the number of updated rows
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int updatedRows = 0;
        updatedRows = database.update(DbContract.Produs.TABLE_NAME, values, selection, selectionArgs);
        if (updatedRows != 0) getContext().getContentResolver().notifyChange(uri, null);
        return updatedRows;
    }
}
