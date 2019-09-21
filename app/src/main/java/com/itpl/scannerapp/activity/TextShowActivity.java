package com.itpl.scannerapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itpl.scannerapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
public class TextShowActivity extends AppCompatActivity {

    private TextView tv_result;
    private static final int REQUEST = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_show);

        tv_result = findViewById(R.id.tv_result);

        tv_result.setText(getIntent().getStringExtra("result"));
    }

    public void saveAsPdf(View view) {
        if(isWriteStoragePermissionGranted()){
            if(isReadStoragePermissionGranted()){
                createandDisplayPdf();
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            }
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @SuppressLint("NewApi")
    public  boolean isReadStoragePermissionGranted() {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
    }


    @SuppressLint("NewApi")
    public  boolean isWriteStoragePermissionGranted() {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    createandDisplayPdf();
                }else{
                    Toast.makeText(this, "You Have to agree the permisson to do this task", Toast.LENGTH_SHORT).show();
                }
                break;

            case 2:
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    createandDisplayPdf();
                }else{
                    Toast.makeText(this, "You Have to agree the permisson to do this task", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void createandDisplayPdf() {

        Document doc = new Document();

        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Dir";

            File dir = new File(path);
            if(!dir.exists())
                dir.mkdirs();

            long time= System.currentTimeMillis();
            File file = new File(dir, time+".pdf");
            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();

            Paragraph p1 = new Paragraph(tv_result.getText().toString());
            p1.setAlignment(Paragraph.ALIGN_CENTER);
            //add paragraph to document
            doc.add(p1);
            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
            Toast.makeText(this, "DocumentException:" + de.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
            Toast.makeText(this, "ioException:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally {
            doc.close();
        }
    }

}
