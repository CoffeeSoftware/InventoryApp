package comcoffeesoftware.httpsgithub.inventarsoft;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Clasa JAVA pentru Contact Activity
 */

public class ContactActivity extends AppCompatActivity {

    // Functia onCreate ruleaza cand e creata activitatea ContactActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setarea layout-ului corespunzator
        setContentView(R.layout.activity_contact);

        // Creaza obiect ImageView si initiaza cu atributele gasite in ImageView-ul cu id=email din activity_contact.xml
        final ImageView emailImageView = findViewById(R.id.email);
        // Asculta pentru click
        emailImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Creare intent implicit pentru trimitere email
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto: dacian73@gmail.com, mihaitucui2009@gmail.com"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Inventory App Feedback");
                startActivity(Intent.createChooser(emailIntent, "Send feedback"));
            }
        });

        // Creaza obiect ImageView si initiaza cu atributele gasite in ImageView-ul cu id=SMS din activity_contact.xml
        final ImageView smsImageView = findViewById(R.id.sms);
        // Asculta pentru click
        smsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Creare intent implicit pentru trimitere sms
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("smsto:" + Uri.encode("0753911272, 0787782071")));
                startActivity(intent);
            }
        });

        // Creaza obiect ImageView si initiaza cu atributele gasite in ImageView-ul cu id=telefon din activity_contact.xml
        final ImageView callImageView = findViewById(R.id.telefon);
        // Asculta pentru click
        callImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Creare intent implicit pentru apelare
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "0787782071", null));
                startActivity(intent);
            }
        });

        // Creaza obiect ImageView si initiaza cu atributele gasite in ImageView-ul cu id=adresa_facebook din activity_contact.xml
        final ImageView facebookImageView = findViewById(R.id.adresa_facebook);
        // Asculta pentru click
        facebookImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Creare intent explicit pentru facebook messenger
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setPackage("com.facebook.orca");
                    intent.setData(Uri.parse("https://m.me/" + "alx.dacian"));
                    startActivity(intent);
                } catch (Exception e) {
                    // Afiseaza eroare daca Facebook Messenger nu e instalat
                    Toast.makeText(ContactActivity.this, getString(R.string.no_facebook_messenger), Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Creaza obiect ImageView si initiaza cu atributele gasite in ImageView-ul cu id=whatsapp din activity_contact.xml
        final ImageView whatsappImageView = findViewById(R.id.whatsapp);
        // Creare intent explicit pentru WhatsApp, afiseaza eroare daca nu e instalat
        try {
            whatsappImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String toNumber = "+40 78778 2071"; // contains spaces.
                    toNumber = toNumber.replace("+", "").replace(" ", "");

                    Intent sendIntent = new Intent("android.intent.action.MAIN");
                    sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, " ");
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setPackage("com.whatsapp");
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                }
            });
        } catch (Exception e) {
            // Afiseaza eroare daca WhatsApp nu e instalat
            Toast.makeText(this, getString(R.string.no_whatsapp), Toast.LENGTH_SHORT).show();
        }
    }
}
