package com.example.beerservice.event;

import com.example.beerservice.web.model.BeerDto;

public class NewInventoryEvent extends BeerEvent {

    public NewInventoryEvent(BeerDto beerDto) {
        super(beerDto);
    }
}
