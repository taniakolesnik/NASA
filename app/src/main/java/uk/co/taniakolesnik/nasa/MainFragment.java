package uk.co.taniakolesnik.nasa;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class MainFragment extends Fragment {

    ImageView imageView;
    TextView textView;
    ProgressBar progressBar;
    FrameLayout videoView;

    private static final String TAG = "Saturday";

    public MainFragment() {
    }

    public static MainFragment newInstance(String url, String info, String type) {
        MainFragment fragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("info", info);
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        imageView = view.findViewById(R.id.image_view);
        textView = view.findViewById(R.id.image_info_textView);
        progressBar = view.findViewById(R.id.progressBar);
        videoView = view.findViewById(R.id.video_container);

        progressBar.setVisibility(View.GONE);
        String url = getArguments() != null ? getArguments().getString("url") : null;
        String info = getArguments() != null ? getArguments().getString("info") : null;
        String type = getArguments() != null ? getArguments().getString("type") : null;

        if (type != null) {
            if (type.equals("image")){
                setImage(url);
            } else if (url != null) {
                if (url.contains("youtube")) {
                    setYouTubeVideo(url);
                } else {
                    setImage("https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png");
                }
            }
        }

        setInfo(info);
    }


    private void setInfo(String info) {
        textView.setText(info);
    }

    private void setImage(String url) {
        imageView.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.GONE);

        Drawable placehoulder = new ColorDrawable(getActivity().getColor(R.color.colorPrimary));

        Glide.with(getActivity())
                .load(url)
                .centerCrop()
                .placeholder(placehoulder)
                .crossFade()
                .into(imageView);

    }

    private void setYouTubeVideo(final String url) {
        imageView.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);
        YouTubeVideoFragment fragment = YouTubeVideoFragment.newInstance(url);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.video_container, fragment)
                .commit();

    }

    private void setVideo(String url) {
    }


}
