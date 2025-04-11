package com.example.footballfieldbooking.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footballfieldbooking.R;
import com.example.footballfieldbooking.activities.BookingActivity;
import com.example.footballfieldbooking.models.Field;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class FieldAdapter extends RecyclerView.Adapter<FieldAdapter.ViewHolder> {
    private Context context;
    private List<Field> fields;
    private boolean isAdmin;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public FieldAdapter(Context context, List<Field> fields, boolean isAdmin) {
        this.context = context;
        this.fields = fields;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_field, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Field field = fields.get(position);

        holder.tvFieldName.setText(field.getName());
        holder.tvFieldType.setText(field.getTypeString());
        holder.tvFieldStatus.setText(field.getStatusString());
        holder.tvFieldPrice.setText(currencyFormat.format(field.getPrice()));
        holder.tvFieldDescription.setText(field.getDescription());

        // Handle field status color
        int statusColor;
        switch(field.getStatus()) {
            case Field.STATUS_AVAILABLE:
                statusColor = context.getResources().getColor(R.color.green);
                break;
            case Field.STATUS_BOOKED:
                statusColor = context.getResources().getColor(R.color.red);
                break;
            case Field.STATUS_MAINTENANCE:
                statusColor = context.getResources().getColor(R.color.orange);
                break;
            default:
                statusColor = context.getResources().getColor(R.color.black);
        }
        holder.tvFieldStatus.setTextColor(statusColor);

        // Set button visibility and text based on status and user role
        if (isAdmin) {
            holder.btnAction.setText(R.string.edit);
            holder.btnAction.setEnabled(true);
        } else {
            holder.btnAction.setText(R.string.book_field);
            holder.btnAction.setEnabled(field.getStatus() == Field.STATUS_AVAILABLE);
        }

        holder.btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdmin) {
                    // This is handled by AdminFieldAdapter
                } else {
                    // Navigate to booking activity
                    Intent intent = new Intent(context, BookingActivity.class);
                    intent.putExtra("field_id", field.getId());
                    intent.putExtra("field_name", field.getName());
                    intent.putExtra("field_type", field.getType());
                    intent.putExtra("field_price", field.getPrice());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return fields.size();
    }

    public void updateData(List<Field> newFields) {
        fields.clear();
        fields.addAll(newFields);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFieldName, tvFieldType, tvFieldStatus, tvFieldPrice, tvFieldDescription;
        Button btnAction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFieldName = itemView.findViewById(R.id.tvFieldName);
            tvFieldType = itemView.findViewById(R.id.tvFieldType);
            tvFieldStatus = itemView.findViewById(R.id.tvFieldStatus);
            tvFieldPrice = itemView.findViewById(R.id.tvFieldPrice);
            tvFieldDescription = itemView.findViewById(R.id.tvFieldDescription);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }
}