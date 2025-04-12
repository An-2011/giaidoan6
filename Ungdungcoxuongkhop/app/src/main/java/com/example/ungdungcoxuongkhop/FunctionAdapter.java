package com.example.ungdungcoxuongkhop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FunctionAdapter extends RecyclerView.Adapter<FunctionAdapter.FunctionViewHolder> {
    private Context context;
    private List<FunctionItem> functionList;
    private OnItemClickListener onItemClickListener;

    public FunctionAdapter(Context context, List<FunctionItem> functionList) {
        this.context = context;
        this.functionList = functionList;
    }

    @Override
    public FunctionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.function_item, parent, false);
        return new FunctionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FunctionViewHolder holder, int position) {
        FunctionItem item = functionList.get(position);
        holder.textView.setText(item.getTitle());
        holder.imageView.setImageResource(item.getIconResId());

        // Thêm hiệu ứng khi nhấn vào item
        holder.itemView.setOnClickListener(v -> {
            // Thêm hiệu ứng Alpha Animation
            AlphaAnimation animation = new AlphaAnimation(1.0f, 0.5f);
            animation.setDuration(200);
            holder.itemView.startAnimation(animation);

            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return functionList.size();
    }

    // Set listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // Interface để lắng nghe sự kiện click
    public interface OnItemClickListener {
        void onItemClick(FunctionItem item);
    }

    public class FunctionViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public FunctionViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.function_title);
            imageView = itemView.findViewById(R.id.function_icon);
        }
    }
}
