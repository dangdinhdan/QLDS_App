package com.example.mobile.view;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.R;
import com.example.mobile.model.Booking;
import com.example.mobile.model.Court;

import java.util.ArrayList;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    public interface OnBookingActionListener {
        void onCancelBooking(Booking booking);
        void onStatusChanged(Booking booking);
    }

    private final List<Booking> bookings = new ArrayList<>();
    private final List<Court> courts = new ArrayList<>();
    private final OnBookingActionListener listener;

    public BookingAdapter(OnBookingActionListener listener) {
        this.listener = listener;
    }

    public void setData(List<Booking> newBookings, List<Court> newCourts) {
        bookings.clear();
        bookings.addAll(newBookings);
        courts.clear();
        courts.addAll(newCourts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        
        // Find court name
        String courtName = "Sân #" + booking.getCourtId();
        for (Court c : courts) {
            if (c.getId() == booking.getCourtId()) {
                courtName = c.getName();
                break;
            }
        }
        
        holder.bind(booking, courtName, listener);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        private final TextView textPlayer;
        private final TextView textCourt;
        private final TextView textStatus;
        private final TextView textTime;
        private final ImageButton buttonAction;
        private final ImageView imageAvatar;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            textPlayer = itemView.findViewById(R.id.text_booking_player);
            textCourt = itemView.findViewById(R.id.text_booking_court);
            textStatus = itemView.findViewById(R.id.text_booking_status);
            textTime = itemView.findViewById(R.id.text_booking_time);
            buttonAction = itemView.findViewById(R.id.button_booking_action);
            imageAvatar = itemView.findViewById(R.id.image_booking_avatar);
        }

        public void bind(final Booking booking, String courtName, final OnBookingActionListener listener) {
            textPlayer.setText(booking.getPlayerName());
            textCourt.setText(courtName);
            textTime.setText(String.format("%s - %s", booking.getStartTime(), booking.getEndTime()));

            itemView.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(v.getContext(), com.example.mobile.BookingDetailActivity.class);
                intent.putExtra("booking_id", booking.getId());
                v.getContext().startActivity(intent);
            });

            // Reset Strikethrough style default
            textTime.setPaintFlags(textTime.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            textTime.setTextColor(Color.parseColor("#191C1D"));

            // Bind status styles
            String status = booking.getStatus();
            if (status == null) status = "Đã đặt";
            textStatus.setText(status);

            switch (status) {
                case "Đang sử dụng":
                    textStatus.setBackgroundResource(R.drawable.bg_chip_selected); // primary-container
                    textStatus.setTextColor(Color.parseColor("#647400")); // on-primary-container
                    buttonAction.setImageResource(R.drawable.ic_more_vert_24);
                    buttonAction.setVisibility(View.VISIBLE);
                    break;
                case "Đã đặt":
                    textStatus.setBackgroundResource(R.drawable.badge_booked); // secondary-container
                    textStatus.setTextColor(Color.parseColor("#4F6073")); // secondary color
                    buttonAction.setImageResource(R.drawable.ic_more_vert_24);
                    buttonAction.setVisibility(View.VISIBLE);
                    break;
                case "Hoàn thành":
                    textStatus.setBackgroundResource(R.drawable.bg_chip_unselected); // surface gray
                    textStatus.setTextColor(Color.parseColor("#777777"));
                    buttonAction.setImageResource(R.drawable.ic_check_circle_24);
                    buttonAction.setVisibility(View.VISIBLE);
                    textTime.setTextColor(Color.parseColor("#777777"));
                    break;
                case "Đã hủy":
                    textStatus.setBackgroundResource(R.drawable.badge_maintenance); // light red/gray
                    textStatus.setTextColor(Color.parseColor("#BA1A1A")); // error
                    buttonAction.setImageResource(R.drawable.ic_cancel_24);
                    buttonAction.setVisibility(View.VISIBLE);
                    
                    // Strikethrough for canceled bookings
                    textTime.setPaintFlags(textTime.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    textTime.setTextColor(Color.parseColor("#777777"));
                    break;
            }

            // Set up interactive menu trigger on button click
            buttonAction.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.getMenu().add("Cập nhật: Đang sử dụng");
                popup.getMenu().add("Cập nhật: Đã đặt");
                popup.getMenu().add("Cập nhật: Hoàn thành");
                popup.getMenu().add("Cập nhật: Đã hủy");
                popup.getMenu().add("Xóa lịch đặt sân");

                popup.setOnMenuItemClickListener(item -> {
                    String title = item.getTitle().toString();
                    if (title.equals("Xóa lịch đặt sân")) {
                        if (listener != null) {
                            listener.onCancelBooking(booking);
                        }
                    } else {
                        if (title.contains("Đang sử dụng")) {
                            booking.setStatus("Đang sử dụng");
                        } else if (title.contains("Đã đặt")) {
                            booking.setStatus("Đã đặt");
                        } else if (title.contains("Hoàn thành")) {
                            booking.setStatus("Hoàn thành");
                        } else if (title.contains("Đã hủy")) {
                            booking.setStatus("Đã hủy");
                        }
                        if (listener != null) {
                            listener.onStatusChanged(booking);
                        }
                    }
                    return true;
                });
                popup.show();
            });
        }
    }
}
