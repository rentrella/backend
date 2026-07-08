package com.example.rentrella.umbrella.controller;

import com.example.rentrella.umbrella.dto.AvailableCountResponse;
import com.example.rentrella.umbrella.dto.MyRentalResponse;
import com.example.rentrella.umbrella.dto.RentResponse;
import com.example.rentrella.umbrella.service.UmbrellaService;
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

@WebMvcTest(UmbrellaController.class)
@AutoConfigureMockMvc(addFilters = false)
class UmbrellaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UmbrellaService umbrellaService;

    @Test
    void 대여_요청하면_success_응답을_반환한다() throws Exception {
        given(umbrellaService.rentUmbrella(1L)).willReturn(RentResponse.accepted());

        mockMvc.perform(post("/umbrella/Rental")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"deviceId\":1}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"success\",\"msg\":\"명령 접수됨\"}"));
    }

    @Test
    void 대여_가능한_우산_수를_조회한다() throws Exception {
        given(umbrellaService.getAvailableCount()).willReturn(new AvailableCountResponse(5L));

        mockMvc.perform(get("/umbrella"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"count\":5}"));
    }

    @Test
    void 반납_요청하면_success_응답을_반환한다() throws Exception {
        given(umbrellaService.returnUmbrella(1L)).willReturn(RentResponse.accepted());

        mockMvc.perform(post("/umbrella/return")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"deviceId\":1}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"success\",\"msg\":\"명령 접수됨\"}"));
    }

    @Test
    void 대여중인_우산이_있으면_deviceId를_반환한다() throws Exception {
        given(umbrellaService.getMyRentedUmbrella()).willReturn(MyRentalResponse.of(5L));

        mockMvc.perform(get("/umbrella/me"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"hasRental\":true,\"deviceId\":5}"));
    }

    @Test
    void 대여중인_우산이_없으면_hasRental_false를_반환한다() throws Exception {
        given(umbrellaService.getMyRentedUmbrella()).willReturn(MyRentalResponse.none());

        mockMvc.perform(get("/umbrella/me"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"hasRental\":false,\"deviceId\":null}"));
    }
}
