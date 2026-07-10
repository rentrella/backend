package com.example.rentrella.umbrella.controller;

import com.example.rentrella.auth.domain.UserRole;
import com.example.rentrella.auth.security.AuthenticatedUser;
import com.example.rentrella.auth.security.JwtTokenProvider;
import com.example.rentrella.umbrella.dto.response.AvailableCountResponse;
import com.example.rentrella.umbrella.dto.response.MyRentalResponse;
import com.example.rentrella.umbrella.dto.response.RentResponse;
import com.example.rentrella.umbrella.service.UmbrellaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UmbrellaController.class)
@AutoConfigureMockMvc(addFilters = false)
class UmbrellaControllerTest {

    private static final AuthenticatedUser AUTHENTICATED_USER = new AuthenticatedUser(10L, "user@test.com", UserRole.USER);
    private static final UsernamePasswordAuthenticationToken AUTHENTICATION = new UsernamePasswordAuthenticationToken(
            AUTHENTICATED_USER, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UmbrellaService umbrellaService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void 대여_요청하면_success_응답을_반환한다() throws Exception {
        given(umbrellaService.rentUmbrella(10L, 1L)).willReturn(RentResponse.accepted());

        mockMvc.perform(post("/umbrella/Rental")
                        .with(authentication(AUTHENTICATION))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"deviceId\":1}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"success\",\"msg\":\"명령 접수됨\"}"));
    }

    @Test
    void 대여_가능한_우산_수를_조회한다() throws Exception {
        given(umbrellaService.getAvailableCount()).willReturn(new AvailableCountResponse(5L));

        mockMvc.perform(get("/umbrella").with(authentication(AUTHENTICATION)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"count\":5}"));
    }

    @Test
    void 반납_요청하면_success_응답을_반환한다() throws Exception {
        given(umbrellaService.returnUmbrella(10L, 1L)).willReturn(RentResponse.accepted());

        mockMvc.perform(post("/umbrella/return")
                        .with(authentication(AUTHENTICATION))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"deviceId\":1}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"success\",\"msg\":\"명령 접수됨\"}"));
    }

    @Test
    void 대여중인_우산이_있으면_deviceId를_반환한다() throws Exception {
        given(umbrellaService.getMyRentedUmbrella(10L)).willReturn(MyRentalResponse.of(5L));

        mockMvc.perform(get("/umbrella/me").with(authentication(AUTHENTICATION)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"hasRental\":true,\"deviceId\":5}"));
    }

    @Test
    void 대여중인_우산이_없으면_hasRental_false를_반환한다() throws Exception {
        given(umbrellaService.getMyRentedUmbrella(10L)).willReturn(MyRentalResponse.none());

        mockMvc.perform(get("/umbrella/me").with(authentication(AUTHENTICATION)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"hasRental\":false,\"deviceId\":null}"));
    }
}
