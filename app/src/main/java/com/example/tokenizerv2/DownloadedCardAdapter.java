package com.example.tokenizerv2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DownloadedCardAdapter extends RecyclerView.Adapter<DownloadedCardAdapter.CardViewHolder> {
    private List<Card> cards;

    public DownloadedCardAdapter(List<Card> cards) {
        this.cards = cards;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.downloaded_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Card card = cards.get(position);
        holder.cardNameTextView.setText(card.getName());
        holder.cardImageView.setImageBitmap(card.getImage());

        if (card.getImageByteArray() != null) {
            holder.downloadedByteArray.setText("Size: " + card.getImageByteArray().length + " bytes");
        }

    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView cardImageView;
        TextView cardNameTextView;
        TextView downloadedByteArray;

        CardViewHolder(View itemView) {
            super(itemView);
            cardImageView = itemView.findViewById(R.id.downloadedCardImageView);
            cardNameTextView = itemView.findViewById(R.id.downloadedCardNameTextView);
            downloadedByteArray = itemView.findViewById(R.id.downloadedByteArray);
        }
    }
}