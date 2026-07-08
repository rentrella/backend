package com.example.rentrella.admin.dto.response;

public record AdminActionResponse(String status, String msg) {

    public static AdminActionResponse of(String msg) {
        return new AdminActionResponse("success", msg);
    }
}
