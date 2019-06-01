package uk.co.taniakolesnik.nasa;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

    private static final String TAG = "Saturday";

    private static final int LOAD_DAYS_NUMBER=100;
    private int checkSum;

    private HashMap<Integer, Result> results = new HashMap<>();
    private String endDate = getCurrentDate();
    private String startDate = getCurrentDate();
    private int startPosition = 0;
    private int endPosition = 0;
    private MyFragmentPagerAdapter adapter;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        loadResultsForLoadNumberDays();

    }

    private void loadResultsForLoadNumberDays() {
        startDate = endDate;
        checkSum += LOAD_DAYS_NUMBER;
        for (int i = 0; i < LOAD_DAYS_NUMBER; i++) {
            makeCall(endDate, endPosition, new GetResultCallback() {
                @Override
                public void onGetData(Result result, int position) {
                    results.put(position, result);
                    Log.d(TAG, "onGetData: position " + position + "; date" + result.getDate());
                    if (results.size()==checkSum){
                        Log.d(TAG, "loadResultsForLoadNumberDays: \nstartPosition " + startPosition
                                + "; \nstartDate "  + startDate
                                + "; \nendPosition " + endPosition
                                + "; \nendDate " + endDate
                                + "; \nresults size is  " + results.size());
                        if (adapter==null){
                            adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), results, new StatusCallback() {
                                @Override
                                public void onPosition(int position) {
                                    Log.d(TAG, " StatusCallback onPosition: " + position);
                                    if (position == endPosition) {
                                        loadResultsForLoadNumberDays();
                                    }
                                    startPosition+=position;
                                }
                            });
                            viewPager.setAdapter(adapter);
                        }
                        adapter.update(results);
                    }
                }

            });
            endDate = getPreviousDate(endDate);
            endPosition++;
        }
    }

    private void makeCall(final String date, final int position, final GetResultCallback getResultCallback) {
        Client client = ServiceGenerator.createService(Client.class);
        Call<Result> call = client.getApod(BuildConfig.API_KEY, date, true);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.body() != null) {
                    Log.d(TAG, "onResponse: response.body() is not NULL " + date);
                    Result result = response.body();
                    getResultCallback.onGetData(result, position);

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
