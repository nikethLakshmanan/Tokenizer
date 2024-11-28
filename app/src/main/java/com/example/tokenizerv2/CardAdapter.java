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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
        holder.cardDescTextView.setText(card.getType());
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
                int cropX = (bitmap.getWidth() - 308) / 2;
                int cropY = (bitmap.getHeight()- 225)/ 2;
                // cropping to a 308x225 image
                Bitmap cropped = Bitmap.createBitmap(bitmap, cropX, cropY, 308,
                        225);
                //Bitmap scaled = Bitmap.createScaledBitmap(cropped, 308, 225, false);
                byte[] bmp = convertBitmapToBMP(cropped);
               // ByteArrayOutputStream baos = new ByteArrayOutputStream();
                //bmp.compress(Bitmap.CompressFormat, 100, baos);
//                byte[] imageInByte = scaled
                card.setImageByteArray(bmp);
                new Handler(Looper.getMainLooper()).post(() -> {
                    SharedViewModel viewModel = new ViewModelProvider((FragmentActivity) v.getContext()).get(SharedViewModel.class);
                    viewModel.addCard(card);
                });



            }).start();


        });
    }

    public byte[] convertBitmapToBMP(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int rowBytes = width * 3 + (width % 4); // Each row must be a multiple of 4 bytes

        // Create a ByteArrayOutputStream to hold the BMP data
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // BMP File Header
            baos.write(new byte[]{0x42, 0x4D}); // 'BM'
            baos.write(intToBytes(54 + rowBytes * height)); // File size
            baos.write(new byte[]{0, 0, 0, 0}); // Reserved
            baos.write(intToBytes(54)); // Offset to pixel array

            // BMP Info Header
            baos.write(intToBytes(40)); // Info header size
            baos.write(intToBytes(width)); // Image width
            baos.write(intToBytes(height)); // Image height
            baos.write(new byte[]{1, 0}); // Planes
            baos.write(new byte[]{24, 0}); // Bits per pixel
            baos.write(new byte[]{0, 0, 0, 0}); // Compression (none)
            baos.write(intToBytes(rowBytes * height)); // Image size
            baos.write(new byte[]{0, 0, 0, 0}); // X pixels per meter
            baos.write(new byte[]{0, 0, 0, 0}); // Y pixels per meter
            baos.write(new byte[]{0, 0, 0, 0}); // Total colors
            baos.write(new byte[]{0, 0, 0, 0}); // Important colors

            // BMP Pixel Data
            for (int y = height - 1; y >= 0; y--) { // BMP stores rows bottom to top
                for (int x = 0; x < width; x++) {
                    int pixel = bitmap.getPixel(x, y);
                    baos.write(pixel & 0xFF);        // Blue
                    baos.write((pixel >> 8) & 0xFF); // Green
                    baos.write((pixel >> 16) & 0xFF); // Red
                }
                // Pad row to multiple of 4 bytes
                for (int p = 0; p < (width % 4); p++) {
                    baos.write(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error in converting bitmap to bmp");
        }

        return baos.toByteArray();
    }

    public byte[] intToBytes(int value) {
        return new byte[]{
                (byte) (value & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) ((value >> 16) & 0xFF),
                (byte) ((value >> 24) & 0xFF)
        };
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