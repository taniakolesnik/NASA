package uk.co.taniakolesnik.nasa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

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

    @BindView(R.id.fragment_container)
    FrameLayout fragmentView;

    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String key = getString(R.string.intent_current_date_key);
        date = intent.getStringExtra(key) != null ? intent.getStringExtra(key) : getCurrentDate();

        getApod(date);

        fragmentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date=getPreviousDate(date);
                getApod(date);
            }
        });
    }

    private void getApod(String date) {
        Log.d(TAG, "getApod: " + date);
        makeCall(date);
    }

    private void makeCall(String date) {
        Client client = ServiceGenerator.createService(Client.class);
        Call<Result> call = client.getApod(BuildConfig.API_KEY, date, true);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.body() != null) {
                    Result result = response.body();
                    String mediaType = result.getMedia_type();
                    String info = getString(R.string.image_info, result.getTitle(), result.getCopyright());
                    String url = mediaType.equals("image") ? result.getHdurl() : result.getUrl();
                    setFragment(url, info, mediaType);
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

    private void setFragment(String url, String info, String mediaType) {
        MainFragment fragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("info", info);
        bundle.putString("type", mediaType);
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    private String getPreviousDate(String current) {

        LocalDate currentDate = LocalDate.parse(current);
        LocalDate previousDate = currentDate.minusDays(1);
        String string = previousDate.toString();
        return string;
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
}
