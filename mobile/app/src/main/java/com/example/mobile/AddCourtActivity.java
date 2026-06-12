package com.example.mobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile.model.Court;
import com.example.mobile.model.CourtStatus;
import com.example.mobile.repository.CourtRepository;
import com.example.mobile.view.ImageLoader;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class AddCourtActivity extends AppCompatActivity {

    private EditText editCourtCode;
    private EditText editCourtName;
    private TextView chipIndoor;
    private TextView chipOutdoor;
    private TextView chipGrass;
    private FrameLayout layoutUploadContainer;
    private LinearLayout layoutUploadHelper;
    private ImageView imagePreview;
    private TextView textStatusLabel;
    private SwitchMaterial switchCourtStatus;
    private TextView textScreenTitle;
    private MaterialButton buttonSave;
    private ImageButton buttonClose;

    private String selectedSurfaceType = "Trong nhà";
    private String selectedImageUrl = "";
    private int courtId = -1;
    private boolean isEditMode = false;
    private Court editingCourt;

    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUrl = uri.toString();
                    imagePreview.setVisibility(View.VISIBLE);
                    layoutUploadHelper.setVisibility(View.GONE);
                    imagePreview.setImageURI(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_court);

        initViews();
        setupListeners();

        // Check if we are in Edit Mode
        courtId = getIntent().getIntExtra("court_id", -1);
        if (courtId != -1) {
            isEditMode = true;
            loadCourtData();
        }
    }

    private void initViews() {
        editCourtCode = findViewById(R.id.edit_court_code);
        editCourtName = findViewById(R.id.edit_court_name);
        chipIndoor = findViewById(R.id.chip_indoor);
        chipOutdoor = findViewById(R.id.chip_outdoor);
        chipGrass = findViewById(R.id.chip_grass);
        layoutUploadContainer = findViewById(R.id.layout_upload_container);
        layoutUploadHelper = findViewById(R.id.layout_upload_helper);
        imagePreview = findViewById(R.id.image_preview);
        textStatusLabel = findViewById(R.id.text_status_label);
        switchCourtStatus = findViewById(R.id.switch_court_status);
        textScreenTitle = findViewById(R.id.text_screen_title);
        buttonSave = findViewById(R.id.button_save);
        buttonClose = findViewById(R.id.button_close);
    }

    private void setupListeners() {
        buttonClose.setOnClickListener(v -> finish());

        chipIndoor.setOnClickListener(v -> selectSurfaceType("Trong nhà"));
        chipOutdoor.setOnClickListener(v -> selectSurfaceType("Ngoài trời"));
        chipGrass.setOnClickListener(v -> selectSurfaceType("Cỏ nhân tạo"));

        layoutUploadContainer.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        switchCourtStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                textStatusLabel.setText("Đang hoạt động");
            } else {
                textStatusLabel.setText("Bảo trì");
            }
        });

        buttonSave.setOnClickListener(v -> saveCourtInfo());
    }

    private void selectSurfaceType(String type) {
        selectedSurfaceType = type;

        chipIndoor.setBackgroundResource(type.equals("Trong nhà") ? R.drawable.bg_chip_selected_dark : R.drawable.bg_chip_unselected_border);
        chipIndoor.setTextColor(type.equals("Trong nhà") ? getResources().getColor(R.color.white) : getResources().getColor(R.color.on_surface));
        chipIndoor.setTypeface(null, type.equals("Trong nhà") ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);

        chipOutdoor.setBackgroundResource(type.equals("Ngoài trời") ? R.drawable.bg_chip_selected_dark : R.drawable.bg_chip_unselected_border);
        chipOutdoor.setTextColor(type.equals("Ngoài trời") ? getResources().getColor(R.color.white) : getResources().getColor(R.color.on_surface));
        chipOutdoor.setTypeface(null, type.equals("Ngoài trời") ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);

        chipGrass.setBackgroundResource(type.equals("Cỏ nhân tạo") ? R.drawable.bg_chip_selected_dark : R.drawable.bg_chip_unselected_border);
        chipGrass.setTextColor(type.equals("Cỏ nhân tạo") ? getResources().getColor(R.color.white) : getResources().getColor(R.color.on_surface));
        chipGrass.setTypeface(null, type.equals("Cỏ nhân tạo") ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
    }

    private void loadCourtData() {
        editingCourt = CourtRepository.getInstance().getCourtById(courtId);
        if (editingCourt != null) {
            textScreenTitle.setText("Cập nhật thông tin sân");
            editCourtCode.setText(editingCourt.getCourtCode());
            editCourtName.setText(editingCourt.getName());

            // Surface type
            String surface = editingCourt.getSurfaceType();
            if (surface != null) {
                if (surface.equalsIgnoreCase("Trong nhà") || surface.equalsIgnoreCase("Cứng")) {
                    selectSurfaceType("Trong nhà");
                } else if (surface.equalsIgnoreCase("Ngoài trời")) {
                    selectSurfaceType("Ngoài trời");
                } else if (surface.equalsIgnoreCase("Cỏ nhân tạo") || surface.equalsIgnoreCase("Thảm")) {
                    selectSurfaceType("Cỏ nhân tạo");
                } else {
                    selectSurfaceType("Trong nhà");
                }
            }

            // Image Preview
            if (editingCourt.getImageUrl() != null && !editingCourt.getImageUrl().isEmpty()) {
                selectedImageUrl = editingCourt.getImageUrl();
                imagePreview.setVisibility(View.VISIBLE);
                layoutUploadHelper.setVisibility(View.GONE);
                if (selectedImageUrl.startsWith("content://") || selectedImageUrl.startsWith("file://")) {
                    imagePreview.setImageURI(Uri.parse(selectedImageUrl));
                } else {
                    ImageLoader.getInstance().loadImage(selectedImageUrl, imagePreview, R.drawable.ic_courts_24);
                }
            }

            // Status
            if (editingCourt.getStatus() == CourtStatus.MAINTENANCE) {
                switchCourtStatus.setChecked(false);
                textStatusLabel.setText("Bảo trì");
            } else {
                switchCourtStatus.setChecked(true);
                textStatusLabel.setText("Đang hoạt động");
            }
        }
    }

    private void saveCourtInfo() {
        String code = editCourtCode.getText().toString().trim();
        String name = editCourtName.getText().toString().trim();

        if (TextUtils.isEmpty(code)) {
            editCourtCode.setError("Vui lòng nhập mã sân");
            return;
        }

        if (TextUtils.isEmpty(name)) {
            editCourtName.setError("Vui lòng nhập tên sân");
            return;
        }

        // Determine status
        CourtStatus status;
        if (switchCourtStatus.isChecked()) {
            if (isEditMode && editingCourt != null && editingCourt.getStatus() != CourtStatus.MAINTENANCE) {
                status = editingCourt.getStatus();
            } else {
                status = CourtStatus.EMPTY;
            }
        } else {
            status = CourtStatus.MAINTENANCE;
        }

        // Default Image URL fallback if empty
        if (TextUtils.isEmpty(selectedImageUrl)) {
            if (selectedSurfaceType.equals("Trong nhà")) {
                selectedImageUrl = "https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=500";
            } else if (selectedSurfaceType.equals("Ngoài trời")) {
                selectedImageUrl = "https://images.unsplash.com/photo-1595435934249-5df7ed86e1c0?w=500";
            } else {
                selectedImageUrl = "https://images.unsplash.com/photo-1505666287802-931dc83948e9?w=500";
            }
        }

        if (isEditMode) {
            CourtRepository.getInstance().updateCourt(courtId, code, name, selectedSurfaceType, status, selectedImageUrl);
            Toast.makeText(this, "Cập nhật sân thành công!", Toast.LENGTH_SHORT).show();
        } else {
            Court newCourt = new Court(0, code, name, status, 15.0, selectedSurfaceType, selectedImageUrl, null);
            CourtRepository.getInstance().addCourt(newCourt);
            Toast.makeText(this, "Thêm sân mới thành công!", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
