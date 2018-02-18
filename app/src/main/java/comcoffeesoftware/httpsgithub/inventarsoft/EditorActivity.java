package comcoffeesoftware.httpsgithub.inventarsoft;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static comcoffeesoftware.httpsgithub.inventarsoft.GeneratorCodBare.codOK;

/**
 * Java class for Editor Activity
 */

public class EditorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        final EditText numeProdus = (EditText) findViewById(R.id.Camp_nume);
        final EditText codProdus=(EditText) findViewById(R.id.Camp_Cod);



        TextView buttonSave = (TextView) findViewById(R.id.save_button_produs);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numeString = numeProdus.getText().toString();
                String codString = codProdus.getText().toString();
                final int codInt = Integer.parseInt(codString);
                MyInventoryDBHelper mHelper = new MyInventoryDBHelper(getApplicationContext());
                SQLiteDatabase myData = mHelper.getWritableDatabase();
                mHelper.insertEntry(numeString, codInt);
                startActivity(new Intent(EditorActivity.this, ListaProduseActivity.class));
            }
        });

        TextView buttonGenerateBarCode = (TextView) findViewById(R.id.generate_button);
        buttonGenerateBarCode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String codString = codProdus.getText().toString();
                Toast.makeText(EditorActivity.this, "Cod: " + codOK(codString), Toast.LENGTH_SHORT).show();

            }
        });
    }
}
