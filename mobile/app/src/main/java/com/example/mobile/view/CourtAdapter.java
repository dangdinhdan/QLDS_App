package com.example.mobile.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.R;
import com.example.mobile.model.Booking;
import com.example.mobile.model.Court;
import com.example.mobile.model.CourtStatus;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CourtAdapter extends RecyclerView.Adapter<CourtAdapter.CourtViewHolder> {

    public interface OnCourtActionListener {
        void onBookCourt(Court court);
        void onChangeStatus(Court court);
    }

    private final List<Court> courts = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();
    private final OnCourtActionListener listener;

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

    @NonNull
    @Override
    public CourtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_court, parent, false);
        return new CourtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourtViewHolder holder, int position) {
        Court court = courts.get(position);
        holder.bind(court, bookings, listener);
    }

    @Override
    public int getItemCount() {
        return courts.size();
    }

    static class CourtViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageCourt;
        private final View viewMaintenanceOverlay;
        private final TextView textName;
        private final TextView textRate;
        private final TextView badgeStatus;
        private final TextView textSurfaceType;
        private final View layoutStatusDetails;
        private final ImageView imageDetailIcon;
        private final TextView textDetails;
        private final MaterialButton buttonChangeStatus;
        private final MaterialButton buttonBookCourt;

        public CourtViewHolder(@NonNull View itemView) {
            super(itemView);
            imageCourt = itemView.findViewById(R.id.image_court);
            viewMaintenanceOverlay = itemView.findViewById(R.id.view_maintenance_overlay);
            textName = itemView.findViewById(R.id.text_court_name);
            textRate = itemView.findViewById(R.id.text_court_rate);
            badgeStatus = itemView.findViewById(R.id.badge_court_status);
            textSurfaceType = itemView.findViewById(R.id.text_surface_type);
            layoutStatusDetails = itemView.findViewById(R.id.layout_status_details);
            imageDetailIcon = itemView.findViewById(R.id.image_detail_icon);
            textDetails = itemView.findViewById(R.id.text_court_details);
            buttonChangeStatus = itemView.findViewById(R.id.button_change_status);
            buttonBookCourt = itemView.findViewById(R.id.button_book_court);
        }

        public void bind(final Court court, List<Booking> bookings, final OnCourtActionListener listener) {
            textName.setText(court.getName());
            textRate.setText(String.format("$%.2f/hr", court.getHourlyRate()));
            textSurfaceType.setText("Mặt sân: " + (court.getSurfaceType() != null ? court.getSurfaceType() : "Cứng"));

            // Load web image using the async ImageLoader
            ImageLoader.getInstance().loadImage(court.getImageUrl(), imageCourt, R.drawable.ic_logo_racket);

            // Find any booking for this court
            Booking activeBooking = null;
            for (Booking b : bookings) {
                if (b.getCourtId() == court.getId()) {
                    activeBooking = b;
                    break; // Just grab the first one for simplicity
                }
            }

            // Reset details text color to default
            textDetails.setTextColor(itemView.getContext().getResources().getColor(R.color.on_surface));

            // Bind status badge styling and detail trays based on state
            switch (court.getStatus()) {
                case EMPTY:
                    badgeStatus.setText("TRỐNG");
                    badgeStatus.setBackgroundResource(R.drawable.badge_empty);
                    badgeStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.primary));
                    
                    layoutStatusDetails.setVisibility(View.GONE);
                    viewMaintenanceOverlay.setVisibility(View.GONE);
                    
                    buttonBookCourt.setVisibility(View.VISIBLE);
                    buttonChangeStatus.setVisibility(View.VISIBLE);
                    break;
                    
                case IN_USE:
                    badgeStatus.setText("ĐANG DÙNG");
                    badgeStatus.setBackgroundResource(R.drawable.badge_in_use);
                    badgeStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.secondary));
                    
                    layoutStatusDetails.setVisibility(View.VISIBLE);
                    viewMaintenanceOverlay.setVisibility(View.GONE);
                    imageDetailIcon.setImageResource(R.drawable.ic_person_24);
                    imageDetailIcon.setColorFilter(itemView.getContext().getResources().getColor(R.color.secondary));
                    
                    if (activeBooking != null) {
                        textDetails.setText(String.format("Còn lại: 45ph • Đang dùng: %s", activeBooking.getPlayerName()));
                    } else {
                        textDetails.setText("Còn lại: 45ph • Trận đấu đang diễn ra");
                    }
                    
                    buttonBookCourt.setVisibility(View.GONE);
                    buttonChangeStatus.setVisibility(View.VISIBLE);
                    break;
                    
                case BOOKED:
                    badgeStatus.setText("ĐÃ ĐẶT");
                    badgeStatus.setBackgroundResource(R.drawable.badge_booked);
                    badgeStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.white));
                    
                    layoutStatusDetails.setVisibility(View.VISIBLE);
                    viewMaintenanceOverlay.setVisibility(View.GONE);
                    imageDetailIcon.setImageResource(R.drawable.ic_calendar_24);
                    imageDetailIcon.setColorFilter(itemView.getContext().getResources().getColor(R.color.secondary));
                    
                    if (activeBooking != null) {
                        textDetails.setText(String.format("Tiếp theo: %s - %s", activeBooking.getStartTime(), activeBooking.getPlayerName()));
                    } else {
                        textDetails.setText("Tiếp theo: 18:00 - Nguyễn Văn A");
                    }
                    
                    buttonBookCourt.setVisibility(View.GONE);
                    buttonChangeStatus.setVisibility(View.VISIBLE);
                    break;
                    
                case MAINTENANCE:
                    badgeStatus.setText("BẢO TRÌ");
                    badgeStatus.setBackgroundResource(R.drawable.badge_maintenance);
                    badgeStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.black));
                    
                    layoutStatusDetails.setVisibility(View.VISIBLE);
                    viewMaintenanceOverlay.setVisibility(View.VISIBLE);
                    imageDetailIcon.setImageResource(R.drawable.ic_analytics_24);
                    imageDetailIcon.setColorFilter(itemView.getContext().getResources().getColor(R.color.error));
                    textDetails.setTextColor(itemView.getContext().getResources().getColor(R.color.error));
                    
                    String completion = court.getEstimatedCompletionDate();
                    textDetails.setText("Dự kiến xong: " + (completion != null ? completion : "20/10"));
                    
                    buttonBookCourt.setVisibility(View.GONE);
                    buttonChangeStatus.setVisibility(View.VISIBLE);
                    break;
            }

            // Button actions
            buttonBookCourt.setOnClickListener(v -> {
                if (listener != null) listener.onBookCourt(court);
            });
            buttonChangeStatus.setOnClickListener(v -> {
                if (listener != null) listener.onChangeStatus(court);
            });
        }
    }
}
