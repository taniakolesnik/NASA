package uk.co.taniakolesnik.nasa;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.HashMap;

import uk.co.taniakolesnik.nasa.module.Result;

public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private HashMap<Integer, Result> results;

    public MyFragmentPagerAdapter(FragmentManager fragmentManager, HashMap<Integer, Result> results) {
        super(fragmentManager);
        this.results = results;
    }

    @Override
    public Fragment getItem(int i) {
        Result result = results.get(i);
        String url = result != null ? result.getUrl() : null;
        String info = result != null ? result.getDate() : null;
        String type = result != null ? result.getMedia_type() : null;
        return MainFragment.newInstance(url, info, type);
    }

    @Override
    public int getCount() {
        return MainActivity.LOAD_DAYS_NUMBER;
    }

}
