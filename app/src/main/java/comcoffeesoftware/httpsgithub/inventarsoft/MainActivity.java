package comcoffeesoftware.httpsgithub.inventarsoft;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Trimitere la lista produse
        ImageView imageViewListaProduse = (ImageView) findViewById(R.id.imagine_lista_produse);
        imageViewListaProduse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ListaProduseActivity.class));
            }
        });

        // Trimitere la contact
        ImageView imageViewContact = (ImageView) findViewById(R.id.imagine_contact);
        imageViewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ContactActivity.class));
            }
        });

        // Trimitere la contact
        ImageView imageViewScanning = (ImageView) findViewById(R.id.imagine_scanare);
        imageViewScanning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ScanActivity.class));
            }
        });
    }
}
