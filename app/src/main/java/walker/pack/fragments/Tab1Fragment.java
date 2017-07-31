package walker.pack.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import walker.pack.DirectoryActivity;
import walker.pack.HomeActivity;
import walker.pack.R;
import walker.pack.adapters.StaffAdapter;
import walker.pack.classes.PlanNode;
import walker.pack.classes.Staff;

/**
 * Created by Olebogeng Malope on 7/20/2017.
 */

public class Tab1Fragment extends Fragment{

    private static final String Tag = "Tab1Fragment";

    private ArrayList<Staff> staffList;
    private ListView listView;
    public  StaffAdapter adapter;

    public interface OnLocationSelectedListener {
        void staffLocationPasserMethod(String door_id);
    }

    OnLocationSelectedListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.staff_list_layout, container, false);

        listView = (ListView) view.findViewById(R.id.staff_list_view);

        staffList = new ArrayList<>();

        staffList = HomeActivity.db.getStaffMembers();

        adapter = new StaffAdapter(staffList, getContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Staff cur = adapter.getItem(position);

                if (cur != null) {
                    View v = LayoutInflater.from(getContext()).inflate(R.layout.staff_detailed_layout, null);

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder .setView(v)
                            .setCancelable(true);

                    ImageView staff_detailed_image_view = (ImageView) v.findViewById(R.id.staff_detailed_image_view);
                    TextView staff_detailed_full_name_text_view = (TextView) v.findViewById(R.id.staff_detailed_full_name_text_view);
                    TextView staff_detailed_position_text_view = (TextView) v.findViewById(R.id.staff_detailed_position_text_view);
                    TextView staff_detailed_department_text_view = (TextView) v.findViewById(R.id.staff_detailed_department_text_view);
                    TextView staff_detailed_campus_text_view = (TextView) v.findViewById(R.id.staff_detailed_campus_text_view);
                    TextView staff_detailed_office_number_text_view = (TextView) v.findViewById(R.id.staff_detailed_office_number_text_view);
                    TextView staff_detailed_telephone_number_text_view = (TextView) v.findViewById(R.id.staff_detailed_telephone_number_text_view);
                    TextView staff_detailed_email_address_text_view = (TextView) v.findViewById(R.id.staff_detailed_email_address_text_view);
                    ImageView staff_detailed_email_image_view = (ImageView) v.findViewById(R.id.staff_detailed_email_image_view);
                    ImageView staff_detailed_call_image_view = (ImageView) v.findViewById(R.id.staff_detailed_call_image_view);
                    Button btn_staff_detailed_set = (Button) v.findViewById(R.id.btn_staff_detailed_set);
                    Button btn_staff_detailed_cancel = (Button) v.findViewById(R.id.btn_staff_detailed_cancel);

                    Picasso.with(getContext()).load(cur.getImage_URL()).into(staff_detailed_image_view);
                    staff_detailed_full_name_text_view.setText(cur.getName() + " "+cur.getSurname());
                    staff_detailed_position_text_view.setText(cur.getPosition());
                    staff_detailed_department_text_view.setText(cur.getDepartment());
                    staff_detailed_campus_text_view.setText(cur.getCampus());
                    staff_detailed_office_number_text_view.setText(cur.getBuilding_Number() + "_" + cur.getFloor_Number() + "_" + cur.getDoor_ID());
                    staff_detailed_telephone_number_text_view.setText(cur.getPhone());
                    staff_detailed_email_address_text_view.setText(cur.getEmail());

                    staff_detailed_call_image_view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + cur.getPhone()));
                            startActivity(intent);
                        }
                    });

                    staff_detailed_email_image_view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                    "mailto",cur.getEmail(), null));
                            startActivity(Intent.createChooser(emailIntent, "Send email..."));
                        }
                    });

                    btn_staff_detailed_set.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.staffLocationPasserMethod(cur.getBuilding_Number() + "_" + cur.getFloor_Number() + "_" + cur.getDoor_ID());
                        }
                    });

                    btn_staff_detailed_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.staffLocationPasserMethod(null);
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
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
            listener = (OnLocationSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLocationSelectedListener");
        }
    }


}
