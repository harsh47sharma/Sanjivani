package com.collection.sanjivani;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class    CartAdapter extends RecyclerView.Adapter<CartAdapter.cartViewHolder> {

    private List<CartInfo> mCartArrayList;
    private OnCartItemClickListener mCartListener;


    public interface OnCartItemClickListener {
        void onCartItemDeleteClick(int position);

        int onCartAddItemClick(int position);

        int onCartRemoveItemClick(int position);
    }

    public void setOnCartItemClickListener(OnCartItemClickListener onCartItemClickListener) {
        this.mCartListener = onCartItemClickListener;
    }


    public static class cartViewHolder extends RecyclerView.ViewHolder {

        TextView mCartItemNameTV, mCartItemPrice, mCartItemSelectedQuantityTV;
        //TextView mCartItemQuantity;
        ImageView mDeleteCartItemImageButton , mAddQuantityCartItemImageButton, mRemoveQuantityCartItemImageButton;


        public cartViewHolder(@NonNull final View itemView, final OnCartItemClickListener onCartItemClickListener) {
            super(itemView);
            mCartItemNameTV = itemView.findViewById(R.id.cartItemNameTV);
            //mCartItemQuantity = itemView.findViewById(R.id.cartItemQuantityTV);
            mCartItemPrice = itemView.findViewById(R.id.cartItemPriceTV);
            mDeleteCartItemImageButton = itemView.findViewById(R.id.cartItemDeleteImageButton);
            mAddQuantityCartItemImageButton = itemView.findViewById(R.id.cartItemAddQuantityImageButton);
            mRemoveQuantityCartItemImageButton = itemView.findViewById(R.id.cartItemRemoveQuantityImageButton);
            mCartItemSelectedQuantityTV = itemView.findViewById(R.id.cartItemSelectedQuantityTV);

            mDeleteCartItemImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onCartItemClickListener != null) {
                        int position = getAdapterPosition();
                        onCartItemClickListener.onCartItemDeleteClick(position);
                    }
                }
            });
            mAddQuantityCartItemImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onCartItemClickListener != null) {
                        int position = getAdapterPosition();
                        int cartItemSelectedQuantity;
                        cartItemSelectedQuantity = onCartItemClickListener.onCartAddItemClick(position);
                        Log.d("item count", "A" + cartItemSelectedQuantity + "Z");
                        mCartItemSelectedQuantityTV.setText(String.valueOf(cartItemSelectedQuantity));

                    }

                }
            });
            mRemoveQuantityCartItemImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onCartItemClickListener != null) {
                        int position = getAdapterPosition();
                        int cartItemSelectedQuantity;
                        cartItemSelectedQuantity = onCartItemClickListener.onCartRemoveItemClick(position);
                        mCartItemSelectedQuantityTV.setText(String.valueOf(String.valueOf(cartItemSelectedQuantity)));
                    }
                }
            });
        }
    }

    public CartAdapter(List<CartInfo> cartList) {
        this.mCartArrayList = cartList;
    }

    @NonNull
    @Override
    public cartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items, parent, false);
        cartViewHolder cvh = new cartViewHolder(view, mCartListener);
        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull cartViewHolder holder, int position) {
        holder.mCartItemNameTV.setText(mCartArrayList.get(position).getMedName());
        //holder.mCartItemQuantity.setText(mCartArrayList.get(position).getMedQuantity());
        holder.mCartItemPrice.setText(mCartArrayList.get(position).getMedPrice());
        holder.mCartItemSelectedQuantityTV.setText(mCartArrayList.get(position).getMedItemCount());
        holder.mCartItemSelectedQuantityTV.setText(mCartArrayList.get(position).getMedItemCount());

    }

    @Override
    public int getItemCount() {
        return mCartArrayList.size();
    }
}
