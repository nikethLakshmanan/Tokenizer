package com.example.tokenizerv2;
import android.app.PendingIntent; //added import
import android.content.Intent; //added import
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
import android.hardware.usb.UsbDeviceConnection;
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
            UsbManager manager = (UsbManager) v.getContext().getSystemService(Context.USB_SERVICE);
           // ProbeTable customTable = new ProbeTable();
           // customTable.addProduct(0x0483, 0x374B, CdcAcmSerialDriver.class);
            // Perform your custom action for exporting
            //UsbSerialProber Probe = new UsbSerialProber(customTable);
            List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
           // List<UsbSerialDriver> availableDrivers = Probe.findAllDrivers(manager);
            if (availableDrivers.isEmpty()) {
                Toast.makeText(v.getContext(), "No USB Driver Found", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
//                Toast.makeText(v.getContext(), "USB Driver Found", Toast.LENGTH_SHORT).show();
            }
            // debugging purposes
            UsbDeviceConnection connection = null;
            UsbSerialDriver driver = availableDrivers.get(0);
            PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(v.getContext(), 0, new Intent("com.example.USB_PERMISSION"), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

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
//            Toast.makeText(v.getContext(), "Device Connected", Toast.LENGTH_SHORT).show();

            UsbSerialPort port = driver.getPorts().get(0); // Most devices have just one port (port 0)
            try {
                port.open(connection);
                port.setDTR(true);
                port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            } catch (IOException e) {
                Toast.makeText(v.getContext(), "OpenErr: " + e.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }
//            Toast.makeText(v.getContext(), "Connection Established", Toast.LENGTH_SHORT).show();
            try {
                port.write(((card.getName() + "\n")).getBytes(), 5000);
                port.write(((card.getType() + "\n")).getBytes(), 5000);
                port.write(((card.getRules() + "\n")).getBytes(), 5000);
                port.write(card.getPowBytes(),5000);
                port.write(card.getTufBytes(), 5000);
            } catch (IOException e) {
                Toast.makeText(v.getContext(), "TextErr: " + e.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }

//            Toast.makeText(v.getContext(), "Port Write Texts Successful", Toast.LENGTH_SHORT).show();
            byte[] bmp = card.getImageByteArray();
            try {
                int len = bmp.length;
                String stringlen = Integer.toString(len) + "\n";
                byte[] byteslen = stringlen.getBytes();
                port.write(byteslen, 1000);
            } catch (IOException e) {
                Toast.makeText(v.getContext(), "LenErr: " + e.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }

            for (int i = 0; i < bmp.length; i += 1024) {
                byte[] buffer = new byte[1]; // Adjust size for expected response
                int bytesRead = 0; // Timeout of 5 seconds
                try {
                    bytesRead = port.read(buffer, 1000000);
                } catch (IOException e) {
                    Toast.makeText(v.getContext(), "AkErr: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                if (bytesRead > 0) {
                    System.out.println(bytesRead);
                }

                int remainingBytes = bmp.length - i;
                int currentChunkSize = Math.min(1024, remainingBytes);

                // Create a chunk from the array
                byte[] chunk = new byte[currentChunkSize];
                System.arraycopy(bmp, i, chunk, 0, currentChunkSize);

                // Write the chunk
                try {
                    port.write(chunk, 5000); // Timeout of 5 seconds
                } catch (IOException e) {
                    Toast.makeText(v.getContext(), "ImgErr: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
            }
            try {
                port.close();
            } catch (IOException e) {
                Toast.makeText(v.getContext(), " Port Close Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                return;
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