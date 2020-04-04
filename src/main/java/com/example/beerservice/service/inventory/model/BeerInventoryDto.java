package com.example.beerservice.service.inventory.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeerInventoryDto {
    private UUID id;
    private UUID beerId;
    private Integer quantityOnHand;
}
