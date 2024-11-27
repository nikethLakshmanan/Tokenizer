package com.example.tokenizerv2;
import android.app.PendingIntent; //added import
import android.content.Intent; //added import
import android.hardware.usb.UsbConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;
//added imports
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.content.Context;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;


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
        holder.exportButton.setOnClickListener(v -> {
            // Perform your custom action for exporting
            UsbManager manager = (UsbManager) v.getContext().getSystemService(Context.USB_SERVICE);
            List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
            if (availableDrivers.isEmpty()) {
                Toast.makeText(v.getContext(), "No USB Device Found", Toast.LENGTH_SHORT).show();
                return;
            }
            UsbSerialDriver driver = availableDrivers.get(0);
            UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
            if (connection == null) {
                // add UsbManager.requestPermission(driver.getDevice(), ..) handling here
                Toast.makeText(v.getContext(), "No connection", Toast.LENGTH_SHORT).show();
                return;
            }

            UsbSerialPort port = driver.getPorts().get(0); // Most devices have just one port (port 0)
            try {
                port.open(connection);
                port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                port.write(card.getImageByteArray(), 10000);
                port.close();
            } catch (IOException e) {
                Toast.makeText(v.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView cardImageView;
        TextView cardNameTextView;
        TextView downloadedByteArray;
        Button exportButton;
        CardViewHolder(View itemView) {
            super(itemView);
            cardImageView = itemView.findViewById(R.id.downloadedCardImageView);
            cardNameTextView = itemView.findViewById(R.id.downloadedCardNameTextView);
            downloadedByteArray = itemView.findViewById(R.id.downloadedByteArray);
            exportButton = itemView.findViewById(R.id.exportButton);
        }
    }
}