package walker.pack;

import android.app.SearchManager;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import walker.pack.adapters.SectionsPageAdapter;
import walker.pack.classes.Staff;
import walker.pack.fragments.Tab1Fragment;
import walker.pack.fragments.Tab2Fragment;
import walker.pack.fragments.Tab3Fragment;

public class DirectoryActivity extends AppCompatActivity implements
        Tab1Fragment.OnLocationSelectedListener,
        Tab2Fragment.OnVenueSetInterface,
        Tab3Fragment.OnPOILocationSetInterface,
        SearchView.OnQueryTextListener{

    private static final String TAG = "ThreeTabActivity";

    private SectionsPageAdapter adapter;

    private ViewPager view_pager;

    public static SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory);

        // set up the view pager with sections adapter
        view_pager = (ViewPager) findViewById(R.id.container);
        setupViewPager(view_pager);

        // tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(view_pager);

        /*// Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchData(query);
        }*/
        //searchView.setOnQueryTextListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchData(query);
        }
    }

    private void searchData(String query){
            if (adapter.getCount() > 0) {
                Toast.makeText(this, "new text: " + query, Toast.LENGTH_SHORT).show();
                switch (view_pager.getCurrentItem()) {
                    case 0:
                        // staff
                        Tab1Fragment staff_frag = (Tab1Fragment) adapter.getItem(view_pager.getCurrentItem());
                        staff_frag.adapter.getFilter().filter(query);
                        adapter.notifyDataSetChanged();
                        break;
                    default:
                        Toast.makeText(this, "new text:" + query, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.directory_menu, menu);

        // Retrieve the SearchView and plug it into SearchManager
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO 17/7/2017: handle action bar item clicks
        // handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.directory_qr_code_menu_item:
                Toast.makeText(this, "QR Code", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.directory_door_number_menu_item:
                startActivity(new Intent(getApplicationContext(), IndoorNumberScannerActivity.class));
                Toast.makeText(this, "Door number", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.directory_show_favourites_menu_item:
                Toast.makeText(this, "Show Favs", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager){
        adapter = new SectionsPageAdapter((getSupportFragmentManager()));
        adapter.addFragment(new Tab1Fragment(), "STAFF");
        adapter.addFragment(new Tab2Fragment(), "VENUE");
        adapter.addFragment(new Tab3Fragment(), "POI");

        viewPager.setAdapter(adapter);
    }

    private void sendDataToTripActMethod(String id, String data_model) {
        if (id != null) {
            Intent data = getIntent();
            int status = data.getIntExtra("request_code", 0);
            Intent intent = new Intent();
            intent.putExtra("data_model", data_model);

            if (status == TripSetupActivity.SET_CURRENT_LOCATION_REQUEST){
                intent.putExtra("current_location", id);
            } else if (status == TripSetupActivity.SET_DESTINATION_LOCATION_REQUEST) {
                intent.putExtra("destination_location", id);
            }
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    @Override
    public void staffLocationPasserMethod(String id) {
        sendDataToTripActMethod(id, "staff");
    }

    @Override
    public void venueLocationPasserMethod(String venue_id) {
        sendDataToTripActMethod(venue_id, "venue");
    }

    @Override
    public void poiLocationPasserMethod(String poi_id) {
        sendDataToTripActMethod(poi_id, "poi");
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchData(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searchData(newText);
        return true;
    }
}
