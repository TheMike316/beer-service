package org.brewery.beerservice.service.inventory;

import java.util.UUID;

public interface BeerInventoryService {

    int getOnHandInventory(UUID beerId);
}
