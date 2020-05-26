package com.myapp.qrcodescanner;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;



public class MainActivity extends AppCompatActivity {
    SurfaceView surfaceView, surfaceViewMain;
    CameraSource cameraSource, cameraSourceMain;
    TextView textView;
    BarcodeDetector barcodeDetector;

    private int CAMERA_PERMISSION_CODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView=(SurfaceView) findViewById(R.id.surfaceView);
        textView=(TextView) findViewById(R.id.textView);
        surfaceViewMain=(SurfaceView) findViewById(R.id.surfaceView_main);
        //BlurKit.init(this);
       // surfaceViewMain=surfaceViewMain.getHolder();

        Context context;
        Check_Permmission();
        // surfaceView.setVisibility(View.INVISIBLE);

        /*barcodeDetector= new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                .build();-/

         */
        barcodeDetector=new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource=new CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(640, 480).setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceViewMain.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    cameraSource.start(surfaceHolder);
                    //cameraSource.start(surfaceHolder);
                    ///BlurKit.getInstance().blur(View view, int 400);

                    //   BlurKit.getInstance().blur(View view, int radius);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    cameraSource.start(surfaceHolder);
                    //cameraSource.start(surfaceHolder);
                    ///BlurKit.getInstance().blur(View view, int 400);

                    //   BlurKit.getInstance().blur(View view, int radius);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            ///
            // onSizeC

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                //Canvas canvas.drawColor(Color.TRANSPARENT,Mode.CLEAR);

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCode=detections.getDetectedItems();
                if (qrCode.size() != 0) {
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            if (textView.getText() == "") {
                                Vibrator vibrator=(Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                                vibrator.vibrate(100);
                                textView.setText(qrCode.valueAt(0).displayValue);
                            }
                        }

                    });
                }
            }
        });
    }

    private void Check_Permmission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Toast.makeText(getApplicationContext(), "Permission already granted", Toast.LENGTH_SHORT).show();
            surfaceView.setVisibility(View.VISIBLE);
            surfaceViewMain.setVisibility(View.VISIBLE);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                new AlertDialog.Builder(this)
                        .setTitle("Permission needed")
                        .setMessage("Camera permission is needed")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                            }
                        })
                       /* .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })*/
                        .create().show();


            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                surfaceView.setVisibility(View.VISIBLE);
                surfaceViewMain.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();

            } else {
                surfaceView.setVisibility(View.INVISIBLE);
                surfaceViewMain.setVisibility(View.INVISIBLE);

                Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);

            }
        }

    }
}
