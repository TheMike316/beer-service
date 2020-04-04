package com.example.beerservice.web.model.mapper;

import com.example.beerservice.domain.Beer;
import com.example.beerservice.service.inventory.BeerInventoryService;
import com.example.beerservice.web.model.BeerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class BeerMapperDecorator implements BeerMapper {

    private BeerMapper beerMapper;

    private BeerInventoryService beerInventoryService;

    @Autowired
    @Qualifier("delegate")
    public void setBeerMapper(BeerMapper beerMapper) {
        this.beerMapper = beerMapper;
    }

    @Autowired
    public void setBeerInventoryService(BeerInventoryService beerInventoryService) {
        this.beerInventoryService = beerInventoryService;
    }

    @Override
    public Beer beerDtoToBeer(BeerDto beerDto) {
        return beerMapper.beerDtoToBeer(beerDto);
    }

    @Override
    public BeerDto beerToBeerDto(Beer beer) {
        var dto = beerMapper.beerToBeerDto(beer);
        var quantityOnHand = beerInventoryService.getOnHandInventory(beer.getId());
        dto.setQuantityOnHand(quantityOnHand);

        return dto;
    }
}
