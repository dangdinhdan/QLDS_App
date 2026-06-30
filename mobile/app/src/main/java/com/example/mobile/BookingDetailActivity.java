package com.example.mobile;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

public class BookingDetailActivity extends AppCompatActivity {

    private ImageButton buttonBack;
    private ImageButton buttonShare;
    private ImageButton buttonProfile;

    private TextView textCustomerName;
    private TextView textPhoneNumber;
    private TextView textBookingDate;

    private TextView textCourtCountBadge;
    private LinearLayout layoutBookedCourtsList;

    private TextView textBookingNotes;

    private TextView textSubtotal;
    private TextView textTax;
    private TextView textTotalFee;
    private TextView textStatusBadge;
    private TextView textPaymentMethod;

    private MaterialButton buttonPayBooking;
    private MaterialButton buttonEditBooking;
    private MaterialButton buttonCancelBooking;
    private View layoutBottomActions;

    private int primaryBookingId;
    private Booking mainBooking;
    private List<Booking> groupBookings = new ArrayList<>();
    private final CourtRepository repository = CourtRepository.getInstance();
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        initViews();
        retrieveIntentData();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBookingData();
    }

    private void initViews() {
        buttonBack = findViewById(R.id.button_back);
        buttonShare = findViewById(R.id.button_share);
        buttonProfile = findViewById(R.id.button_profile);

        textCustomerName = findViewById(R.id.text_customer_name);
        textPhoneNumber = findViewById(R.id.text_phone_number);
        textBookingDate = findViewById(R.id.text_booking_date);

        textCourtCountBadge = findViewById(R.id.text_court_count_badge);
        layoutBookedCourtsList = findViewById(R.id.layout_booked_courts_list);

        textBookingNotes = findViewById(R.id.text_booking_notes);

        textSubtotal = findViewById(R.id.text_subtotal);
        textTax = findViewById(R.id.text_tax);
        textTotalFee = findViewById(R.id.text_total_fee);
        textStatusBadge = findViewById(R.id.text_status_badge);
        // textPaymentMethod = findViewById(R.id.text_payment_method);

        buttonPayBooking = findViewById(R.id.button_pay_booking);
        buttonEditBooking = findViewById(R.id.button_edit_booking);
        buttonCancelBooking = findViewById(R.id.button_cancel_booking);
        layoutBottomActions = findViewById(R.id.layout_bottom_actions);
    }

    private void retrieveIntentData() {
        primaryBookingId = getIntent().getIntExtra("booking_id", -1);
    }

    private void setupListeners() {
        buttonBack.setOnClickListener(v -> finish());

        buttonShare.setOnClickListener(v -> 
            Toast.makeText(this, "Chia sẻ thông tin đặt sân thành công!", Toast.LENGTH_SHORT).show()
        );

        buttonProfile.setOnClickListener(v -> 
            Toast.makeText(this, "Quản trị viên đang xem chi tiết", Toast.LENGTH_SHORT).show()
        );

        buttonPayBooking.setOnClickListener(v -> {
            Intent intent = new Intent(BookingDetailActivity.this, PaymentActivity.class);
            intent.putExtra("booking_id", primaryBookingId);
            startActivity(intent);
        });

        buttonEditBooking.setOnClickListener(v -> {
            Intent intent = new Intent(BookingDetailActivity.this, EditBookingActivity.class);
            intent.putExtra("booking_id", primaryBookingId);
            startActivity(intent);
        });

        buttonCancelBooking.setOnClickListener(v -> showCancelBookingDialog());
    }

    private void loadBookingData() {
        if (primaryBookingId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin đặt sân!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Find main booking
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
        // Customer Info Card
        textCustomerName.setText(mainBooking.getPlayerName());
        textPhoneNumber.setText(mainBooking.getPhoneNumber() != null ? mainBooking.getPhoneNumber() : "Không có SĐT");
        textBookingDate.setText(formatDbDateToDisplay(mainBooking.getDate()));

        // Booked Courts Card
        textCourtCountBadge.setText(groupBookings.size() + " Sân");
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
            double hourlyRate = court != null ? court.getHourlyRate() : 150000.0;

            textCourtName.setText(cName);
            textCourtTime.setText(b.getStartTime() + " - " + b.getEndTime() + " (" + formatDuration(b.getStartTime(), b.getEndTime()) + ")");
            textCourtTotalCost.setText(formatVnd(b.getFee()));

            totalFee += b.getFee();
            layoutBookedCourtsList.addView(courtItemView);
        }

        // Note Card
        if (mainBooking.getNotes() != null && !mainBooking.getNotes().isEmpty()) {
            textBookingNotes.setText("\"" + mainBooking.getNotes() + "\"");
        } else {
            textBookingNotes.setText("\"không có\"");
        }

        // Payment Summary Card
        textSubtotal.setText(formatVnd(totalFee));
        textTax.setText(formatVnd(0.0));
        textTotalFee.setText(formatVnd(totalFee));

        // Format Status Badge
        String status = mainBooking.getStatus();
        if (status == null) status = "Đã đặt";
        textStatusBadge.setText(status);

        switch (status) {
            case "Đang sử dụng":
                textStatusBadge.setText("Đang sử dụng");
                textStatusBadge.setBackgroundResource(R.drawable.bg_chip_selected); // Primary yellow
                textStatusBadge.setTextColor(Color.parseColor("#647400"));
                break;
            case "Hoàn thành":
            case "Đã thanh toán":
                textStatusBadge.setText("Đã thanh toán");
                textStatusBadge.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DFFF00"))); // Pickleball Neon
                textStatusBadge.setTextColor(Color.parseColor("#191E00"));
                break;
            case "Đã đặt":
                textStatusBadge.setText("Đã đặt");
                textStatusBadge.setBackgroundResource(R.drawable.badge_booked); // Navy
                textStatusBadge.setTextColor(Color.parseColor("#4F6073"));
                break;
            case "Đã hủy":
                textStatusBadge.setText("Đã hủy");
                textStatusBadge.setBackgroundResource(R.drawable.badge_maintenance); // Error light red/gray
                textStatusBadge.setTextColor(Color.parseColor("#BA1A1A"));
                break;
        }

        if (textPaymentMethod != null) {
            textPaymentMethod.setText("Tiền mặt");
        }

        if ("Hoàn thành".equalsIgnoreCase(status) || "Đã thanh toán".equalsIgnoreCase(status)) {
            buttonPayBooking.setVisibility(View.GONE);
            buttonEditBooking.setVisibility(View.GONE);
            buttonCancelBooking.setVisibility(View.GONE);
            if (layoutBottomActions != null) {
                layoutBottomActions.setVisibility(View.GONE);
            }
        } else {
            buttonPayBooking.setVisibility(View.VISIBLE);
            buttonEditBooking.setVisibility(View.VISIBLE);
            buttonCancelBooking.setVisibility(View.VISIBLE);
            if (layoutBottomActions != null) {
                layoutBottomActions.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showEditBookingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa thông tin đặt sân");

        // Dynamic dialog view in code
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 30, 40, 20);

        // Edit Name Label & Field
        TextView labelName = new TextView(this);
        labelName.setText("Tên khách hàng:");
        labelName.setTextSize(14);
        labelName.setPadding(0, 10, 0, 10);
        layout.addView(labelName);

        final EditText editName = new EditText(this);
        editName.setText(mainBooking.getPlayerName());
        editName.setHint("Nhập tên khách hàng");
        layout.addView(editName);

        // Edit Phone Label & Field
        TextView labelPhone = new TextView(this);
        labelPhone.setText("Số điện thoại:");
        labelPhone.setTextSize(14);
        labelPhone.setPadding(0, 20, 0, 10);
        layout.addView(labelPhone);

        final EditText editPhone = new EditText(this);
        editPhone.setText(mainBooking.getPhoneNumber());
        editPhone.setHint("Nhập số điện thoại");
        layout.addView(editPhone);

        // Status Label & RadioGroup
        TextView labelStatus = new TextView(this);
        labelStatus.setText("Trạng thái:");
        labelStatus.setTextSize(14);
        labelStatus.setPadding(0, 20, 0, 10);
        layout.addView(labelStatus);

        final RadioGroup radioGroupStatus = new RadioGroup(this);
        radioGroupStatus.setOrientation(RadioGroup.VERTICAL);

        String[] statuses = {"Đã đặt", "Đang sử dụng", "Hoàn thành", "Đã hủy"};
        for (String s : statuses) {
            RadioButton rb = new RadioButton(this);
            rb.setText(s);
            rb.setId(View.generateViewId());
            if (s.equals(mainBooking.getStatus())) {
                rb.setChecked(true);
            }
            radioGroupStatus.addView(rb);
        }
        layout.addView(radioGroupStatus);

        builder.setView(layout);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newName = editName.getText().toString().trim();
            String newPhone = editPhone.getText().toString().trim();

            if (TextUtils.isEmpty(newName)) {
                Toast.makeText(this, "Tên khách hàng không được để trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            int checkedId = radioGroupStatus.getCheckedRadioButtonId();
            RadioButton selectedRb = radioGroupStatus.findViewById(checkedId);
            String newStatus = selectedRb != null ? selectedRb.getText().toString() : mainBooking.getStatus();

            // Save updates to all group bookings
            for (Booking b : groupBookings) {
                Booking updated = new Booking(b.getId(), b.getCourtId(), newName, b.getDate(), b.getStartTime(), b.getEndTime(), b.getFee(), newStatus);
                updated.setPhoneNumber(newPhone);
                viewModel.updateBooking(updated);
            }

            Toast.makeText(this, "Cập nhật đặt sân thành công!", Toast.LENGTH_SHORT).show();
            loadBookingData(); // Reload UI
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showCancelBookingDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Hủy đặt sân")
                .setMessage("Bạn có chắc chắn muốn hủy lịch đặt sân của " + mainBooking.getPlayerName() + "? Thao tác này sẽ xóa tất cả các sân trong ngày của khách hàng.")
                .setPositiveButton("Xóa lịch đặt", (dialog, which) -> {
                    for (Booking b : groupBookings) {
                        viewModel.deleteBooking(b.getId());
                    }
                    Toast.makeText(BookingDetailActivity.this, "Đã xóa lịch đặt sân thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Quay lại", null)
                .show();
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

    private String formatDbDateToDisplay(String dbDate) {
        try {
            if (dbDate == null || !dbDate.contains("-")) return dbDate;
            String[] parts = dbDate.split("-");
            return parts[2] + "/" + parts[1] + "/" + parts[0];
        } catch (Exception e) {
            return dbDate;
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

    private void disableAndBlackoutButton(MaterialButton button) {
        if (button == null) return;
        button.setEnabled(false);
        button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#444444")));
        button.setTextColor(Color.parseColor("#888888"));
        button.setIconTint(ColorStateList.valueOf(Color.parseColor("#888888")));
        button.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#444444")));
    }
}
