package com.collection.sanjivani;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderInfoItemAdapter extends RecyclerView.Adapter<OrderInfoItemAdapter.orderInfoItemViewHolder> {

    private List<OrderInfo> mOrderInfoItemArrayList;
    private Context context;

    public static class orderInfoItemViewHolder extends RecyclerView.ViewHolder{

        TextView mOrderInfoItemMedName, mOrderInfoItemMedPrice, mOrderInfoItemMedCount, mOrderInfoItemTotal;

        public orderInfoItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mOrderInfoItemMedName = itemView.findViewById(R.id.orderInfoItemMedName);
            mOrderInfoItemMedCount = itemView.findViewById(R.id.orderInfoItemMedCount);
            mOrderInfoItemMedPrice = itemView.findViewById(R.id.orderInfoItemMedPrice);
            mOrderInfoItemTotal = itemView.findViewById(R.id.orderInfoItemTotal);

        }
    }

    public OrderInfoItemAdapter(Context context, List<OrderInfo> mOrderInfoItemArrayList) {
        this.context = context;
        this.mOrderInfoItemArrayList = mOrderInfoItemArrayList;
    }

    @NonNull
    @Override
    public orderInfoItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item_list, parent, false);
        return new OrderInfoItemAdapter.orderInfoItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull orderInfoItemViewHolder holder, int position) {
        float itemCount = Float.parseFloat(mOrderInfoItemArrayList.get(position).getMedPrice());
        float itemPrice = Float.parseFloat(mOrderInfoItemArrayList.get(position).getMedItemCount());
        float allItemCost = itemCount*itemPrice;
        holder.mOrderInfoItemMedName.setText(mOrderInfoItemArrayList.get(position).getMedName());
        holder.mOrderInfoItemMedCount.setText(mOrderInfoItemArrayList.get(position).getMedItemCount());
        holder.mOrderInfoItemMedPrice.setText(mOrderInfoItemArrayList.get(position).getMedPrice());
        holder.mOrderInfoItemTotal.setText(String.valueOf(allItemCost));
    }

    @Override
    public int getItemCount() {
        return mOrderInfoItemArrayList.size();
    }
}
