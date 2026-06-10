package com.example.mobile.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.R;
import com.example.mobile.model.Booking;
import com.example.mobile.model.Court;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    public interface OnBookingActionListener {
        void onCancelBooking(Booking booking);
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
        String courtName = "Court #" + booking.getCourtId();
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
        private final TextView textDate;
        private final TextView textCourt;
        private final TextView textTime;
        private final TextView textFee;
        private final MaterialButton buttonCancel;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            textPlayer = itemView.findViewById(R.id.text_booking_player);
            textDate = itemView.findViewById(R.id.text_booking_date);
            textCourt = itemView.findViewById(R.id.text_booking_court);
            textTime = itemView.findViewById(R.id.text_booking_time);
            textFee = itemView.findViewById(R.id.text_booking_fee);
            buttonCancel = itemView.findViewById(R.id.button_cancel_booking);
        }

        public void bind(final Booking booking, String courtName, final OnBookingActionListener listener) {
            textPlayer.setText(booking.getPlayerName());
            textDate.setText(booking.getDate());
            textCourt.setText(courtName);
            textTime.setText(String.format("Time: %s - %s", booking.getStartTime(), booking.getEndTime()));
            textFee.setText(String.format("Total Fee: $%.2f", booking.getFee()));

            buttonCancel.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCancelBooking(booking);
                }
            });
        }
    }
}
