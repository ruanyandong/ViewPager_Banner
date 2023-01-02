package com.ryd.banner.viewpager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ryd.banner.R;

import java.util.List;

/**
 * @author : ruanyandong
 * @e-mail : ruanyandong@didiglobal.com
 * @date : 1/2/23 8:29 PM
 * @desc : com.ryd.banner.viewpager2
 */
public class HorizontalViewPager2Adapter extends RecyclerView.Adapter<HorizontalViewPager2Adapter.HorizontalViewHolder>  {


    private List<ViewModel> viewModels;

    public HorizontalViewPager2Adapter(List<ViewModel> models){
        this.viewModels = models;
    }

    @NonNull
    @Override
    public HorizontalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_pager2_item_layout,parent,false);
        return new HorizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalViewHolder holder, int position) {
        ViewModel viewModel = viewModels.get(position);
        holder.textView.setText(viewModel.getText());
        holder.imageView.setImageResource(viewModel.getImage());
    }

    @Override
    public int getItemCount() {
        return viewModels.size();
    }

    static class HorizontalViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        ImageView imageView;

        public HorizontalViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text);
            imageView = itemView.findViewById(R.id.image);
        }
    }


}
