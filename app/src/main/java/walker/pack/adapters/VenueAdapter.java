package walker.pack.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import walker.pack.R;
import walker.pack.classes.Venue;

/**
 * Created by Olebogeng Malope on 7/22/2017.
 */

public class VenueAdapter extends ArrayAdapter<Venue> implements Filterable {

    private  ArrayList<Venue> venueArrayList, clonedList;
    private Context context;
    private Filter venueNumberFilter;

    public VenueAdapter(ArrayList<Venue> data, Context context){
        super(context, R.layout.venue_row_layout, data);
        this.context = context;
        this.venueArrayList = data;
        clonedList = new ArrayList<>();
        clonedList.addAll(data);

        venueNumberFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filter_results = new FilterResults();
                ArrayList<Venue> temp = new ArrayList<>();
                if (constraint != null && venueArrayList != null){
                    if (clonedList.size() >= venueArrayList.size()){
                        venueArrayList.clear();
                        venueArrayList.addAll(clonedList);
                    }
                    boolean constraint_found = false;
                    for (int x = 0; x < venueArrayList.size(); x++){
                        Venue v = getItem(x);
                        assert v != null;
                        int index_constraint = v.getBuildingFloorDoorID().indexOf(constraint.toString());
                        if (index_constraint != -1) {
                            temp.add(v);
                            constraint_found = true;
                        }
                        if (x == venueArrayList.size()-1)
                            if(!constraint_found)
                                venueArrayList.clear();
                    }
                    filter_results.count = temp.size();
                    filter_results.values = temp;
                }
                return filter_results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                assert filterResults != null;
                if (filterResults.count > 0) {
                    try {
                        setVenueList((ArrayList<Venue>) filterResults.values);
                        notifyDataSetChanged();
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                    }
                } else
                    notifyDataSetInvalidated();
            }
        };
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
            viewHolder.venue_id_text_view = (TextView) convertView.findViewById(R.id.venue_id_text_view);
            viewHolder.venue_type_text_view = (TextView) convertView.findViewById(R.id.venue_type_text_view);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        assert venue != null;
        viewHolder.venue_id_text_view.setText(venue.getBuilding_Number()+"_"+venue.getFloor_Number()+"_"+venue.getDoor_ID());
        viewHolder.venue_type_text_view.setText(venue.getType());

        return result;
    }

    public void setVenueList(ArrayList<Venue> list){
        venueArrayList.clear();
        venueArrayList.addAll(list);
    }

    public Filter getFilter(){
        return venueNumberFilter;
    }
}
