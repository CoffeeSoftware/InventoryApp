package comcoffeesoftware.httpsgithub.inventarsoft;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import static android.graphics.Path.Direction.CW;
import static comcoffeesoftware.httpsgithub.inventarsoft.GeneratorCodBare.codCompletBinarizat;
import static comcoffeesoftware.httpsgithub.inventarsoft.GeneratorCodBare.codCompletNebinarizat;

/**
 * Clasa JAVA pentru Editor Activity
 */

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // Constanta pentru permisiune declansare camera
    static final int REQUEST_IMAGE_CAPTURE = 1;
    // ID constant pentru Loader
    private static final int EXISTING_ITEM_LOADER = 0;
    // URI pentru obiectul curent
    private Uri mCurrentItemUri;
    // Variabila care tine socoteala daca s-au editat campuri
    private boolean mItemChanged = false;
    // Variabile pentru parti din layout care pot fi atinse
    private EditText numeProdus;
    private EditText codProdus;
    private ImageView camera;
    private ImageView pozaProdus;

    // onTouchListener ca sa stim daca s-a editat ceva
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemChanged = true;
            return false;
        }
    };

    // Functie pentru transformarea din byte in bitmap
    public static byte[] getByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // Functie pentru transformarea din bitmap in byte
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    // Functia onCreate ruleaza cand e creata activitatea
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Seteaza layout-ul corespunzator activitatii
        setContentView(R.layout.activity_editor);

        // Extrage datele cu care s-a initiat activitatea de editare
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        // Gaseste View-urile editabile
        numeProdus = findViewById(R.id.Camp_nume);
        codProdus = findViewById(R.id.Camp_Cod);
        final ImageView imagineCuCod = findViewById(R.id.cod_image);
        final FrameLayout frameLayoutCod = findViewById(R.id.frame_cod);
        final Bitmap bitmapCod = Bitmap.createBitmap(197, 120, Bitmap.Config.ARGB_8888);
        TextView editare = findViewById(R.id.edit_title);
        camera = findViewById(R.id.poza_button);
        pozaProdus = findViewById(R.id.poza_produs);
        ImageView deleteProdus = findViewById(R.id.delete_button);

        // Inlatura view-urile pentru cod bare, pana cand va fi generat
        imagineCuCod.setVisibility(View.GONE);
        frameLayoutCod.setVisibility(View.GONE);

        // Verifica daca editam un produs existent sau daca adaugam unul nou
        if (mCurrentItemUri == null) {
            editare.setText(getString(R.string.new_item));
        } else {
            editare.setText(getString(R.string.edit_item));
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        // Asculta pentru a stii daca s-a editat ceva
        numeProdus.setOnTouchListener(mTouchListener);
        codProdus.setOnTouchListener(mTouchListener);
        camera.setOnTouchListener(mTouchListener);
        pozaProdus.setOnTouchListener(mTouchListener);

        // Buton de salveaza
        ImageView buttonSave = findViewById(R.id.save_button_produs);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProdus();
            }
        });

        // Buton pentru poza
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        pozaProdus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        // Dialog pentru stergere
        deleteProdus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });

        // Buton pentru generarea codului de bare
        ImageView buttonGenerateBarCode = findViewById(R.id.generate_button);
        buttonGenerateBarCode.setOnClickListener(new View.OnClickListener() {

            @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                // Extrage String din campul cu codul produsului
                String codString = codProdus.getText().toString();
                // Verifica sa avem un cod mai lung de 0 caractere
                if (codString.length() < 1) {
                    Toast.makeText(EditorActivity.this, getString(R.string.no_code), Toast.LENGTH_SHORT).show();
                    return;
                }

                // Invoca functia care transforma codul introdus in cod de bare EAN13 in format binar
                codString = codCompletBinarizat(codString);

                // Creare bitmap desenat cu codul de bare, numele produsului si semnatura
                Canvas barcodeCanvas = new Canvas(bitmapCod);
                Path path = new Path();
                Paint paint = new Paint();
                paint.setColor(getColor(R.color.secondaryColor));

                // Pentru fiecare 1 din codul EAN13 binar se traseaza o linie
                for (int i = 1; i < 96; i++) {
                    if (codString.charAt(i - 1) == '1') {
                        int bottom = 80;
                        // Barele de control sunt cu 10 pixeli mai lungi in partea de jos
                        if ((i < 4) || (i == 46) || (i == 47) || (i == 48) || (i == 49) || (i == 50) || (i > 92))
                            bottom += 10;
                        path.addRect(i * 2 - 2, 10, i * 2, bottom, CW);
                    }
                }
                // Deseneaza barele pe bitmap
                barcodeCanvas.drawPath(path, paint);

                // Deseneaza numele produsului
                paint.setTextSize((12));
                barcodeCanvas.drawText(numeProdus.getText().toString(), 10, 110, paint);
                // Deseneaza o semnatura
                paint.setColor(getColor(R.color.primaryColor));
                paint.setTextSize(10);
                barcodeCanvas.drawText("by CoffeeSoftware", 100, 110, paint);

                // Ataseaza bitmap-ul generat la ImageView-ul cu id cod_imagine din activity_editor.xml si-l face vizibil
                ImageView img = findViewById(R.id.cod_image);
                img.setImageBitmap(bitmapCod);
                frameLayoutCod.setVisibility(View.VISIBLE);
                imagineCuCod.setVisibility(View.VISIBLE);
            }
        });

        // Atingere buton de print
        ImageView butonPrinteza = findViewById(R.id.print_button);
        butonPrinteza.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Printeaza bitmap-ul generat
                PrintHelper printHelper = new PrintHelper(EditorActivity.this);
                printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                printHelper.printBitmap("printare Cod", bitmapCod);
            }
        });
    }

    // Creare cursor cand e creat Loader-ul
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Definirea proiectiei cursorului
        String[] projection = {
                DbContract.Produs._ID,
                DbContract.Produs.COLUMN_NAME,
                DbContract.Produs.COLUMN_COD,
                DbContract.Produs.COLUMN_IMAGE};

        // Cauta in baza de date in background
        return new CursorLoader(this, mCurrentItemUri, projection, null, null, null);
    }

    // Setare valori pentru campuri la finalul incarcarii Loader-ului
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Daca cursorul e gol, nu se mai seteaza valori la campurile din layout
        if (cursor == null || cursor.getCount() < 0) {
            return;
        }

        if (cursor.moveToFirst()) {
            // Returneaza indexul coloanelor
            int nameColumnIndex = cursor.getColumnIndex(DbContract.Produs.COLUMN_NAME);
            int codColumnIndex = cursor.getColumnIndex(DbContract.Produs.COLUMN_COD);
            int imageColumnIndex = cursor.getColumnIndex(DbContract.Produs.COLUMN_IMAGE);

            // Extrage date (string, integer si blob pentru imagine)
            String name = cursor.getString(nameColumnIndex);
            String cod = cursor.getString(codColumnIndex);
            byte[] imageByte = cursor.getBlob(imageColumnIndex);

            // Updatare Layout cu datele extrase
            numeProdus.setText(name);
            codProdus.setText(cod);
            pozaProdus.setImageBitmap(getImage(imageByte));
        }
    }

    // Resetare Layout la resetarea loaderului
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        numeProdus.setText("");
        codProdus.setText("");
    }

    // Functie pentru salvarea unui produs in baza de date
    private void saveProdus() {
        // Extrage datele introduse
        String name = numeProdus.getText().toString().trim();
        String cod = codProdus.getText().toString();
        String codComplet = codCompletNebinarizat(cod);

        ImageView imageView = findViewById(R.id.poza_produs);
        // Daca nu avem nume, cod si imagine, nu se salveaza
        if (mCurrentItemUri == null && (TextUtils.isEmpty(name) || (TextUtils.isEmpty(codProdus.getText())) || (imageView.getDrawable() == null))) {
            Toast.makeText(this, getString(R.string.missing_data), Toast.LENGTH_SHORT).show();
            return;
        }

        // Colectarea valorilor care vor fi salvate
        ContentValues values = new ContentValues();
        values.put(DbContract.Produs.COLUMN_NAME, name);
        values.put(DbContract.Produs.COLUMN_COD, cod);
        values.put(DbContract.Produs.COLUMN_COD_COMPLET, codComplet);

        // Converteste in bitmap imaginea produsului afisata in layout
        Bitmap mBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        if (mBitmap != null) {
            // Converteste bitmap-ul in byte si il adauga la valorile ce vor fi salvate
            values.put(DbContract.Produs.COLUMN_IMAGE, getByte(mBitmap));
        }

        // Verifica daca editam un produs existent sau daca adaugam unul nou
        if (mCurrentItemUri == null) {
            // Salveaza produs nou
            Uri newUri = getContentResolver().insert(DbContract.Produs.CONTENT_URI, values);
            // Verifica daca produsul nou a fost adaugat
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insertion_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insertion_succeded), Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            // Salveaza editarile facute unui produs existent
            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);
            // Verifica daca editarile s-au salvat cu succes
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.update_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_succeded), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    // Functie pentru deschiderea camerei
    private void dispatchTakePictureIntent() {
        // Daca e o versiune mai noua de Mashmalow trebuie sa cerem permisiune
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        5);
            }
        } else
        // Daca e versiune mai veche decat Marshmalow deschidem camera
        {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    // Daca primim permisiune deschidem camera
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 5) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } else {
                    Toast.makeText(this, R.string.camera_permision_denied, Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    // Primim poza
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            // Convertim imaginea primita in bitmap si o setam la layout-ul corespunzator
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView mImageView = findViewById(R.id.poza_produs);
            mImageView.setImageBitmap(imageBitmap);
        }
    }

    // Creare dialog pentru a preveni iesirea accidentala fara salvare
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Afiseaza dialogul
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Cand se apasa butonul Back, verifica daca s-au facut editari si afiseaza un dialog daca e necesar
    @Override
    public void onBackPressed() {
        if (!mItemChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    // Dialog pentru confirmarea stergerii
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_plus_circle_outline_white_48dp);
        builder.setMessage(R.string.delete_dialog);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Sterge un produs
    private void deleteItem() {
        if (mCurrentItemUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.failed_to_delete), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.successfully_deleted), Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}
