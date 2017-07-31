package walker.pack.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import walker.pack.R;
import walker.pack.classes.POI;

/**
 * Created by Olebogeng Malope on 7/22/2017.
 */

public class POIAdapter extends ArrayAdapter<POI> {

    Context context;

    private class ViewHolder{
        TextView poi_type_text_view, poi_description_text_view;
    }
    public POIAdapter(ArrayList<POI> data, Context context){
        super(context, R.layout.venue_row_layout, data);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // get the data item for this position
        POI poi = getItem(position);

        ViewHolder viewHolder;

        final View result;

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.poi_row_layout, parent, false);

            viewHolder.poi_type_text_view = (TextView) convertView.findViewById(R.id.poi_type_text_view);
            viewHolder.poi_description_text_view = (TextView) convertView.findViewById(R.id.poi_description_text_view);

            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        assert poi != null;
        viewHolder.poi_type_text_view.setText(poi.getType());
        viewHolder.poi_description_text_view.setText(poi.getDescription());

        return result;
    }
}
