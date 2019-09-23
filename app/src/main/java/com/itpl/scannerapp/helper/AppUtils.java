package com.itpl.scannerapp.helper;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.itpl.scannerapp.activity.TextShowActivity;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AppUtils {

    public static void getTextFromImage(ProgressDialog pd, Context context, Bitmap bitmap) {
        TextRecognizer txtRecognizer = new TextRecognizer.Builder(context).build();
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray items = txtRecognizer.detect(frame);
        StringBuilder strBuilder = new StringBuilder();
        if (items.size() == 0) {
            pd.dismiss();
            Intent intent = new Intent(context, TextShowActivity.class);
            intent.putExtra("result", "No Text is Detected");
            context.startActivity(intent);
        } else {
            for (int i = 0; i < items.size(); i++) {
                TextBlock item = (TextBlock) items.valueAt(i);
                strBuilder.append(item.getValue());
                strBuilder.append("\n");
                if (i == (items.size() - 1)) {
                    pd.dismiss();
                    Intent intent = new Intent(context, TextShowActivity.class);
                    intent.putExtra("result", strBuilder.toString());
                    context.startActivity(intent);
                }
            }
        }
    }

    public static void viewPdf(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    public static ArrayList<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    inFiles.addAll(getListFiles(file));
                } else {
                    inFiles.add(file);
                }
        }
        return inFiles;
    }

    public static String size(File file) {
        int size = (int) file.length();
        DecimalFormat df = new DecimalFormat("0.00");

        float sizeKb = 1024.0f;
        float sizeMb = sizeKb * sizeKb;
        float sizeGb = sizeMb * sizeKb;
        float sizeTerra = sizeGb * sizeKb;

        if (size < sizeMb) {
            return df.format(size / sizeKb) + " Kb";
        }
        else if (size < sizeGb) {
            return df.format(size / sizeMb) + " Mb";
        }
        else if (size < sizeTerra) {
            return df.format(size / sizeGb) + " Gb";
        }

        return "Error to get size";
    }

    public static String getTime(File file){
        Date lastModDate = new Date(file.lastModified());
        String str_date=lastModDate.toString();

        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        Date newDate = null;
        try {
            newDate = format.parse(str_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        format = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
        return format.format(newDate);

    }

    public static void shareFile(String path, String message, Context context){
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        Uri screenshotUri = Uri.parse(path);
        sharingIntent.setType("*/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        context.startActivity(Intent.createChooser(sharingIntent, message));
    }

    public static void openWith(Context context, File file) {
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        String mime = context.getContentResolver().getType(uri);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }

}
