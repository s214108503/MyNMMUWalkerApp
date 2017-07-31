package walker.pack.adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Olebogeng Malope on 7/20/2017.
 */

public class SectionsPageAdapter extends FragmentPagerAdapter {

    private final ArrayList<Fragment> fragments = new ArrayList<>();
    private final ArrayList<String> fragment_titles = new ArrayList<>();

    public  SectionsPageAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public void addFragment(Fragment fragment, String title){
        fragments.add(fragment);
        fragment_titles.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragment_titles.get(position);
    }
}
