package com.example.footballfieldbooking.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.footballfieldbooking.R;
import com.example.footballfieldbooking.database.DatabaseHelper;
import com.example.footballfieldbooking.models.Field;

public class AddEditFieldActivity extends AppCompatActivity {
    private EditText etFieldName, etFieldPrice, etFieldDescription;
    private Spinner spinnerFieldType;
    private Button btnSave, btnBack;
    private DatabaseHelper dbHelper;
    private boolean isEditMode = false;
    private int fieldId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_field);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // Ẩn cả ActionBar nếu có
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        dbHelper = new DatabaseHelper(this);

        // Initialize views
        etFieldName = findViewById(R.id.etFieldName);
        etFieldPrice = findViewById(R.id.etFieldPrice);
        etFieldDescription = findViewById(R.id.etFieldDescription);
        spinnerFieldType = findViewById(R.id.spinnerFieldType);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        // Set up spinner for field types
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.field_types_values, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFieldType.setAdapter(adapter);

        // Check if we're in edit mode
        if (getIntent().hasExtra("field_id")) {
            isEditMode = true;
            fieldId = getIntent().getIntExtra("field_id", 0);
            loadFieldData();
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveField();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadFieldData() {
        String fieldName = getIntent().getStringExtra("field_name");
        int fieldType = getIntent().getIntExtra("field_type", Field.TYPE_5_ASIDE);
        double fieldPrice = getIntent().getDoubleExtra("field_price", 0);
        String fieldDescription = getIntent().getStringExtra("field_description");

        etFieldName.setText(fieldName);
        etFieldPrice.setText(String.valueOf(fieldPrice));
        etFieldDescription.setText(fieldDescription);

        int spinnerPosition;
        switch (fieldType) {
            case Field.TYPE_5_ASIDE:
                spinnerPosition = 0;
                break;
            case Field.TYPE_7_ASIDE:
                spinnerPosition = 1;
                break;
            case Field.TYPE_11_ASIDE:
                spinnerPosition = 2;
                break;
            default:
                spinnerPosition = 0;
        }
        spinnerFieldType.setSelection(spinnerPosition);
    }

    private void saveField() {
        String name = etFieldName.getText().toString().trim();
        String priceStr = etFieldPrice.getText().toString().trim();
        String description = etFieldDescription.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show();
            return;
        }

        int type;
        switch (spinnerFieldType.getSelectedItemPosition()) {
            case 0:
                type = Field.TYPE_5_ASIDE;
                break;
            case 1:
                type = Field.TYPE_7_ASIDE;
                break;
            case 2:
                type = Field.TYPE_11_ASIDE;
                break;
            default:
                type = Field.TYPE_5_ASIDE;
        }

        Field field = new Field();
        field.setName(name);
        field.setType(type);
        field.setPrice(price);
        field.setDescription(description);

        if (isEditMode) {
            field.setId(fieldId);
            field.setStatus(getIntent().getIntExtra("field_status", Field.STATUS_AVAILABLE));
            dbHelper.updateField(field);
            Toast.makeText(this, "Field updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            field.setStatus(Field.STATUS_AVAILABLE);
            dbHelper.addField(field);
            Toast.makeText(this, "Field added successfully", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}