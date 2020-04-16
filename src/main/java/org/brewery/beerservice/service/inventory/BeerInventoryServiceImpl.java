package org.brewery.beerservice.service.inventory;

import org.brewery.common.model.BeerInventoryDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class BeerInventoryServiceImpl implements BeerInventoryService {

    private static final String API_PATH = "/api/v1/beer/{beerId}/inventory";

    private final String inventoryApiAddress;
    private final RestTemplate restTemplate;

    public BeerInventoryServiceImpl(RestTemplateBuilder restTemplateBuilder,
                                    @Value("${brewery.inventory.address}") String inventoryApiAddress) {
        this.restTemplate = restTemplateBuilder.build();
        this.inventoryApiAddress = inventoryApiAddress;
    }

    @Override
    public int getOnHandInventory(UUID beerId) {
        var uri = UriComponentsBuilder.fromUriString(inventoryApiAddress + API_PATH).build(beerId);

        ResponseEntity<List<BeerInventoryDto>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<BeerInventoryDto>>() {
                });

        return Objects.requireNonNull(response.getBody())
                .stream()
                .mapToInt(BeerInventoryDto::getQuantityOnHand)
                .sum();
    }
}
