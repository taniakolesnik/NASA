package uk.co.taniakolesnik.nasa;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.taniakolesnik.nasa.module.Result;

public class ListRecyclerViewAdapter extends RecyclerView.Adapter<ListRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "Sunday";

    private Context context;
    private HashMap<Integer, Result> results;

    ListRecyclerViewAdapter(Context context, HashMap<Integer, Result> results) {
        this.context = context;
        this.results = results;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Result result = results.get(i);
        final int position = i;
        String imageUrl;
        try {
            imageUrl = result != null ? result.getHdurl() : null;
        } catch (NullPointerException e) {
            imageUrl = "https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png";
        }


        Drawable placeholder = new ColorDrawable(context.getResources().getColor(R.color.colorPrimaryDark));
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(placeholder)
                    .error(R.drawable.ic_launcher_background)
                    .resize(0, 200)
                    .centerCrop()
                    .into(viewHolder.imageListView);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("position", position);
                Log.d(TAG, "onClick: position is " + position);
                intent.putExtra("results", results);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    void update(HashMap<Integer, Result> newResults) {
        results = newResults;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return MainActivity.LOAD_DAYS_NUMBER;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_list_view)
        ImageView imageListView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

    }
}
