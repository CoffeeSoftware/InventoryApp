package comcoffeesoftware.httpsgithub.inventarsoft;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Vector;

import static android.graphics.Path.Direction.CW;
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
        final Bitmap bitmapCod = Bitmap.createBitmap(300, 100, Bitmap.Config.ARGB_8888);;


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

            @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                String codString = codProdus.getText().toString();
                codString = codOK(codString);
                Toast.makeText(EditorActivity.this, "Cod: " + codString, Toast.LENGTH_SHORT).show();

                // TODO
                Canvas barcodeCanvas = new Canvas(bitmapCod);
                Path path = new Path();
                Paint paintAlb = new Paint();
                paintAlb.setColor(getColor(R.color.primaryLightColor));

                for (int i = 1; i < 96; i++) {
                    if (codString.charAt(i-1) == '1') {
                        int bottom = 80;
                        if ((i < 4) || (i == 46) || (i == 47) || (i == 48) || (i == 49) || (i == 50) || (i > 92)) bottom += 10;
                        path.addRect(i * 2 - 2, 10, i * 2, bottom, CW);
                    }
                }

                barcodeCanvas.drawPath(path, paintAlb);

                ImageView img = (ImageView) findViewById(R.id.cod_image);
                img.setImageBitmap(bitmapCod);


            }
        });

        TextView butonPrinteza = (TextView) findViewById(R.id.print_button);
        butonPrinteza.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                PrintHelper printHelper = new PrintHelper(EditorActivity.this);
                printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                printHelper.printBitmap("printare Cod", bitmapCod);
            }
        });
    }
}
