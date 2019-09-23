package com.itpl.scannerapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.itpl.scannerapp.R;
import com.itpl.scannerapp.activity.PdfViewActivity;

import java.io.File;
import java.util.ArrayList;

import static com.itpl.scannerapp.helper.AppUtils.getTime;
import static com.itpl.scannerapp.helper.AppUtils.openWith;
import static com.itpl.scannerapp.helper.AppUtils.shareFile;
import static com.itpl.scannerapp.helper.AppUtils.size;

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
        holder.fileName.setText(arrayList.get(position).getName());
        holder.date.setText(getTime(arrayList.get(position)));
        holder.size.setText(size(arrayList.get(position)));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                viewPdf(context, arrayList.get(position));
                Intent intent = new Intent(context, PdfViewActivity.class);
                intent.putExtra("myFile", arrayList.get(position));
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final String[] fonts = {"Open", "Share", "Open With", "Delete"};

                AlertDialog.Builder mainBuilder = new AlertDialog.Builder(context);
                mainBuilder.setTitle("Select one");
                mainBuilder.setItems(fonts, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface mainDialog, int which) {
                        if ("Open".equals(fonts[which])) {
                            mainDialog.dismiss();
                            Intent intent = new Intent(context, PdfViewActivity.class);
                            intent.putExtra("myFile", arrayList.get(position));
                            context.startActivity(intent);
                        } else if ("Share".equals(fonts[which])) {
                            mainDialog.dismiss();
                            shareFile(arrayList.get(position).getPath(), "Share PDF Using", context);
                        } else if ("Open With".equals(fonts[which])) {
                            mainDialog.dismiss();
                            openWith(context, arrayList.get(position));
                        } else if ("Delete".equals(fonts[which])) {
                            mainDialog.dismiss();
                            AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(context);
                            deleteBuilder.setTitle("Delete");
                            deleteBuilder.setMessage("Are you sure you want to delete " + arrayList.get(position).getName() + " file?");
                            deleteBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    arrayList.get(position).delete();
                                    arrayList.remove(position);
                                    notifyDataSetChanged();
                                    Toast.makeText(context, "File deleted successfully.", Toast.LENGTH_SHORT).show();
                                }
                            });

                            deleteBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            deleteBuilder.show();
                        }
                    }
                });
                mainBuilder.show();

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView fileName, date, size;

        public ViewHolder(View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.textView2);
            date = itemView.findViewById(R.id.textView);
            size = itemView.findViewById(R.id.textView3);
        }
    }
}
