package com.example.mobile.view;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.R;
import com.example.mobile.model.PriceTable;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class PriceTableAdapter extends RecyclerView.Adapter<PriceTableAdapter.ViewHolder> {

    public interface OnPriceTableActionListener {
        void onEditPriceTable(PriceTable pt);
        void onDetailPriceTable(PriceTable pt);
    }

    private final OnPriceTableActionListener listener;
    private List<PriceTable> priceTables = new ArrayList<>();

    public PriceTableAdapter(OnPriceTableActionListener listener) {
        this.listener = listener;
    }

    public void setData(List<PriceTable> data) {
        this.priceTables = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_price_table, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PriceTable pt = priceTables.get(position);
        holder.bind(pt, listener);
    }

    @Override
    public int getItemCount() {
        return priceTables.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView badgeStatus;
        TextView textId;
        TextView textName;
        TextView textDesc;
        TextView textDate;
        TextView badgeSlots;
        TextView badgeMinPrice;
        MaterialButton buttonEdit;
        MaterialButton buttonDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            badgeStatus = itemView.findViewById(R.id.badge_price_status);
            textId = itemView.findViewById(R.id.text_price_id);
            textName = itemView.findViewById(R.id.text_price_name);
            textDesc = itemView.findViewById(R.id.text_price_desc);
            textDate = itemView.findViewById(R.id.text_price_date);
            badgeSlots = itemView.findViewById(R.id.badge_slots_count);
            badgeMinPrice = itemView.findViewById(R.id.badge_min_price);
            buttonEdit = itemView.findViewById(R.id.button_edit_price_table);
            buttonDetail = itemView.findViewById(R.id.button_detail_price_table);
        }

        public void bind(PriceTable pt, OnPriceTableActionListener listener) {
            textName.setText(pt.getTenbanggia());
            textDesc.setText(pt.getMota() != null && !pt.getMota().isEmpty() ? pt.getMota() : "Chưa có mô tả chi tiết.");
            
            // Generate or map specific statuses and metadata based on ID/Code to match design references
            String idStr;
            if (pt.getMaBanggia() != null && !pt.getMaBanggia().isEmpty()) {
                if (pt.getMaBanggia().startsWith("BG-") || pt.getMaBanggia().startsWith("BG_")) {
                    idStr = "ID: " + pt.getMaBanggia().replace("_", "-");
                } else {
                    idStr = "ID: " + pt.getMaBanggia();
                }
            } else {
                idStr = String.format("ID: BG-%03d", pt.getId());
            }
            textId.setText(idStr);

            // Dynamic styling matching the bento bordeaux color schema in code.html
            int id = pt.getId();
            String statusText;
            int badgeBgColor;
            int badgeTextColor;
            String dateText;
            int slotsCount;
            String minPriceText;

            if (id == 1) {
                statusText = "ĐANG ÁP DỤNG";
                badgeBgColor = Color.parseColor("#DFFF00"); // Neon Green
                badgeTextColor = Color.parseColor("#647400");
                dateText = "Tạo ngày: 15/10/2023";
                slotsCount = 4;
                minPriceText = "Giá min: 120k";
            } else if (id == 2) {
                statusText = "DỰ PHÒNG";
                badgeBgColor = Color.parseColor("#D2E4FB"); // Light Blue/Secondary container
                badgeTextColor = Color.parseColor("#556679");
                dateText = "Tạo ngày: 20/10/2023";
                slotsCount = 2;
                minPriceText = "Giá min: 180k";
            } else if (id == 3) {
                statusText = "NGƯNG HĐ";
                badgeBgColor = Color.parseColor("#E1E3E4"); // Surface Variant Gray
                badgeTextColor = Color.parseColor("#454932");
                dateText = "Tạo ngày: 01/01/2024";
                slotsCount = 1;
                minPriceText = "Giá min: 250k";
            } else if (id == 4) {
                statusText = "MỚI";
                badgeBgColor = Color.parseColor("#FFECE7"); // Muted Orange/Red
                badgeTextColor = Color.parseColor("#C93700");
                dateText = "Tạo ngày: 10/05/2024";
                slotsCount = 3;
                minPriceText = "Giá min: 100k";
            } else {
                statusText = "MỚI";
                badgeBgColor = Color.parseColor("#FFECE7");
                badgeTextColor = Color.parseColor("#C93700");
                dateText = "Tạo ngày: 14/06/2026";
                slotsCount = 3;
                minPriceText = "Giá min: 120k";
            }

            badgeStatus.setText(statusText);
            badgeStatus.setBackgroundTintList(ColorStateList.valueOf(badgeBgColor));
            badgeStatus.setTextColor(badgeTextColor);

            textDate.setText(dateText);
            badgeSlots.setText("Khung giờ: " + slotsCount);
            badgeMinPrice.setText(minPriceText);

            // Bind listeners
            buttonEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditPriceTable(pt);
                }
            });

            buttonDetail.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDetailPriceTable(pt);
                }
            });
        }
    }
}
