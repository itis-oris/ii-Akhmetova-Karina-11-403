package org.example.cakeshop.controller.api;

import org.example.cakeshop.service.CakeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CakeApiController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class CakeApiControllerTest {

    @Autowired
    MockMvc mockMvc; //имитатор запросов

    @MockitoBean
    CakeService cakeService; //фальш сервис

    @Test
    void getAll_returnsCatalogJson() throws Exception {
        when(cakeService.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/cakes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
