package comcoffeesoftware.httpsgithub.inventarsoft;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract pentru baza de date
 */

public class DbContract {
    // Creare si initializare constante necesare pentru baza de date SQLite
    public static final String CONTENT_AUTHORITY = "comcoffeesoftware.httpsgithub.inventarsoft";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ITEM = "item";

    // Constructor gol
    private DbContract() {
    }

    public static final class Produs implements BaseColumns {

        // Creare URI
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEM);
        // MIME type pentru lista
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEM;
        // MIME type pentru un singur produs
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEM;

        // Nume tabel
        public static final String TABLE_NAME = BaseColumns._ID;

        // Nume pentru coloanele bazei de date
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_IMAGE = "Imagine";
        public static final String COLUMN_COD = "Cod";
        public static final String COLUMN_COD_COMPLET = "CodComplet";
    }
}
