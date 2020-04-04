package com.example.beerservice.service.inventory;

import java.util.UUID;

public interface BeerInventoryService {

    int getOnHandInventory(UUID beerId);
}
