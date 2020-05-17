package com.collection.sanjivani;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchDrugAdapter extends RecyclerView.Adapter<SearchDrugAdapter.SearchViewHolder> {

    private Context context;
    private List<MedInfo> medInfoArrayList;
    private OnMedClickListener mMedListener;

    public interface OnMedClickListener {
        void onMedClick(int position);
    }

    public void setOnMedClickListener(OnMedClickListener medListener){
        this.mMedListener = medListener;
    }

   static class SearchViewHolder extends RecyclerView.ViewHolder{

        TextView nameTextView, priceTextView, availabilityTextView;

        private SearchViewHolder(@NonNull View itemView, final OnMedClickListener medListener) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.nameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            availabilityTextView = itemView.findViewById(R.id.availabilityTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(medListener != null){
                        int position = getAdapterPosition();
                        medListener.onMedClick(position);
                    }
                }
            });

        }
    }

    public SearchDrugAdapter(Context context, List<MedInfo> medInfoArrayList) {
        this.context = context;
        this.medInfoArrayList = medInfoArrayList;
    }

    @NonNull
    @Override
    public SearchDrugAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_items, parent, false);
        return new SearchDrugAdapter.SearchViewHolder(view, mMedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        holder.nameTextView.setText(medInfoArrayList.get(position).getMedName());
        holder.priceTextView.setText(medInfoArrayList.get(position).getMedPrice());
        holder.availabilityTextView.setText(medInfoArrayList.get(position).getMedAvailability());
    }

    @Override
    public int getItemCount() {
        return medInfoArrayList.size();
    }

}
