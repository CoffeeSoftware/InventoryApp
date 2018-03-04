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
import android.database.sqlite.SQLiteDatabase;
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
import static comcoffeesoftware.httpsgithub.inventarsoft.GeneratorCodBare.codOK;

/**
 * Java class for Editor Activity
 */

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // Loader identifier
    private static final int EXISTING_ITEM_LOADER = 0;
    // Current item URI
    private Uri mCurrentItemUri;

    private boolean mItemChanged = false;

    static final int REQUEST_IMAGE_CAPTURE = 1;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();



        // Gaseste View-urile editabile
        numeProdus = (EditText) findViewById(R.id.Camp_nume);
        codProdus=(EditText) findViewById(R.id.Camp_Cod);
        final ImageView imagineCuCod = (ImageView) findViewById(R.id.cod_image);
        final FrameLayout frameLayoutCod = (FrameLayout) findViewById(R.id.frame_cod);
        final Bitmap bitmapCod = Bitmap.createBitmap(197, 120, Bitmap.Config.ARGB_8888);
        TextView editare = (TextView) findViewById(R.id.edit_title);
        camera = (ImageView) findViewById(R.id.poza_button);
        pozaProdus = (ImageView) findViewById(R.id.poza_produs);
        ImageView deleteProdus = (ImageView) findViewById(R.id.delete_button);
        // Inlatura view-urile pentru cod bare, pana cand va fi generat
        imagineCuCod.setVisibility(View.GONE);
        frameLayoutCod.setVisibility(View.GONE);

        // Check if we are editing an existing row or adding a new one
        if (mCurrentItemUri == null) {
            editare.setText(getString(R.string.new_item));
        } else {
            editare.setText(getString(R.string.edit_item));
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        numeProdus.setOnTouchListener(mTouchListener);
        codProdus.setOnTouchListener(mTouchListener);
        camera.setOnTouchListener(mTouchListener);
        pozaProdus.setOnTouchListener(mTouchListener);

        // Buton de salvarealveaza
        ImageView buttonSave = (ImageView) findViewById(R.id.save_button_produs);
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
                Toast.makeText(EditorActivity.this, "Functia inca nu e implementata", Toast.LENGTH_SHORT).show();
            }
        });



        ImageView buttonGenerateBarCode = (ImageView) findViewById(R.id.generate_button);
        buttonGenerateBarCode.setOnClickListener(new View.OnClickListener() {

            @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                String codString = codProdus.getText().toString();
                if (codString.length() < 1) {
                    Toast.makeText(EditorActivity.this, "Nu ati introdus codul", Toast.LENGTH_SHORT).show();
                    return;

                }
                codString = codOK(codString);

                // TODO
                Canvas barcodeCanvas = new Canvas(bitmapCod);
                Path path = new Path();
                Paint paint = new Paint();
                paint.setColor(getColor(R.color.secondaryColor));

                for (int i = 1; i < 96; i++) {
                    if (codString.charAt(i-1) == '1') {
                        int bottom = 80;
                        if ((i < 4) || (i == 46) || (i == 47) || (i == 48) || (i == 49) || (i == 50) || (i > 92)) bottom += 10;
                        path.addRect(i * 2 - 2, 10, i * 2, bottom, CW);
                    }
                }

                barcodeCanvas.drawPath(path, paint);
                paint.setTextSize((12));
                barcodeCanvas.drawText(numeProdus.getText().toString(), 10, 100, paint);
                paint.setColor(getColor(R.color.primaryColor));
                paint.setTextSize(10);
                barcodeCanvas.drawText("by CoffeeSoftware", 100, 110, paint);

                ImageView img = (ImageView) findViewById(R.id.cod_image);
                img.setImageBitmap(bitmapCod);
                frameLayoutCod.setVisibility(View.VISIBLE);
                imagineCuCod.setVisibility(View.VISIBLE);


            }
        });

        ImageView butonPrinteza = (ImageView) findViewById(R.id.print_button);
        butonPrinteza.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                PrintHelper printHelper = new PrintHelper(EditorActivity.this);
                printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                printHelper.printBitmap("printare Cod", bitmapCod);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define the projection
        String[] projection = {
                DbContract.Produs._ID,
                DbContract.Produs.COLUMN_NAME,
                DbContract.Produs.COLUMN_COD,
                DbContract.Produs.COLUMN_IMAGE};

        // Query on background
        return new CursorLoader(this, mCurrentItemUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 0) {
            return;
        }

        if (cursor.moveToFirst()) {
            // Get column indexes
            int nameColumnIndex = cursor.getColumnIndex(DbContract.Produs.COLUMN_NAME);
            int codColumnIndex = cursor.getColumnIndex(DbContract.Produs.COLUMN_COD);
            int imageColumnIndex = cursor.getColumnIndex(DbContract.Produs.COLUMN_IMAGE);
            // Extract string, integers and blob
            String name = cursor.getString(nameColumnIndex);
            String cod = cursor.getString(codColumnIndex);
            byte[] imageByte = cursor.getBlob(imageColumnIndex);
            // Update the views
            numeProdus.setText(name);
            codProdus.setText(cod);
            pozaProdus.setImageBitmap(getImage(imageByte));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        numeProdus.setText("");
        codProdus.setText("");
    }

    private void saveProdus() {
        // Get input values
        String name = numeProdus.getText().toString().trim();
        ImageView imageView = (ImageView) findViewById(R.id.poza_produs);
        // If no name was introduced, do not save
        if (mCurrentItemUri == null && (TextUtils.isEmpty(name) || (TextUtils.isEmpty(codProdus.getText())) || (imageView.getDrawable() == null))) {
            Toast.makeText(this, getString(R.string.missing_data), Toast.LENGTH_SHORT).show();
            return;
        }


        ContentValues values = new ContentValues();
        values.put(DbContract.Produs.COLUMN_NAME, name);
        values.put(DbContract.Produs.COLUMN_COD, codProdus.getText().toString());

        // Convert the image from the image view to bitmap, convert to byte and put it in our content values
        Bitmap mBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        if (mBitmap != null) {
            values.put(DbContract.Produs.COLUMN_IMAGE, getByte(mBitmap));
        }

        // Check if it is a new item or if we are editing an existing item
        if (mCurrentItemUri == null) {
            Uri newUri = getContentResolver().insert(DbContract.Produs.CONTENT_URI, values);
            // Verify if item was inserted
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insertion_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insertion_succeded), Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);
            // Verify if the update was successful
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.update_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_succeded), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    // For taking a photo
    private void dispatchTakePictureIntent() {
        // Daca e o versiune mai noua de Mashmalow trebuie sa cerem permisiune
        if( ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
                        Toast.makeText(this, "Nu avem permisiune sa folosim camera", Toast.LENGTH_SHORT).show();
                    }
                }

            }
    }

    // Get the thumbnail of the photo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView mImageView = (ImageView) findViewById(R.id.poza_produs);
            mImageView.setImageBitmap(imageBitmap);
        }
    }

    // Get byte from bitmap
    public static byte[] getByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // Get bitmap from byte
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    // Create dialog to prevent from accidentally exiting without saving
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

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // When touching back, verify if changes were made and make the dialog if necessary
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

}
