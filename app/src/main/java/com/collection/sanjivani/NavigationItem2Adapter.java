package com.collection.sanjivani;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

 class Navigation2ItemAdapter extends RecyclerView.Adapter<Navigation2ItemAdapter.Navigation2ItemViewHolder> {

    private List<MedInfo> mNav2ItemArrayList;
    private Context context;
    public OnNav2ItemClickListener mOnNav2ItemClickListener;

    public interface OnNav2ItemClickListener{
        void onNav2ItemClick(int position);
    }

    public void setOnNav2ItemClickListener(OnNav2ItemClickListener onNav2ItemClickListener){
        mOnNav2ItemClickListener = onNav2ItemClickListener;
    }

    public static class Navigation2ItemViewHolder extends RecyclerView.ViewHolder{

        TextView navItemName, navItemPrice;

        public Navigation2ItemViewHolder(@NonNull View itemView, final OnNav2ItemClickListener onNav2ItemClickListener) {
            super(itemView);
            navItemName = itemView.findViewById(R.id.navPage_itemName);
            navItemPrice = itemView.findViewById(R.id.navPage_itemPrice);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onNav2ItemClickListener != null){
                        int position = getAdapterPosition();
                        onNav2ItemClickListener.onNav2ItemClick(position);
                    }
                }
            });
        }
    }

    public Navigation2ItemAdapter(List<MedInfo> mNav2ItemArrayList, Context context) {
        this.mNav2ItemArrayList = mNav2ItemArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public Navigation2ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_page_items, parent, false);
        return new Navigation2ItemViewHolder(view, mOnNav2ItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull Navigation2ItemViewHolder holder, int position) {
        holder.navItemName.setText(mNav2ItemArrayList.get(position).getMedName());
        holder.navItemPrice.setText(mNav2ItemArrayList.get(position).getMedPrice());
    }

    @Override
    public int getItemCount() {
        return mNav2ItemArrayList.size();
    }
}