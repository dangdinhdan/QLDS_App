package com.example.mobile.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mobile.model.Booking;
import com.example.mobile.model.Court;
import com.example.mobile.model.CourtStatus;
import com.example.mobile.model.PriceTable;
import com.example.mobile.repository.CourtRepository;

import java.util.List;

public class MainViewModel extends ViewModel {
    private final CourtRepository repository;

    private final MutableLiveData<List<Court>> courtsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Booking>> bookingsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<PriceTable>> priceTablesLiveData = new MutableLiveData<>();
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

    public LiveData<List<PriceTable>> getPriceTables() {
        return priceTablesLiveData;
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

    public void refreshDataLocalOnly() {
        courtsLiveData.postValue(repository.getAllCourts());
        bookingsLiveData.postValue(repository.getBookings());
        priceTablesLiveData.postValue(repository.getPriceTables());
        occupancyRateLiveData.postValue(repository.getOccupancyRate());
        totalRevenueLiveData.postValue(repository.getTotalRevenue());
        activeCourtsLiveData.postValue(repository.getActiveCourtsCount());
        popularCourtLiveData.postValue(repository.getMostPopularCourtName());
    }

    public void refreshData() {
        refreshDataLocalOnly();
        repository.refreshCourtsFromBackend(this::refreshDataLocalOnly);
        repository.refreshPriceTablesFromBackend(this::refreshDataLocalOnly);
        repository.refreshBookingsFromBackend(this::refreshDataLocalOnly);
    }

    public void addBooking(Booking booking) {
        repository.addBooking(booking);
        refreshDataLocalOnly();
    }

    public void deleteBooking(int bookingId) {
        repository.deleteBooking(bookingId);
        refreshDataLocalOnly();
    }

    public void updateCourtStatus(int courtId, CourtStatus status) {
        repository.updateCourtStatus(courtId, status);
        refreshDataLocalOnly();
    }

    public void updateCourt(int courtId, String name, String surfaceType, CourtStatus status) {
        repository.updateCourt(courtId, name, surfaceType, status);
        refreshDataLocalOnly();
    }

    public void deleteCourt(int courtId) {
        repository.deleteCourt(courtId);
        refreshDataLocalOnly();
    }

    public void addCourt(Court court) {
        repository.addCourt(court, this::refreshData);
        refreshDataLocalOnly();
    }

    public void addPriceTable(PriceTable pt) {
        repository.addPriceTable(pt, this::refreshData);
        refreshDataLocalOnly();
    }

    public void updatePriceTable(PriceTable pt) {
        repository.updatePriceTable(pt, this::refreshData);
        refreshDataLocalOnly();
    }

    public void deletePriceTable(int id) {
        repository.deletePriceTable(id, this::refreshData);
        refreshDataLocalOnly();
    }
}
