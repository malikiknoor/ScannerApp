package com.itpl.scannerapp.barcode.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.itpl.scannerapp.R;

import static com.itpl.scannerapp.helper.AppUtils.openSettingMesseage;

public class BarCodeScanActivity extends AppCompatActivity implements View.OnClickListener {

    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView statusMessage;
    private TextView barcodeValue;
    private Barcode barcode;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final int CALL_REQUEST_CODE = 1;
    private static final int SMS_REQUEST_CODE = 2;
    private static final int SAVE_NUMBER_REQUEST_CODE = 3;
    private static final String TAG = "BarcodeMain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_code_scan);

        statusMessage = (TextView) findViewById(R.id.status_message);
        barcodeValue = (TextView) findViewById(R.id.barcode_value);

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
                    barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    statusMessage.setText(R.string.barcode_success);
                    getBarcodeType();
                } else {
                    statusMessage.setText(R.string.barcode_failure);
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                statusMessage.setText(String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        } else if (requestCode == SAVE_NUMBER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Added Contact", Toast.LENGTH_SHORT).show();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled Added Contact", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getBarcodeType() {
        switch (barcode.valueFormat) {
            case 1:
                saveNumber(barcode.contactInfo.name.last, barcode.contactInfo.phones[0].number,
                        barcode.contactInfo.emails[0].address, barcode.contactInfo.addresses[0].addressLines[0]);
                break;
            case 2:
                Toast.makeText(this, "Email", Toast.LENGTH_SHORT).show();
                break;
            case 4:
                if (barcode.phone.number != null) {
                    barcodeValue.setText("Phone: " + barcode.phone.number);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        callToNumber();
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST_CODE);
                    }
                }
                break;
            case 5:
                Toast.makeText(this, "This is bar code", Toast.LENGTH_SHORT).show();
                break;
            case 6:
                if (barcode.sms.phoneNumber != null) {
                    barcodeValue.setText("Phone: " + barcode.sms.phoneNumber + "\n Sms Body: " + barcode.sms.message);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                        sendMessage();
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_REQUEST_CODE);
                    }
                }
                break;
            case 7:
                Toast.makeText(this, "Plain text", Toast.LENGTH_SHORT).show();
                break;
            case 8:
                openUrl();
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
                Toast.makeText(this, "Error: unknown qr code value format: " + barcode.valueFormat, Toast.LENGTH_SHORT).show();
                break;

        }
    }

    private void openUrl() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Open URL");
        builder.setMessage("Do you want to open " + barcode.rawValue + " ?");
        builder.setPositiveButton("Open", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Uri uri = Uri.parse(barcode.rawValue); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CALL_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callToNumber();
                } else {
                    openSettingMesseage(this);
                }
                break;
            case SMS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendMessage();
                } else {
                    openSettingMesseage(this);
                }
                break;
        }
    }

    @SuppressLint("MissingPermission")
    private void callToNumber() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("CALL");
        builder.setMessage("Do you want to call " + barcode.phone.number + " number ?");
        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + barcode.phone.number));
                startActivity(intent);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void sendMessage() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Send SMS");
        builder.setMessage("Do you want to send SMS " + barcode.sms.phoneNumber + " on this number?\n\nMessage Body: " + barcode.sms.message + ".");
        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                try {
                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage(barcode.sms.phoneNumber, null, barcode.sms.message, null, null);
                    Toast.makeText(BarCodeScanActivity.this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(BarCodeScanActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void saveNumber(final String name, final String number, final String email, String addressLine) {
        barcodeValue.setText("Name: " + barcode.contactInfo.name.last + "\n" +
                "Phone: " + barcode.contactInfo.phones[0].number + "\n" +
                "Email: " + barcode.contactInfo.emails[0].address + "\n" +
                "Address: " + barcode.contactInfo.addresses[0].addressLines[0]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save Contact");
        builder.setMessage("Do you want to Save this contact number: \n"+barcodeValue.getText());
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
                contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

                contactIntent
                        .putExtra(ContactsContract.Intents.Insert.NAME, name)
                        .putExtra(ContactsContract.Intents.Insert.PHONE, number)
                        .putExtra(ContactsContract.Intents.Insert.EMAIL, email);

                startActivityForResult(contactIntent, SAVE_NUMBER_REQUEST_CODE);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
