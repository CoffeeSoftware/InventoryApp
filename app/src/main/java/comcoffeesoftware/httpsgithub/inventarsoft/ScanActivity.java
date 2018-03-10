package comcoffeesoftware.httpsgithub.inventarsoft;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.acker.simplezxing.activity.CaptureActivity;

public class ScanActivity extends AppCompatActivity {


private static final int REQ_CODE_PERMISSION = 0x1111;
    private TextView tvResult;
    TextView goToProdus;
    private static Context mContext;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        id = -99;
        mContext = this;
        tvResult = (TextView) findViewById(R.id.tv_result);
        ImageView btn = (ImageView) findViewById(R.id.scan_button);
        openScanner();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openScanner();
          }
      });

        goToProdus = (TextView) findViewById(R.id.goToProdus);
        goToProdus.setVisibility(View.GONE);
        goToProdus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (goToProdus.getText().toString() != null && goToProdus.getText().toString() != "nu exista in DB" && id != -99) {
                    Intent intent = new Intent(mContext, EditorActivity.class);
                    Uri currentUri = ContentUris.withAppendedId(DbContract.Produs.CONTENT_URI, id);
                    intent.setData(currentUri);
                    mContext.startActivity(intent);
                } else Toast.makeText(ScanActivity.this, "Not Found", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void openScanner() {
        if (ContextCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Do not have the permission of camera, request it.
            ActivityCompat.requestPermissions(ScanActivity.this, new String[]{Manifest.permission.CAMERA}, REQ_CODE_PERMISSION);
        } else {
            // Have gotten the permission
            startCaptureActivityForResult();
        }
    }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // User agree the permission
                    startCaptureActivityForResult();
                } else {
                    // User disagree the permission
                    Toast.makeText(this, "Trebuie sa dai permisiunea de folosire a camerei", Toast.LENGTH_LONG).show();
                }
            }
            break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CaptureActivity.REQ_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        String cod = data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT);
                        tvResult.setText(cod);  //or do sth
                        goToProdus.setText(cautaCodInDb(cod));
                        break;
                    case RESULT_CANCELED:
                        if (data != null) {
                            // for some reason camera is not working correctly
                            tvResult.setText(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
                        }
                        break;
                }
                break;
        }
    }

    private String cautaCodInDb(String cod) {
        String name = "nu exista in DB";
        MyInventoryDBHelper dbHelper = new MyInventoryDBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DbContract.Produs.TABLE_NAME, new String[] { DbContract.Produs._ID, DbContract.Produs.COLUMN_COD, DbContract.Produs.COLUMN_NAME }, DbContract.Produs.COLUMN_COD_COMPLET + "=?", new String[] {cod}, null, null, null , null);
        if (cursor != null) {
            cursor.moveToFirst();
            int nameColumnIndex = cursor.getColumnIndex(DbContract.Produs.COLUMN_NAME);
            name = cursor.getString(nameColumnIndex);
            int idColumnIndex = cursor.getColumnIndex(DbContract.Produs._ID);
            id = cursor.getInt(idColumnIndex);
        }
        } catch (Exception e) {
            Toast.makeText(mContext, "not Found", Toast.LENGTH_SHORT).show();
        }
        goToProdus.setVisibility(View.VISIBLE);
        return name;
    }



}
