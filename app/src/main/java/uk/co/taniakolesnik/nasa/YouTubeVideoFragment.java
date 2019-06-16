package uk.co.taniakolesnik.nasa;

import android.os.Bundle;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import org.apache.commons.lang3.StringUtils;

public class YouTubeVideoFragment extends YouTubePlayerSupportFragment {



    public YouTubeVideoFragment() {
    }

    public static YouTubeVideoFragment newInstance(String url) {

        YouTubeVideoFragment fragment = new YouTubeVideoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        fragment.setArguments(bundle);
        fragment.init();
        return fragment;
    }

    private void init() {

        initialize(BuildConfig.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    assert getArguments() != null;
                    String url = getArguments().getString("url");
                    String pre = "https://www.youtube.com/embed/";
                    String post = "?rel=0";
                    String code = StringUtils.substringBetween(url, pre, post);
                    player.loadVideo(code);

                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
            }
        });
    }

}
