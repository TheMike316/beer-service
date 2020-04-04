package com.example.beerservice.service;

import com.example.beerservice.web.model.BeerDto;
import com.example.beerservice.web.model.BeerList;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface BeerService {
    BeerDto getById(UUID beerId);

    BeerDto saveNewBeer(BeerDto beerDto);

    void updateBeer(UUID beerId, BeerDto beerDto);

    void deleteById(UUID beerId);

    BeerList getList(String beerName, String beerStyle, PageRequest pageRequest);
}
