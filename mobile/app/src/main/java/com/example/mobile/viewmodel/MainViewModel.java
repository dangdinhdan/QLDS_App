package com.example.mobile.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mobile.model.Booking;
import com.example.mobile.model.Court;
import com.example.mobile.model.CourtStatus;
import com.example.mobile.repository.CourtRepository;

import java.util.List;

public class MainViewModel extends ViewModel {
    private final CourtRepository repository;

    private final MutableLiveData<List<Court>> courtsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Booking>> bookingsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Double> occupancyRateLiveData = new MutableLiveData<>();
    private final MutableLiveData<Double> totalRevenueLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> activeCourtsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> popularCourtLiveData = new MutableLiveData<>();

    public MainViewModel() {
        this.repository = CourtRepository.getInstance();
        refreshData();
    }

    public LiveData<List<Court>> getCourts() {
        return courtsLiveData;
    }

    public LiveData<List<Booking>> getBookings() {
        return bookingsLiveData;
    }

    public LiveData<Double> getOccupancyRate() {
        return occupancyRateLiveData;
    }

    public LiveData<Double> getTotalRevenue() {
        return totalRevenueLiveData;
    }

    public LiveData<Integer> getActiveCourtsCount() {
        return activeCourtsLiveData;
    }

    public LiveData<String> getMostPopularCourt() {
        return popularCourtLiveData;
    }

    public void refreshData() {
        courtsLiveData.setValue(repository.getAllCourts());
        bookingsLiveData.setValue(repository.getBookings());
        occupancyRateLiveData.setValue(repository.getOccupancyRate());
        totalRevenueLiveData.setValue(repository.getTotalRevenue());
        activeCourtsLiveData.setValue(repository.getActiveCourtsCount());
        popularCourtLiveData.setValue(repository.getMostPopularCourtName());
    }

    public void addBooking(Booking booking) {
        repository.addBooking(booking);
        refreshData();
    }

    public void deleteBooking(int bookingId) {
        repository.deleteBooking(bookingId);
        refreshData();
    }

    public void updateCourtStatus(int courtId, CourtStatus status) {
        repository.updateCourtStatus(courtId, status);
        refreshData();
    }
}
