package com.itpl.scannerapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.itpl.scannerapp.R;
import com.itpl.scannerapp.adapter.FilesAdapter;
import com.itpl.scannerapp.barcode.activity.BarCodeScanActivity;
import com.itpl.scannerapp.helper.AppUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import static com.itpl.scannerapp.helper.AppUtils.getListFiles;
import static com.itpl.scannerapp.helper.AppUtils.openSettingMesseage;
import static com.itpl.scannerapp.helper.RealPathUtil.getPath;

public class HomeActivity extends AppCompatActivity {

    private ProgressDialog pd;
    private ArrayList<File> fileList;
    private static final String TAG = "HomeActivity";
    private RecyclerView rvFiles;
    private LinearLayout viewFile;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        viewFile = findViewById(R.id.viewFile);
        rvFiles = findViewById(R.id.rvFiles);
        rvFiles.setLayoutManager(new LinearLayoutManager(this));

        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Please wait...");

        path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Dir";
        fileList = new ArrayList<>();

    }

    public void openCamera(View view) {
        startActivity(new Intent(this, CameraActivity.class));
    }

    public void openGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK
                && null != data) {
            try {
                Uri selectedImage = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                pd.show();
                AppUtils.getTextFromImage(pd, this, bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Image Not Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isWriteStoragePermissionGranted()) {
                if (isReadStoragePermissionGranted()) {
                    getFiles();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                }
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }else{
            getFiles();
        }
        super.onResume();
    }

    @SuppressLint("NewApi")
    public boolean isReadStoragePermissionGranted() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    @SuppressLint("NewApi")
    public boolean isWriteStoragePermissionGranted() {
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void getFiles() {
        fileList = getListFiles(new File(path));
        if (fileList.size() == 0) {
            viewFile.setVisibility(View.GONE);
        } else {
            viewFile.setVisibility(View.VISIBLE);
            Collections.reverse(fileList);
            rvFiles.setAdapter(new FilesAdapter(this, fileList));
        }
    }

    public void scanCode(View view) {
        startActivity(new Intent(this, BarCodeScanActivity.class));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getFiles();
                } else {
                    openSettingMesseage(this);
                }
                break;
        }
    }
}
