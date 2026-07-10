package com.example.rentrella.device.controller;

import com.example.rentrella.auth.security.JwtTokenProvider;
import com.example.rentrella.device.dto.CompleteCommandResponse;
import com.example.rentrella.device.dto.PollResponse;
import com.example.rentrella.device.service.DeviceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeviceController.class)
@AutoConfigureMockMvc(addFilters = false)
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeviceService deviceService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void 대기중인_명령이_있으면_hasCommand_true를_반환한다() throws Exception {
        given(deviceService.pollLatestCommand(1L)).willReturn(new PollResponse(true, 102L));

        mockMvc.perform(get("/api/commands/latest").param("deviceId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"hasCommand\":true,\"commandId\":102}"));
    }

    @Test
    void 대기중인_명령이_없으면_hasCommand_false를_반환한다() throws Exception {
        given(deviceService.pollLatestCommand(2L)).willReturn(PollResponse.none());

        mockMvc.perform(get("/api/commands/latest").param("deviceId", "2"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"hasCommand\":false,\"commandId\":null}"));
    }

    @Test
    void 결과를_보고하면_success_응답을_반환한다() throws Exception {
        given(deviceService.completeCommand(102L)).willReturn(CompleteCommandResponse.success());

        mockMvc.perform(post("/api/commands/102/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"done\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"success\",\"msg\":\"상태 업데이트 완료\"}"));
    }
}
