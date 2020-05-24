package com.collection.sanjivani;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ordersViewHolder> {

    private Context context;
    private List<OrderInfo> mOrdersArrayList;
    public OnOrderItemClickListener mOnOrderItemClickListener;

    public interface OnOrderItemClickListener {
        void onOrderClick(int position);
    }

    public void setOnOrderClickListener(OnOrderItemClickListener orderListener){
        this.mOnOrderItemClickListener = orderListener;
    }


    public static class ordersViewHolder extends RecyclerView.ViewHolder{

        TextView mOrderIdTextView, mOrderStatusTextView, mOrderTotalAmountTextView, mOrderPlacedDateTextView, mOrderPlacedTimeTextView;

        public ordersViewHolder(@NonNull View itemView, final OnOrderItemClickListener orderItemClickListener) {
            super(itemView);
            mOrderIdTextView = itemView.findViewById(R.id.orderIdTextView);
            mOrderStatusTextView = itemView.findViewById(R.id.orderStatusTextView);
            mOrderTotalAmountTextView = itemView.findViewById(R.id.orderTotalAmountTextView);
            mOrderPlacedDateTextView = itemView.findViewById(R.id.orderPlacedDateTextView);
            mOrderPlacedTimeTextView = itemView.findViewById(R.id.orderPlacedTimeTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (orderItemClickListener != null){
                        int position = getAdapterPosition();
                        orderItemClickListener.onOrderClick(position);
                    }
                }
            });
        }
    }

    public OrderAdapter(Context context, List<OrderInfo> mOrdersArrayList) {
        this.context = context;
        this.mOrdersArrayList = mOrdersArrayList;
    }

    @NonNull
    @Override
    public ordersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_items, parent, false);
        ordersViewHolder ovh = new ordersViewHolder(view, mOnOrderItemClickListener);
        return ovh;
    }

    @Override
    public void onBindViewHolder(@NonNull ordersViewHolder holder, int position) {

        holder.mOrderIdTextView.setText(mOrdersArrayList.get(position).getOrderId());
        holder.mOrderStatusTextView.setText(mOrdersArrayList.get(position).getOrderStatus());
        holder.mOrderTotalAmountTextView.setText(mOrdersArrayList.get(position).getOrderTotal());
        holder.mOrderPlacedDateTextView.setText(mOrdersArrayList.get(position).getOrderDate());
        holder.mOrderPlacedTimeTextView.setText(mOrdersArrayList.get(position).getOrderTime());

    }

    @Override
    public int getItemCount() {
        return mOrdersArrayList.size();
    }

}
