package com.example.rentrella.admin.controller;

import com.example.rentrella.admin.dto.response.AdminActionResponse;
import com.example.rentrella.admin.dto.response.RentalLogResponse;
import com.example.rentrella.admin.service.AdminService;
import com.example.rentrella.auth.security.JwtTokenProvider;
import com.example.rentrella.umbrella.entity.RentalStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void 우산_꽂이_잠금을_요청하면_success_응답을_반환한다() throws Exception {
        given(adminService.lockUmbrella(1L)).willReturn(AdminActionResponse.of("우산 꽂이 잠금 완료"));

        mockMvc.perform(patch("/admin/lock/umbrella")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"deviceId\":1}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"success\",\"msg\":\"우산 꽂이 잠금 완료\"}"));
    }

    @Test
    void 우산_꽂이_열기를_요청하면_success_응답을_반환한다() throws Exception {
        given(adminService.openUmbrellaSlot(1L)).willReturn(AdminActionResponse.of("명령 접수됨"));

        mockMvc.perform(patch("/admin/open").param("deviceId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"success\",\"msg\":\"명령 접수됨\"}"));
    }

    @Test
    void 유저_대여_금지를_요청하면_success_응답을_반환한다() throws Exception {
        given(adminService.lockUser(1L)).willReturn(AdminActionResponse.of("대여 금지 처리 완료"));

        mockMvc.perform(patch("/admin/lock/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"success\",\"msg\":\"대여 금지 처리 완료\"}"));
    }

    @Test
    void 대여_반납_로그를_조회한다() throws Exception {
        RentalLogResponse log = new RentalLogResponse(1L, 1L, 5L, RentalStatus.BORROW.name(), LocalDateTime.now());
        given(adminService.getRentalLogs()).willReturn(List.of(log));

        mockMvc.perform(get("/admin/umbrella"))
                .andExpect(status().isOk());
    }
}
