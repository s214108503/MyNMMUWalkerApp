package walker.pack.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Filter;

import java.util.ArrayList;

import walker.pack.HomeActivity;
import walker.pack.R;
import walker.pack.classes.POI;

/**
 * Created by Olebogeng Malope on 7/22/2017.
 */

public class POIAdapter extends ArrayAdapter<POI> implements Filterable{

    Context context;
    private ArrayList<POI> poiArrayList, clonedList;
    private Filter typeFilter;
    private boolean isFavourite;

    private class ViewHolder {
        TextView poi_type_text_view, poi_description_text_view;
        ImageView poi_favourite_image_view;
    }

    public POIAdapter(ArrayList<POI> data, Context context) {
        super(context, R.layout.venue_row_layout, data);
        this.context = context;
        this.poiArrayList = data;
        clonedList = new ArrayList<>();
        clonedList.addAll(data);

        typeFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                assert constraint != null;
                FilterResults filterResults = new FilterResults();
                ArrayList<POI> temp = new ArrayList<>();
                if (clonedList.size() > poiArrayList.size())
                {
                    poiArrayList.clear();
                    poiArrayList.addAll(clonedList);
                }
                boolean constraint_found = false;
                for (int x = 0; x < poiArrayList.size(); x++){
                    POI cur = getItem(x);
                    assert cur != null;
                    int index_constraint = cur.getType().toLowerCase().indexOf(constraint.toString().toLowerCase());
                    if (index_constraint != -1)
                    {
                        temp.add(cur);
                        constraint_found = true;
                    }
                    if (x == poiArrayList.size()-1)
                        if(!constraint_found)
                            poiArrayList.clear();
                }
                filterResults.count = temp.size();
                filterResults.values = temp;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                assert filterResults != null;
                if (filterResults.count >= 0){
                    try{
                        setPoiArrayList((ArrayList<POI>) filterResults.values);
                        notifyDataSetChanged();
                    } catch (ClassCastException e){
                        e.printStackTrace();
                        notifyDataSetInvalidated();
                    }
                } else
                    notifyDataSetInvalidated();

            }
        };
    }

    public void ShowFavouritesOnly(boolean showFavourites){
        if (showFavourites){
            if (poiArrayList != null){
                if (clonedList.size() >= poiArrayList.size()) {
                    poiArrayList.clear();
                    poiArrayList.addAll(clonedList);
                }
                ArrayList<POI> temp = new ArrayList<>();
                ArrayList<String> poi_ids = HomeActivity.db.getFavPOIIDs();
                for (int i = 0; i < poiArrayList.size(); i++) {
                    POI cur = poiArrayList.get(i);
                    if (!poi_ids.contains(cur.getPOI_ID()))
                        temp.add(cur);
                }
                setPoiArrayList(temp);
                notifyDataSetChanged();
            }
        } else {
            setPoiArrayList(clonedList);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // get the data item for this position
        final POI poi = getItem(position);
        final ViewHolder viewHolder;
        View result;

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.poi_row_layout, parent, false);
            viewHolder.poi_type_text_view = (TextView) convertView.findViewById(R.id.poi_type_text_view);
            viewHolder.poi_description_text_view = (TextView) convertView.findViewById(R.id.poi_description_text_view);
            viewHolder.poi_favourite_image_view = (ImageView) convertView.findViewById(R.id.poi_favourite_image_view);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        assert poi != null;
        viewHolder.poi_type_text_view.setText(poi.getType());
        viewHolder.poi_description_text_view.setText(poi.getDescription());
        viewHolder.poi_favourite_image_view.setTag(position);

        if (HomeActivity.db.getFavPOIIDs().contains(poi.getPOI_ID())){
            updateImageView(viewHolder, (isFavourite = true));
        } else {
            updateImageView(viewHolder, (isFavourite = false));
        }

        viewHolder.poi_favourite_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavourite){
                    // remove from list and change icon
                    HomeActivity.db.deleteFavPoi(poi);
                    setFavourite(false);
                } else {
                    HomeActivity.db.addFavPOI(poi);
                    setFavourite(true);
                }
                updateImageView(viewHolder, isFavourite);
            }
        });
        updateImageView(viewHolder, isFavourite);
        return result;
    }

    public Filter getFilter() {
        return typeFilter;
    }

    public void setPoiArrayList(ArrayList<POI> list){
        poiArrayList.clear();
        poiArrayList.addAll(list);
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    private void updateImageView(ViewHolder viewholder, boolean fav){
        if (fav)
            viewholder.poi_favourite_image_view.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        else
            viewholder.poi_favourite_image_view.setImageResource(R.drawable.ic_favorite_black_24dp);
    }
}
