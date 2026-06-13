package com.example.mobile.repository;

import android.util.Log;
import com.example.mobile.model.Booking;
import com.example.mobile.model.Court;
import com.example.mobile.model.CourtStatus;
import com.example.mobile.model.PriceTable;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CourtRepository {
    private static CourtRepository instance;
    private static final String API_URL = "http://10.0.2.2:8080/api/san";
    private final ExecutorService executorService;
    private final List<Court> courts;
    private final List<Booking> bookings;
    private final List<PriceTable> priceTables;
    private int nextBookingId = 5;

    private CourtRepository() {
        executorService = Executors.newSingleThreadExecutor();
        courts = new ArrayList<>();
        bookings = new ArrayList<>();
        priceTables = new ArrayList<>();
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
        bookings.add(new Booking(1, 1, "Nguyễn Văn An", "2026-06-11", "17:00", "19:00", 30.0, "Đang sử dụng"));
        bookings.add(new Booking(2, 3, "Trần Thị Bích", "2026-06-11", "18:30", "20:00", 27.0, "Đã đặt"));
        bookings.add(new Booking(3, 2, "Lê Hoàng Nam", "2026-06-11", "15:00", "17:00", 40.0, "Hoàn thành"));
        bookings.add(new Booking(4, 4, "Phạm Minh Đức", "2026-06-11", "19:00", "21:00", 30.0, "Đã hủy"));
    }

    public List<Court> getAllCourts() {
        return new ArrayList<>(courts);
    }

    public List<PriceTable> getPriceTables() {
        synchronized (priceTables) {
            return new ArrayList<>(priceTables);
        }
    }

    public void refreshPriceTablesFromBackend(Runnable onComplete) {
        executorService.execute(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL("http://10.0.2.2:8080/api/banggia");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) {
                    java.io.InputStream inputStream = conn.getInputStream();
                    try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(inputStream, java.nio.charset.StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            response.append(line.trim());
                        }

                        JSONObject jsonResponse = new JSONObject(response.toString());
                        if (jsonResponse.optBoolean("success", false)) {
                            org.json.JSONArray data = jsonResponse.optJSONArray("data");
                            if (data != null) {
                                List<PriceTable> newPriceTables = new ArrayList<>();
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject obj = data.getJSONObject(i);
                                    int id = obj.optInt("id", 0);
                                    String maBanggia = obj.optString("maBanggia", "");
                                    String tenbanggia = obj.optString("tenbanggia", "");
                                    String mota = obj.optString("mota", "");
                                    newPriceTables.add(new PriceTable(id, maBanggia, tenbanggia, mota));
                                }
                                synchronized (priceTables) {
                                    priceTables.clear();
                                    priceTables.addAll(newPriceTables);
                                }
                            }
                        }
                    }
                } else {
                    Log.e("CourtRepository", "Lỗi tải bảng giá từ backend. Response code: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("CourtRepository", "Lỗi kết nối khi tải danh sách bảng giá từ backend", e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
    }

    public void addPriceTable(PriceTable pt, Runnable onComplete) {
        int nextId = 1;
        synchronized (priceTables) {
            for (PriceTable p : priceTables) {
                if (p.getId() >= nextId) {
                    nextId = p.getId() + 1;
                }
            }
        }
        PriceTable newPt = new PriceTable(nextId, pt.getMaBanggia(), pt.getTenbanggia(), pt.getMota());
        synchronized (priceTables) {
            priceTables.add(newPt);
        }

        executorService.execute(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL("http://10.0.2.2:8080/api/banggia");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                JSONObject jsonRequest = new JSONObject();
                jsonRequest.put("maBanggia", newPt.getMaBanggia());
                jsonRequest.put("tenbanggia", newPt.getTenbanggia());
                jsonRequest.put("mota", newPt.getMota());

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonRequest.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) {
                    Log.d("CourtRepository", "Thêm bảng giá lên backend thành công");
                } else {
                    Log.e("CourtRepository", "Lỗi thêm bảng giá lên backend. Response code: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("CourtRepository", "Lỗi kết nối khi thêm bảng giá lên backend", e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
    }

    public void updatePriceTable(PriceTable pt, Runnable onComplete) {
        synchronized (priceTables) {
            for (int i = 0; i < priceTables.size(); i++) {
                if (priceTables.get(i).getId() == pt.getId()) {
                    priceTables.set(i, pt);
                    break;
                }
            }
        }

        executorService.execute(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL("http://10.0.2.2:8080/api/banggia/" + pt.getId());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                JSONObject jsonRequest = new JSONObject();
                jsonRequest.put("maBanggia", pt.getMaBanggia());
                jsonRequest.put("tenbanggia", pt.getTenbanggia());
                jsonRequest.put("mota", pt.getMota());

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonRequest.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) {
                    Log.d("CourtRepository", "Cập nhật bảng giá lên backend thành công");
                } else {
                    Log.e("CourtRepository", "Lỗi cập nhật bảng giá lên backend. Response code: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("CourtRepository", "Lỗi kết nối khi cập nhật bảng giá lên backend", e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
    }

    public void deletePriceTable(int id, Runnable onComplete) {
        synchronized (priceTables) {
            PriceTable target = null;
            for (PriceTable p : priceTables) {
                if (p.getId() == id) {
                    target = p;
                    break;
                }
            }
            if (target != null) {
                priceTables.remove(target);
            }
        }

        executorService.execute(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL("http://10.0.2.2:8080/api/banggia/" + id);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) {
                    Log.d("CourtRepository", "Xóa bảng giá trên backend thành công");
                } else {
                    Log.e("CourtRepository", "Lỗi xóa bảng giá trên backend. Response code: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("CourtRepository", "Lỗi kết nối khi xóa bảng giá trên backend", e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
    }

    public boolean isCourtCodeExists(String code) {
        if (code == null) return false;
        for (Court c : courts) {
            if (c.getCourtCode() != null && c.getCourtCode().equalsIgnoreCase(code.trim())) {
                return true;
            }
        }
        return false;
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
                booking.getFee(),
                booking.getStatus()
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

    public void addCourt(Court court) {
        addCourt(court, null);
    }

    public void addCourt(Court court, Runnable onComplete) {
        int nextId = 1;
        for (Court c : courts) {
            if (c.getId() >= nextId) {
                nextId = c.getId() + 1;
            }
        }
        Court newCourt = new Court(
                nextId,
                court.getCourtCode() != null ? court.getCourtCode() : "PB-" + String.format("%02d", nextId),
                court.getName(),
                court.getStatus(),
                court.getHourlyRate() > 0 ? court.getHourlyRate() : 15.0,
                court.getSurfaceType(),
                court.getImageUrl() != null ? court.getImageUrl() : "",
                court.getEstimatedCompletionDate()
        );
        newCourt.setIdBanggia(court.getIdBanggia());
        courts.add(newCourt);

        // Call backend API in background thread
        executorService.execute(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(API_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                JSONObject jsonRequest = new JSONObject();
                jsonRequest.put("maSan", newCourt.getCourtCode());
                jsonRequest.put("ten", newCourt.getName());
                jsonRequest.put("loaimatsan", newCourt.getSurfaceType());
                jsonRequest.put("idBanggia", newCourt.getIdBanggia() != null ? newCourt.getIdBanggia() : JSONObject.NULL);

                String statusStr = "Trong";
                if (newCourt.getStatus() == CourtStatus.MAINTENANCE) {
                    statusStr = "Bảo trì";
                } else if (newCourt.getStatus() == CourtStatus.IN_USE) {
                    statusStr = "Đang sử dụng";
                } else if (newCourt.getStatus() == CourtStatus.BOOKED) {
                    statusStr = "Đã đặt";
                }
                jsonRequest.put("trangthai", statusStr);
                jsonRequest.put("url", newCourt.getImageUrl());

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonRequest.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) {
                    Log.d("CourtRepository", "Thêm sân lên backend thành công");
                } else {
                    java.io.InputStream errorStream = conn.getErrorStream();
                    if (errorStream != null) {
                        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(errorStream, java.nio.charset.StandardCharsets.UTF_8))) {
                            StringBuilder response = new StringBuilder();
                            String line;
                            while ((line = br.readLine()) != null) {
                                response.append(line.trim());
                            }
                            Log.e("CourtRepository", "Lỗi backend: " + response.toString());
                        }
                    } else {
                        Log.e("CourtRepository", "Lỗi thêm sân lên backend. Response code: " + responseCode);
                    }
                }
            } catch (Exception e) {
                Log.e("CourtRepository", "Lỗi kết nối khi thêm sân lên backend", e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
    }


    public void updateCourt(int courtId, String name, String surfaceType, CourtStatus status) {
        Court court = getCourtById(courtId);
        if (court != null) {
            court.setName(name);
            court.setSurfaceType(surfaceType);
            court.setStatus(status);
            sendUpdateToBackend(court);
        }
    }

    public void updateCourt(int courtId, String courtCode, String name, String surfaceType, CourtStatus status, String imageUrl, Integer idBanggia) {
        Court court = getCourtById(courtId);
        if (court != null) {
            court.setCourtCode(courtCode);
            court.setName(name);
            court.setSurfaceType(surfaceType);
            court.setStatus(status);
            court.setIdBanggia(idBanggia);
            if (imageUrl != null && !imageUrl.isEmpty()) {
                court.setImageUrl(imageUrl);
            }
            sendUpdateToBackend(court);
        }
    }

    private void sendUpdateToBackend(Court court) {
        executorService.execute(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(API_URL + "/" + court.getId());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                JSONObject jsonRequest = new JSONObject();
                jsonRequest.put("maSan", court.getCourtCode());
                jsonRequest.put("ten", court.getName());
                jsonRequest.put("loaimatsan", court.getSurfaceType());
                jsonRequest.put("idBanggia", court.getIdBanggia() != null ? court.getIdBanggia() : JSONObject.NULL);

                String statusStr = "Trong";
                if (court.getStatus() == CourtStatus.MAINTENANCE) {
                    statusStr = "Bảo trì";
                } else if (court.getStatus() == CourtStatus.IN_USE) {
                    statusStr = "Đang sử dụng";
                } else if (court.getStatus() == CourtStatus.BOOKED) {
                    statusStr = "Đã đặt";
                }
                jsonRequest.put("trangthai", statusStr);
                jsonRequest.put("url", court.getImageUrl());

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonRequest.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) {
                    Log.d("CourtRepository", "Cập nhật sân lên backend thành công");
                } else {
                    Log.e("CourtRepository", "Lỗi cập nhật sân lên backend. Response code: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("CourtRepository", "Lỗi kết nối khi cập nhật sân lên backend", e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        });
    }

    public boolean deleteCourt(int courtId) {
        Court court = getCourtById(courtId);
        if (court != null) {
            courts.remove(court);
            // Delete bookings related to this court
            List<Booking> toRemove = new ArrayList<>();
            for (Booking b : bookings) {
                if (b.getCourtId() == courtId) {
                    toRemove.add(b);
                }
            }
            bookings.removeAll(toRemove);
            sendDeleteToBackend(courtId);
            return true;
        }
        return false;
    }

    private void sendDeleteToBackend(int courtId) {
        executorService.execute(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(API_URL + "/" + courtId);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) {
                    Log.d("CourtRepository", "Xóa sân trên backend thành công");
                } else {
                    Log.e("CourtRepository", "Lỗi xóa sân trên backend. Response code: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("CourtRepository", "Lỗi kết nối khi xóa sân trên backend", e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        });
    }

    public void refreshCourtsFromBackend(Runnable onComplete) {
        executorService.execute(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(API_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) {
                    java.io.InputStream inputStream = conn.getInputStream();
                    try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(inputStream, java.nio.charset.StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            response.append(line.trim());
                        }

                        JSONObject jsonResponse = new JSONObject(response.toString());
                        if (jsonResponse.optBoolean("success", false)) {
                            org.json.JSONArray data = jsonResponse.optJSONArray("data");
                            if (data != null) {
                                List<Court> newCourts = new ArrayList<>();
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject obj = data.getJSONObject(i);
                                    int id = obj.optInt("id", 0);
                                    String maSan = obj.optString("maSan", "");
                                    String ten = obj.optString("ten", "");
                                    String loaimatsan = obj.optString("loaimatsan", "Trong nhà");
                                    String trangthai = obj.optString("trangthai", "Trong");
                                    String urlStr = obj.optString("url", "");

                                    CourtStatus status = CourtStatus.EMPTY;
                                    if (trangthai.equalsIgnoreCase("Trong") || trangthai.equalsIgnoreCase("EMPTY")) {
                                        status = CourtStatus.EMPTY;
                                    } else if (trangthai.equalsIgnoreCase("Bảo trì") || trangthai.equalsIgnoreCase("MAINTENANCE")) {
                                        status = CourtStatus.MAINTENANCE;
                                    } else if (trangthai.equalsIgnoreCase("Đang sử dụng") || trangthai.equalsIgnoreCase("IN_USE")) {
                                        status = CourtStatus.IN_USE;
                                    } else if (trangthai.equalsIgnoreCase("Đã đặt") || trangthai.equalsIgnoreCase("BOOKED")) {
                                        status = CourtStatus.BOOKED;
                                    }

                                    Court courtItem = new Court(id, maSan, ten, status, 15.0, loaimatsan, urlStr, null);
                                    if (!obj.isNull("idBanggia")) {
                                        courtItem.setIdBanggia(obj.optInt("idBanggia"));
                                    }
                                    newCourts.add(courtItem);
                                }
                                synchronized (courts) {
                                    courts.clear();
                                    courts.addAll(newCourts);
                                }
                            }
                        }
                    }
                } else {
                    Log.e("CourtRepository", "Lỗi tải sân từ backend. Response code: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("CourtRepository", "Lỗi kết nối khi tải danh sách sân từ backend", e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
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
