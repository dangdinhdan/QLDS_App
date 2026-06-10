package com.example.mobile.repository;

import com.example.mobile.model.Booking;
import com.example.mobile.model.Court;
import com.example.mobile.model.CourtStatus;

import java.util.ArrayList;
import java.util.List;

public class CourtRepository {
    private static CourtRepository instance;
    private final List<Court> courts;
    private final List<Booking> bookings;
    private int nextBookingId = 5;

    private CourtRepository() {
        courts = new ArrayList<>();
        bookings = new ArrayList<>();
        initMockData();
    }

    public static synchronized CourtRepository getInstance() {
        if (instance == null) {
            instance = new CourtRepository();
        }
        return instance;
    }

    private void initMockData() {
        // Pre-populate 5 courts matching code.html
        courts.add(new Court(1, "Sân A1", CourtStatus.EMPTY, 15.0, "Cứng", "https://lh3.googleusercontent.com/aida-public/AB6AXuCpEMfgR-e3j0Hdx7AtEIcosPQyuCy7fmLQh-zfa8GxY6FnktWphi8fum0F15eVySWBKWs0l-ektHgmLHr6QHXeFI7E3gT4771TKqamANJfmGvNI2iJSdS5sgyHW1xDe5OCkFoy6lE4OAL8qkF-TabGD-kBwRloqHn9kvnpDL-NIn2ici4VtoYojJsHlvyQqbxKuy9WV4eWjN5vqzdA3tLqH7RQf4M9GVz4cAtEGggsPw5SfmblpK80e7TQV7M5AGx3O1VUfHtZWe1B", null));
        courts.add(new Court(2, "Sân A2", CourtStatus.IN_USE, 20.0, "Thảm", "https://lh3.googleusercontent.com/aida-public/AB6AXuCgrmfaKmP2JJRpl0m6xtud8DkYijdJhC_u7bz1kZJvYB-iCwaGRsoWOmT2tiXJtE5wHbAVW5y1Cmj_v8Nb9fx4Ay03UwW6SoxxoTh15kEb2szmUtsW-YKrq8r-K2ntObpycv19V892cVkkEXRK5-WaRyEC2qMwt40EGit165y7LffiIb1uigCJIpvTfAVR87Nb32xqyUYFa_yfMPWenl7Mew767ZDkPUM3-4tRg8eBLrk0KF_NkKYRlEavBbeRHiLCFASacnqwovYO", null));
        courts.add(new Court(3, "Sân B1", CourtStatus.BOOKED, 18.0, "Cứng", "https://lh3.googleusercontent.com/aida-public/AB6AXuBFGJlwmqur9Qa9WLmIKgC3U5unB3wGl5wySmSBNLkADTQpwaXi-sfS0CphrVisXrTQXn57ofKFpZlSMevpmGEs3twZdF5nTVACRIFSEHtq7Ua-JE5Y86xVu_R1Y4STxhul9F-shdjCefPSv-kqIb04lXT681JoSsFFFCd6yPGaVWGIn4D5AIlNOnk96-Het4aC5fZvEzZyMqGb3eDFN83ct2YM3e4Ing13NlihkgnVsxIgs1yGm7Mx3v2q0s_Bo2wnG4JJwm-ZXiA4", null));
        courts.add(new Court(4, "Sân C1", CourtStatus.MAINTENANCE, 15.0, "Cứng", "", "20/10"));
        courts.add(new Court(5, "Sân B2", CourtStatus.EMPTY, 15.0, "Thảm", "https://lh3.googleusercontent.com/aida-public/AB6AXuDIiHgvnl3jhQzzGSgh61_S1VOTMOTTSV2Hab3IjOF4Di9XpgutWEacyMT3nx8pp8dOVsnSPI2uyNwB8m-lNgo71XTXkQk5PIiFxWMLnqdR816CjqPxgdCkr1jFvSXzeyEjyTPNr-he0856Dl8L3SykteUz3j7y6D4NcB2XImuTMm9MtheCXjmkoPgUJPIGu-UBvb8Zv2FlV6iJgYA5sUhU-Drxc8UdFKTnTBH__y4E2BbDtxRu6fVn6XqTHQwBQ3tjU0lwUr4gareh", null));

        // Pre-populate bookings matching the visual context
        bookings.add(new Booking(1, 2, "Trần Thị B", "2026-06-11", "08:00", "10:00", 40.0));
        bookings.add(new Booking(2, 2, "Emma Watson", "2026-06-11", "10:00", "12:00", 40.0));
        bookings.add(new Booking(3, 3, "Nguyễn Văn A", "2026-06-11", "18:00", "20:00", 36.0));
    }

    public List<Court> getAllCourts() {
        return new ArrayList<>(courts);
    }

    public Court getCourtById(int courtId) {
        for (Court court : courts) {
            if (court.getId() == courtId) {
                return court;
            }
        }
        return null;
    }

    public List<Booking> getBookings() {
        return new ArrayList<>(bookings);
    }

    public void addBooking(Booking booking) {
        Booking newBooking = new Booking(
                nextBookingId++,
                booking.getCourtId(),
                booking.getPlayerName(),
                booking.getDate(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getFee()
        );
        bookings.add(newBooking);
        
        // Auto-update court status based on booking
        Court court = getCourtById(booking.getCourtId());
        if (court != null && court.getStatus() == CourtStatus.EMPTY) {
            court.setStatus(CourtStatus.BOOKED);
        }
    }

    public boolean deleteBooking(int bookingId) {
        Booking target = null;
        for (Booking b : bookings) {
            if (b.getId() == bookingId) {
                target = b;
                break;
            }
        }
        if (target != null) {
            bookings.remove(target);
            
            // Check if court has any other bookings, if not, update status to EMPTY
            int courtId = target.getCourtId();
            boolean hasOther = false;
            for (Booking b : bookings) {
                if (b.getCourtId() == courtId) {
                    hasOther = true;
                    break;
                }
            }
            Court court = getCourtById(courtId);
            if (court != null && !hasOther && (court.getStatus() == CourtStatus.BOOKED || court.getStatus() == CourtStatus.IN_USE)) {
                court.setStatus(CourtStatus.EMPTY);
            }
            return true;
        }
        return false;
    }

    public void updateCourtStatus(int courtId, CourtStatus status) {
        Court court = getCourtById(courtId);
        if (court != null) {
            court.setStatus(status);
        }
    }

    // Analytics / Calculation helper methods
    public double getOccupancyRate() {
        int busy = 0;
        for (Court c : courts) {
            if (c.getStatus() == CourtStatus.BOOKED || c.getStatus() == CourtStatus.IN_USE) {
                busy++;
            }
        }
        return courts.isEmpty() ? 0.0 : ((double) busy / courts.size()) * 100.0;
    }

    public double getTotalRevenue() {
        double total = 0.0;
        for (Booking b : bookings) {
            total += b.getFee();
        }
        return total;
    }

    public int getActiveCourtsCount() {
        int active = 0;
        for (Court c : courts) {
            if (c.getStatus() == CourtStatus.IN_USE) {
                active++;
            }
        }
        return active;
    }

    public String getMostPopularCourtName() {
        if (courts.isEmpty()) return "None";
        int[] counts = new int[100]; // Assume court IDs are small
        for (Booking b : bookings) {
            if (b.getCourtId() > 0 && b.getCourtId() < counts.length) {
                counts[b.getCourtId()]++;
            }
        }
        int maxId = 1;
        int maxVal = -1;
        for (Court c : courts) {
            int cid = c.getId();
            if (cid < counts.length && counts[cid] > maxVal) {
                maxVal = counts[cid];
                maxId = cid;
            }
        }
        Court popular = getCourtById(maxId);
        return popular != null ? popular.getName() : "None";
    }
}
