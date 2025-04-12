package com.example.ungdungcoxuongkhop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FutureFeatureAdapter extends RecyclerView.Adapter<FutureFeatureAdapter.ViewHolder> {
    private List<FutureFeature> featureList;

    public FutureFeatureAdapter(List<FutureFeature> featureList) {
        this.featureList = featureList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_future_feature, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FutureFeature feature = featureList.get(position);
        holder.titleText.setText(feature.getTitle());
        holder.descriptionText.setText(feature.getDescription());
    }

    @Override
    public int getItemCount() {
        return featureList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, descriptionText;

        public ViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
        }
    }
}
