package com.example.rentrella.device.dto.response;

public record CompleteCommandResponse(String status, String msg) {

    public static CompleteCommandResponse success() {
        return new CompleteCommandResponse("success", "상태 업데이트 완료");
    }
}
