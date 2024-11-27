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

import com.hoho.android.usbserial.driver.CdcAcmSerialDriver;
import com.hoho.android.usbserial.driver.ProbeTable;
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
            UsbManager manager = (UsbManager) v.getContext().getSystemService(Context.USB_SERVICE);
            ProbeTable customTable = new ProbeTable();
            customTable.addProduct(0x0483, 0x374B, CdcAcmSerialDriver.class);
            // Perform your custom action for exporting
            UsbSerialProber Probe = new UsbSerialProber(customTable);

            List<UsbSerialDriver> availableDrivers = Probe.findAllDrivers(manager);
            if (availableDrivers.isEmpty()) {
                Toast.makeText(v.getContext(), "No USB Driver Found", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                Toast.makeText(v.getContext(), "USB Driver Found", Toast.LENGTH_SHORT).show();

            }
            // debugging purposes
            UsbDeviceConnection connection = null;
            UsbSerialDriver driver = availableDrivers.get(0);
            PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(v.getContext(), 0, new Intent("com.example.USB_PERMISSION"), PendingIntent.FLAG_UPDATE_CURRENT);

            manager.requestPermission(driver.getDevice(), usbPermissionIntent);
            try{
                connection = manager.openDevice(driver.getDevice());
            } catch(Exception e){
                Toast.makeText(v.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            if (connection == null) {
                // add UsbManager.requestPermission(driver.getDevice(), ..) handling here
                Toast.makeText(v.getContext(), "No connection", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(v.getContext(), "Device Connected", Toast.LENGTH_SHORT).show();

            UsbSerialPort port = driver.getPorts().get(0); // Most devices have just one port (port 0)
            try {
                port.open(connection);
                port.setDTR(true);
                port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            } catch (IOException e) {
                Toast.makeText(v.getContext(), " Port Open Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            Toast.makeText(v.getContext(), "Connection Established", Toast.LENGTH_SHORT).show();
            try {
                port.write("hello".getBytes(), 10000);
            } catch (IOException e) {
                Toast.makeText(v.getContext(), " Port Write Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            Toast.makeText(v.getContext(), "Port Write Successful", Toast.LENGTH_SHORT).show();
            try {
                port.close();
            } catch (IOException e) {
                Toast.makeText(v.getContext(), " Port Close Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            Toast.makeText(v.getContext(), "Port Close Successful", Toast.LENGTH_SHORT).show();
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