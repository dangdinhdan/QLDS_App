package com.example.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.model.Booking;
import com.example.mobile.model.Court;
import com.example.mobile.model.CourtStatus;
import com.example.mobile.view.BookingAdapter;
import com.example.mobile.view.CourtAdapter;
import com.example.mobile.viewmodel.MainViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CourtAdapter.OnCourtActionListener, BookingAdapter.OnBookingActionListener {

    private MainViewModel viewModel;

    // View toggles for Bottom Nav
    private View scrollHome;
    private View layoutCourtsTab;
    private View layoutBookingsTab;
    private View scrollStatsTab;

    // Dashboard Views (Home Tab)
    private TextView textTodayRevenue;
    private TextView textBookingsCount;
    private TextView textActiveCourtsRatio;
    private LinearLayout layoutQuickCourtsContainer;
    private LinearLayout layoutRecentActivities;

    // Recyclers and adapters
    private RecyclerView recyclerCourts;
    private RecyclerView recyclerBookings;
    private CourtAdapter courtAdapter;
    private BookingAdapter bookingAdapter;

    // Stats Views
    private TextView textStatsOccupancy;
    private ProgressBar progressStatsOccupancy;
    private TextView textStatsPopularCourt;
    private TextView textStatsRevenue;

    // Keep cached lists of data
    private List<Court> cachedCourts = new ArrayList<>();
    private List<Booking> cachedBookings = new ArrayList<>();

    // Search and Filter in Courts Tab
    private EditText editSearchCourts;
    private TextView chipFilterAll;
    private TextView chipFilterEmpty;
    private TextView chipFilterInUse;
    private TextView chipFilterBooked;
    private TextView chipFilterMaintenance;
    private String selectedFilter = "Tất cả";
    private String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate ViewModel
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        initViews();
        setupNavigation();
        setupRecyclerViews();
        observeViewModel();
        setupActions();

        // Initialize active tab state and FAB action
        showTab(scrollHome);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.refreshData();
        }
    }

    private void initViews() {
        // Tabs content
        scrollHome = findViewById(R.id.scroll_home);
        layoutCourtsTab = findViewById(R.id.layout_courts_tab);
        layoutBookingsTab = findViewById(R.id.layout_bookings_tab);
        scrollStatsTab = findViewById(R.id.scroll_stats_tab);

        // Dashboard metrics
        textTodayRevenue = findViewById(R.id.text_today_revenue);
        textBookingsCount = findViewById(R.id.text_bookings_count);
        textActiveCourtsRatio = findViewById(R.id.text_active_courts_ratio);
        layoutQuickCourtsContainer = findViewById(R.id.layout_quick_courts_container);
        layoutRecentActivities = findViewById(R.id.layout_recent_activities);

        // Recyclers
        recyclerCourts = findViewById(R.id.recycler_courts);
        recyclerBookings = findViewById(R.id.recycler_bookings);

        // Stats elements
        textStatsOccupancy = findViewById(R.id.text_stats_occupancy);
        progressStatsOccupancy = findViewById(R.id.progress_stats_occupancy);
        textStatsPopularCourt = findViewById(R.id.text_stats_popular_court);
        textStatsRevenue = findViewById(R.id.text_stats_revenue);

        // Search & Filter in Courts Tab
        editSearchCourts = findViewById(R.id.edit_search_courts);
        chipFilterAll = findViewById(R.id.chip_filter_all);
        chipFilterEmpty = findViewById(R.id.chip_filter_empty);
        chipFilterInUse = findViewById(R.id.chip_filter_in_use);
        chipFilterBooked = findViewById(R.id.chip_filter_booked);
        chipFilterMaintenance = findViewById(R.id.chip_filter_maintenance);
    }

    private void setupNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                showTab(scrollHome);
                return true;
            } else if (itemId == R.id.navigation_courts) {
                showTab(layoutCourtsTab);
                return true;
            } else if (itemId == R.id.navigation_bookings) {
                showTab(layoutBookingsTab);
                return true;
            } else if (itemId == R.id.navigation_stats) {
                showTab(scrollStatsTab);
                return true;
            }
            return false;
        });

        // "Xem tất cả" redirect link to courts tab
        findViewById(R.id.text_view_all_courts).setOnClickListener(v -> {
            bottomNavigation.setSelectedItemId(R.id.navigation_courts);
        });
    }

    private void showTab(View activeTab) {
        scrollHome.setVisibility(View.GONE);
        layoutCourtsTab.setVisibility(View.GONE);
        layoutBookingsTab.setVisibility(View.GONE);
        scrollStatsTab.setVisibility(View.GONE);

        activeTab.setVisibility(View.VISIBLE);

        FloatingActionButton fab = findViewById(R.id.fab_add_booking);
        if (fab != null) {
            if (activeTab == layoutCourtsTab) {
                fab.setVisibility(View.VISIBLE);
                fab.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, AddCourtActivity.class);
                    startActivity(intent);
                });
            } else if (activeTab == layoutBookingsTab) {
                fab.setVisibility(View.VISIBLE);
                fab.setOnClickListener(v -> showAddBookingDialog(null));
            } else if (activeTab == scrollHome) {
                fab.setVisibility(View.VISIBLE);
                fab.setOnClickListener(v -> showAddBookingDialog(null));
            } else {
                fab.setVisibility(View.GONE);
            }
        }
    }

    private void setupRecyclerViews() {
        // Courts list config
        recyclerCourts.setLayoutManager(new LinearLayoutManager(this));
        courtAdapter = new CourtAdapter(this);
        recyclerCourts.setAdapter(courtAdapter);

        // Bookings list config
        recyclerBookings.setLayoutManager(new LinearLayoutManager(this));
        bookingAdapter = new BookingAdapter(this);
        recyclerBookings.setAdapter(bookingAdapter);
    }

    private void setupActions() {
        // Notifications placeholder
        ImageButton buttonNotifications = findViewById(R.id.button_notifications);
        buttonNotifications.setOnClickListener(v -> 
            Toast.makeText(MainActivity.this, "Không có thông báo mới", Toast.LENGTH_SHORT).show()
        );

        // Add booking FAB action
        FloatingActionButton fabAddBooking = findViewById(R.id.fab_add_booking);
        fabAddBooking.setOnClickListener(v -> showAddBookingDialog(null));

        // Setup new layout bookings tab actions
        View addBookingTop = findViewById(R.id.button_add_booking_top);
        if (addBookingTop != null) {
            addBookingTop.setOnClickListener(v -> showAddBookingDialog(null));
        }

        View btnRequests = findViewById(R.id.button_view_requests);
        if (btnRequests != null) {
            btnRequests.setOnClickListener(v -> 
                Toast.makeText(MainActivity.this, "Đang mở danh sách yêu cầu chờ duyệt...", Toast.LENGTH_SHORT).show()
            );
        }

        View btnReports = findViewById(R.id.button_view_reports);
        if (btnReports != null) {
            btnReports.setOnClickListener(v -> 
                Toast.makeText(MainActivity.this, "Đang xuất báo cáo thống kê đặt sân...", Toast.LENGTH_SHORT).show()
            );
        }

        setupCalendarStrip();

        // Search listener
        editSearchCourts.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().trim();
                filterAndApplyCourts();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Filter chips listeners
        chipFilterAll.setOnClickListener(v -> selectFilterChip("Tất cả"));
        chipFilterEmpty.setOnClickListener(v -> selectFilterChip("Trống"));
        chipFilterInUse.setOnClickListener(v -> selectFilterChip("Đang dùng"));
        chipFilterBooked.setOnClickListener(v -> selectFilterChip("Đã đặt"));
        chipFilterMaintenance.setOnClickListener(v -> selectFilterChip("Bảo trì"));
    }

    private void setupCalendarStrip() {
        com.google.android.material.card.MaterialCardView day23 = findViewById(R.id.calendar_day_23);
        com.google.android.material.card.MaterialCardView day24 = findViewById(R.id.calendar_day_24);
        com.google.android.material.card.MaterialCardView day25 = findViewById(R.id.calendar_day_25);
        com.google.android.material.card.MaterialCardView day26 = findViewById(R.id.calendar_day_26);
        com.google.android.material.card.MaterialCardView day27 = findViewById(R.id.calendar_day_27);

        com.google.android.material.card.MaterialCardView[] days = {day23, day24, day25, day26, day27};
        int[] dayNums = {23, 24, 25, 26, 27};
        
        for (int i = 0; i < days.length; i++) {
            final int index = i;
            if (days[i] != null) {
                days[i].setOnClickListener(v -> {
                    // Update visual selection
                    for (int j = 0; j < days.length; j++) {
                        if (days[j] != null) {
                            if (j == index) {
                                days[j].setCardBackgroundColor(getResources().getColor(R.color.primary_container));
                                days[j].setStrokeWidth((int) (1.5 * getResources().getDisplayMetrics().density));
                                days[j].setStrokeColor(getResources().getColor(R.color.primary));
                            } else {
                                days[j].setCardBackgroundColor(getResources().getColor(R.color.white));
                                days[j].setStrokeWidth((int) (1 * getResources().getDisplayMetrics().density));
                                days[j].setStrokeColor(getResources().getColor(R.color.border_gray));
                            }
                        }
                    }
                    
                    // Filter bookings or show Toast
                    if (dayNums[index] == 24) {
                        bookingAdapter.setData(cachedBookings, cachedCourts);
                        TextView badge = findViewById(R.id.badge_booking_count);
                        if (badge != null) badge.setText(cachedBookings.size() + " lượt đặt");
                    } else {
                        bookingAdapter.setData(new ArrayList<>(), cachedCourts);
                        TextView badge = findViewById(R.id.badge_booking_count);
                        if (badge != null) badge.setText("0 lượt đặt");
                    }
                    Toast.makeText(MainActivity.this, "Đang xem lịch ngày " + dayNums[index] + "/05", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

    private void observeViewModel() {
        // Observe courts list
        viewModel.getCourts().observe(this, courts -> {
            if (courts != null) {
                cachedCourts = courts;
                updateCourtsUI(courts);
            }
        });

        // Observe bookings list
        viewModel.getBookings().observe(this, bookings -> {
            if (bookings != null) {
                cachedBookings = bookings;
                updateBookingsUI(bookings);
            }
        });

        // Observe Occupancy rate
        viewModel.getOccupancyRate().observe(this, rate -> {
            if (rate != null) {
                String rateStr = String.format("%.1f%%", rate);
                textStatsOccupancy.setText(rateStr);
                progressStatsOccupancy.setProgress((int) Math.round(rate));
            }
        });

        // Observe total revenue
        viewModel.getTotalRevenue().observe(this, revenue -> {
            if (revenue != null) {
                String revStr = String.format("$%.2f", revenue);
                textTodayRevenue.setText(revStr);
                textStatsRevenue.setText(revStr);
            }
        });

        // Observe active courts count
        viewModel.getActiveCourtsCount().observe(this, activeCount -> {
            if (activeCount != null) {
                updateActiveCourtsRatio(activeCount);
            }
        });

        // Observe most popular court
        viewModel.getMostPopularCourt().observe(this, popularCourt -> {
            if (popularCourt != null) {
                textStatsPopularCourt.setText(popularCourt);
            }
        });
    }

    private void updateCourtsUI(List<Court> courts) {
        // Rebuild filtered courts on recycler
        filterAndApplyCourts();

        // Update active courts count on metrics card
        int activeCount = 0;
        for (Court c : courts) {
            if (c.getStatus() == CourtStatus.IN_USE) {
                activeCount++;
            }
        }
        updateActiveCourtsRatio(activeCount);

        // Rebuild Quick Court chips horizontal layout
        layoutQuickCourtsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Court court : courts) {
            View chipView = inflater.inflate(R.layout.item_quick_court_chip, layoutQuickCourtsContainer, false);
            
            TextView textName = chipView.findViewById(R.id.text_quick_court_name);
            TextView textStatus = chipView.findViewById(R.id.text_quick_court_status);
            View statusDot = chipView.findViewById(R.id.view_status_dot);

            textName.setText(court.getName());
            
            // Set status string & dot background
            switch (court.getStatus()) {
                case IN_USE:
                    textStatus.setText("Đang dùng");
                    textStatus.setTextColor(getResources().getColor(R.color.secondary));
                    statusDot.setBackgroundResource(R.drawable.badge_in_use);
                    break;
                case BOOKED:
                    textStatus.setText("Đã đặt");
                    textStatus.setTextColor(getResources().getColor(R.color.secondary));
                    statusDot.setBackgroundResource(R.drawable.badge_booked);
                    break;
                case EMPTY:
                    textStatus.setText("Trống");
                    textStatus.setTextColor(getResources().getColor(R.color.primary));
                    statusDot.setBackgroundResource(R.drawable.badge_empty);
                    break;
                case MAINTENANCE:
                    textStatus.setText("Bảo trì");
                    textStatus.setTextColor(getResources().getColor(R.color.black));
                    statusDot.setBackgroundResource(R.drawable.badge_maintenance);
                    break;
            }

            // Quick interaction: click chip to set status directly
            chipView.setOnClickListener(v -> showChangeStatusDialog(court));

            layoutQuickCourtsContainer.addView(chipView);
        }
    }

    private void updateBookingsUI(List<Booking> bookings) {
        // Update main bookings tab recycler
        bookingAdapter.setData(bookings, cachedCourts);

        // Update dashboard metrics count
        textBookingsCount.setText(String.valueOf(bookings.size()));

        // Update badge booking count on bookings tab
        TextView badgeBookingCount = findViewById(R.id.badge_booking_count);
        if (badgeBookingCount != null) {
            badgeBookingCount.setText(bookings.size() + " lượt đặt");
        }

        // Update Recent Activities list on home tab (limit to latest 5 bookings)
        layoutRecentActivities.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        
        int count = 0;
        // Iterate backwards to show latest bookings first
        for (int i = bookings.size() - 1; i >= 0 && count < 5; i--) {
            Booking booking = bookings.get(i);
            View activityView = inflater.inflate(R.layout.item_recent_activity, layoutRecentActivities, false);

            TextView textPlayer = activityView.findViewById(R.id.text_activity_player);
            TextView textDetails = activityView.findViewById(R.id.text_activity_details);
            TextView badgeFee = activityView.findViewById(R.id.badge_activity_fee);

            textPlayer.setText(booking.getPlayerName());
            
            String courtName = "Court #" + booking.getCourtId();
            for (Court c : cachedCourts) {
                if (c.getId() == booking.getCourtId()) {
                    courtName = c.getName();
                    break;
                }
            }
            textDetails.setText(String.format("%s • %s - %s", courtName, booking.getStartTime(), booking.getEndTime()));
            badgeFee.setText(String.format("$%.2f", booking.getFee()));

            layoutRecentActivities.addView(activityView);
            count++;
        }
    }

    private void updateActiveCourtsRatio(int activeCount) {
        int totalCourts = cachedCourts.isEmpty() ? 5 : cachedCourts.size();
        textActiveCourtsRatio.setText(String.format("%d / %d", activeCount, totalCourts));
    }

    // ================= Adapters Action Callbacks =================

    @Override
    public void onEditCourt(Court court) {
        Intent intent = new Intent(MainActivity.this, AddCourtActivity.class);
        intent.putExtra("court_id", court.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteCourt(Court court) {
        showDeleteCourtDialog(court);
    }

    private void showEditCourtDialog(Court court) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_court, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        EditText editCourtName = dialogView.findViewById(R.id.edit_court_name);
        Spinner spinnerSurface = dialogView.findViewById(R.id.spinner_court_surface);
        Spinner spinnerStatus = dialogView.findViewById(R.id.spinner_court_status);

        editCourtName.setText(court.getName());

        // Surface spinner
        String[] surfaces = {"Cứng", "Thảm"};
        ArrayAdapter<String> surfaceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, surfaces);
        surfaceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSurface.setAdapter(surfaceAdapter);
        if (court.getSurfaceType() != null && court.getSurfaceType().equalsIgnoreCase("Thảm")) {
            spinnerSurface.setSelection(1);
        } else {
            spinnerSurface.setSelection(0);
        }

        // Status spinner
        String[] statuses = {"Trống", "Đang sử dụng", "Đã đặt", "Bảo trì"};
        CourtStatus[] statusValues = {CourtStatus.EMPTY, CourtStatus.IN_USE, CourtStatus.BOOKED, CourtStatus.MAINTENANCE};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        int selectedStatusIndex = 0;
        for (int i = 0; i < statusValues.length; i++) {
            if (court.getStatus() == statusValues[i]) {
                selectedStatusIndex = i;
                break;
            }
        }
        spinnerStatus.setSelection(selectedStatusIndex);

        // Cancel button
        dialogView.findViewById(R.id.button_dialog_cancel).setOnClickListener(v -> dialog.dismiss());

        // Confirm button
        dialogView.findViewById(R.id.button_dialog_confirm).setOnClickListener(v -> {
            String name = editCourtName.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                editCourtName.setError("Vui lòng nhập tên sân");
                return;
            }

            String surface = surfaces[spinnerSurface.getSelectedItemPosition()];
            CourtStatus status = statusValues[spinnerStatus.getSelectedItemPosition()];

            viewModel.updateCourt(court.getId(), name, surface, status);
            Toast.makeText(MainActivity.this, "Cập nhật thông tin sân thành công!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showDeleteCourtDialog(Court court) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa sân")
                .setMessage("Bạn có chắc chắn muốn xóa " + court.getName() + "? Tất cả lịch đặt liên quan đến sân này cũng sẽ bị xóa.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    viewModel.deleteCourt(court.getId());
                    if (courtAdapter != null) {
                        courtAdapter.setSelectedCourtId(-1); // Clear selection
                    }
                    Toast.makeText(MainActivity.this, "Đã xóa sân thành công!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onCancelBooking(Booking booking) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa lịch đặt sân")
                .setMessage("Bạn có chắc chắn muốn xóa lịch đặt sân của " + booking.getPlayerName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    viewModel.deleteBooking(booking.getId());
                    Toast.makeText(MainActivity.this, "Đã xóa lịch đặt sân thành công!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Quay lại", null)
                .show();
    }

    @Override
    public void onStatusChanged(Booking booking) {
        viewModel.refreshData();
        Toast.makeText(this, "Đã cập nhật trạng thái đặt sân!", Toast.LENGTH_SHORT).show();
    }

    // ================= Business Dialogs Managers =================

    private void showAddBookingDialog(Court preselectedCourt) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_booking, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        // Bind Dialog Views
        Spinner spinnerCourts = dialogView.findViewById(R.id.spinner_courts);
        EditText editPlayerName = dialogView.findViewById(R.id.edit_player_name);
        EditText editStartTime = dialogView.findViewById(R.id.edit_start_time);
        EditText editEndTime = dialogView.findViewById(R.id.edit_end_time);

        // Populate spinner with courts names
        List<String> courtNames = new ArrayList<>();
        List<Integer> courtIds = new ArrayList<>();
        int selectPosition = 0;

        for (int i = 0; i < cachedCourts.size(); i++) {
            Court c = cachedCourts.get(i);
            courtNames.add(c.getName() + " ($" + c.getHourlyRate() + "/hr)");
            courtIds.add(c.getId());

            if (preselectedCourt != null && c.getId() == preselectedCourt.getId()) {
                selectPosition = i;
            }
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courtNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourts.setAdapter(spinnerAdapter);
        if (!cachedCourts.isEmpty()) {
            spinnerCourts.setSelection(selectPosition);
        }

        // Cancel button click
        dialogView.findViewById(R.id.button_dialog_cancel).setOnClickListener(v -> dialog.dismiss());

        // Confirm button click
        dialogView.findViewById(R.id.button_dialog_confirm).setOnClickListener(v -> {
            String playerName = editPlayerName.getText().toString().trim();
            String startTime = editStartTime.getText().toString().trim();
            String endTime = editEndTime.getText().toString().trim();

            if (TextUtils.isEmpty(playerName)) {
                editPlayerName.setError("Vui lòng nhập tên người chơi");
                return;
            }

            if (!isValidTime(startTime)) {
                editStartTime.setError("Định dạng giờ không hợp lệ (HH:MM)");
                return;
            }

            if (!isValidTime(endTime)) {
                editEndTime.setError("Định dạng giờ không hợp lệ (HH:MM)");
                return;
            }

            if (spinnerCourts.getSelectedItemPosition() == Spinner.INVALID_POSITION) {
                Toast.makeText(MainActivity.this, "Không tìm thấy sân nào để đặt", Toast.LENGTH_SHORT).show();
                return;
            }

            // Calculate duration and fee
            int selectedIndex = spinnerCourts.getSelectedItemPosition();
            Court court = cachedCourts.get(selectedIndex);

            double duration = calculateDuration(startTime, endTime);
            if (duration <= 0) {
                editEndTime.setError("Giờ kết thúc phải lớn hơn giờ bắt đầu");
                return;
            }

            double fee = duration * court.getHourlyRate();

            // Create Booking
            Booking booking = new Booking(0, court.getId(), playerName, "2026-06-11", startTime, endTime, fee);
            viewModel.addBooking(booking);

            Toast.makeText(MainActivity.this, "Đặt sân thành công!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showChangeStatusDialog(Court court) {
        String[] statuses = {"EMPTY (Trống)", "BOOKED (Đã đặt)", "IN USE (Đang dùng)", "MAINTENANCE (Bảo trì)"};
        CourtStatus[] enumValues = {CourtStatus.EMPTY, CourtStatus.BOOKED, CourtStatus.IN_USE, CourtStatus.MAINTENANCE};
        
        int checkedItem = 0;
        for (int i = 0; i < enumValues.length; i++) {
            if (court.getStatus() == enumValues[i]) {
                checkedItem = i;
                break;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Cập nhật trạng thái - " + court.getName())
                .setSingleChoiceItems(statuses, checkedItem, (dialog, which) -> {
                    viewModel.updateCourtStatus(court.getId(), enumValues[which]);
                    Toast.makeText(MainActivity.this, "Cập nhật trạng thái thành công!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Helper method to validate HH:MM time format
    private boolean isValidTime(String time) {
        if (TextUtils.isEmpty(time)) return false;
        String[] parts = time.split(":");
        if (parts.length != 2) return false;
        try {
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            return hour >= 0 && hour < 24 && minute >= 0 && minute < 60;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Helper method to calculate hours between start & end times
    private double calculateDuration(String start, String end) {
        try {
            String[] startParts = start.split(":");
            String[] endParts = end.split(":");
            double startHour = Integer.parseInt(startParts[0]) + Integer.parseInt(startParts[1]) / 60.0;
            double endHour = Integer.parseInt(endParts[0]) + Integer.parseInt(endParts[1]) / 60.0;
            return endHour - startHour;
        } catch (Exception e) {
            return 0.0;
        }
    }

    private void showAddCourtDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_court, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        TextView textTitle = dialogView.findViewById(R.id.text_dialog_title);
        EditText editCourtName = dialogView.findViewById(R.id.edit_court_name);
        Spinner spinnerSurface = dialogView.findViewById(R.id.spinner_court_surface);
        Spinner spinnerStatus = dialogView.findViewById(R.id.spinner_court_status);

        if (textTitle != null) {
            textTitle.setText("Thêm sân mới");
        }

        // Surface spinner
        String[] surfaces = {"Cứng", "Thảm"};
        ArrayAdapter<String> surfaceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, surfaces);
        surfaceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSurface.setAdapter(surfaceAdapter);
        spinnerSurface.setSelection(0); // Default to Cứng

        // Status spinner
        String[] statuses = {"Trống", "Đang sử dụng", "Đã đặt", "Bảo trì"};
        CourtStatus[] statusValues = {CourtStatus.EMPTY, CourtStatus.IN_USE, CourtStatus.BOOKED, CourtStatus.MAINTENANCE};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);
        spinnerStatus.setSelection(0); // Default to EMPTY (Trống)

        // Cancel button
        dialogView.findViewById(R.id.button_dialog_cancel).setOnClickListener(v -> dialog.dismiss());

        // Confirm button
        MaterialButton buttonConfirm = dialogView.findViewById(R.id.button_dialog_confirm);
        if (buttonConfirm != null) {
            buttonConfirm.setText("Thêm sân");
        }
        dialogView.findViewById(R.id.button_dialog_confirm).setOnClickListener(v -> {
            String name = editCourtName.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                editCourtName.setError("Vui lòng nhập tên sân");
                return;
            }

            String surface = surfaces[spinnerSurface.getSelectedItemPosition()];
            CourtStatus status = statusValues[spinnerStatus.getSelectedItemPosition()];

            // Use default images
            String defaultImage = "https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=500";
            if (surface.equalsIgnoreCase("Thảm")) {
                defaultImage = "https://images.unsplash.com/photo-1595435934249-5df7ed86e1c0?w=500";
            }

            Court newCourt = new Court(0, name, status, 15.0, surface, defaultImage, null);
            viewModel.addCourt(newCourt);

            Toast.makeText(MainActivity.this, "Thêm sân mới thành công!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void selectFilterChip(String filter) {
        selectedFilter = filter;

        // Reset all chips to unselected
        TextView[] chips = {chipFilterAll, chipFilterEmpty, chipFilterInUse, chipFilterBooked, chipFilterMaintenance};
        for (TextView chip : chips) {
            if (chip != null) {
                chip.setBackgroundResource(R.drawable.bg_chip_unselected);
                chip.setTextColor(getResources().getColor(R.color.secondary));
            }
        }

        // Set selected chip
        TextView selected = null;
        switch (filter) {
            case "Tất cả": selected = chipFilterAll; break;
            case "Trống": selected = chipFilterEmpty; break;
            case "Đang dùng": selected = chipFilterInUse; break;
            case "Đã đặt": selected = chipFilterBooked; break;
            case "Bảo trì": selected = chipFilterMaintenance; break;
        }

        if (selected != null) {
            selected.setBackgroundResource(R.drawable.bg_chip_selected);
            selected.setTextColor(getResources().getColor(R.color.on_primary_container));
        }

        filterAndApplyCourts();
    }

    private void filterAndApplyCourts() {
        if (cachedCourts == null) return;
        List<Court> filtered = new ArrayList<>();
        for (Court court : cachedCourts) {
            // Apply status filter
            boolean matchesStatus = false;
            if (selectedFilter.equals("Tất cả")) {
                matchesStatus = true;
            } else if (selectedFilter.equals("Trống") && court.getStatus() == CourtStatus.EMPTY) {
                matchesStatus = true;
            } else if (selectedFilter.equals("Đang dùng") && court.getStatus() == CourtStatus.IN_USE) {
                matchesStatus = true;
            } else if (selectedFilter.equals("Đã đặt") && court.getStatus() == CourtStatus.BOOKED) {
                matchesStatus = true;
            } else if (selectedFilter.equals("Bảo trì") && court.getStatus() == CourtStatus.MAINTENANCE) {
                matchesStatus = true;
            }

            // Apply search filter
            boolean matchesSearch = true;
            if (!searchQuery.isEmpty()) {
                matchesSearch = court.getName().toLowerCase().contains(searchQuery.toLowerCase());
            }

            if (matchesStatus && matchesSearch) {
                filtered.add(court);
            }
        }
        courtAdapter.setData(filtered, cachedBookings);
    }
}
