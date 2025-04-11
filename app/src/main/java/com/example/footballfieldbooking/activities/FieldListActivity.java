package com.example.footballfieldbooking.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footballfieldbooking.R;
import com.example.footballfieldbooking.adapters.FieldAdapter;
import com.example.footballfieldbooking.database.DatabaseHelper;
import com.example.footballfieldbooking.models.Field;

import java.util.ArrayList;
import java.util.List;

public class FieldListActivity extends AppCompatActivity {
    private RecyclerView recyclerViewFields;
    private FieldAdapter fieldAdapter;
    private Spinner spinnerFieldType;
    private DatabaseHelper dbHelper;
    private List<Field> fieldList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_list);
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
        spinnerFieldType = findViewById(R.id.spinnerFieldType);

        // Set up RecyclerView
        recyclerViewFields.setLayoutManager(new LinearLayoutManager(this));
        fieldList = new ArrayList<>();
        fieldAdapter = new FieldAdapter(this, fieldList, false);
        recyclerViewFields.setAdapter(fieldAdapter);

        // Set up spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                this, R.array.field_types, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFieldType.setAdapter(spinnerAdapter);

        spinnerFieldType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadFields(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Load all fields initially
        loadFields(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload fields when returning to this activity
        int position = spinnerFieldType.getSelectedItemPosition();
        loadFields(position);
    }

    private void loadFields(int filterPosition) {
        List<Field> fields;

        switch (filterPosition) {
            case 1: // 5-a-side
                fields = dbHelper.getFieldsByType(Field.TYPE_5_ASIDE);
                break;
            case 2: // 7-a-side
                fields = dbHelper.getFieldsByType(Field.TYPE_7_ASIDE);
                break;
            case 3: // 11-a-side
                fields = dbHelper.getFieldsByType(Field.TYPE_11_ASIDE);
                break;
            default: // All fields
                fields = dbHelper.getAllFields();
                break;
        }

        fieldAdapter.updateData(fields);
    }
}