package com.example.tokenizerv2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    private List<Card> cards;

    public CardAdapter(List<Card> cards) {
        this.cards = cards;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Card card = cards.get(position);
        holder.cardNameTextView.setText(card.getName());
        if (card.getImage() != null) {
            holder.cardImageView.setImageBitmap(card.getImage());
        } else {
            holder.cardImageView.setImageResource(R.drawable.placeholder_image); // Set a placeholder image
            new ImageLoader(card, holder.cardImageView, this).execute(card.getImageUrl());
        }
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView cardImageView;
        TextView cardNameTextView;

        CardViewHolder(View itemView) {
            super(itemView);
            cardImageView = itemView.findViewById(R.id.cardImageView);
            cardNameTextView = itemView.findViewById(R.id.cardNameTextView);
        }
    }
}