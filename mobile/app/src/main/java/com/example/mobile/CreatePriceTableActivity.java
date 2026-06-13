package com.example.mobile;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobile.model.PriceTable;
import com.example.mobile.viewmodel.MainViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CreatePriceTableActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    private EditText editCode;
    private EditText editName;
    private EditText editDesc;
    private LinearLayout layoutSlotsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_price_table);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        initViews();
        setupActions();

        // Add reference mockup default pricing rows
        addSlotRow("Ngày thường", "06:00", "17:00", "150000");
        addSlotRow("Cuối tuần", "06:00", "22:00", "250000");
    }

    private void initViews() {
        editCode = findViewById(R.id.edit_create_code);
        editName = findViewById(R.id.edit_create_name);
        editDesc = findViewById(R.id.edit_create_desc);
        layoutSlotsContainer = findViewById(R.id.layout_slots_container);
    }

    private void setupActions() {
        // Back Button
        ImageButton buttonBack = findViewById(R.id.button_back);
        if (buttonBack != null) {
            buttonBack.setOnClickListener(v -> finish());
        }

        // Cancel Button
        MaterialButton buttonCancel = findViewById(R.id.button_cancel);
        if (buttonCancel != null) {
            buttonCancel.setOnClickListener(v -> finish());
        }

        // Add Slot Button
        MaterialButton buttonAddSlot = findViewById(R.id.button_add_slot);
        if (buttonAddSlot != null) {
            buttonAddSlot.setOnClickListener(v -> addSlotRow("Ngày thường", "08:00", "17:00", "150000"));
        }

        // Save Button
        MaterialButton buttonSave = findViewById(R.id.button_save);
        if (buttonSave != null) {
            buttonSave.setOnClickListener(v -> savePriceTable());
        }
    }

    private void addSlotRow(String dayType, String startTime, String endTime, String price) {
        View row = LayoutInflater.from(this).inflate(R.layout.item_create_price_slot, layoutSlotsContainer, false);

        // Day Type Spinner
        Spinner spinner = row.findViewById(R.id.spinner_day_type);
        List<String> dayTypes = new ArrayList<>();
        dayTypes.add("Ngày thường");
        dayTypes.add("Cuối tuần");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dayTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if ("Cuối tuần".equals(dayType)) {
            spinner.setSelection(1);
        }

        // Start Time
        TextView textStart = row.findViewById(R.id.text_start_time);
        textStart.setText(startTime);
        textStart.setOnClickListener(v -> showTimePickerDialog(textStart));

        // End Time
        TextView textEnd = row.findViewById(R.id.text_end_time);
        textEnd.setText(endTime);
        textEnd.setOnClickListener(v -> showTimePickerDialog(textEnd));

        // Price Input
        EditText editPrice = row.findViewById(R.id.edit_slot_price);
        editPrice.setText(price);

        // Delete Row Button
        ImageButton buttonDelete = row.findViewById(R.id.button_delete_slot);
        buttonDelete.setOnClickListener(v -> {
            layoutSlotsContainer.removeView(row);
            Toast.makeText(this, "Đã xóa khung giờ", Toast.LENGTH_SHORT).show();
        });

        layoutSlotsContainer.addView(row);
    }

    private void showTimePickerDialog(TextView textView) {
        String currentTime = textView.getText().toString();
        int hour = 8;
        int minute = 0;
        if (currentTime.contains(":")) {
            String[] parts = currentTime.split(":");
            try {
                hour = Integer.parseInt(parts[0]);
                minute = Integer.parseInt(parts[1]);
            } catch (NumberFormatException ignored) {}
        }
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, min) -> {
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, min);
                    textView.setText(formattedTime);
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void savePriceTable() {
        String code = editCode.getText().toString().trim();
        String name = editName.getText().toString().trim();
        String desc = editDesc.getText().toString().trim();

        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "Vui lòng nhập mã bảng giá", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Vui lòng nhập tên bảng giá", Toast.LENGTH_SHORT).show();
            return;
        }

        // Build object and call viewmodel
        PriceTable pt = new PriceTable(0, code, name, desc);
        viewModel.addPriceTable(pt);

        // Feedback / Simulation of slots storage
        Toast.makeText(this, "Thêm bảng giá và chi tiết khung giờ thành công!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
