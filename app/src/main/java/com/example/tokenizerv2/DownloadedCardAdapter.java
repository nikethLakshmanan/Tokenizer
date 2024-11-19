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
import java.util.List;
//added imports
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.content.Context;


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
            UsbManager usbManager = (UsbManager) v.getContext().getSystemService(Context.USB_SERVICE);

            // Get the connected USB device (STM32F1 in this case)
            UsbDevice device = null;
            for (UsbDevice devices : usbManager.getDeviceList().values()) {
                //if (devices.getVendorId() == 0x1234 && devices.getProductId() == 0x5678) { // Replace with your STM32F1 device criteria
                    if(devices != null) {
                        device = devices;
                        break;
                    }
               // }
            }

            if (device != null) {
                // Request permission
                if (!usbManager.hasPermission(device)) {
                    PendingIntent permissionIntent = PendingIntent.getBroadcast(
                            v.getContext(), 0, new Intent("com.example.USB_PERMISSION"), PendingIntent.FLAG_UPDATE_CURRENT
                    );
                    usbManager.requestPermission(device, permissionIntent);
                    //requestUsbPermission(usbManager, device, v.getContext());
                } else {
                    byte[] data = card.getImageByteArray();
                    UsbInterface usbInterface = device.getInterface(0); // Assuming single interface
                    UsbEndpoint outputEndpoint = null;

                    for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
                        UsbEndpoint endpoint = usbInterface.getEndpoint(i);
                        if (endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK && endpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
                            outputEndpoint = endpoint;
                            break;
                        }
                    }

                    if (outputEndpoint == null) {
                        System.out.println("Output endpoint not found.");
                        return;
                    }

                    UsbDeviceConnection connection = usbManager.openDevice(device);
                    if (connection == null || !connection.claimInterface(usbInterface, true)) {
                        System.out.println("Failed to open connection or claim interface.");
                        return;
                    }

                    // Send the data
                    int result = connection.bulkTransfer(outputEndpoint, data, data.length, 5000); // 5-second timeout
                    if (result >= 0) {
                        System.out.println("Data transfer successful: " + result + " bytes transferred.");
                    } else {
                        System.out.println("Data transfer failed.");
                    }

                    connection.releaseInterface(usbInterface);
                    connection.close();
                }
            } else {
                System.out.println("No USB device found.");
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