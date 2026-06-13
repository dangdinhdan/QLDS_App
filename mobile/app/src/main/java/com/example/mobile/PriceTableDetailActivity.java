package com.example.mobile;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobile.model.Court;
import com.example.mobile.model.PriceTable;
import com.example.mobile.repository.CourtRepository;
import com.example.mobile.viewmodel.MainViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class PriceTableDetailActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    private int priceTableId;
    private PriceTable currentPriceTable;

    // View bindings
    private TextView textTitle;
    private TextView textInfoName;
    private TextView textInfoCode;
    private TextView textInfoDesc;
    private TextView textStatSlots;
    private TextView textStatDays;

    private LinearLayout layoutSlotsTable;
    private LinearLayout layoutLinkedCourts;

    private List<Court> allCourts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_table_detail);

        // Fetch ID passed from intent
        priceTableId = getIntent().getIntExtra("price_table_id", -1);
        if (priceTableId == -1) {
            Toast.makeText(this, "Không thể tải thông tin bảng giá", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        initViews();
        setupActions();
        observeViewModel();
    }

    private void initViews() {
        textTitle = findViewById(R.id.text_detail_title);
        textInfoName = findViewById(R.id.text_info_name);
        textInfoCode = findViewById(R.id.text_info_code);
        textInfoDesc = findViewById(R.id.text_info_desc);
        textStatSlots = findViewById(R.id.text_stat_slots);
        textStatDays = findViewById(R.id.text_stat_days);

        layoutSlotsTable = findViewById(R.id.layout_slots_table_container);
        layoutLinkedCourts = findViewById(R.id.layout_linked_courts);
    }

    private void setupActions() {
        // Back Button
        ImageButton buttonBack = findViewById(R.id.button_back);
        if (buttonBack != null) {
            buttonBack.setOnClickListener(v -> finish());
        }

        // Edit Button
        MaterialButton buttonEdit = findViewById(R.id.button_detail_edit);
        if (buttonEdit != null) {
            buttonEdit.setOnClickListener(v -> {
                if (currentPriceTable != null) {
                    android.content.Intent intent = new android.content.Intent(PriceTableDetailActivity.this, CreatePriceTableActivity.class);
                    intent.putExtra("price_table_id", currentPriceTable.getId());
                    startActivity(intent);
                }
            });
        }

        // Delete Button
        MaterialButton buttonDelete = findViewById(R.id.button_detail_delete);
        if (buttonDelete != null) {
            buttonDelete.setOnClickListener(v -> {
                if (currentPriceTable != null) {
                    showDeletePriceTableDialog(currentPriceTable);
                }
            });
        }

        // Add Slot Placeholder
        TextView buttonAddSlot = findViewById(R.id.button_add_time_slot);
        if (buttonAddSlot != null) {
            buttonAddSlot.setOnClickListener(v -> 
                Toast.makeText(this, "Tính năng cấu hình khung giờ chi tiết đang được mở rộng!", Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void observeViewModel() {
        viewModel.getPriceTables().observe(this, priceTables -> {
            if (priceTables != null) {
                // Find matching price table
                for (PriceTable pt : priceTables) {
                    if (pt.getId() == priceTableId) {
                        currentPriceTable = pt;
                        bindPriceTableData(pt);
                        if (allCourts != null) {
                            bindLinkedCourts(allCourts, pt.getId());
                        }
                        break;
                    }
                }
            }
        });

        // Observe courts to dynamically show linked courts
        viewModel.getCourts().observe(this, courts -> {
            if (courts != null) {
                allCourts = courts;
                if (currentPriceTable != null) {
                    bindLinkedCourts(courts, currentPriceTable.getId());
                }
            }
        });
    }

    private void bindPriceTableData(PriceTable pt) {
        textTitle.setText(pt.getTenbanggia());
        textInfoName.setText(pt.getTenbanggia());
        textInfoCode.setText(pt.getMaBanggia());
        textInfoDesc.setText(pt.getMota() != null && !pt.getMota().isEmpty() ? pt.getMota() : "Chưa có mô tả chi tiết.");

        // Dynamic statistics based on slots
        int slotsCount = pt.getDetails().size();
        String daysApplied = "Tất cả";
        if (slotsCount == 0) {
            daysApplied = "Chưa có";
        } else {
            boolean hasWeekday = false;
            boolean hasWeekend = false;
            for (com.example.mobile.model.PriceSlot slot : pt.getDetails()) {
                if ("Cuối tuần".equals(slot.getLoaingay())) {
                    hasWeekend = true;
                } else {
                    hasWeekday = true;
                }
            }
            if (hasWeekday && !hasWeekend) {
                daysApplied = "T2 - T6";
            } else if (!hasWeekday && hasWeekend) {
                daysApplied = "T7 - CN";
            }
        }

        textStatSlots.setText(String.valueOf(slotsCount));
        textStatDays.setText(daysApplied);

        buildSlotsTable(pt);
    }

    private void buildSlotsTable(PriceTable pt) {
        layoutSlotsTable.removeAllViews();
        float density = getResources().getDisplayMetrics().density;
        int rowPadding = (int) (12 * density);

        List<com.example.mobile.model.PriceSlot> slots = pt.getDetails();
        if (slots.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("Chưa có khung giờ nào được cấu hình.");
            emptyText.setTextSize(13);
            emptyText.setTextColor(Color.parseColor("#778899"));
            emptyText.setPadding(rowPadding, rowPadding, rowPadding, rowPadding);
            layoutSlotsTable.addView(emptyText);
            return;
        }

        java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,###");

        for (com.example.mobile.model.PriceSlot slot : slots) {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setPadding(rowPadding, rowPadding, rowPadding, rowPadding);

            TextView tvTime = new TextView(this);
            tvTime.setText(slot.getGiobatdau() + " - " + slot.getGiokethuc());
            tvTime.setTextSize(14);
            tvTime.setTypeface(null, Typeface.BOLD);
            tvTime.setTextColor(getResources().getColor(R.color.on_background));
            LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.2f);
            tvTime.setLayoutParams(p1);

            TextView tvDays = new TextView(this);
            tvDays.setText(slot.getLoaingay());
            tvDays.setTextSize(13);
            tvDays.setTextColor(Color.parseColor("#556679"));
            LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            tvDays.setLayoutParams(p2);

            TextView tvPrice = new TextView(this);
            tvPrice.setText(formatter.format(slot.getDongia()) + "đ");
            tvPrice.setTextSize(14);
            tvPrice.setTypeface(null, Typeface.BOLD);
            tvPrice.setTextColor(getResources().getColor(R.color.primary));
            tvPrice.setGravity(Gravity.END);
            LinearLayout.LayoutParams p3 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            tvPrice.setLayoutParams(p3);

            linearLayout.addView(tvTime);
            linearLayout.addView(tvDays);
            linearLayout.addView(tvPrice);

            layoutSlotsTable.addView(linearLayout);
        }
    }

    private void bindLinkedCourts(List<Court> courts, int ptId) {
        layoutLinkedCourts.removeAllViews();
        float density = getResources().getDisplayMetrics().density;
        int padding = (int) (12 * density);

        List<Court> linked = new ArrayList<>();
        for (Court court : courts) {
            if (court.getIdBanggia() != null && court.getIdBanggia() == ptId) {
                linked.add(court);
            }
        }

        if (linked.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("Chưa áp dụng bảng giá này cho sân nào.");
            emptyText.setTextSize(13);
            emptyText.setTextColor(Color.parseColor("#778899"));
            emptyText.setPadding(padding, padding, padding, padding);
            layoutLinkedCourts.addView(emptyText);
            return;
        }

        for (Court court : linked) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(padding, padding, padding, padding);

            // Stadium icon
            ImageView icon = new ImageView(this);
            icon.setImageResource(R.drawable.ic_courts_24);
            icon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary)));
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams((int)(36 * density), (int)(36 * density));
            iconParams.rightMargin = (int) (12 * density);
            icon.setLayoutParams(iconParams);

            LinearLayout textContainer = new LinearLayout(this);
            textContainer.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams textContainerParams = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            textContainer.setLayoutParams(textContainerParams);

            TextView tvName = new TextView(this);
            tvName.setText(court.getName());
            tvName.setTextSize(14);
            tvName.setTypeface(null, Typeface.BOLD);
            tvName.setTextColor(getResources().getColor(R.color.on_background));

            TextView tvSub = new TextView(this);
            tvSub.setText("Mặt sân: " + court.getSurfaceType());
            tvSub.setTextSize(12);
            tvSub.setTextColor(Color.parseColor("#556679"));

            textContainer.addView(tvName);
            textContainer.addView(tvSub);

            row.addView(icon);
            row.addView(textContainer);

            layoutLinkedCourts.addView(row);
        }
    }



    private void showDeletePriceTableDialog(PriceTable pt) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa bảng giá")
                .setMessage("Bạn có chắc chắn muốn xóa bảng giá \"" + pt.getTenbanggia() + "\"? Thao tác này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    viewModel.deletePriceTable(pt.getId());
                    Toast.makeText(PriceTableDetailActivity.this, "Đã xóa bảng giá thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Return back since it's deleted
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
