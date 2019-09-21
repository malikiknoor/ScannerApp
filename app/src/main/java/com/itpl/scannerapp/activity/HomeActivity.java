package com.itpl.scannerapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
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
import com.itpl.scannerapp.helper.AppUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import static com.itpl.scannerapp.helper.AppUtils.getListFiles;
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
        }else{
            Toast.makeText(this, "Image Not Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fileList = getListFiles(new File(path));
        if(fileList.size() == 0){
            viewFile.setVisibility(View.GONE);
        }else{
            viewFile.setVisibility(View.VISIBLE);
            Collections.reverse(fileList);
            rvFiles.setAdapter(new FilesAdapter(this, fileList));
        }
    }
}
