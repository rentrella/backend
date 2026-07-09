package com.example.rentrella.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "USER")
    void adminApiRejectsRegularUser() throws Exception {
        mockMvc.perform(get("/admin/umbrella"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminApiRejectsUnauthenticatedUser() throws Exception {
        mockMvc.perform(get("/admin/umbrella"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void studentSyncApiAlsoRejectsRegularUser() throws Exception {
        mockMvc.perform(post("/admin/students/sync"))
                .andExpect(status().isForbidden());
    }
}
