package comcoffeesoftware.httpsgithub.inventarsoft;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract for our Database
 */

public class DbContract {
    public static final String CONTENT_AUTHORITY = "com.example.mihai.proiectintegrator";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ITEM = "item";

    public static final class Produse implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEM);

        public static final String TABLE_NAME = "InventarProduse";
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_IMAGE = "Imagine";
        public static final String COLUMN_COD = "Cod";
    }
}
