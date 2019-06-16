package uk.co.taniakolesnik.nasa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.taniakolesnik.nasa.module.Result;

public class DetailsActivity extends AppCompatActivity {

    @BindView(R.id.view_pager)
    ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        HashMap<Integer, Result> results = (HashMap<Integer, Result>) intent.getSerializableExtra("results");
        int position = intent.getIntExtra("position", 0);
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), results);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
    }
}
