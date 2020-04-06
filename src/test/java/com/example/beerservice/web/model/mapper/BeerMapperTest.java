package com.example.beerservice.web.model.mapper;

import com.example.beerservice.domain.Beer;
import com.example.beerservice.service.inventory.BeerInventoryService;
import com.example.beerservice.web.model.BeerDto;
import com.example.beerservice.web.model.BeerStyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {BeerMapperImpl.class, BeerMapperImpl_.class, DateMapper.class})
class BeerMapperTest {

    @MockBean
    BeerInventoryService beerInventoryService;

    @Autowired
    BeerMapper beerMapper;


    @Test
    void beerToBeerDto() {
        Beer beer = Beer.builder()
                .id(UUID.randomUUID())
                .version(1L)
                .createdDate(Timestamp.from(Instant.now()))
                .lastModifiedDate(Timestamp.from(Instant.now()))
                .beerName("Asdf Beer")
                .beerStyle("ALE")
                .price(new BigDecimal("8.99"))
                .upc("12345")
                .minOnHand(100)
                .quantityToBrew(10)
                .build();

        var dto = beerMapper.beerToBeerDto(beer);
        assertBeers(dto, beer);
        verify(beerInventoryService, times(0)).getOnHandInventory(any());
    }

    @Test
    void beerToBeerDtoWithInventory() {
        Beer beer = Beer.builder()
                .id(UUID.randomUUID())
                .version(1L)
                .createdDate(Timestamp.from(Instant.now()))
                .lastModifiedDate(Timestamp.from(Instant.now()))
                .beerName("Asdf Beer")
                .beerStyle("ALE")
                .price(new BigDecimal("8.99"))
                .upc("12345")
                .minOnHand(100)
                .quantityToBrew(10)
                .build();

        given(beerInventoryService.getOnHandInventory(any())).willReturn(100);

        var dto = beerMapper.beerToBeerDtoWithInventoryData(beer);
        assertBeers(dto, beer);
        assertEquals(100, dto.getQuantityOnHand());
        verify(beerInventoryService, times(1)).getOnHandInventory(any());
    }

    @Test
    void beerDtoToBeer() {
        UUID id = UUID.randomUUID();
        BeerDto dto = BeerDto.builder()
                .id(id)
                .version(1L)
                .createdDate(OffsetDateTime.now())
                .lastModifiedDate(OffsetDateTime.now())
                .beerName("Le Test")
                .beerStyle(BeerStyle.LAGER)
                .price(new BigDecimal("9.98"))
                .upc("1234")
                .quantityOnHand(10)
                .build();

        Beer entity = beerMapper.beerDtoToBeer(dto);

        assertBeers(dto, entity);
    }

    private void assertBeers(BeerDto dto, Beer entity) {
        assertNotNull(entity);
        assertNotNull(dto);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getVersion(), entity.getVersion());
        assertEquals(dto.getCreatedDate().getDayOfMonth(), entity.getCreatedDate().toLocalDateTime().getDayOfMonth());
        assertEquals(dto.getCreatedDate().getMonth(), entity.getCreatedDate().toLocalDateTime().getMonth());
        assertEquals(dto.getCreatedDate().getYear(), entity.getCreatedDate().toLocalDateTime().getYear());
        assertEquals(dto.getLastModifiedDate().getDayOfMonth(), entity.getLastModifiedDate().toLocalDateTime().getDayOfMonth());
        assertEquals(dto.getLastModifiedDate().getMonth(), entity.getLastModifiedDate().toLocalDateTime().getMonth());
        assertEquals(dto.getLastModifiedDate().getYear(), entity.getLastModifiedDate().toLocalDateTime().getYear());
        assertEquals(dto.getBeerName(), entity.getBeerName());
        assertEquals(dto.getBeerStyle().name(), entity.getBeerStyle());
        assertEquals(dto.getPrice(), entity.getPrice());
        assertEquals(dto.getUpc(), entity.getUpc());
    }
}