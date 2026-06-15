package com.example.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.model.Booking;
import com.example.mobile.model.Court;
import com.example.mobile.model.CourtStatus;
import com.example.mobile.view.BookingAdapter;
import com.example.mobile.view.CourtAdapter;
import com.example.mobile.model.PriceTable;
import com.example.mobile.view.PriceTableAdapter;
import com.example.mobile.viewmodel.MainViewModel;
import android.view.ViewGroup;
import android.util.Log;
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
    private View layoutPricingTab;
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
    private RecyclerView recyclerPriceTables;
    private CourtAdapter courtAdapter;
    private BookingAdapter bookingAdapter;
    private PriceTableAdapter priceTableAdapter;

    // Stats Views
    private TextView textStatsOccupancy;
    private ProgressBar progressStatsOccupancy;
    private TextView textStatsPopularCourt;
    private TextView textStatsRevenue;

    // Keep cached lists of data
    private List<Court> cachedCourts = new ArrayList<>();
    private List<Booking> cachedBookings = new ArrayList<>();
    private List<PriceTable> cachedPriceTables = new ArrayList<>();

    // Search and Filter in Courts Tab
    private EditText editSearchCourts;
    private TextView chipFilterAll;
    private TextView chipFilterEmpty;
    private TextView chipFilterInUse;
    private TextView chipFilterBooked;
    private TextView chipFilterMaintenance;
    private String selectedFilter = "Tất cả";
    private String searchQuery = "";
    private EditText editSearchPriceTables;
    private String ptSearchQuery = "";

    // New Booking Timeline Views & Filter States
    private com.google.android.material.card.MaterialCardView cardFilterDate;
    private com.google.android.material.card.MaterialCardView cardFilterDuration;
    private TextView textFilterDate;
    private TextView textFilterDuration;
    private LinearLayout layoutTimelineTimeHeaders;
    private LinearLayout layoutTimelineCourtsColumn;
    private LinearLayout layoutTimelineRowsContainer;
    private com.google.android.material.button.MaterialButton buttonRefreshBookings;
    private com.google.android.material.button.MaterialButton buttonAddBookingNew;
    
    private String selectedBookingDate = "2026-06-11"; // Default date matching mock data seed
    private String selectedBookingDuration = "60 phút"; // Default duration

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
        layoutPricingTab = findViewById(R.id.layout_pricing_tab);
        scrollStatsTab = findViewById(R.id.scroll_stats_tab);

        // Dashboard metrics
        textTodayRevenue = findViewById(R.id.text_today_revenue);
        textBookingsCount = findViewById(R.id.text_bookings_count);
        textActiveCourtsRatio = findViewById(R.id.text_active_courts_ratio);
        layoutQuickCourtsContainer = findViewById(R.id.layout_quick_courts_container);
        layoutRecentActivities = findViewById(R.id.layout_recent_activities);

        // Recyclers
        recyclerCourts = findViewById(R.id.recycler_courts);
        recyclerPriceTables = findViewById(R.id.recycler_price_tables);

        // Stats elements
        textStatsOccupancy = findViewById(R.id.text_stats_occupancy);
        progressStatsOccupancy = findViewById(R.id.progress_stats_occupancy);
        textStatsPopularCourt = findViewById(R.id.text_stats_popular_court);
        textStatsRevenue = findViewById(R.id.text_stats_revenue);

        // Search & Filter in Courts Tab
        editSearchCourts = findViewById(R.id.edit_search_courts);
        editSearchPriceTables = findViewById(R.id.edit_search_price_tables);
        chipFilterAll = findViewById(R.id.chip_filter_all);
        chipFilterEmpty = findViewById(R.id.chip_filter_empty);
        chipFilterInUse = findViewById(R.id.chip_filter_in_use);
        chipFilterBooked = findViewById(R.id.chip_filter_booked);
        chipFilterMaintenance = findViewById(R.id.chip_filter_maintenance);

        // New Booking Timeline Views
        cardFilterDate = findViewById(R.id.card_filter_date);
        cardFilterDuration = findViewById(R.id.card_filter_duration);
        textFilterDate = findViewById(R.id.text_filter_date);
        textFilterDuration = findViewById(R.id.text_filter_duration);
        layoutTimelineTimeHeaders = findViewById(R.id.layout_timeline_time_headers);
        layoutTimelineCourtsColumn = findViewById(R.id.layout_timeline_courts_column);
        layoutTimelineRowsContainer = findViewById(R.id.layout_timeline_rows_container);
        buttonRefreshBookings = findViewById(R.id.button_refresh_bookings);
        buttonAddBookingNew = findViewById(R.id.button_add_booking_new);

        // Prepopulate filter text
        if (textFilterDate != null) {
            textFilterDate.setText(formatDbDateToDisplay(selectedBookingDate));
        }
        if (textFilterDuration != null) {
            textFilterDuration.setText(selectedBookingDuration);
        }
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
            } else if (itemId == R.id.navigation_pricing) {
                showTab(layoutPricingTab);
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
        layoutPricingTab.setVisibility(View.GONE);
        scrollStatsTab.setVisibility(View.GONE);

        activeTab.setVisibility(View.VISIBLE);
    }

    private void setupRecyclerViews() {
        // Courts list config
        recyclerCourts.setLayoutManager(new LinearLayoutManager(this));
        courtAdapter = new CourtAdapter(this);
        recyclerCourts.setAdapter(courtAdapter);

        // Pricing list config
        recyclerPriceTables.setLayoutManager(new LinearLayoutManager(this));
        priceTableAdapter = new PriceTableAdapter(new PriceTableAdapter.OnPriceTableActionListener() {
            @Override
            public void onEditPriceTable(PriceTable pt) {
                Intent intent = new Intent(MainActivity.this, CreatePriceTableActivity.class);
                intent.putExtra("price_table_id", pt.getId());
                startActivity(intent);
            }

            @Override
            public void onDetailPriceTable(PriceTable pt) {
                Intent intent = new Intent(MainActivity.this, PriceTableDetailActivity.class);
                intent.putExtra("price_table_id", pt.getId());
                startActivity(intent);
            }
        });
        recyclerPriceTables.setAdapter(priceTableAdapter);
    }

    private void setupActions() {
        // Profile picture action (Logout Popup Menu)
        ImageView imageProfile = findViewById(R.id.image_profile);
        if (imageProfile != null) {
            imageProfile.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(MainActivity.this, imageProfile);
                popup.getMenu().add("Thông tin tài khoản");
                popup.getMenu().add("Đăng xuất");
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getTitle().equals("Thông tin tài khoản")) {
                        Toast.makeText(MainActivity.this, "Tài khoản: admin (Quản trị viên)", Toast.LENGTH_SHORT).show();
                        return true;
                    } else if (item.getTitle().equals("Đăng xuất")) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Đăng xuất")
                                .setMessage("Bạn có chắc chắn muốn đăng xuất tài khoản?")
                                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(MainActivity.this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();
                                })
                                .setNegativeButton("Hủy", null)
                                .show();
                        return true;
                    }
                    return false;
                });
                popup.show();
            });
        }

        // Notifications placeholder
        ImageButton buttonNotifications = findViewById(R.id.button_notifications);
        buttonNotifications.setOnClickListener(v -> 
            Toast.makeText(MainActivity.this, "Không có thông báo mới", Toast.LENGTH_SHORT).show()
        );

        // Add court button action
        View buttonAddCourt = findViewById(R.id.button_add_court);
        if (buttonAddCourt != null) {
            buttonAddCourt.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, AddCourtActivity.class);
                startActivity(intent);
            });
        }

        // Setup new layout bookings tab actions
        if (cardFilterDate != null) {
            cardFilterDate.setOnClickListener(v -> showDatePickerDialog());
        }
        if (cardFilterDuration != null) {
            cardFilterDuration.setOnClickListener(v -> showDurationPopupMenu());
        }
        if (buttonRefreshBookings != null) {
            buttonRefreshBookings.setOnClickListener(v -> {
                viewModel.refreshData();
                Toast.makeText(MainActivity.this, "Đã cập nhật dữ liệu đặt sân mới nhất!", Toast.LENGTH_SHORT).show();
            });
        }
        if (buttonAddBookingNew != null) {
            buttonAddBookingNew.setOnClickListener(v -> showAddBookingDialog(null));
        }



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

        // Price table search textwatcher
        if (editSearchPriceTables != null) {
            editSearchPriceTables.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    ptSearchQuery = s.toString().trim();
                    filterAndApplyPriceTables();
                }

                @Override
                public void afterTextChanged(android.text.Editable s) {}
            });
        }

        // Add Price Table action
        View buttonAddPriceTable = findViewById(R.id.button_add_price_table);
        if (buttonAddPriceTable != null) {
            buttonAddPriceTable.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(MainActivity.this, CreatePriceTableActivity.class);
                startActivity(intent);
            });
        }
    }

    private void setupCalendarStrip() {
        // Obsolete calendar strip - replaced by Date Selector dropdown filter.
    }

    private void observeViewModel() {
        // Observe courts list
        viewModel.getCourts().observe(this, courts -> {
            if (courts != null) {
                cachedCourts = courts;
                updateCourtsUI(courts);
                rebuildTimelineGrid();
            }
        });

        // Observe bookings list
        viewModel.getBookings().observe(this, bookings -> {
            if (bookings != null) {
                cachedBookings = bookings;
                updateBookingsUI(bookings);
                rebuildTimelineGrid();
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

        // Observe price tables list
        viewModel.getPriceTables().observe(this, priceTables -> {
            if (priceTables != null) {
                cachedPriceTables = priceTables;
                filterAndApplyPriceTables();
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
        if (bookingAdapter != null) {
            bookingAdapter.setData(bookings, cachedCourts);
        }

        // Update dashboard metrics count
        textBookingsCount.setText(String.valueOf(bookings.size()));

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
            Booking booking = new Booking(0, court.getId(), playerName, selectedBookingDate, startTime, endTime, fee);
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

    // ================= Price Table Management =================

    private void filterAndApplyPriceTables() {
        if (cachedPriceTables == null) return;
        List<PriceTable> filtered = new ArrayList<>();
        for (PriceTable pt : cachedPriceTables) {
            boolean matchesSearch = TextUtils.isEmpty(ptSearchQuery) || 
                    (pt.getTenbanggia() != null && pt.getTenbanggia().toLowerCase().contains(ptSearchQuery.toLowerCase())) ||
                    (pt.getMaBanggia() != null && pt.getMaBanggia().toLowerCase().contains(ptSearchQuery.toLowerCase())) ||
                    (pt.getMota() != null && pt.getMota().toLowerCase().contains(ptSearchQuery.toLowerCase()));

            if (matchesSearch) {
                filtered.add(pt);
            }
        }
        if (priceTableAdapter != null) {
            priceTableAdapter.setData(filtered);
        }
    }



    private void showDeletePriceTableDialog(PriceTable pt) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa bảng giá")
                .setMessage("Bạn có chắc chắn muốn xóa bảng giá \"" + pt.getTenbanggia() + "\"? Thao tác này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    viewModel.deletePriceTable(pt.getId());
                    Toast.makeText(MainActivity.this, "Đã xóa bảng giá thành công!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // ================= Booking Grid Schedule Helper Methods =================

    private void showDatePickerDialog() {
        try {
            String[] parts = selectedBookingDate.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]) - 1;
            int day = Integer.parseInt(parts[2]);

            android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        selectedBookingDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        if (textFilterDate != null) {
                            textFilterDate.setText(formatDbDateToDisplay(selectedBookingDate));
                        }
                        rebuildTimelineGrid();
                        Toast.makeText(MainActivity.this, "Xem lịch ngày: " + formatDbDateToDisplay(selectedBookingDate), Toast.LENGTH_SHORT).show();
                    }, year, month, day);
            datePickerDialog.show();
        } catch (Exception e) {
            Log.e("MainActivity", "Error opening date picker", e);
        }
    }

    private int getSelectedDurationMinutes() {
        if (selectedBookingDuration == null) return 60;
        if (selectedBookingDuration.contains("15")) return 15;
        if (selectedBookingDuration.contains("30")) return 30;
        return 60;
    }

    private void showDurationPopupMenu() {
        PopupMenu popup = new PopupMenu(this, cardFilterDuration);
        popup.getMenu().add("15 phút");
        popup.getMenu().add("30 phút");
        popup.getMenu().add("60 phút");
        popup.setOnMenuItemClickListener(item -> {
            selectedBookingDuration = item.getTitle().toString();
            if (textFilterDuration != null) {
                textFilterDuration.setText(selectedBookingDuration);
            }
            rebuildTimelineGrid();
            return true;
        });
        popup.show();
    }

    private void rebuildTimelineGrid() {
        if (layoutTimelineCourtsColumn == null || layoutTimelineRowsContainer == null) return;

        // Clear layouts
        layoutTimelineCourtsColumn.removeAllViews();
        layoutTimelineRowsContainer.removeAllViews();
        if (layoutTimelineTimeHeaders != null) {
            layoutTimelineTimeHeaders.removeAllViews();
        }

        // Add top-left spacer to courts column
        View spacer = new View(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(40)
        ));
        spacer.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        layoutTimelineCourtsColumn.addView(spacer);

        int intervalMinutes = getSelectedDurationMinutes();
        int numCols = 960 / intervalMinutes; // 16 hours * 60 minutes = 960 minutes

        // Rebuild time headers dynamically
        if (layoutTimelineTimeHeaders != null) {
            ViewGroup.LayoutParams headersParams = layoutTimelineTimeHeaders.getLayoutParams();
            if (headersParams != null) {
                headersParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                layoutTimelineTimeHeaders.setLayoutParams(headersParams);
            }

            for (int i = 0; i < numCols; i++) {
                int minutesFromStart = i * intervalMinutes;
                int totalMinutes = 6 * 60 + minutesFromStart;
                int hour = totalMinutes / 60;
                int minute = totalMinutes % 60;
                String timeStr = String.format("%02d:%02d", hour, minute);

                TextView textHeader = new TextView(this);
                LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(
                        dpToPx(100),
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
                textHeader.setLayoutParams(headerParams);
                textHeader.setText(timeStr);
                textHeader.setTextSize(12);
                textHeader.setTextColor(android.graphics.Color.parseColor("#556679"));
                textHeader.setGravity(android.view.Gravity.CENTER);
                textHeader.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
                layoutTimelineTimeHeaders.addView(textHeader);
            }
        }

        if (layoutTimelineRowsContainer != null) {
            ViewGroup.LayoutParams rowsParams = layoutTimelineRowsContainer.getLayoutParams();
            if (rowsParams != null) {
                rowsParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                layoutTimelineRowsContainer.setLayoutParams(rowsParams);
            }
        }

        if (cachedCourts == null || cachedCourts.isEmpty()) {
            return;
        }

        // Iterate courts to build sticky column & timelines
        for (Court court : cachedCourts) {
            // Sticky Court Name
            TextView textCourtName = new TextView(this);
            LinearLayout.LayoutParams courtParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dpToPx(60)
            );
            textCourtName.setLayoutParams(courtParams);
            textCourtName.setText(court.getName());
            textCourtName.setGravity(android.view.Gravity.CENTER);
            textCourtName.setTextSize(13);
            textCourtName.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            textCourtName.setTextColor(getResources().getColor(R.color.primary));
            textCourtName.setBackgroundColor(getResources().getColor(R.color.surface_header));
            layoutTimelineCourtsColumn.addView(textCourtName);

            // Add border below court name
            View borderLeft = new View(this);
            borderLeft.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(1)));
            borderLeft.setBackgroundColor(getResources().getColor(R.color.border_gray));
            layoutTimelineCourtsColumn.addView(borderLeft);

            // Row Container
            android.widget.FrameLayout rowFrame = new android.widget.FrameLayout(this);
            LinearLayout.LayoutParams frameParams = new LinearLayout.LayoutParams(
                    dpToPx(numCols * 100),
                    dpToPx(60)
            );
            rowFrame.setLayoutParams(frameParams);
            rowFrame.setBackgroundColor(getResources().getColor(R.color.white));

            // Background cells and gridlines
            LinearLayout bgLayout = new LinearLayout(this);
            bgLayout.setLayoutParams(new android.widget.FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            bgLayout.setOrientation(LinearLayout.HORIZONTAL);

            for (int i = 0; i < numCols; i++) {
                View cellView = new View(this);
                LinearLayout.LayoutParams cellParams = new LinearLayout.LayoutParams(
                        dpToPx(99),
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
                cellView.setLayoutParams(cellParams);
                bgLayout.addView(cellView);

                View lineView = new View(this);
                LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                        dpToPx(1),
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
                lineView.setLayoutParams(lineParams);
                lineView.setBackgroundColor(getResources().getColor(R.color.border_gray));
                bgLayout.addView(lineView);
            }
            rowFrame.addView(bgLayout);

            // Filter bookings
            List<Booking> courtBookings = new ArrayList<>();
            if (cachedBookings != null) {
                for (Booking b : cachedBookings) {
                    if (b.getCourtId() == court.getId() && selectedBookingDate.equals(b.getDate())) {
                        courtBookings.add(b);
                    }
                }
            }

            // Draw bookings
            for (Booking booking : courtBookings) {
                double startHour = timeToHours(booking.getStartTime());
                double endHour = timeToHours(booking.getEndTime());

                if (startHour < 6.0) startHour = 6.0;
                if (endHour > 22.0) endHour = 22.0;
                if (startHour >= endHour) continue;

                double offset = startHour - 6.0;
                double duration = endHour - startHour;

                double pxPerHour = 60.0 * (100.0 / intervalMinutes);
                int cardLeft = dpToPx((int) Math.round(offset * pxPerHour));
                int cardWidth = dpToPx((int) Math.round(duration * pxPerHour)) - dpToPx(4);

                com.google.android.material.card.MaterialCardView cardView = new com.google.android.material.card.MaterialCardView(this);
                android.widget.FrameLayout.LayoutParams cardParams = new android.widget.FrameLayout.LayoutParams(
                        cardWidth,
                        dpToPx(50)
                );
                cardParams.leftMargin = cardLeft + dpToPx(2);
                cardParams.gravity = android.view.Gravity.CENTER_VERTICAL;
                cardView.setLayoutParams(cardParams);
                cardView.setCardElevation(0);
                cardView.setRadius((float) dpToPx(4));
                
                int bgColorRes = R.color.secondary;
                int textColorRes = R.color.white;
                if (booking.getStatus() != null) {
                    if (booking.getStatus().equalsIgnoreCase("Đang sử dụng")) {
                        bgColorRes = R.color.primary_container;
                        textColorRes = R.color.black;
                    } else if (booking.getStatus().equalsIgnoreCase("Hoàn thành")) {
                        bgColorRes = R.color.status_empty;
                        textColorRes = R.color.white;
                    } else if (booking.getStatus().equalsIgnoreCase("Đã hủy")) {
                        bgColorRes = R.color.status_maintenance;
                        textColorRes = R.color.white;
                    }
                }
                cardView.setCardBackgroundColor(getResources().getColor(bgColorRes));
                cardView.setStrokeWidth(0);

                TextView textInfo = new TextView(this);
                android.widget.FrameLayout.LayoutParams textParams = new android.widget.FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
                textInfo.setLayoutParams(textParams);
                textInfo.setText(booking.getPlayerName() + "\n" + booking.getStartTime() + " - " + booking.getEndTime());
                textInfo.setGravity(android.view.Gravity.CENTER);
                textInfo.setTextSize(10);
                textInfo.setTextColor(getResources().getColor(textColorRes));
                textInfo.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
                textInfo.setPadding(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));
                cardView.addView(textInfo);

                cardView.setOnClickListener(v -> showBookingActionDialog(booking));

                rowFrame.addView(cardView);
            }

            layoutTimelineRowsContainer.addView(rowFrame);

            View borderRow = new View(this);
            borderRow.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(1)));
            borderRow.setBackgroundColor(getResources().getColor(R.color.border_gray));
            layoutTimelineRowsContainer.addView(borderRow);
        }
    }

    private void showBookingActionDialog(Booking booking) {
        String courtName = "Court #" + booking.getCourtId();
        if (cachedCourts != null) {
            for (Court c : cachedCourts) {
                if (c.getId() == booking.getCourtId()) {
                    courtName = c.getName();
                    break;
                }
            }
        }
        String message = "Khách hàng: " + booking.getPlayerName() + "\n" +
                "Sân: " + courtName + "\n" +
                "Thời gian: " + booking.getStartTime() + " - " + booking.getEndTime() + "\n" +
                "Ngày đặt: " + formatDbDateToDisplay(booking.getDate()) + "\n" +
                "Trạng thái: " + booking.getStatus() + "\n" +
                "Chi phí: $" + booking.getFee();

        new AlertDialog.Builder(this)
                .setTitle("Chi tiết lịch đặt sân")
                .setMessage(message)
                .setPositiveButton("Đóng", null)
                .setNegativeButton("Hủy lịch đặt (Xóa)", (dialog, which) -> {
                    onCancelBooking(booking);
                })
                .show();
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private double timeToHours(String time) {
        try {
            if (time == null || !time.contains(":")) return 6.0;
            String[] parts = time.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            return hour + minute / 60.0;
        } catch (Exception e) {
            return 6.0;
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

    private String formatDisplayDateToDb(String displayDate) {
        try {
            if (displayDate == null || !displayDate.contains("/")) return displayDate;
            String[] parts = displayDate.split("/");
            return parts[2] + "-" + parts[1] + "-" + parts[0];
        } catch (Exception e) {
            return displayDate;
        }
    }
}
