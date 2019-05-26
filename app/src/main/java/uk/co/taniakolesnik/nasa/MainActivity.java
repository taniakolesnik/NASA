package uk.co.taniakolesnik.nasa;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.palette.graphics.Palette;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
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

    @BindView(R.id.view)
    YouTubePlayerView playerView;

    private YouTubePlayer.OnInitializedListener onInitializedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getApod();
    }


    private void getApod() {
        makeCall(BuildConfig.API_KEY, getCurrentDate(), true);
    }

    private void getApod(String date) {
        makeCall(BuildConfig.API_KEY, date, true);
    }

    private void getApod(String date, Boolean isHd) {
        makeCall(BuildConfig.API_KEY, date, isHd);
    }

    private void getApod(Boolean isHd) {
        makeCall(BuildConfig.API_KEY, getCurrentDate(), isHd);
    }

    private void makeCall(String api_key, String date, boolean isHD) {
        Client client = ServiceGenerator.createService(Client.class);
        Call<Result> call = client.getApod(api_key, date, isHD);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.body() != null) {
                    String mediaType = response.body().getMedia_type();
                    String srcUlr;
                    switch (mediaType) {
                        case "video":
                            srcUlr = response.body().getUrl();
                            setVideo(srcUlr);
                            break;
                        case "image":
                            srcUlr = response.body().getHdurl();
                            setImage(srcUlr);
                            break;
                    }


                    String imageInfo = getString(R.string.image_info, response.body().getTitle(), response.body().getCopyright());
                    textView.setText(imageInfo);

                } else {
                    //TODO add default pic load if load from internet failed
                    Log.d(TAG, "onResponse: response.body() is NULL");
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.toString());
            }
        });
    }

    private void setVideo(final String srcUlr) {
        playerView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo("PBL1RBj-P1g");
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d(TAG, "onInitializationFailure : Failed");
            }
        };

        playerView.initialize(BuildConfig.YOUTUBE_API_KEY, onInitializedListener);

    }



    private void setImage(String imageUrl) {
        playerView.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
        Picasso.get()
                .load(imageUrl)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        assert imageView != null;
                        imageView.setImageBitmap(bitmap);
                        Palette.from(bitmap)
                                .generate(new Palette.PaletteAsyncListener() {
                                    @Override
                                    public void onGenerated(Palette palette) {
                                        Palette.Swatch darkSwatch = palette.getDarkVibrantSwatch();
                                        if (darkSwatch != null) {
//                                            Objects.requireNonNull(getActionBar())
//                                                    .setBackgroundDrawable(new ColorDrawable(darkSwatch.getRgb()));
                                        }

                                        Palette.Swatch swatch = palette.getDarkMutedSwatch();
                                        if (swatch != null) {
                                            imageView.setBackgroundColor(swatch.getRgb());
                                            getWindow().setStatusBarColor(swatch.getRgb());
                                            textView.setTextColor(swatch.getBodyTextColor());
                                        }


                                    }
                                });
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        Toast.makeText(getApplicationContext(), "onBitmapFailed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }

    private String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = new Date(System.currentTimeMillis());
        String currentDate = formatter.format(date);
        return currentDate;
    }

}
