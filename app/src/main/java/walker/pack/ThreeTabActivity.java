package walker.pack;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import walker.pack.adapters.SectionsPageAdapter;
import walker.pack.fragments.Tab1Fragment;
import walker.pack.fragments.Tab2Fragment;
import walker.pack.fragments.Tab3Fragment;

public class ThreeTabActivity extends AppCompatActivity {

    private static final String TAG = "ThreeTabActivity";

    private SectionsPageAdapter sections_page_adapter;

    private ViewPager view_pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory);
        Log.d(TAG, "onCreate: Starting");

        sections_page_adapter = new SectionsPageAdapter(getSupportFragmentManager());

        // set up the view pager with sections adapter
        view_pager = (ViewPager) findViewById(R.id.container);
        setupViewPager(view_pager);

        // tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(view_pager);

    }

    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter((getSupportFragmentManager()));
        adapter.addFragment(new Tab1Fragment(), "TAB1");
        adapter.addFragment(new Tab2Fragment(), "TAB2");
        adapter.addFragment(new Tab3Fragment(), "TAB3");

        viewPager.setAdapter(adapter);
    }
}
