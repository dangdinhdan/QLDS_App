package com.example.mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobile.model.Booking;
import com.example.mobile.model.Court;
import com.example.mobile.repository.CourtRepository;
import com.example.mobile.viewmodel.MainViewModel;
import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {

    private ImageButton buttonBack;
    private TextView textBookingCode;
    private TextView textCustomerName;
    private LinearLayout layoutBookedCourtsList;
    private TextView textSubtotal;
    private TextView textTotalFee;
    private MaterialButton buttonConfirmPayment;

    private int primaryBookingId;
    private Booking mainBooking;
    private List<Booking> groupBookings = new ArrayList<>();
    private final CourtRepository repository = CourtRepository.getInstance();
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        initViews();
        retrieveIntentData();
        setupListeners();
        loadBookingData();
    }

    private void initViews() {
        buttonBack = findViewById(R.id.button_back);
        textBookingCode = findViewById(R.id.text_booking_code);
        textCustomerName = findViewById(R.id.text_customer_name);
        layoutBookedCourtsList = findViewById(R.id.layout_booked_courts_list);
        textSubtotal = findViewById(R.id.text_subtotal);
        textTotalFee = findViewById(R.id.text_total_fee);
        buttonConfirmPayment = findViewById(R.id.button_confirm_payment);
    }

    private void retrieveIntentData() {
        primaryBookingId = getIntent().getIntExtra("booking_id", -1);
    }

    private void setupListeners() {
        buttonBack.setOnClickListener(v -> finish());
        buttonConfirmPayment.setOnClickListener(v -> processPayment());
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

        populateUI();
    }

    private void populateUI() {
        // Set Codes and Names
        textBookingCode.setText("#BK-" + (1000 + mainBooking.getId()));
        textCustomerName.setText(mainBooking.getPlayerName());

        // Render Courts list
        layoutBookedCourtsList.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        double totalFee = 0.0;

        for (Booking b : groupBookings) {
            View courtItemView = inflater.inflate(R.layout.item_detail_booked_court, layoutBookedCourtsList, false);

            TextView textCourtName = courtItemView.findViewById(R.id.text_court_name);
            TextView textCourtTime = courtItemView.findViewById(R.id.text_court_time);

            TextView textCourtTotalCost = courtItemView.findViewById(R.id.text_court_total_cost);

            Court court = repository.getCourtById(b.getCourtId());
            String cName = court != null ? court.getName() : "Sân #" + b.getCourtId();


            textCourtName.setText(cName);
            textCourtTime.setText(b.getStartTime() + " - " + b.getEndTime() + " (" + formatDuration(b.getStartTime(), b.getEndTime()) + ")");

            textCourtTotalCost.setText(formatVnd(b.getFee()));

            totalFee += b.getFee();
            layoutBookedCourtsList.addView(courtItemView);
        }

        // Subtotal & Total
        textSubtotal.setText(formatVnd(totalFee));
        textTotalFee.setText(formatVnd(totalFee));
    }

    private void processPayment() {
        double totalFee = 0.0;
        // Update all related bookings to Hoàn thành status
        for (Booking b : groupBookings) {
            Booking updated = new Booking(
                    b.getId(),
                    b.getCourtId(),
                    b.getPlayerName(),
                    b.getDate(),
                    b.getStartTime(),
                    b.getEndTime(),
                    b.getFee(),
                    "Hoàn thành"
            );
            updated.setPhoneNumber(b.getPhoneNumber());
            updated.setNotes(b.getNotes());
            viewModel.updateBooking(updated);
            totalFee += b.getFee();
        }

        Toast.makeText(this, "Thanh toán thành công " + formatVnd(totalFee) + "!", Toast.LENGTH_LONG).show();
        android.content.Intent intent = new android.content.Intent(this, MainActivity.class);
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("select_tab", "bookings");
        startActivity(intent);
        finish();
    }

    private String formatDuration(String start, String end) {
        try {
            String[] startParts = start.split(":");
            String[] endParts = end.split(":");
            int startMin = Integer.parseInt(startParts[0]) * 60 + Integer.parseInt(startParts[1]);
            int endMin = Integer.parseInt(endParts[0]) * 60 + Integer.parseInt(endParts[1]);
            int diff = endMin - startMin;
            double hrs = diff / 60.0;
            if (hrs == (int) hrs) {
                return (int) hrs + " Giờ";
            } else {
                return String.format("%.1f Giờ", hrs);
            }
        } catch (Exception e) {
            return "1 Giờ";
        }
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
