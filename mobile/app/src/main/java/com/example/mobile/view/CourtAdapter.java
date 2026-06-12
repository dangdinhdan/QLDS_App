package com.example.mobile.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.R;
import com.example.mobile.model.Booking;
import com.example.mobile.model.Court;
import com.example.mobile.model.CourtStatus;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CourtAdapter extends RecyclerView.Adapter<CourtAdapter.CourtViewHolder> {

    public interface OnCourtActionListener {
        void onEditCourt(Court court);
        void onDeleteCourt(Court court);
    }

    private final List<Court> courts = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();
    private final OnCourtActionListener listener;
    private int selectedCourtId = 5; // Default to Sân B2 (ID 5) pre-selected on load

    public CourtAdapter(OnCourtActionListener listener) {
        this.listener = listener;
    }

    public void setData(List<Court> newCourts, List<Booking> newBookings) {
        courts.clear();
        courts.addAll(newCourts);
        bookings.clear();
        bookings.addAll(newBookings);
        notifyDataSetChanged();
    }

    public int getSelectedCourtId() {
        return selectedCourtId;
    }

    public void setSelectedCourtId(int selectedCourtId) {
        this.selectedCourtId = selectedCourtId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CourtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_court, parent, false);
        return new CourtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourtViewHolder holder, int position) {
        Court court = courts.get(position);
        holder.bind(court, listener);
    }

    @Override
    public int getItemCount() {
        return courts.size();
    }

    class CourtViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardRoot;
        private final ImageView imageCourt;
        private final View viewMaintenanceOverlay;
        private final ImageView imageMaintenanceIcon;
        private final TextView badgeStatus;
        private final TextView textName;
        private final ImageButton buttonCourtMenu;
        private final ImageView imageSurfaceIcon;
        private final TextView textSurfaceType;
        
        // Action Tray elements
        private final LinearLayout layoutCourtActions;
        private final MaterialButton buttonEditCourt;
        private final MaterialButton buttonDeleteCourt;

        public CourtViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRoot = itemView.findViewById(R.id.card_court_root);
            imageCourt = itemView.findViewById(R.id.image_court);
            viewMaintenanceOverlay = itemView.findViewById(R.id.view_maintenance_overlay);
            imageMaintenanceIcon = itemView.findViewById(R.id.image_maintenance_icon);
            badgeStatus = itemView.findViewById(R.id.badge_court_status);
            textName = itemView.findViewById(R.id.text_court_name);
            buttonCourtMenu = itemView.findViewById(R.id.button_court_menu);
            imageSurfaceIcon = itemView.findViewById(R.id.image_surface_icon);
            textSurfaceType = itemView.findViewById(R.id.text_surface_type);
            
            layoutCourtActions = itemView.findViewById(R.id.layout_court_actions);
            buttonEditCourt = itemView.findViewById(R.id.button_edit_court);
            buttonDeleteCourt = itemView.findViewById(R.id.button_delete_court);
        }

        public void bind(final Court court, final OnCourtActionListener listener) {
            textName.setText(court.getName());
            textSurfaceType.setText("Mặt sân: " + (court.getSurfaceType() != null ? court.getSurfaceType() : "Cứng"));

            // Load web image using the async ImageLoader
            ImageLoader.getInstance().loadImage(court.getImageUrl(), imageCourt, R.drawable.ic_logo_racket);

            // 1. Surface type icon mapping
            if (court.getSurfaceType() != null && court.getSurfaceType().equalsIgnoreCase("Thảm")) {
                imageSurfaceIcon.setImageResource(R.drawable.ic_dashboard_24);
            } else {
                imageSurfaceIcon.setImageResource(R.drawable.ic_courts_24);
            }

            // 2. Status Badge Overlay Styling
            switch (court.getStatus()) {
                case EMPTY:
                    badgeStatus.setText("Trống");
                    badgeStatus.setBackgroundResource(R.drawable.badge_empty);
                    badgeStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.primary));
                    break;
                case IN_USE:
                    badgeStatus.setText("Đang sử dụng");
                    badgeStatus.setBackgroundResource(R.drawable.badge_in_use);
                    badgeStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.secondary));
                    break;
                case BOOKED:
                    badgeStatus.setText("Đã đặt");
                    badgeStatus.setBackgroundResource(R.drawable.badge_booked);
                    badgeStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.white));
                    break;
                case MAINTENANCE:
                    badgeStatus.setText("Bảo trì");
                    badgeStatus.setBackgroundResource(R.drawable.badge_maintenance);
                    badgeStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.black));
                    break;
            }

            // 3. Maintenance Overlay & Icon
            if (court.getStatus() == CourtStatus.MAINTENANCE) {
                viewMaintenanceOverlay.setVisibility(View.VISIBLE);
                imageMaintenanceIcon.setVisibility(View.VISIBLE);
            } else {
                viewMaintenanceOverlay.setVisibility(View.GONE);
                imageMaintenanceIcon.setVisibility(View.GONE);
            }

            // 4. Selection Highlight & Action Tray Visibility
            boolean isSelected = (court.getId() == selectedCourtId);
            if (isSelected) {
                cardRoot.setStrokeColor(itemView.getContext().getResources().getColor(R.color.secondary));
                cardRoot.setStrokeWidth((int) (2 * itemView.getContext().getResources().getDisplayMetrics().density));
                layoutCourtActions.setVisibility(View.VISIBLE);
            } else {
                cardRoot.setStrokeColor(itemView.getContext().getResources().getColor(R.color.border_gray));
                cardRoot.setStrokeWidth((int) (1 * itemView.getContext().getResources().getDisplayMetrics().density));
                layoutCourtActions.setVisibility(View.GONE);
            }

            // 5. Dynamic Action Buttons Layout inside Tray
            LinearLayout.LayoutParams deleteParams = (LinearLayout.LayoutParams) buttonDeleteCourt.getLayoutParams();
            if (court.getStatus() == CourtStatus.MAINTENANCE) {
                buttonEditCourt.setVisibility(View.GONE);
                deleteParams.width = 0;
                deleteParams.weight = 1f;
                deleteParams.rightMargin = 0;
            } else {
                buttonEditCourt.setVisibility(View.VISIBLE);
                deleteParams.width = (int) (48 * itemView.getContext().getResources().getDisplayMetrics().density);
                deleteParams.weight = 0f;
            }
            buttonDeleteCourt.setLayoutParams(deleteParams);

            // 6. Click Listeners for Selection Toggle
            View.OnClickListener selectToggleListener = v -> {
                int previousSelected = selectedCourtId;
                if (selectedCourtId == court.getId()) {
                    selectedCourtId = -1; // Unselect if clicked again
                } else {
                    selectedCourtId = court.getId();
                }
                notifyDataSetChanged();
            };

            itemView.setOnClickListener(selectToggleListener);
            buttonCourtMenu.setOnClickListener(selectToggleListener);

            // 7. Action Button Callbacks
            buttonEditCourt.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditCourt(court);
                }
            });

            buttonDeleteCourt.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteCourt(court);
                }
            });
        }
    }
}
