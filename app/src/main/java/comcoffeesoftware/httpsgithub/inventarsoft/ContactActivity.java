package comcoffeesoftware.httpsgithub.inventarsoft;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        final ImageView emailImageView = (ImageView) findViewById(R.id.email);
        emailImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto: dacian73@gmail.com, mihaitucui2009@gmail.com"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Inventory App Feedback");
                startActivity(Intent.createChooser(emailIntent, "Send feedback"));
            }
        });


        final ImageView smsImageView = (ImageView) findViewById(R.id.SMS);
        smsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("smsto:" + Uri.encode("0753911272, 0787782071")));
                startActivity(intent);
            }
        });

        final ImageView callImageView = (ImageView) findViewById(R.id.Telefon);
        callImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "0787782071", null));
                startActivity(intent);
            }
        });
        final ImageView facebookImageView = (ImageView) findViewById(R.id.Adresa_Facebook);
        facebookImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setPackage("com.facebook.orca");
                intent.setData(Uri.parse("https://m.me/"+"alx.dacian"));
                startActivity(intent);
            }
        });
        final ImageView whatsappImageView = (ImageView) findViewById(R.id.Whats_Up);
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
    }
}
