package org.brewery.beerservice.domain.mapper;

import org.brewery.beerservice.domain.Beer;
import org.brewery.beerservice.service.inventory.BeerInventoryService;
import org.brewery.common.model.BeerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class BeerMapperDecorator implements BeerMapper {

    private BeerMapper delegate;

    private BeerInventoryService beerInventoryService;

    @Autowired
    @Qualifier("delegate")
    public void setDelegate(BeerMapper delegate) {
        this.delegate = delegate;
    }

    @Autowired
    public void setBeerInventoryService(BeerInventoryService beerInventoryService) {
        this.beerInventoryService = beerInventoryService;
    }

    @Override
    public Beer beerDtoToBeer(BeerDto beerDto) {
        return delegate.beerDtoToBeer(beerDto);
    }

    @Override
    public BeerDto beerToBeerDto(Beer beer) {
        return delegate.beerToBeerDto(beer);
    }

    @Override
    public BeerDto beerToBeerDtoWithInventoryData(Beer beer) {
        var dto = delegate.beerToBeerDto(beer);
        var quantityOnHand = beerInventoryService.getOnHandInventory(beer.getId());
        dto.setQuantityOnHand(quantityOnHand);

        return dto;
    }
}
