package uk.co.taniakolesnik.nasa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.taniakolesnik.nasa.module.Result;
import uk.co.taniakolesnik.nasa.retrofit.Client;
import uk.co.taniakolesnik.nasa.retrofit.ServiceGenerator;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Monday";

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.fragment_container)
    FrameLayout fragmentView;

    @BindView(R.id.image_info_textView)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        progressBar.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        String key = getString(R.string.intent_current_date_key);
        final String date = intent.getStringExtra(key) != null ? intent.getStringExtra(key) : getCurrentDate();

        getApod(date);
    }

    private String getPreviousDate(String current) {
        LocalDate currentDate = LocalDate.parse(current);
        LocalDate previousDate = currentDate.minusDays(1);
        String previous = previousDate.toString();
        return previous;
    }

    private String getNextDate(String current) {
        if (getCurrentDate().equals(current)) {
            return current;
        }
        LocalDate currentDate = LocalDate.parse(current);
        LocalDate previousDate = currentDate.plusDays(1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String next = format.format(previousDate);
        return next;
    }

    private String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    private void getApod(String date) {
        Log.d(TAG, "getApod: " + date);
        makeCall(date);
    }

    private void makeCall(String date) {
        Client client = ServiceGenerator.createService(Client.class);
        Call<Result> call = client.getApod(BuildConfig.API_KEY, "2019-05-25", true);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.body() != null) {
                    String mediaType = response.body().getMedia_type();
                    String imageInfo = getString(R.string.image_info, response.body().getTitle(), response.body().getCopyright());
                    textView.setText(imageInfo);
                    Log.d(TAG, "makeCall: imageInfo " + imageInfo);
                    switch (mediaType) {
                        case "video":
                            Log.d(TAG, "makeCall: video");
                            setVideo(response.body().getUrl());
                            break;
                        case "image":
                            Log.d(TAG, "makeCall: image");
                            setImage(response.body().getHdurl());
                            break;
                    }
                } else {
                    Log.d(TAG, "onResponse: response.body() is NULL");
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.toString());
            }
        });
    }

    private void setVideo(final String url) {
        progressBar.setVisibility(View.GONE);
        VideoFragment fragment = VideoFragment.newInstance(url);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

    }

    private void setImage(String url) {
        progressBar.setVisibility(View.GONE);
        ImageFragment fragment = new ImageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
