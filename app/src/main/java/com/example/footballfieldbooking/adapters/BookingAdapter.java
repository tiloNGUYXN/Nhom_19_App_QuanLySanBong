package com.example.footballfieldbooking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footballfieldbooking.R;
import com.example.footballfieldbooking.models.Booking;
import com.example.footballfieldbooking.models.Service;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {
    private Context context;
    private List<Booking> bookings;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public BookingAdapter(Context context, List<Booking> bookings) {
        this.context = context;
        this.bookings = bookings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking, parent, false);
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
            servicesText.append(context.getString(R.string.services)).append(" ");
            for (int i = 0; i < services.size(); i++) {
                servicesText.append(services.get(i).getName());
                if (i < services.size() - 1) {
                    servicesText.append(", ");
                }
            }
        } else {
            servicesText.append(context.getString(R.string.no_services));
        }
        holder.tvServices.setText(servicesText.toString());

        // Set status color
        int statusColor;
        switch(booking.getStatus()) {
            case Booking.STATUS_NOT_PLAYED:
                statusColor = context.getResources().getColor(R.color.blue);
                break;
            case Booking.STATUS_PLAYING:
                statusColor = context.getResources().getColor(R.color.green);
                break;
            case Booking.STATUS_BREAK:
                statusColor = context.getResources().getColor(R.color.orange);
                break;
            case Booking.STATUS_PLAYED:
                statusColor = context.getResources().getColor(R.color.gray);
                break;
            default:
                statusColor = context.getResources().getColor(R.color.black);
        }
        holder.tvBookingStatus.setTextColor(statusColor);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFieldName, tvBookingDate, tvBookingTime, tvBookingStatus, tvServices, tvTotalPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFieldName = itemView.findViewById(R.id.tvFieldName);
            tvBookingDate = itemView.findViewById(R.id.tvBookingDate);
            tvBookingTime = itemView.findViewById(R.id.tvBookingTime);
            tvBookingStatus = itemView.findViewById(R.id.tvBookingStatus);
            tvServices = itemView.findViewById(R.id.tvServices);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
        }
    }
}