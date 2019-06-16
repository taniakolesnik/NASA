package uk.co.taniakolesnik.nasa;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
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

    private static final String TAG = "Thursday";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private String endDate = getCurrentDate();
    //private int endPosition = 0;

    public static final int LOAD_DAYS_NUMBER = 100;

    private HashMap<Integer, Result> results = new HashMap<>();
    private ListRecyclerViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        loadResultsForLoadNumberDays();

    }

    private void loadResultsForLoadNumberDays() {
        for (int i = 0; i < LOAD_DAYS_NUMBER; i++) {
            makeCall(endDate, i, new GetResultCallback() {
                @Override
                public void onGetData(Result result, int position) {
                    results.put(position, result);
                        if (adapter == null) {
                            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),
                                    calculateNumbeOfColumns(getApplicationContext())));
                            adapter = new ListRecyclerViewAdapter(getApplicationContext(), results);
                            recyclerView.setAdapter(adapter);
                        }
                        adapter.update(results);
                }
            });
            endDate = getPreviousDate(endDate);
        }
    }

    private void makeCall(final String date, final int position, final GetResultCallback getResultCallback) {
        Client client = ServiceGenerator.createService(Client.class);
        Call<Result> call = client.getApod(BuildConfig.API_KEY, date, true);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.body() != null) {
                    Result result = response.body();
                    getResultCallback.onGetData(result, position);
                } else {
                    Log.d(TAG, "makeCall onResponse: response.body() is NULL");
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

    //https://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns
    public static int calculateNumbeOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float widthDp = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (widthDp / 180);
        return noOfColumns;
    }

}
