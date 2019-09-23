package com.itpl.scannerapp.barcode.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.itpl.scannerapp.R;

public class BarCodeScanActivity extends AppCompatActivity implements View.OnClickListener{

    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView statusMessage;
    private TextView barcodeValue;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_code_scan);

        statusMessage = (TextView)findViewById(R.id.status_message);
        barcodeValue = (TextView)findViewById(R.id.barcode_value);

        autoFocus = (CompoundButton) findViewById(R.id.auto_focus);
        useFlash = (CompoundButton) findViewById(R.id.use_flash);

        findViewById(R.id.read_barcode).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_barcode) {
            // launch barcode activity.
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
            intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());

            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    statusMessage.setText(R.string.barcode_success);
                    barcodeValue.setText(barcode.displayValue);
                    getBarcodeType(barcode.valueFormat);
                } else {
                    statusMessage.setText(R.string.barcode_failure);
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                statusMessage.setText(String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    
    private void getBarcodeType(int type){
        switch (type){
            case 1:
                Toast.makeText(this, "Contact Data", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(this, "Email", Toast.LENGTH_SHORT).show();
                break;
            case 4:
                Toast.makeText(this, "Phone", Toast.LENGTH_SHORT).show();
                break;
            case 6:
                Toast.makeText(this, "SMS", Toast.LENGTH_SHORT).show();
                break;
            case 7:
                Toast.makeText(this, "Plain text", Toast.LENGTH_SHORT).show();
                break;
            case 8:
                Toast.makeText(this, "Url", Toast.LENGTH_SHORT).show();
                break;
            case 9:
                Toast.makeText(this, "WIFI", Toast.LENGTH_SHORT).show();
                break;
            case 10:
                Toast.makeText(this, "Geo", Toast.LENGTH_SHORT).show();
                break;
            case 11:
                Toast.makeText(this, "Event", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "Error: unknown qr code value format: "+type, Toast.LENGTH_SHORT).show();
                break;

        }
    }

}
