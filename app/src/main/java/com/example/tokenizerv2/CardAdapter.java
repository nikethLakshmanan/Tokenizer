package com.example.tokenizerv2;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
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
        holder.cardDescTextView.setText(card.getDescription());
        holder.cardPowerTextView.setText(card.getPower());
        holder.cardToughnessTextView.setText(card.getToughness());
        if (card.getImage() != null) {
            holder.cardImageView.setImageBitmap(card.getImage());
        } else {
            holder.cardImageView.setImageResource(R.drawable.placeholder_image); // Set a placeholder image
            new ImageLoader(card, holder.cardImageView, this).execute(card.getImageUrl());
        }

        holder.downloadButton.setOnClickListener(v-> {
            new Thread(() -> {
                Bitmap bitmap = card.getImage();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageInByte = baos.toByteArray();
                card.setImageByteArray(imageInByte);
                new Handler(Looper.getMainLooper()).post(() -> {
                    SharedViewModel viewModel = new ViewModelProvider((FragmentActivity) v.getContext()).get(SharedViewModel.class);
                    viewModel.addCard(card);
                });



            }).start();


        });
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView cardImageView;
        TextView cardNameTextView;
        TextView cardDescTextView;
        TextView cardPowerTextView;
        TextView cardToughnessTextView;
        Button downloadButton;
        CardViewHolder(View itemView) {
            super(itemView);
            cardImageView = itemView.findViewById(R.id.cardImageView);
            cardNameTextView = itemView.findViewById(R.id.cardNameTextView);
            cardDescTextView = itemView.findViewById(R.id.cardDescTextView);
            cardPowerTextView = itemView.findViewById(R.id.cardPowerTextView);
            cardToughnessTextView = itemView.findViewById(R.id.cardToughnessTextView);
            downloadButton = itemView.findViewById(R.id.downloadButton);

        }
    }
}