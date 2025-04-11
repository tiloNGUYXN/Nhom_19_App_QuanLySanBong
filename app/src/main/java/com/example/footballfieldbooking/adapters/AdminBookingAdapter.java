package com.example.footballfieldbooking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footballfieldbooking.R;
import com.example.footballfieldbooking.database.DatabaseHelper;
import com.example.footballfieldbooking.models.Booking;
import com.example.footballfieldbooking.models.Field;
import com.example.footballfieldbooking.models.Notification;
import com.example.footballfieldbooking.models.Service;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminBookingAdapter extends RecyclerView.Adapter<AdminBookingAdapter.ViewHolder> {
    private Context context;
    private List<Booking> bookings;
    private DatabaseHelper dbHelper;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public AdminBookingAdapter(Context context, List<Booking> bookings, DatabaseHelper dbHelper) {
        this.context = context;
        this.bookings = bookings;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookings.get(position);

        holder.tvFieldName.setText(booking.getFieldName());
        holder.tvBookingDate.setText(dateFormat.format(booking.getBookingDate()));
        holder.tvBookingTime.setText(timeFormat.format(booking.getStartTime()) +
                " - " +
                timeFormat.format(booking.getEndTime()));
        holder.tvBookingStatus.setText(booking.getStatusString());
        holder.tvTotalPrice.setText(currencyFormat.format(booking.getTotalPrice()));

        // Set services if any
        StringBuilder servicesText = new StringBuilder();
        List<Service> services = booking.getServices();
        if (services != null && !services.isEmpty()) {
            servicesText.append("Services: ");
            for (int i = 0; i < services.size(); i++) {
                servicesText.append(services.get(i).getName());
                if (i < services.size() - 1) {
                    servicesText.append(", ");
                }
            }
        } else {
            servicesText.append("No additional services");
        }
        holder.tvServices.setText(servicesText.toString());

        // Set status color and adjust status buttons
        int statusColor;
        switch(booking.getStatus()) {
            case Booking.STATUS_NOT_PLAYED:
                statusColor = context.getResources().getColor(R.color.blue);
                holder.btnStartPlaying.setEnabled(true);
                holder.btnTakeBreak.setEnabled(false);
                holder.btnEndMatch.setEnabled(false);
                break;
            case Booking.STATUS_PLAYING:
                statusColor = context.getResources().getColor(R.color.green);
                holder.btnStartPlaying.setEnabled(false);
                holder.btnTakeBreak.setEnabled(true);
                holder.btnEndMatch.setEnabled(true);
                break;
            case Booking.STATUS_BREAK:
                statusColor = context.getResources().getColor(R.color.orange);
                holder.btnStartPlaying.setEnabled(true);
                holder.btnTakeBreak.setEnabled(false);
                holder.btnEndMatch.setEnabled(true);
                break;
            case Booking.STATUS_PLAYED:
                statusColor = context.getResources().getColor(R.color.gray);
                holder.btnStartPlaying.setEnabled(false);
                holder.btnTakeBreak.setEnabled(false);
                holder.btnEndMatch.setEnabled(false);
                break;
            default:
                statusColor = context.getResources().getColor(R.color.black);
        }
        holder.tvBookingStatus.setTextColor(statusColor);

        // Set button click listeners
        holder.btnStartPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBookingStatus(booking, Booking.STATUS_PLAYING);
            }
        });

        holder.btnTakeBreak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBookingStatus(booking, Booking.STATUS_BREAK);
            }
        });

        holder.btnEndMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBookingStatus(booking, Booking.STATUS_PLAYED);
                // When match is over, set field back to available
                dbHelper.updateFieldStatus(booking.getFieldId(), Field.STATUS_AVAILABLE);
            }
        });
    }

    private void updateBookingStatus(Booking booking, int newStatus) {
        int result = dbHelper.updateBookingStatus(booking.getId(), newStatus);

        if (result > 0) {
            // Send notification to user
            Notification notification = new Notification();
            notification.setUserId(booking.getUserId());
            notification.setTitle("Booking Status Updated");

            String statusMessage;
            switch (newStatus) {
                case Booking.STATUS_PLAYING:
                    statusMessage = "Your booking is now active. Match has started.";
                    break;
                case Booking.STATUS_BREAK:
                    statusMessage = "Your match is now on break.";
                    break;
                case Booking.STATUS_PLAYED:
                    statusMessage = "Your match has been completed. Thank you for using our service!";
                    break;
                default:
                    statusMessage = "Your booking status has been updated.";
            }

            notification.setMessage("Field: " + booking.getFieldName() + "\nDate: " +
                    dateFormat.format(booking.getBookingDate()) + "\nStatus: " + statusMessage);
            notification.setTimestamp(new Date());
            notification.setRead(false);

            dbHelper.addNotification(notification);

            // Update UI
            booking.setStatus(newStatus);
            notifyDataSetChanged();

            Toast.makeText(context, "Status updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFieldName, tvBookingDate, tvBookingTime, tvBookingStatus, tvServices, tvTotalPrice;
        Button btnStartPlaying, btnTakeBreak, btnEndMatch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFieldName = itemView.findViewById(R.id.tvFieldName);
            tvBookingDate = itemView.findViewById(R.id.tvBookingDate);
            tvBookingTime = itemView.findViewById(R.id.tvBookingTime);
            tvBookingStatus = itemView.findViewById(R.id.tvBookingStatus);
            tvServices = itemView.findViewById(R.id.tvServices);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            btnStartPlaying = itemView.findViewById(R.id.btnStartPlaying);
            btnTakeBreak = itemView.findViewById(R.id.btnTakeBreak);
            btnEndMatch = itemView.findViewById(R.id.btnEndMatch);
        }
    }
}