package org.brewery.beerservice.service.inventory;

import org.brewery.beerservice.service.inventory.model.BeerInventoryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

class BeerInventoryServiceImplTest {

    @Mock
    RestTemplateBuilder builder;

    @Mock
    RestTemplate restTemplate;

    String apiAddress = "http://testhost:8080";

    BeerInventoryServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        given(builder.build()).willReturn(restTemplate);

        service = new BeerInventoryServiceImpl(builder, apiAddress);
    }

    @Test
    void getOnHandInventory() {
        var list = Arrays.asList(
                BeerInventoryDto.builder().id(UUID.randomUUID()).beerId(UUID.randomUUID()).quantityOnHand(100).build(),
                BeerInventoryDto.builder().id(UUID.randomUUID()).beerId(UUID.randomUUID()).quantityOnHand(150).build()
        );
        var response = ResponseEntity.ok(list);


        given(restTemplate.exchange(any(URI.class), any(HttpMethod.class), eq(null), any(ParameterizedTypeReference.class)))
                .willReturn(response);

        var actual = service.getOnHandInventory(UUID.randomUUID());

        assertEquals(250, actual);
    }
}