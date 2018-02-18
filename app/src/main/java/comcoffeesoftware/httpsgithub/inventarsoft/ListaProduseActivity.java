package comcoffeesoftware.httpsgithub.inventarsoft;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Java class for Lista Produse Activity
 */

public class ListaProduseActivity extends AppCompatActivity implements AdaptorListaProduse.MyOnItemClickListener{

    private static final int numarProduse = 100;

    private AdaptorListaProduse mAdaptorListaProduse;
    private RecyclerView mRecyclerView;
    private SQLiteDatabase myData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_produse);

        final MyInventoryDBHelper mHelper = new MyInventoryDBHelper(this);
        myData = mHelper.getWritableDatabase();

        mRecyclerView = (RecyclerView) findViewById(R.id.lista_produse);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdaptorListaProduse = new AdaptorListaProduse(getEntries(), this);

        mRecyclerView.setAdapter(mAdaptorListaProduse);

        TextView butonFake = (TextView) findViewById(R.id.fake_data);
        butonFake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHelper.insertEntry("nume produs", 13234);
                mRecyclerView.setAdapter(mAdaptorListaProduse);
            }
        });

        TextView butonAdd = (TextView) findViewById(R.id.add);
        butonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListaProduseActivity.this, EditorActivity.class));
            }
        });
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Toast.makeText(this, "produs nr. "+ clickedItemIndex, Toast.LENGTH_SHORT).show();

    }


    private Cursor getEntries() {
        return myData.query(
                DbContract.Produse.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                DbContract.Produse._ID + " DESC"// Order by column
        );
    }
}
