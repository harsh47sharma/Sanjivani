package com.collection.sanjivani;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NavigationItemAdapter extends RecyclerView.Adapter<NavigationItemAdapter.NavigationItemViewHolder> {

    private List<MedInfo> mNavItemArrayList;
    private Context context;
    public OnNavItemClickListener mOnNavItemClickListener;

    public interface OnNavItemClickListener{
        void onNavItemClick(int position);
    }

    public void setOnNavItemClickListener(OnNavItemClickListener onNavItemClickListener){
        mOnNavItemClickListener = onNavItemClickListener;
    }

    public static class NavigationItemViewHolder extends RecyclerView.ViewHolder{

        TextView navItemName, navItemPrice;

        public NavigationItemViewHolder(@NonNull View itemView, final OnNavItemClickListener onNavItemClickListener) {
            super(itemView);
            navItemName = itemView.findViewById(R.id.navPage_itemName);
            navItemPrice = itemView.findViewById(R.id.navPage_itemPrice);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onNavItemClickListener != null){
                        int position = getAdapterPosition();
                        onNavItemClickListener.onNavItemClick(position);
                    }
                }
            });
        }
    }

    public NavigationItemAdapter(List<MedInfo> mNavItemArrayList, Context context) {
        this.mNavItemArrayList = mNavItemArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public NavigationItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_page_items, parent, false);
        return new NavigationItemViewHolder(view, mOnNavItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NavigationItemViewHolder holder, int position) {
        holder.navItemName.setText(mNavItemArrayList.get(position).getMedName());
        holder.navItemPrice.setText(mNavItemArrayList.get(position).getMedPrice());
    }

    @Override
    public int getItemCount() {
        return mNavItemArrayList.size();
    }
}
