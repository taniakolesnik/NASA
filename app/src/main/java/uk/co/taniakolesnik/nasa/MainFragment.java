package uk.co.taniakolesnik.nasa;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
        String url = getArguments().getString("url");
        String info = getArguments().getString("info");
        String type = getArguments().getString("type");

        if (type.equals("image")){
            setImage(url);
        } else {
            setVideo(url);
        }

        setInfo(info);
    }

    private void setInfo(String info) {
        textView.setText(info);
    }

    private void setImage(String url) {
        Log.d(TAG, "setImage: " + url);
        imageView.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.GONE);
        Picasso.get()
                .load(url)
              //  .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .placeholder(new ColorDrawable(getActivity().getColor(R.color.colorPrimary)))
                .error(R.drawable.placehoulder)
                .fit()
                .centerInside()
                .into(imageView);
    }

    private void setVideo(final String url) {
        imageView.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);
        VideoFragment fragment = VideoFragment.newInstance(url);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.video_container, fragment)
                .commit();

    }

}
