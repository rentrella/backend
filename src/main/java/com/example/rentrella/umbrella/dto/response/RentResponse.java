package com.example.rentrella.umbrella.dto.response;

public record RentResponse(String status, String msg) {

    public static RentResponse accepted() {
        return new RentResponse("success", "명령 접수됨");
    }
}
