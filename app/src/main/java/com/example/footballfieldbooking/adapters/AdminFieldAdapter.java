package com.example.footballfieldbooking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footballfieldbooking.R;
import com.example.footballfieldbooking.models.Field;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdminFieldAdapter extends RecyclerView.Adapter<AdminFieldAdapter.ViewHolder> {
    private Context context;
    private List<Field> fields;
    private AdminFieldListener listener;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public interface AdminFieldListener {
        void onEditField(Field field);
        void onToggleMaintenanceStatus(Field field);
    }

    public AdminFieldAdapter(Context context, List<Field> fields, AdminFieldListener listener) {
        this.context = context;
        this.fields = fields;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_field, parent, false);
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
                holder.btnToggleMaintenance.setText(R.string.set_maintenance);
                break;
            case Field.STATUS_BOOKED:
                statusColor = context.getResources().getColor(R.color.red);
                holder.btnToggleMaintenance.setEnabled(false);
                holder.btnToggleMaintenance.setText(R.string.field_booked);
                break;
            case Field.STATUS_MAINTENANCE:
                statusColor = context.getResources().getColor(R.color.orange);
                holder.btnToggleMaintenance.setText(R.string.end_maintenance);
                break;
            default:
                statusColor = context.getResources().getColor(R.color.black);
                holder.btnToggleMaintenance.setText(R.string.set_maintenance);
        }
        holder.tvFieldStatus.setTextColor(statusColor);

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEditField(field);
            }
        });

        holder.btnToggleMaintenance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onToggleMaintenanceStatus(field);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fields.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFieldName, tvFieldType, tvFieldStatus, tvFieldPrice, tvFieldDescription;
        Button btnEdit, btnToggleMaintenance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFieldName = itemView.findViewById(R.id.tvFieldName);
            tvFieldType = itemView.findViewById(R.id.tvFieldType);
            tvFieldStatus = itemView.findViewById(R.id.tvFieldStatus);
            tvFieldPrice = itemView.findViewById(R.id.tvFieldPrice);
            tvFieldDescription = itemView.findViewById(R.id.tvFieldDescription);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnToggleMaintenance = itemView.findViewById(R.id.btnToggleMaintenance);
        }
    }
}