package uk.co.taniakolesnik.nasa;

import android.os.Bundle;
import android.util.Log;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import org.apache.commons.lang3.StringUtils;

public class VideoFragment extends YouTubePlayerSupportFragment {

    private static final String TAG = "Monday";

    public VideoFragment() {
    }

    public static VideoFragment newInstance(String url) {

        VideoFragment fragment = new VideoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        Log.d(TAG, "onInitializationSuccess: " + url);
        fragment.setArguments(bundle);
        fragment.init();
        return fragment;
    }

    private void init() {

        initialize(BuildConfig.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    String url = getArguments().getString("url");
                    String pre = "https://www.youtube.com/embed/";
                    String post = "?rel=0";
                    String code = StringUtils.substringBetween(url, pre, post);
                    Log.d(TAG, "onInitializationSuccess: " + code);
                    player.loadVideo(code);

                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d(TAG, "onInitializationFailure : Failed");
            }
        });
    }

}
