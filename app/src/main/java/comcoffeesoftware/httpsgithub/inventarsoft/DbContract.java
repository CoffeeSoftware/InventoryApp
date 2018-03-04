package comcoffeesoftware.httpsgithub.inventarsoft;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract for our Database
 */

public class DbContract {
    // Constructor gol
    private DbContract(){}

    public static final String CONTENT_AUTHORITY = "comcoffeesoftware.httpsgithub.inventarsoft";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ITEM = "item";

    public static final class Produs implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEM);
        // MIME type pentru lista
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEM;
        // MIME type pentru un singur produs
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEM;

        public static final String TABLE_NAME = BaseColumns._ID;

        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_IMAGE = "Imagine";
        public static final String COLUMN_COD = "Cod";
    }
}
