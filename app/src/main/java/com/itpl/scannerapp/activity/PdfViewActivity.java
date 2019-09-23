package com.itpl.scannerapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.itpl.scannerapp.R;
import com.itpl.scannerapp.helper.AppUtils;

import java.io.File;

import static com.itpl.scannerapp.helper.AppUtils.openWith;

public class PdfViewActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {

    private PDFView pdfView;
    private TextView tv_showPage, tvPdfTitle;
    private int pageNumber = 0, totalPageCount = 0;
    private File file;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);

        toolbar = findViewById(R.id.toolbar2);
        tvPdfTitle = findViewById(R.id.tvPdfTitle);
        tv_showPage = findViewById(R.id.textView5);
        pdfView = findViewById(R.id.pdfView);
        file = (File) getIntent().getSerializableExtra("myFile");
        tvPdfTitle.setText(file.getName() + "\n" + AppUtils.size(file));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        displayFromSdcard(file);

    }

    private void displayFromSdcard(File file) {
        pdfView.fromFile(file)
                .swipeHorizontal(true)
                .onPageChange(this)
                .onLoad(this)
                .load();
    }

    @Override
    public void loadComplete(int nbPages) {
        totalPageCount = nbPages;
        tv_showPage.setText("1/" + totalPageCount);
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        tv_showPage.setText((page + 1) + "/" + totalPageCount);
    }

    public void sharePdf(View view) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pdf_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_share_pdf:
                AppUtils.shareFile(file.getPath(), "Share PDF using", this);
                return true;
            case R.id.action_open_pdf:
                openWith(this, file);
                return true;
            case R.id.action_delete_pdf:
                AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(this);
                deleteBuilder.setTitle("Delete");
                deleteBuilder.setMessage("Are you sure you want to delete " + file.getName() + " file?");
                deleteBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        file.delete();
                        Toast.makeText(PdfViewActivity.this, "File deleted successfully.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

                deleteBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                deleteBuilder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
