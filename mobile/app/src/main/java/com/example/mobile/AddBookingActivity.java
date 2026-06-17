package com.example.mobile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile.model.Booking;
import com.example.mobile.model.Court;
import com.example.mobile.repository.CourtRepository;
import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class AddBookingActivity extends AppCompatActivity {

    private ImageButton buttonBack;
    private ImageButton buttonNotifications;
    private EditText editPhoneNumber;
    private EditText editCustomerName;
    private LinearLayout layoutSelectedCourtsSummary;
    private TextView textTotalFeeDisplay;
    private MaterialButton buttonSaveBooking;

    private String selectedDate;
    private int durationMinutes;
    private int[] courtIds;
    private int[] startIndices;
    private int[] endIndices;

    private final CourtRepository repository = CourtRepository.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_booking);

        initViews();
        retrieveIntentData();
        setupListeners();
        populateSelectedCourts();
    }

    private void initViews() {
        buttonBack = findViewById(R.id.button_back);
        buttonNotifications = findViewById(R.id.button_notifications);
        editPhoneNumber = findViewById(R.id.edit_phone_number);
        editCustomerName = findViewById(R.id.edit_customer_name);
        layoutSelectedCourtsSummary = findViewById(R.id.layout_selected_courts_summary);
        textTotalFeeDisplay = findViewById(R.id.text_total_fee_display);
        buttonSaveBooking = findViewById(R.id.button_save_booking);
    }

    private void retrieveIntentData() {
        if (getIntent() != null) {
            selectedDate = getIntent().getStringExtra("selected_date");
            durationMinutes = getIntent().getIntExtra("duration_minutes", 60);
            courtIds = getIntent().getIntArrayExtra("court_ids");
            startIndices = getIntent().getIntArrayExtra("start_indices");
            endIndices = getIntent().getIntArrayExtra("end_indices");
        }

        if (selectedDate == null) {
            selectedDate = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());
        }
    }

    private void setupListeners() {
        buttonBack.setOnClickListener(v -> finish());
        buttonNotifications.setOnClickListener(v -> Toast.makeText(this, "Không có thông báo mới", Toast.LENGTH_SHORT).show());

        editPhoneNumber.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = s.toString().trim();
                String mockName = getAutoName(phone);
                if (mockName != null) {
                    editCustomerName.setText(mockName);
                } else if (phone.length() >= 9) {
                    repository.lookupCustomerByPhone(phone, name -> {
                        if (name != null) {
                            runOnUiThread(() -> {
                                if (editPhoneNumber.getText().toString().trim().equals(phone)) {
                                    editCustomerName.setText(name);
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        buttonSaveBooking.setOnClickListener(v -> saveBookings());
    }

    private String getAutoName(String phone) {
        if (phone == null) return null;
        String clean = phone.replaceAll("\\s+", "").trim();
        if (clean.equals("0912345678")) return "Nguyễn Văn An";
        if (clean.equals("0987654321")) return "Trần Thị Bích";
        if (clean.equals("0905123456")) return "Lê Hoàng Nam";
        if (clean.equals("0934567890")) return "Phạm Minh Đức";
        return null;
    }

    private void populateSelectedCourts() {
        if (courtIds == null || startIndices == null || endIndices == null || courtIds.length == 0) {
            Toast.makeText(this, "Không có sân nào được chọn!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        layoutSelectedCourtsSummary.removeAllViews();
        double totalFee = 0.0;
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < courtIds.length; i++) {
            int courtId = courtIds[i];
            int startIdx = startIndices[i];
            int endIdx = endIndices[i];

            int minIdx = Math.min(startIdx, endIdx);
            int maxIdx = Math.max(startIdx, endIdx);

            Court court = repository.getCourtById(courtId);
            if (court != null) {
                int startTotalMinutes = 6 * 60 + minIdx * durationMinutes;
                String startStr = String.format("%02d:%02d", startTotalMinutes / 60, startTotalMinutes % 60);

                int endTotalMinutes = 6 * 60 + (maxIdx + 1) * durationMinutes;
                String endStr = String.format("%02d:%02d", endTotalMinutes / 60, endTotalMinutes % 60);

                double duration = (endTotalMinutes - startTotalMinutes) / 60.0;
                double fee = duration * court.getHourlyRate();
                totalFee += fee;

                View itemView = inflater.inflate(R.layout.item_selected_court_summary, layoutSelectedCourtsSummary, false);
                TextView textCourtName = itemView.findViewById(R.id.text_court_name);
                TextView textHoursRange = itemView.findViewById(R.id.text_hours_range);
                TextView textCourtFee = itemView.findViewById(R.id.text_court_fee);

                textCourtName.setText(court.getName());
                textHoursRange.setText("Khung giờ: " + startStr + " - " + endStr + " (" + formatVnd(court.getHourlyRate()) + "/h)");
                textCourtFee.setText("Chi phí: " + formatVnd(fee));

                layoutSelectedCourtsSummary.addView(itemView);
            }
        }

        textTotalFeeDisplay.setText("Tổng tạm tính: " + formatVnd(totalFee));
    }

    private void saveBookings() {
        String playerName = editCustomerName.getText().toString().trim();
        String phoneNumber = editPhoneNumber.getText().toString().trim();

        if (TextUtils.isEmpty(phoneNumber)) {
            editPhoneNumber.setError("Vui lòng nhập số điện thoại");
            return;
        }

        if (TextUtils.isEmpty(playerName)) {
            editCustomerName.setError("Vui lòng nhập tên khách hàng");
            return;
        }

        if (courtIds == null || courtIds.length == 0) {
            Toast.makeText(this, "Không có sân nào được chọn!", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i = 0; i < courtIds.length; i++) {
            int courtId = courtIds[i];
            int startIdx = startIndices[i];
            int endIdx = endIndices[i];

            int minIdx = Math.min(startIdx, endIdx);
            int maxIdx = Math.max(startIdx, endIdx);

            Court court = repository.getCourtById(courtId);
            if (court != null) {
                int startTotalMinutes = 6 * 60 + minIdx * durationMinutes;
                String startStr = String.format("%02d:%02d", startTotalMinutes / 60, startTotalMinutes % 60);

                int endTotalMinutes = 6 * 60 + (maxIdx + 1) * durationMinutes;
                String endStr = String.format("%02d:%02d", endTotalMinutes / 60, endTotalMinutes % 60);

                double duration = (endTotalMinutes - startTotalMinutes) / 60.0;
                double fee = duration * court.getHourlyRate();

                Booking booking = new Booking(0, courtId, playerName, selectedDate, startStr, endStr, fee);
                booking.setPhoneNumber(phoneNumber);
                repository.addBooking(booking);
            }
        }

        Toast.makeText(this, "Lưu đặt sân thành công!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private String formatVnd(double value) {
        long vnd = Math.round(value);
        DecimalFormat formatter = new DecimalFormat("#,###");
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(vnd) + "đ";
    }
}
