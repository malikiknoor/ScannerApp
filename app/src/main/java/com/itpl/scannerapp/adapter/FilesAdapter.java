package com.itpl.scannerapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itpl.scannerapp.R;
import com.itpl.scannerapp.activity.PdfViewActivity;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.itpl.scannerapp.helper.AppUtils.size;
import static com.itpl.scannerapp.helper.AppUtils.viewPdf;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<File> arrayList;

    public FilesAdapter(Context context, ArrayList<File> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_files, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.fileName.setText((position+1)+")   "+arrayList.get(position).getName() + "   " + size(arrayList.get(position)));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                viewPdf(context, arrayList.get(position));
                Intent intent = new Intent(context, PdfViewActivity.class);
                intent.putExtra("myFile", arrayList.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView fileName;

        public ViewHolder(View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.textView2);
        }
    }
}
