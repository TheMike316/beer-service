package org.brewery.beerservice.service;

import org.brewery.common.model.BeerDto;
import org.brewery.common.model.BeerList;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface BeerService {
    BeerDto getById(UUID beerId, boolean showInventoryOnHand);

    BeerDto getByUpc(String upc, boolean showInventoryOnHand);

    BeerDto saveNewBeer(BeerDto beerDto);

    void updateBeer(UUID beerId, BeerDto beerDto);

    void deleteById(UUID beerId);

    BeerList getList(String beerName, String beerStyle, PageRequest pageRequest, boolean showInventoryOnHand);

}
