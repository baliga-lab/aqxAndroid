package org.systemsbiology.baliga.aqx1010;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.systemsbiology.baliga.aqx1010.apiclient.AqxSystem;
import org.systemsbiology.baliga.aqx1010.apiclient.SystemDefaults;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class AqxSystemListAdapter extends ArrayAdapter<AqxSystem> {
    public AqxSystemListAdapter(Context context, int resourceId, int textViewResourceId,
                                List<AqxSystem> items) {
        super(context, resourceId, textViewResourceId, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.system_list_item, null);
        }
        AqxSystem item = this.getItem(position);
        if (item != null) {
            TextView tv = (TextView) v.findViewById(R.id.systemNameTextView);
            ImageView iv = (ImageView) v.findViewById(R.id.systemThumbnailView);
            tv.setText(item.name);
            iv.setImageBitmap(item.thumbnail);
        }
        //return super.getView(position, convertView, parent);
        return v;
    }
}
