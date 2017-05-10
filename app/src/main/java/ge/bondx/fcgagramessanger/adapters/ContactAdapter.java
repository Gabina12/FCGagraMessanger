package ge.bondx.fcgagramessanger.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import ge.bondx.fcgagramessanger.R;
import ge.bondx.fcgagramessanger.custom.CustomImageView;
import ge.bondx.fcgagramessanger.models.Contact;
import ge.bondx.fcgagramessanger.utils.ImageLoader;

/**
 * Created by Admin on 4/23/2017.
 */

public class ContactAdapter extends BaseAdapter implements Filterable {

    private Activity activity;
    private ArrayList<Contact> data;
    private ArrayList<Contact> databkp;
    private static LayoutInflater inflater = null;
    public ImageLoader imageLoader;

    public ContactAdapter(Activity a, ArrayList<Contact> d) {
        activity = a;
        data = d;
        databkp = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.list_row, null);

        TextView title = (TextView) vi.findViewById(R.id.title); // title
        TextView artist = (TextView) vi.findViewById(R.id.artist); // artist name
        TextView duration = (TextView) vi.findViewById(R.id.duration); // duration
        CircleImageView thumb_image = (CircleImageView) vi.findViewById(R.id.list_image); // thumb image

        Contact song = new Contact();
        song = data.get(position);

        // Setting all values in listview
        title.setText(song.getFirstName() + " " + song.getLastName());
        artist.setText(song.getPosition());
        duration.setText(song.getCategory());

        Glide.with(activity.getApplicationContext())
                .load(song.getImageUrl())
                .centerCrop()
                .error(R.mipmap.ic_launcher)
                .into(thumb_image);
        return vi;
    }

    Filter myFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            ArrayList<Contact> tempList = new ArrayList<Contact>();
            data = databkp;
            if(constraint != "") {

                if (constraint != null && data != null) {
                    int length = data.size();
                    int i = 0;
                    while (i < length) {
                        Contact item = data.get(i);
                        if (item.getFirstName().contains(constraint) || item.getLastName().contains(constraint)) {
                            tempList.add(item);
                        }
                        i++;
                    }
                    filterResults.values = tempList;
                    filterResults.count = tempList.size();
                }
            }else{
                filterResults.values = databkp;
                filterResults.count = databkp.size();
            }
            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence contraint, FilterResults results) {
            data = (ArrayList<Contact>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    };


    @Override
    public Filter getFilter() {
        return myFilter;
    }
}