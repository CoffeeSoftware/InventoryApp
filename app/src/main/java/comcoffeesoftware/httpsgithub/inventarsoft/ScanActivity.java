package comcoffeesoftware.httpsgithub.inventarsoft;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.acker.simplezxing.activity.CaptureActivity;

/**
 * Clasa Java pentru Activitatea de Scanare
 */
public class ScanActivity extends AppCompatActivity {

    // Declararea variabilelor si constantelor folosite in aceasta clasa
    private static final int REQ_CODE_PERMISSION = 0x1111;
    private static Context mContext;
    Button goToProdus;
    int id;
    private TextView tvResult;

    // Functia onCreate ruleaza cand e creata activitatea
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Seteaza layout-ul corespunzator activitatii
        setContentView(R.layout.activity_scan);
        // Blocarea orientarii ecranului pe pozitia Portret pentru a impiedica recrearea activitatii
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        // Crearea unei variabile id initializata cu -99 care va contine id-ul din baza de date pentru produsul scanat
        id = -99;
        // Initializare context
        mContext = this;
        // Initializare obiecte pentru TextView-ul cu codul scanat si pentru Butonul cu numele produsului scanat -- setate cu vizibilitate GONE (=-8)
        tvResult = findViewById(R.id.tv_result);
        tvResult.setVisibility(View.GONE);
        ImageView btn = findViewById(R.id.scan_button);
        // Initializare scanare la pornirea activitatii
        openScanner();

        // Butonul care va avea ca text numele produsului scanat sau mesaj care spune ca nu exista in baza de date si care va trimite la produsul scanat daca exista
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openScanner();
            }
        });
        goToProdus = findViewById(R.id.goToProdus);
        goToProdus.setVisibility(View.GONE);
        goToProdus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (goToProdus.getText().toString() != null && goToProdus.getText().toString() != getString(R.string.not_found) && id != -99) {
                    // Trimitere la activitatea de editare pentru produsul scanat
                    Intent intent = new Intent(mContext, EditorActivity.class);
                    Uri currentUri = ContentUris.withAppendedId(DbContract.Produs.CONTENT_URI, id);
                    intent.setData(currentUri);
                    mContext.startActivity(intent);
                }
                // Mesaj daca codul scanat nu corespunde unui produs existent in baza de date
                else
                    Toast.makeText(ScanActivity.this, getString(R.string.not_found), Toast.LENGTH_SHORT).show();

            }
        });
    }

    // Deschidere scanner
    private void openScanner() {
        if (ContextCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Daca nu avem permisiune sa folosim camera, o cerem
            ActivityCompat.requestPermissions(ScanActivity.this, new String[]{Manifest.permission.CAMERA}, REQ_CODE_PERMISSION);
        } else {
            // Daca avem permisiunea, scanam
            startCaptureActivityForResult();
        }
    }

    // Scanam codul folosind libraria simplezxing
    private void startCaptureActivityForResult() {
        Intent intent = new Intent(ScanActivity.this, CaptureActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(CaptureActivity.KEY_NEED_BEEP, CaptureActivity.VALUE_BEEP);
        bundle.putBoolean(CaptureActivity.KEY_NEED_VIBRATION, CaptureActivity.VALUE_VIBRATION);
        bundle.putBoolean(CaptureActivity.KEY_NEED_EXPOSURE, CaptureActivity.VALUE_NO_EXPOSURE);
        bundle.putByte(CaptureActivity.KEY_FLASHLIGHT_MODE, CaptureActivity.VALUE_FLASHLIGHT_OFF);
        bundle.putByte(CaptureActivity.KEY_ORIENTATION_MODE, CaptureActivity.VALUE_ORIENTATION_AUTO);
        bundle.putBoolean(CaptureActivity.KEY_SCAN_AREA_FULL_SCREEN, CaptureActivity.VALUE_SCAN_AREA_FULL_SCREEN);
        bundle.putBoolean(CaptureActivity.KEY_NEED_SCAN_HINT_TEXT, CaptureActivity.VALUE_SCAN_HINT_TEXT);
        intent.putExtra(CaptureActivity.EXTRA_SETTING_BUNDLE, bundle);
        startActivityForResult(intent, CaptureActivity.REQ_CODE);
    }

    // Functie executata dupa ce se cere permisiunea pentru utilizarea camerei
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Utilizatorul a acordat permisiunea utilizarii camerei
                    startCaptureActivityForResult();
                } else {
                    // Utilizatorul nu a acordat permisiunea utilizarii camerei
                    Toast.makeText(this, getString(R.string.camera_permission_ask), Toast.LENGTH_LONG).show();
                }
            }
            break;
        }
    }

    // Functia se executa la primirea rezultatelor scanarii
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CaptureActivity.REQ_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        // Primeste String cu codul scanat si il seteaza la TextView-ul corespunzator
                        String cod = data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT);
                        tvResult.setText(cod);
                        // Cauta codul primit in baza de date si daca exista, seteaza numele produsului corespunzator la butonul goToProdus
                        goToProdus.setText(cautaCodInDb(cod));
                        break;
                    case RESULT_CANCELED:
                        if (data != null) {
                            // Camera nu functioneaza
                            tvResult.setText(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
                        }
                        break;
                }
                break;
        }
    }

    // Functie pentru cautarea codului primit in urma scanarii in baza de date
    private String cautaCodInDb(String cod) {
        String name = getString(R.string.not_found);
        MyInventoryDBHelper dbHelper = new MyInventoryDBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DbContract.Produs.TABLE_NAME, new String[]{DbContract.Produs._ID, DbContract.Produs.COLUMN_COD, DbContract.Produs.COLUMN_NAME}, DbContract.Produs.COLUMN_COD_COMPLET + "=?", new String[]{cod}, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int nameColumnIndex = cursor.getColumnIndex(DbContract.Produs.COLUMN_NAME);
                name = cursor.getString(nameColumnIndex);
                int idColumnIndex = cursor.getColumnIndex(DbContract.Produs._ID);
                id = cursor.getInt(idColumnIndex);
            }
        } catch (Exception e) {
            Toast.makeText(mContext, getString(R.string.not_found), Toast.LENGTH_SHORT).show();
        }
        // Setarea TextView-ului si Butonului ca si Vizibile
        goToProdus.setVisibility(View.VISIBLE);
        tvResult.setVisibility(View.VISIBLE);
        return name;
    }


}
