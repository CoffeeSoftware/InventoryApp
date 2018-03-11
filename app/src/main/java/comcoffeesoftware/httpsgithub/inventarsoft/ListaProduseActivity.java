package comcoffeesoftware.httpsgithub.inventarsoft;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Clasa JAVA pentru Activitatea Lista Produse
 */

public class ListaProduseActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Constanta cu id pentru Loader
    private static final int ITEM_LOADER = 0;
    // Context pentru referinta in functiile de mai jos
    private static Context mContext;
    // Creare adaptor pentru cursor
    private AdaptorListaProduse mAdaptorListaProduse;

    // Trimitere la Activitatea de Editare cu un URI care indica produsul care va fi editat
    public static void goToEditor(int id) {
        Intent intent = new Intent(mContext, EditorActivity.class);
        Uri currentUri = ContentUris.withAppendedId(DbContract.Produs.CONTENT_URI, id);
        intent.setData(currentUri);
        mContext.startActivity(intent);
    }

    // Functia onCreate ruleaza cand e creata activitatea
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Seteaza layout-ul corespunzator activitatii
        setContentView(R.layout.activity_lista_produse);

        // Initierea variabilei mContext
        mContext = this;

        // Creare obiect ListView si initializare cu ListView-ul cu id=List din activity_lista_produse.xml
        ListView listView = findViewById(R.id.list);

        // Initializare adaptor
        mAdaptorListaProduse = new AdaptorListaProduse(this, null);
        // Setare adaptor la ListView
        listView.setAdapter(mAdaptorListaProduse);
        // Initializare Loader
        getLoaderManager().initLoader(ITEM_LOADER, null, this);

        // Buton pentru adaugare
        FloatingActionButton butonAdd = findViewById(R.id.add);
        butonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListaProduseActivity.this, EditorActivity.class));
            }
        });

        // Buton pentru stergerea intregii liste de produse
        FloatingActionButton delete = findViewById(R.id.delete_all_button);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteAllConfirmationDialog();
            }
        });
    }

    // Creare cursor cand e creat Loader-ul
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Definirea proiectiei cursorului
        String[] projection = {
                DbContract.Produs._ID,
                DbContract.Produs.COLUMN_NAME,
                DbContract.Produs.COLUMN_COD,
                DbContract.Produs.COLUMN_IMAGE
        };

        // Cauta in baza de date in background
        return new CursorLoader(this, DbContract.Produs.CONTENT_URI, projection, null, null, null);

    }

    // Resetarea adaptorului cu cursorul nou la finalul incarcarii Loader-ului
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdaptorListaProduse.swapCursor(cursor);
    }

    // Resetarea adaptorului fara cursor la Resetarea Loaderului
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdaptorListaProduse.swapCursor(null);
    }

    // Afiseaza dialog pentru stergerea tuturor produselor
    private void showDeleteAllConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_dialog);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAll();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Sterge toate produsele
    private void deleteAll() {
        int rowsDeleted = getContentResolver().delete(DbContract.Produs.CONTENT_URI, null, null);
        if (rowsDeleted == 0) {
            Toast.makeText(this, getString(R.string.failed_to_delete_all), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.successfully_deleted_all), Toast.LENGTH_SHORT).show();
        }
    }
}
