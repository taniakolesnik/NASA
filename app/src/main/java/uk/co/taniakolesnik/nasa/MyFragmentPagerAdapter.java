package uk.co.taniakolesnik.nasa;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.HashMap;

import uk.co.taniakolesnik.nasa.module.Result;

public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "Saturday";
    private int numberOfTabs;
    private HashMap<Integer, Result> results;
    private StatusCallback callback;

    public MyFragmentPagerAdapter(FragmentManager fragmentManager, HashMap<Integer, Result> results, StatusCallback callback) {
        super(fragmentManager);
        this.numberOfTabs = results.size();
        this.results = results;
        this.callback = callback;
        Log.d(TAG, "MyFragmentPagerAdapter: adapter created" + results.size());
    }

    void update(HashMap<Integer, Result> newResults){
        results = newResults;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int i) {
        Result result = results.get(i);
        String url =  result.getUrl();
        String info =  result.getDate();
        String type =  result.getMedia_type();
        callback.onPosition(i);
        return MainFragment.newInstance(url, info, type);
    }

    @Override
    public int getCount() {
        Log.d(TAG, "getCount: " + numberOfTabs);
        return 7;
    }

}
