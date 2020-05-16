package com.collection.sanjivani;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private Context context;
    private List<String> medNameArrayList;
    private List<String> medPriceArrayList;
    private List<String> medAvailabilityArrayList;

    class SearchViewHolder extends RecyclerView.ViewHolder{

        TextView nameTextView, priceTextView, availabilityTextView;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.nameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            availabilityTextView = itemView.findViewById(R.id.availabilityTextView);

        }
    }

    public SearchAdapter(Context context, List<String> medNameArrayList, List<String> medPriceArrayList, List<String> medAvailabilityArrayList) {
        this.context = context;
        this.medNameArrayList = medNameArrayList;
        this.medPriceArrayList = medPriceArrayList;
        this.medAvailabilityArrayList = medAvailabilityArrayList;
    }

    @NonNull
    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_items, parent, false);
        return new SearchAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        holder.nameTextView.setText(medNameArrayList.get(position));
        holder.priceTextView.setText(medPriceArrayList.get(position));
        holder.availabilityTextView.setText(medAvailabilityArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return medNameArrayList.size();
    }
}
