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

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainFragment extends Fragment {

    private static final String TAG = "Wednesday MainFragment";

    @BindView(R.id.image_view)
    ImageView imageView;

    @BindView(R.id.image_info_textView)
    TextView textView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.video_container)
    FrameLayout videoView;

    public MainFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        progressBar.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);

        String url = getArguments().getString("url");
        String info = getArguments().getString("info");
        String type = getArguments().getString("type");

        Log.d(TAG, "onViewCreated: url is " + url + "; info is " + info + "; type is " + type);

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
        Log.d(TAG, "setImage: started");
        imageView.setVisibility(View.VISIBLE);
        Picasso.get()
                .load(url)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .placeholder(new ColorDrawable(getActivity().getColor(R.color.colorPrimary)))
                .error(R.drawable.placehoulder)
                .fit()
                .centerInside()
                .into(imageView);
    }

    private void setVideo(final String url) {
        Log.d(TAG, "setVideo: started");
        videoView.setVisibility(View.VISIBLE);
        VideoFragment fragment = VideoFragment.newInstance(url);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.video_container, fragment)
                .commit();

    }

}
