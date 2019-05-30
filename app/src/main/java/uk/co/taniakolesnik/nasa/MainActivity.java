package uk.co.taniakolesnik.nasa;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.taniakolesnik.nasa.module.Result;
import uk.co.taniakolesnik.nasa.retrofit.Client;
import uk.co.taniakolesnik.nasa.retrofit.GetResultCallback;
import uk.co.taniakolesnik.nasa.retrofit.ServiceGenerator;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Friday";

    private static final int LOAD_DAYS_NUMBER=5;
    private int checkSum;

    private HashMap<String, Result> results = new HashMap<>();
    private String endDate = getCurrentDate();
    private String startDate = getCurrentDate();

    @BindView(R.id.fragment_container)
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        loadResultsForLoadNumberDays();

    }

    private void loadResultsForLoadNumberDays() {
        startDate =  endDate;
        checkSum += LOAD_DAYS_NUMBER;
        for (int i = 0; i < LOAD_DAYS_NUMBER; i++) {
            makeCall(endDate, new GetResultCallback() {
                @Override
                public void onGetData(final Result result) {
                    results.put(result.getDate(), result);
                    if (results.size()==checkSum){
                        setFragment(results.get(startDate));
                        new SetOnClickListener().invoke();
                    }
                }
            });
            endDate = getPreviousDate(endDate);
        }
    }

    private class SetOnClickListener {
        void invoke() {
            frameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startDate=getPreviousDate(startDate);
                    if (startDate.equals(endDate)) {
                        Log.d(TAG, "onClick: checkSum is " + checkSum);
                        loadResultsForLoadNumberDays();
                    } else {
                        Result result = results.get(startDate);
                        setFragment(result);
                        Log.d(TAG, "onClick: setFragment for " + result.getDate());
                    }
                }
            });
        }
    }

    private void makeCall(final String date, final GetResultCallback getResultCallback) {
        Client client = ServiceGenerator.createService(Client.class);
        Call<Result> call = client.getApod(BuildConfig.API_KEY, date, true);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.body() != null) {
                    Log.d(TAG, "onResponse: response.body() is not NULL " + date);
                    Result result = response.body();
                    getResultCallback.onGetData(result);

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

    private void setFragment(Result result) {
        Log.d(TAG, "setFragment: started for " + result.getDate());
        MainFragment fragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", result.getUrl());
        bundle.putString("info", result.getTitle());
        bundle.putString("type", result.getMedia_type());
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
}
