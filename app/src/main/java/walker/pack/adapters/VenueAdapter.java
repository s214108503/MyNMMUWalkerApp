package walker.pack.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import walker.pack.R;
import walker.pack.classes.Venue;

/**
 * Created by Olebogeng Malope on 7/22/2017.
 */

public class VenueAdapter extends ArrayAdapter<Venue> {

    private Context context;

    public VenueAdapter(ArrayList<Venue> data, Context context){
        super(context, R.layout.venue_row_layout, data);
        this.context = context;
    }

    // view lookup cache
    private static class ViewHolder{
        TextView venue_id_text_view, venue_type_text_view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get the data item for this position
        Venue venue = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.venue_row_layout, parent, false);
            /*viewHolder.poi_detailed_building_floor_door_text_view = (TextView) convertView.findViewById(R.id.poi_detailed_building_floor_door_text_view);
            viewHolder.poi_detailed_qr_code_text_view = (TextView) convertView.findViewById(R.id.poi_detailed_qr_code_text_view);
            viewHolder.poi_detailed_Type_text_view = (TextView) convertView.findViewById(R.id.poi_detailed_Type_text_view);
            viewHolder.poi_detailed_description_text_view = (TextView) convertView.findViewById(R.id.poi_detailed_description_text_view);*/
            viewHolder.venue_id_text_view = (TextView) convertView.findViewById(R.id.venue_id_text_view);
            viewHolder.venue_type_text_view = (TextView) convertView.findViewById(R.id.venue_type_text_view);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        assert venue != null;
        /*String b_f_d = "";
        if (venue.getBuilding_Number().length() > 0)
            b_f_d += venue.getBuilding_Number();
        if (venue.getFloor_Number().length() > 0)
            b_f_d += "_" + venue.getFloor_Number();
        if (venue.getDoor_ID().length() > 0)
            b_f_d += "_" + venue.getDoor_ID();

        if (b_f_d.length() > 0)
            viewHolder.poi_detailed_building_floor_door_text_view.setText(b_f_d);
        else
            viewHolder.poi_detailed_building_floor_door_text_view.setVisibility(View.INVISIBLE);*/

        viewHolder.venue_id_text_view.setText(venue.getBuilding_Number()+"_"+venue.getFloor_Number()+"_"+venue.getDoor_ID());
        viewHolder.venue_type_text_view.setText(venue.getType());

        return result;
    }
}
