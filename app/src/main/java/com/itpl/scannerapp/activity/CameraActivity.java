package com.itpl.scannerapp.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfDocument;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.itpl.scannerapp.R;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import static com.itpl.scannerapp.helper.AppUtils.getTextFromImage;

public class CameraActivity extends AppCompatActivity {

    private CameraView cameraView;
    //    private GraphicOverlay graphicOverlay;
    private ProgressDialog pd;
    private boolean isFlash = true;
    private ImageView imgFlash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        cameraView = findViewById(R.id.cameraView);
        imgFlash = findViewById(R.id.imgFlash);

        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Please wait...");

        cameraView.setFlash(CameraKit.Constants.FLASH_ON);

        cameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.setFocus(CameraKit.Constants.FOCUS_TAP_WITH_MARKER);
            }
        });

        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                pd.show();
                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, cameraView.getWidth(), cameraView.getHeight(), false);
                cameraView.stop();
                getTextFromImage(pd, CameraActivity.this, bitmap);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });
    }

    public void recognizeText(View view) {
        cameraView.start();
        cameraView.setFocus(CameraKit.Constants.FOCUS_CONTINUOUS);
        cameraView.captureImage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
        cameraView.setFocus(CameraKit.Constants.FOCUS_TAP_WITH_MARKER);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    public void chnageFlashSetting(View view) {
        if(isFlash){
            isFlash = false;
            imgFlash.setBackgroundResource(R.drawable.ic_flash_off);
            cameraView.setFlash(CameraKit.Constants.FLASH_OFF);
        }else{
            isFlash = true;
            imgFlash.setBackgroundResource(R.drawable.ic_flash);
            cameraView.setFlash(CameraKit.Constants.FLASH_ON);
        }
    }
}
