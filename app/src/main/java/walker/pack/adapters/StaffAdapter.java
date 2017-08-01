package walker.pack.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import walker.pack.R;
import walker.pack.classes.Staff;

/**
 * Created by Olebogeng Malope on 7/17/2017.
 */

public class StaffAdapter extends ArrayAdapter<Staff> implements Filterable {

    private ArrayList<Staff> staffArrayList, clonedStaffList;
    Context context;

    private Filter staffSurnameFilter;

    // view lookup cache
    private static class ViewHolder{
        ImageView staff_image_view, staff_favourite_image_view;
        TextView staff_full_name_text_view,
                staff_department_text_view,
                staff_office_number_text_view;
    }

    public StaffAdapter(ArrayList<Staff> data, Context context){
        super(context, R.layout.staff_row_layout, data);
        this.context = context;
        this.staffArrayList = data;
        clonedStaffList = new ArrayList<>();
        clonedStaffList.addAll(data);

        staffSurnameFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filter_results = new FilterResults();
                    ArrayList<Staff> temp_list = new ArrayList<>();
                    if (constraint != null && staffArrayList != null) {
                        // Update list as user clicks backspace
                        if (clonedStaffList.size() >= staffArrayList.size()) {
                            staffArrayList.clear();
                            staffArrayList.addAll(clonedStaffList);
                        }

                        for (int x = 0; x < staffArrayList.size(); x++) {
                            Staff cur = getItem(x);
                            assert cur != null;
                            int constraint_index = cur.getSurname().toLowerCase().indexOf(String.valueOf(constraint).toLowerCase());
                            if (constraint_index != -1)
                                temp_list.add(cur);
                            else
                                staffArrayList.clear();
                        }
                        filter_results.values = temp_list;
                        filter_results.count = temp_list.size();
                    }
                return filter_results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                assert filterResults != null;
                if (filterResults.count > 0) {
                    try {
                        setStaffArrayList((ArrayList<Staff>) filterResults.values);
                        notifyDataSetChanged();
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                    }
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get the data item for this position
        Staff staff_member = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.staff_row_layout, parent, false);
            viewHolder.staff_full_name_text_view = (TextView) convertView.findViewById(R.id.staff_full_name_text_view);
            viewHolder.staff_department_text_view = (TextView) convertView.findViewById(R.id.staff_department_text_view);
            viewHolder.staff_office_number_text_view = (TextView) convertView.findViewById(R.id.staff_office_number_text_view);
            viewHolder.staff_favourite_image_view = (ImageView) convertView.findViewById(R.id.staff_favourite_image_view);
            viewHolder.staff_image_view = (ImageView) convertView.findViewById(R.id.staff_image_view);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        lastPosition = position;

        assert staff_member != null;
        viewHolder.staff_full_name_text_view .setText(staff_member.getName().concat(" ").concat(staff_member.getSurname()));
        viewHolder.staff_department_text_view.setText(staff_member.getDepartment());
        viewHolder.staff_office_number_text_view.setText(staff_member.getBuilding_Number() + "_" + staff_member.getFloor_Number() + "_" + staff_member.getDoor_ID());
        Picasso.with(getContext()).load(staff_member.getImage_URL()).into(viewHolder.staff_image_view);

        //viewHolder.staff_favourite_image_view.setTag(position);
        // TODO cater for favourites

        // Return the completed view to render on screen
        return result;
    }

    public int getItemCount(){
        return staffArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return staffSurnameFilter;
    }

    public void setStaffArrayList(ArrayList<Staff> staffArrayList) {
        this.staffArrayList.clear();
        this.staffArrayList.addAll(staffArrayList);
    }
}
