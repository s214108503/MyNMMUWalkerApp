package walker.pack.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import walker.pack.HomeActivity;
import walker.pack.R;
import walker.pack.adapters.VenueAdapter;
import walker.pack.classes.Venue;

/**
 * Created by Olebogeng Malope on 7/20/2017.
 */

public class Tab2Fragment extends Fragment {
    private static final String Tag = "Venue Fragment";

    private ArrayList<Venue> venues;
    private ListView listView;
    private VenueAdapter adapter;

    public interface OnVenueSetInterface{
        void venueLocationPasserMethod(String venue_id);
    }

    OnVenueSetInterface listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.venue_list_layout, container, false);

        listView = (ListView) view.findViewById(R.id.venue_list_view);

        venues = new ArrayList<>();

        venues = HomeActivity.db.getVenues();

        adapter = new VenueAdapter(venues, getContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Venue cur = adapter.getItem(i);

                assert cur != null;

                View v = LayoutInflater.from(getContext()).inflate(R.layout.venue_detailed_layout, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true).setView(v);

                TextView venue_detailed_id_text_view = (TextView) v.findViewById(R.id.venue_detailed_id_text_view);
                TextView venue_detailed_type_text_view = (TextView) v.findViewById(R.id.venue_detailed_type_text_view);
                TextView venue_detailed_alternative_door_text_view = (TextView) v.findViewById(R.id.venue_detailed_alternative_door_text_view);

                venue_detailed_id_text_view.setText(cur.getBuilding_Number()+"_"+cur.getFloor_Number()+"_"+cur.getDoor_ID());
                venue_detailed_type_text_view.setText(cur.getType());
                String alt_doors = "";

                for (String s: cur.getAlternative_Doors())
                    alt_doors += s + "\n";

                venue_detailed_alternative_door_text_view.setText(alt_doors);

                Button btn_venue_detailed_set = (Button) v.findViewById(R.id.btn_venue_detailed_set);
                Button btn_venue_detailed_cancel = (Button) v.findViewById(R.id.btn_venue_detailed_cancel);

                btn_venue_detailed_set.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.venueLocationPasserMethod(cur.getBuilding_Number() + "_" + cur.getFloor_Number() + "_" + cur.getDoor_ID());
                    }
                });

                btn_venue_detailed_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.venueLocationPasserMethod(null);
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            listener = (OnVenueSetInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnVenueSetInterface");
        }
    }
}
