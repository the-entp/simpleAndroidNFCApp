package com.example.coursehero.nfc_test;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;

import java.io.File;
import android.view.Display;
import android.graphics.Point;

/**
 * Created by coursehero on 11/20/14.
 */
public class DocumentAdapter extends ArrayAdapter<File> {
    private final Activity context;
    private final File[] files;
    private BitmapFactory.Options options;
    private String[] values = new String[] { "Cut, Click, Shudder\nSocio 131\nIntroduction to Sociology", "Principality of Sealand\nPsy 101\nIntroduction to Psychology", "An Attempt to Exhaust\nCS 324\nComputer Architecture",
            "Guseppi Mercieca\nHist 45\nEuropean History" };

    static class ViewHolder {
        public TextView text;
        public ImageView image;
    }

    public DocumentAdapter(Activity context, File[] files) {
        super(context, R.layout.row_layout, files);
        this.context = context;
        this.files = files;
        Log.d("int", "Document adapter constructor");
        this.options = new BitmapFactory.Options();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("int", "I am in getview");
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.row_layout, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) rowView.findViewById(R.id.image);
            viewHolder.text = (TextView) rowView.findViewById(R.id.metadata);
            rowView.setTag(viewHolder);
            Log.d("int", "Am I here then?");
        }
        ViewHolder holder = (ViewHolder) rowView.getTag();
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        File file = files[position];
        Log.d("int", file.getAbsolutePath() + " that is the file");
        if (file.exists()) {
            Log.d("int", "The file exists right?");
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            int width_o = bitmap.getWidth();
            int height_o = bitmap.getHeight();
            int scaled_height = (width * height_o)/width_o;
            bitmap = Bitmap.createScaledBitmap(bitmap, width, scaled_height, false);
            holder.image.setImageBitmap(bitmap);
            holder.text.setText(values[position % (values.length)]);
        }
        return rowView;

    }

}
