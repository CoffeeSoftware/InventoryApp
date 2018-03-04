package comcoffeesoftware.httpsgithub.inventarsoft;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;

/**
 * Java class for Lista Produs Activity
 */

public class ListaProduseActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static Context mContext;
    // Loader identifier
    private static final int ITEM_LOADER = 0;
    // Create a cursor adapter

    private static final int numarProduse = 100;

    private AdaptorListaProduse mAdaptorListaProduse;
    private RecyclerView mRecyclerView;
    private SQLiteDatabase myData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_produse);

        mContext = this;

        // TODO Find and set a empty view

        ListView listView = (ListView) findViewById(R.id.list);

        mAdaptorListaProduse = new AdaptorListaProduse(this, null);

        listView.setAdapter(mAdaptorListaProduse);

        getLoaderManager().initLoader(ITEM_LOADER, null, this);


        FloatingActionButton butonAdd = (FloatingActionButton) findViewById(R.id.add);
        butonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListaProduseActivity.this, EditorActivity.class));
            }
        });

        // TODO Add delete button
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define the projection
        String[] projection = {
                DbContract.Produs._ID,
                DbContract.Produs.COLUMN_NAME,
                DbContract.Produs.COLUMN_COD,
                DbContract.Produs.COLUMN_IMAGE
        };

        // Query on background
        return new CursorLoader(this, DbContract.Produs.CONTENT_URI, projection, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdaptorListaProduse.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdaptorListaProduse.swapCursor(null);
    }

    public static void goToEditor(int id) {
        Intent intent = new Intent(mContext, EditorActivity.class);
        Uri currentUri = ContentUris.withAppendedId(DbContract.Produs.CONTENT_URI, id);
        intent.setData(currentUri);
        mContext.startActivity(intent);
    }
}
