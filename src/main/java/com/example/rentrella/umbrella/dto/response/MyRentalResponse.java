package com.example.rentrella.umbrella.dto.response;

public record MyRentalResponse(boolean hasRental, Long deviceId) {

    public static MyRentalResponse none() {
        return new MyRentalResponse(false, null);
    }

    public static MyRentalResponse of(Long deviceId) {
        return new MyRentalResponse(true, deviceId);
    }
}
