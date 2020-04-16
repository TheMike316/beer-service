package org.brewery.beerservice.domain.mapper;

import org.brewery.beerservice.domain.Beer;
import org.brewery.common.model.BeerDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = DateMapper.class)
@DecoratedWith(BeerMapperDecorator.class)
public interface BeerMapper {

    BeerDto beerToBeerDto(Beer beer);

    BeerDto beerToBeerDtoWithInventoryData(Beer beer);

    Beer beerDtoToBeer(BeerDto beerDto);

}
