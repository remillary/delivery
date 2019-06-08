package dev.muskrat.delivery.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.muskrat.delivery.dto.shop.*;
import dev.muskrat.delivery.service.shop.ShopService;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class ShopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShopService shopService;

    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    private ShopCreateResponseDTO createTestItem() {
        ShopCreateDTO createDTO = ShopCreateDTO.builder()
                .name("test")
                .build();

        String contentAsString = mockMvc.perform(post("/shop/create")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO))
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper
                .readValue(contentAsString, ShopCreateResponseDTO.class);
    }

    @Test
    @SneakyThrows
    @Transactional
    public void ShopCreateTest() {
        ShopCreateResponseDTO item = createTestItem();

        Long createdShopId = item.getId();

        ShopDTO createdShopDTO = shopService
                .findById(createdShopId).orElseThrow();

        assertEquals(createdShopDTO.getId(), createdShopId);
        assertEquals(createdShopDTO.getName(), "test");
    }

    @Test
    @SneakyThrows
    @Transactional
    public void shopUpdateDTO() {
        ShopCreateResponseDTO item = createTestItem();

        Long createdShopId = item.getId();

        ShopUpdateDTO updateDTO = ShopUpdateDTO.builder()
                .id(createdShopId)
                .description("description")
                .freeOrderPrice(10D)
                .minOrderPrice(5D)
                .logo("logo")
                .name("new name")
                .build();

        String contentAsString = mockMvc.perform(patch("/shop/update")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO))
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ShopUpdateResponseDTO productUpdateDTO = objectMapper
                .readValue(contentAsString, ShopUpdateResponseDTO.class);

        Long updatedProductId = productUpdateDTO.getId();

        ShopDTO updatedShopDTO = shopService
                .findById(updatedProductId).orElseThrow();

        assertEquals(updateDTO.getId(), updatedShopDTO.getId());
        assertEquals(updateDTO.getDescription(), updatedShopDTO.getDescription());
        assertEquals(updateDTO.getFreeOrderPrice(), updatedShopDTO.getFreeOrderPrice());
        assertEquals(updateDTO.getMinOrderPrice(), updatedShopDTO.getMinOrderPrice());
        assertEquals(updateDTO.getLogo(), updatedShopDTO.getLogo());
        assertEquals(updateDTO.getName(), "new name");
    }

    @Test
    @SneakyThrows
    @Transactional
    public void shopScheduleUpdateDTO() {
        ShopCreateResponseDTO item = createTestItem();

        Long createdShopId = item.getId();

        ShopScheduleUpdateDTO updateDTO = ShopScheduleUpdateDTO.builder()
                .id(createdShopId)
                .open(Arrays.asList(
                        LocalTime.of(9, 0),
                        LocalTime.of(9, 0),
                        LocalTime.of(9, 0),
                        LocalTime.of(9, 0),
                        LocalTime.of(9, 0),
                        LocalTime.of(9, 0),
                        LocalTime.of(9, 0))
                )
                .close(Arrays.asList(
                        LocalTime.of(22, 0),
                        LocalTime.of(22, 0),
                        LocalTime.of(22, 0),
                        LocalTime.of(22, 0),
                        LocalTime.of(22, 0),
                        LocalTime.of(22, 0),
                        LocalTime.of(22, 0))
                )
                .build();

        String contentAsString = mockMvc.perform(patch("/shop/schedule/update")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO))
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ShopScheduleResponseDTO productUpdateDTO = objectMapper
                .readValue(contentAsString, ShopScheduleResponseDTO.class);

        Long updatedProductId = productUpdateDTO.getId();

        ShopScheduleDTO updatedShopDTO = shopService
                .findScheduleById(updatedProductId).orElseThrow();

        assertEquals(updateDTO.getId(), updatedShopDTO.getId());
        assertEquals(updateDTO.getOpen(), updatedShopDTO.getOpen());
        assertEquals(updateDTO.getClose(), updatedShopDTO.getClose());
    }

    @Test
    @SneakyThrows
    public void shopDeleteTest() {
        ShopCreateResponseDTO item = createTestItem();

        Long itemId = item.getId();

        mockMvc.perform(delete("/shop/" + itemId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Optional<ShopDTO> byId = shopService.findById(itemId);
        assertTrue(byId.isEmpty());
    }
}