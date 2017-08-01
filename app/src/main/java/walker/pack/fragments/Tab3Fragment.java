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
import java.util.List;

import walker.pack.HomeActivity;
import walker.pack.R;
import walker.pack.adapters.POIAdapter;
import walker.pack.classes.POI;

/**
 * Created by Olebogeng Malope on 7/20/2017.
 */

public class Tab3Fragment extends Fragment {
    private static final String Tag = "POI Fragment";

    private ArrayList<POI> pois;
    private ListView listView;
    public POIAdapter adapter;

    public interface OnPOILocationSetInterface{
        void poiLocationPasserMethod(String poi_id);
    }

    OnPOILocationSetInterface listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.poi_list_layout, container, false);

        listView = (ListView) view.findViewById(R.id.poi_list_view);

        pois = HomeActivity.db.getPOIs();

        adapter = new POIAdapter(pois, getContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final POI cur = adapter.getItem(i);

                assert cur != null;

                View v = LayoutInflater.from(getContext()).inflate(R.layout.poi_detailed_layout, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true).setView(v);

                TextView poi_detailed_building_floor_door_text_view = (TextView) v.findViewById(R.id.poi_detailed_building_floor_door_text_view);
                TextView poi_detailed_qr_code_text_view = (TextView) v.findViewById(R.id.poi_detailed_qr_code_text_view);
                TextView poi_detailed_Type_text_view = (TextView) v.findViewById(R.id.poi_detailed_Type_text_view);
                TextView poi_detailed_description_text_view = (TextView) v.findViewById(R.id.poi_detailed_description_text_view);

                String b_f_d = "";
                if (cur.getBuilding_Number().length() > 0)
                    b_f_d += cur.getBuilding_Number();
                if (cur.getFloor_Level().length() > 0)
                    b_f_d += "_" + cur.getFloor_Level();
                if (cur.getDoor_ID().length() > 0)
                    b_f_d += "_" + cur.getDoor_ID();

                if (b_f_d.length() > 0)
                    poi_detailed_building_floor_door_text_view.setText(b_f_d);
                else
                    poi_detailed_building_floor_door_text_view.setVisibility(View.INVISIBLE);

                if (cur.getQR_ID().length() > 0)
                    poi_detailed_qr_code_text_view.setText(cur.getQR_ID());
                else
                    poi_detailed_qr_code_text_view.setVisibility(View.INVISIBLE);

                poi_detailed_Type_text_view.setText(cur.getType());
                poi_detailed_description_text_view.setText(cur.getDescription());

                Button btn_poi_detailed_set = (Button) v.findViewById(R.id.btn_poi_detailed_set);
                Button btn_poi_detailed_cancel = (Button) v.findViewById(R.id.btn_poi_detailed_cancel);

                final String finalB_f_d = b_f_d;
                btn_poi_detailed_set.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (finalB_f_d.length() > 0)
                            listener.poiLocationPasserMethod(finalB_f_d);
                        else
                            listener.poiLocationPasserMethod(cur.getQR_ID());
                    }
                });

                btn_poi_detailed_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.poiLocationPasserMethod(null);
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
            listener = (OnPOILocationSetInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPOILocationSetInterface");
        }
    }
}
