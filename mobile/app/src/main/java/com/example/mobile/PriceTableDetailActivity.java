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
                    showEditPriceTableDialog(currentPriceTable);
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

        // Custom config statistics based on ID (mirroring design reference in code.html)
        int slotsCount;
        String daysApplied;
        if (pt.getId() == 1) {
            slotsCount = 4;
            daysApplied = "T2 - T6";
        } else if (pt.getId() == 2) {
            slotsCount = 2;
            daysApplied = "T7 - CN";
        } else if (pt.getId() == 3) {
            slotsCount = 1;
            daysApplied = "Lễ/Tết";
        } else {
            slotsCount = 3;
            daysApplied = "Tất cả";
        }

        textStatSlots.setText(String.valueOf(slotsCount));
        textStatDays.setText(daysApplied);

        buildSlotsTable(pt.getId());
    }



    private void buildSlotsTable(int id) {
        layoutSlotsTable.removeAllViews();
        float density = getResources().getDisplayMetrics().density;
        int rowPadding = (int) (12 * density);

        class SlotRow {
            final String time;
            final String days;
            final String price;

            SlotRow(String time, String days, String price) {
                this.time = time;
                this.days = days;
                this.price = price;
            }
        }

        List<SlotRow> rows = new ArrayList<>();
        if (id == 1) {
            rows.add(new SlotRow("05:00 - 08:00", "Ngày thường (T2-T6)", "120.000đ"));
            rows.add(new SlotRow("08:00 - 15:00", "Ngày thường (T2-T6)", "150.000đ"));
            rows.add(new SlotRow("15:00 - 21:00", "Ngày thường (T2-T6)", "250.000đ"));
            rows.add(new SlotRow("21:00 - 00:00", "Ngày thường (T2-T6)", "180.000đ"));
        } else if (id == 2) {
            rows.add(new SlotRow("05:00 - 16:00", "Cuối tuần (T7-CN)", "180.000đ"));
            rows.add(new SlotRow("16:00 - 00:00", "Cuối tuần (T7-CN)", "240.000đ"));
        } else {
            rows.add(new SlotRow("Cả ngày", "Tất cả", "200.000đ"));
        }

        for (SlotRow row : rows) {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setPadding(rowPadding, rowPadding, rowPadding, rowPadding);

            TextView tvTime = new TextView(this);
            tvTime.setText(row.time);
            tvTime.setTextSize(14);
            tvTime.setTypeface(null, Typeface.BOLD);
            tvTime.setTextColor(getResources().getColor(R.color.on_background));
            LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.2f);
            tvTime.setLayoutParams(p1);

            TextView tvDays = new TextView(this);
            tvDays.setText(row.days);
            tvDays.setTextSize(13);
            tvDays.setTextColor(Color.parseColor("#556679"));
            LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            tvDays.setLayoutParams(p2);

            TextView tvPrice = new TextView(this);
            tvPrice.setText(row.price);
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

    private void showEditPriceTableDialog(PriceTable pt) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa bảng giá");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (18 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, padding);

        final EditText editCode = new EditText(this);
        editCode.setHint("Mã bảng giá (ví dụ: BG_STANDARD)");
        editCode.setText(pt.getMaBanggia());
        editCode.setSingleLine(true);
        layout.addView(editCode);

        final EditText editName = new EditText(this);
        editName.setHint("Tên bảng giá (ví dụ: Bảng giá chuẩn)");
        editName.setText(pt.getTenbanggia());
        editName.setSingleLine(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = (int) (12 * getResources().getDisplayMetrics().density);
        editName.setLayoutParams(params);
        layout.addView(editName);

        final EditText editDesc = new EditText(this);
        editDesc.setHint("Mô tả (ví dụ: Áp dụng từ Thứ 2 đến Thứ 6)");
        editDesc.setText(pt.getMota());
        editDesc.setSingleLine(false);
        editDesc.setLines(2);
        editDesc.setLayoutParams(params);
        layout.addView(editDesc);

        builder.setView(layout);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
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

            PriceTable updated = new PriceTable(pt.getId(), code, name, desc);
            viewModel.updatePriceTable(updated);
            Toast.makeText(this, "Cập nhật bảng giá thành công!", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
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
