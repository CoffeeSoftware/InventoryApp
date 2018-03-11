package comcoffeesoftware.httpsgithub.inventarsoft;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

/**
 * Clasa JAVA pentru Activitatea Main
 */

public class MainActivity extends AppCompatActivity {

    // Functia onCreate ruleaza cand e creata activitatea
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Seteaza layout-ul corespunzator activitatii
        setContentView(R.layout.activity_main);

        // Trimitere la lista produse
        ImageView imageViewListaProduse = findViewById(R.id.imagine_lista_produse);
        imageViewListaProduse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ListaProduseActivity.class));
            }
        });

        // Trimitere la contact
        ImageView imageViewContact = findViewById(R.id.imagine_contact);
        imageViewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ContactActivity.class));
            }
        });

        // Trimitere la scanare
        ImageView imageViewScanning = findViewById(R.id.imagine_scanare);
        imageViewScanning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ScanActivity.class));
            }
        });

        // Trimitere la help
        FloatingActionButton helpImage = findViewById(R.id.help);
        helpImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, HelpActivity.class));
            }
        });
    }
}
