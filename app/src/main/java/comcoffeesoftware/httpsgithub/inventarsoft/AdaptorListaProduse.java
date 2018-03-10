package comcoffeesoftware.httpsgithub.inventarsoft;

import android.content.Context;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



import static comcoffeesoftware.httpsgithub.inventarsoft.EditorActivity.getImage;

/**
 * Adaptor for the RecyclerView containing a list of our saved products
 */

public class AdaptorListaProduse extends CursorAdapter {
    public AdaptorListaProduse(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.produs, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Cauta view-urile din produs.xml care vor contine date despre produs
        TextView numeProdus = (TextView) view.findViewById(R.id.nume_produs);
        TextView codProdus = (TextView) view.findViewById(R.id.cod_produs);
        ImageView imagineProdus = (ImageView) view.findViewById(R.id.imagine_produs);

        // Gaseste indexurile
        int numeColumnIndex = cursor.getColumnIndex(DbContract.Produs.COLUMN_NAME);
        int codColumnIndex = cursor.getColumnIndex(DbContract.Produs.COLUMN_COD);
        int imagineColumnIndex = cursor.getColumnIndex(DbContract.Produs.COLUMN_IMAGE);
        int idColumnIndex = cursor.getColumnIndex(DbContract.Produs._ID);

        // Extrage nume si cod
        String nume = cursor.getString(numeColumnIndex);
        String cod = cursor.getString(codColumnIndex);
        byte[] imageByte = cursor.getBlob(imagineColumnIndex);


        // Ataseaza stringurile pentru nume si cod la TextView-urile corespunzatoare
        numeProdus.setText(nume);
        codProdus.setText(cod);
        imagineProdus.setImageBitmap(getImage(imageByte));

        final LinearLayout editView = (LinearLayout) view.findViewById(R.id.edit_view);
        editView.setTag(cursor.getInt(idColumnIndex));
        editView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Do when Edit Item is pressed
                ListaProduseActivity.goToEditor(((Integer) editView.getTag()));
            }
        });
    }

}
