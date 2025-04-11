package com.example.footballfieldbooking.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footballfieldbooking.R;
import com.example.footballfieldbooking.adapters.AdminFieldAdapter;
import com.example.footballfieldbooking.database.DatabaseHelper;
import com.example.footballfieldbooking.models.Field;

import java.util.List;

public class ManageFieldsActivity extends AppCompatActivity implements AdminFieldAdapter.AdminFieldListener {
    private RecyclerView recyclerViewFields;
    private AdminFieldAdapter fieldAdapter;
    private Button btnAddField;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_fields);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // Ẩn cả ActionBar nếu có
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        dbHelper = new DatabaseHelper(this);

        // Initialize views
        recyclerViewFields = findViewById(R.id.recyclerViewFields);
        btnAddField = findViewById(R.id.btnAddField);

        // Set up RecyclerView
        recyclerViewFields.setLayoutManager(new LinearLayoutManager(this));
        loadFields();

        btnAddField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageFieldsActivity.this, AddEditFieldActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFields(); // Reload fields when returning to this activity
    }

    private void loadFields() {
        List<Field> fields = dbHelper.getAllFields();
        fieldAdapter = new AdminFieldAdapter(this, fields, this);
        recyclerViewFields.setAdapter(fieldAdapter);
    }

    @Override
    public void onEditField(Field field) {
        Intent intent = new Intent(this, AddEditFieldActivity.class);
        intent.putExtra("field_id", field.getId());
        intent.putExtra("field_name", field.getName());
        intent.putExtra("field_type", field.getType());
        intent.putExtra("field_status", field.getStatus());
        intent.putExtra("field_price", field.getPrice());
        intent.putExtra("field_description", field.getDescription());
        startActivity(intent);
    }

    @Override
    public void onToggleMaintenanceStatus(Field field) {
        int newStatus;
        if (field.getStatus() == Field.STATUS_MAINTENANCE) {
            newStatus = Field.STATUS_AVAILABLE;
        } else {
            newStatus = Field.STATUS_MAINTENANCE;
        }

        dbHelper.updateFieldStatus(field.getId(), newStatus);
        loadFields(); // Reload fields after status change
    }
}