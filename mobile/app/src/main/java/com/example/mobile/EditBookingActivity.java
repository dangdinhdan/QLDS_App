package com.example.mobile;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobile.model.Booking;
import com.example.mobile.repository.CourtRepository;
import com.example.mobile.viewmodel.MainViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class EditBookingActivity extends AppCompatActivity {

    private ImageButton buttonBack;
    private EditText editCustomerName;
    private EditText editPhoneNumber;
    private EditText editBookingNotes;
    private MaterialButton buttonSaveChanges;

    private int primaryBookingId;
    private Booking mainBooking;
    private List<Booking> groupBookings = new ArrayList<>();
    private final CourtRepository repository = CourtRepository.getInstance();
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_booking);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        initViews();
        retrieveIntentData();
        setupListeners();
        loadBookingData();
    }

    private void initViews() {
        buttonBack = findViewById(R.id.button_back);
        editCustomerName = findViewById(R.id.edit_customer_name);
        editPhoneNumber = findViewById(R.id.edit_phone_number);
        editBookingNotes = findViewById(R.id.edit_booking_notes);
        buttonSaveChanges = findViewById(R.id.button_save_changes);
    }

    private void retrieveIntentData() {
        primaryBookingId = getIntent().getIntExtra("booking_id", -1);
    }

    private void setupListeners() {
        buttonBack.setOnClickListener(v -> finish());
        buttonSaveChanges.setOnClickListener(v -> saveChanges());
    }

    private void loadBookingData() {
        if (primaryBookingId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin đặt sân!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mainBooking = null;
        for (Booking b : repository.getBookings()) {
            if (b.getId() == primaryBookingId) {
                mainBooking = b;
                break;
            }
        }

        if (mainBooking == null) {
            Toast.makeText(this, "Lịch đặt sân không tồn tại!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Group related bookings by customer (phone or name) and date
        groupBookings.clear();
        String mainPhone = mainBooking.getPhoneNumber();
        String mainName = mainBooking.getPlayerName();
        String mainDate = mainBooking.getDate();

        String mainStatus = mainBooking.getStatus() != null ? mainBooking.getStatus() : "";
        for (Booking b : repository.getBookings()) {
            if (b.getDate().equals(mainDate)) {
                String bStatus = b.getStatus() != null ? b.getStatus() : "";
                if (bStatus.equalsIgnoreCase(mainStatus)) {
                    if (mainPhone != null && !mainPhone.isEmpty()) {
                        if (mainPhone.equals(b.getPhoneNumber())) {
                            groupBookings.add(b);
                        }
                    } else if (mainName != null && mainName.equals(b.getPlayerName())) {
                        groupBookings.add(b);
                    }
                }
            }
        }

        // Populate fields
        editCustomerName.setText(mainBooking.getPlayerName());
        editPhoneNumber.setText(mainBooking.getPhoneNumber() != null ? mainBooking.getPhoneNumber() : "");
        editBookingNotes.setText(mainBooking.getNotes() != null ? mainBooking.getNotes() : "");
    }

    private void saveChanges() {
        String newName = editCustomerName.getText().toString().trim();
        String newPhone = editPhoneNumber.getText().toString().trim();
        String newNotes = editBookingNotes.getText().toString().trim();

        if (TextUtils.isEmpty(newName)) {
            editCustomerName.setError("Vui lòng nhập tên khách hàng");
            return;
        }

        // Update all related bookings
        for (Booking b : groupBookings) {
            Booking updated = new Booking(
                    b.getId(),
                    b.getCourtId(),
                    newName,
                    b.getDate(),
                    b.getStartTime(),
                    b.getEndTime(),
                    b.getFee(),
                    b.getStatus()
            );
            updated.setPhoneNumber(newPhone);
            updated.setNotes(newNotes);
            viewModel.updateBooking(updated);
        }

        Toast.makeText(this, "Cập nhật đặt sân thành công!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
