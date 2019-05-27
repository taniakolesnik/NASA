package uk.co.taniakolesnik.nasa;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

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

public class MainActivity extends YouTubeBaseActivity {

    private static final String TAG = "Sunday";
    @BindView(R.id.imageView)
    ImageView imageView;

    @BindView(R.id.image_info_textView)
    TextView textView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.view)
    YouTubePlayerView playerView;

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
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.putExtra("date", getPreviousDate(date));
                startActivity(intent);
                finish();
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
                    String mediaType = response.body().getMedia_type();
                    String srcUlr;
                    String imageInfo = getString(R.string.image_info, response.body().getTitle(), response.body().getCopyright());
                    switch (mediaType) {
                        case "video":
                            Log.d(TAG, "makeCall: video");
                            srcUlr = response.body().getUrl();
                            setVideo(srcUlr, imageInfo);
                            break;
                        case "image":
                            Log.d(TAG, "makeCall: image");
                            srcUlr = response.body().getHdurl();
                            setImage(srcUlr, imageInfo);
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

    private void setVideo(final String srcUlr, String info) {
        playerView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        YouTubePlayer.OnInitializedListener onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                String pre = "https://www.youtube.com/embed/";
                String post = "?rel=0";
                String code = StringUtils.substringBetween(srcUlr, pre, post);
                Log.d(TAG, "onInitializationSuccess: " + code);
                youTubePlayer.loadVideo(code);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d(TAG, "onInitializationFailure : Failed");
            }
        };
        textView.setText(info);
        playerView.initialize(BuildConfig.YOUTUBE_API_KEY, onInitializedListener);

    }


    private void setImage(String imageUrl, String info) {
        playerView.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
        Picasso.get()
                .load(imageUrl)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .placeholder(new ColorDrawable(getColor(R.color.colorPrimary)))
                .error(R.drawable.placehoulder)
                .fit()
                .centerInside()
                .into(imageView);
        progressBar.setVisibility(View.GONE);
        textView.setText(info);
    }

    private String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    private String getPreviousDate(String current) {
        LocalDate currentDate = LocalDate.parse(current);
        LocalDate previousDate = currentDate.minusDays(1);
        String previous = previousDate.toString();
        return previous;
    }

    private String getNextDate(String current) {
        if (getCurrentDate().equals(current)){
            return current;
        }
        LocalDate currentDate = LocalDate.parse(current);
        LocalDate previousDate = currentDate.plusDays(1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String next = format.format(previousDate);
        return next;
    }

}
