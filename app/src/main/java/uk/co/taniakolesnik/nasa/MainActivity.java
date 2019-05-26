package uk.co.taniakolesnik.nasa;

import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.taniakolesnik.nasa.module.ApodResult;
import uk.co.taniakolesnik.nasa.retrofit.Client;
import uk.co.taniakolesnik.nasa.retrofit.ServiceGenerator;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Saturday";
    @BindView(R.id.main_image)
    ImageView mainImage;

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
        Call<ApodResult> call = client.getApod(api_key, date, isHD);
        call.enqueue(new Callback<ApodResult>() {
            @Override
            public void onResponse(Call<ApodResult> call, Response<ApodResult> response) {
                if (response.body() != null) {
                    String imageUrl = response.body().getHdurl();
                    Picasso.get()
                            .load(imageUrl)
                            .into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    assert mainImage != null;
                                    mainImage.setImageBitmap(bitmap);
                                    Palette.from(bitmap)
                                            .generate(new Palette.PaletteAsyncListener() {
                                                @Override
                                                public void onGenerated(Palette palette) {
                                                    Palette.Swatch darkSwatch = palette.getDarkVibrantSwatch();
                                                    if (darkSwatch != null) {
                                                        Objects.requireNonNull(getSupportActionBar())
                                                                .setBackgroundDrawable(new ColorDrawable(darkSwatch.getRgb()));
                                                    }

                                                    Palette.Swatch swatch = palette.getDarkMutedSwatch();
                                                    if (swatch != null) {
                                                        mainImage.setBackgroundColor(swatch.getRgb());
                                                        getWindow().setStatusBarColor(swatch.getRgb());
                                                    }


                                                }
                                            });
                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            });

                } else {
                    //TODO add default pic load if load from internet failed
                    Log.d(TAG, "onResponse: response.body() is NULL");
                }
            }

            @Override
            public void onFailure(Call<ApodResult> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.toString());
            }
        });
    }

    private String getCurrentDate() {
        return "2019-05-25";
    }

    public void setBackgroundColor(Bitmap bitmap) {
        Palette palette = createPaletteSync(bitmap);
        Palette.Swatch swatch = palette.getVibrantSwatch();
        if (swatch != null) {
            mainImage.setBackgroundColor(swatch.getRgb());
        }
    }

    public Palette createPaletteSync(Bitmap bitmap) {
        return Palette.from(bitmap).generate();
    }

}
